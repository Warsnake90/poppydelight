#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

uniform float Time;
uniform float Intensity;
uniform float FeedbackAmount;
uniform float Morphiness;

in vec2 texCoord;
out vec4 fragColor;

vec2 safeUV(vec2 uv) { return clamp(uv, vec2(0.001), vec2(0.999)); }

float hash21(vec2 p) {
    p = fract(p * vec2(234.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash21(i);
    float b = hash21(i + vec2(1,0));
    float c = hash21(i + vec2(0,1));
    float d = hash21(i + vec2(1,1));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a,b,u.x), mix(c,d,u.x), u.y);
}

vec2 grad(vec2 p) {
    float e = 0.002;
    float n1 = noise(p + vec2(e, 0));
    float n2 = noise(p - vec2(e, 0));
    float n3 = noise(p + vec2(0, e));
    float n4 = noise(p - vec2(0, e));
    return vec2(n1 - n2, n3 - n4) / (2.0 * e);
}

void main() {
    vec2 uv = texCoord;

    vec3 cur = texture(DiffuseSampler, safeUV(uv)).rgb;

    float t = Time * 0.6;

    vec2 g = grad(uv * (2.0 + 2.0 * Morphiness) + vec2(t, -t));
    vec2 duv = g * (0.008 + 0.012 * Morphiness) * Intensity;
    duv = clamp(duv, vec2(-0.006), vec2(0.006));

    vec3 prev = texture(PrevSampler, safeUV(uv + duv)).rgb;

    vec3 diff = abs(prev - cur);
    float motion = clamp((diff.r + diff.g + diff.b) * 2.0, 0.0, 1.0);
    motion = smoothstep(0.10, 0.35, motion);

    float f = noise(uv * (3.0 + 5.0 * Morphiness) + vec2(t * 0.4, t * 0.2));
    float k = 0.45 + 0.25 * sin(Time * 0.9);
    float edge = smoothstep(k - 0.12, k + 0.12, f);

    float morphAmt = edge * motion;

    vec3 morphed = mix(cur, prev, morphAmt);

    float fbLocal = clamp(FeedbackAmount, 0.0, 0.20) * motion;
    vec3 outc = morphed + (prev - morphed) * fbLocal;

    outc += diff * (0.12 * Morphiness) * motion * Intensity;

    outc = outc / (1.0 + outc);

    fragColor = vec4(clamp(outc, 0.0, 1.0), 1.0);
    outc = clamp(outc, 0.0, 1.0);

    outc += diff * (0.20 * Morphiness) * Intensity;

    outc = outc / (1.0 + outc);

    fragColor = vec4(clamp(outc, 0.0, 1.0), 1.0);
}