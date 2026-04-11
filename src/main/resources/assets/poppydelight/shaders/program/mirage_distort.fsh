#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform float Time;
uniform float Intensity;
uniform float Strength;
uniform float Scale;
uniform float Chromatic;
uniform float Fade;

in vec2 texCoord;
out vec4 fragColor;

float depthFactor(float d) {
    float x = smoothstep(0.15, 0.98, d);
    return x * x; // ramps harder far away
}

void main() {
    float k = clamp(Intensity, 0.0, 1.0);

    vec2 uv = texCoord;

    float d = texture(DepthSampler, uv).r;
    float df = depthFactor(d);

    // Heat-haze style distortion that ramps with distance
    float t = Time;
    float s = Scale;

    vec2 wave;
    wave.x = sin((uv.y * (18.0 * s)) + t * 0.90) + sin((uv.y * (7.0 * s)) - t * 0.55);
    wave.y = cos((uv.x * (16.0 * s)) - t * 0.85) + cos((uv.x * (6.5 * s)) + t * 0.50);
    wave *= 0.5;

    vec2 offset = wave * (Strength * (0.25 + 1.75 * df) * k);

    // Chromatic aberration (also stronger with distance)
    float c = Chromatic * df * k;

    vec3 baseCol = texture(DiffuseSampler, clamp(uv + offset, 0.0, 1.0)).rgb;

    vec3 splitCol;
    splitCol.r = texture(DiffuseSampler, clamp(uv + offset + vec2( c, 0.0), 0.0, 1.0)).r;
    splitCol.g = texture(DiffuseSampler, clamp(uv + offset,               0.0, 1.0)).g;
    splitCol.b = texture(DiffuseSampler, clamp(uv + offset - vec2( c, 0.0), 0.0, 1.0)).b;

    vec3 col = mix(baseCol, splitCol, 0.75);

    // Gentle fade breathing so it’s not constantly blasting the player
    float breathe = 0.55 + 0.45 * sin(t * 0.18);
    float amt = clamp(Fade, 0.0, 1.0) * breathe * k;

    vec3 scene = texture(DiffuseSampler, uv).rgb;
    col = mix(scene, col, amt);

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}