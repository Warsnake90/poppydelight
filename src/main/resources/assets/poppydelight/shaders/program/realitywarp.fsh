#version 150

uniform sampler2D DiffuseSampler;

uniform float Time;
uniform float Intensity;
uniform float WarpAmp;
uniform float Fold;

in vec2 texCoord;
out vec4 fragColor;

vec2 safeUV(vec2 uv) { return clamp(uv, vec2(0.001), vec2(0.999)); }

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 345.45));
    p += dot(p, p + 34.345);
    return fract(p.x * p.y);
}

vec2 noise2(vec2 p) {
    float n1 = hash21(p);
    float n2 = hash21(p + 17.13);
    return vec2(n1, n2) * 2.0 - 1.0;
}

vec2 rot(vec2 p, float a) {
    float s = sin(a), c = cos(a);
    return mat2(c, -s, s, c) * p;
}

// gentle fold: no fract repeat (repeat is what makes huge jumps)
vec2 foldSpace(vec2 uv, float amount) {
    vec2 p = uv * 2.0 - 1.0;
    p = rot(p, 0.2 * sin(Time * 0.7));
    p = mix(p, abs(p), clamp(amount, 0.0, 1.0));
    return p * 0.5 + 0.5;
}

void main() {
    vec2 uv = texCoord;

    float amp = WarpAmp * Intensity;

    // multi-warp domain distortion (bounded)
    vec2 w1 = noise2(uv * 2.0 + Time * 0.15) * amp;
    vec2 w2 = noise2((uv + w1) * 6.0 - Time * 0.23) * (amp * 0.6);

    vec2 warped = uv + (w1 + w2);

    // cap warp so it can't fling UVs to oblivion
    warped = uv + clamp(warped - uv, vec2(-0.012), vec2(0.012));

    // optional gentle fold (keep small)
    float f = clamp(Fold, 0.0, 0.35);
    vec2 folded = mix(warped, foldSpace(warped, f), f);

    // sample scene (clamped)
    vec2 suv = safeUV(folded);
    vec3 col = texture(DiffuseSampler, suv).rgb;

    // mild chroma split driven by warp direction (adds “reality shear”)
    vec2 d = clamp((w1 + w2) * 10.0, vec2(-0.01), vec2(0.01));
    float r = texture(DiffuseSampler, safeUV(suv + d * 0.08)).r;
    float b = texture(DiffuseSampler, safeUV(suv - d * 0.08)).b;
    col = vec3(r, col.g, b);

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}