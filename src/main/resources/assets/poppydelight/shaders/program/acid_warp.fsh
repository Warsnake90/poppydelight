#version 150

uniform sampler2D DiffuseSampler;

uniform float Time;
uniform float Intensity;
uniform float WarpStrength;
uniform float WarpScale;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    float s = max(Intensity, 0.0);
    float t = Time;

    vec2 uv = texCoord;
    float center = 1.0 - smoothstep(0.0, 0.55, length(uv - vec2(0.5)));

    // layered waves = "breathing" distortion
    float w1 = sin((uv.y * WarpScale * 3.5) + t * 1.2);
    float w2 = sin((uv.y * WarpScale * 1.6) - t * 0.8);
    float w3 = cos((uv.x * WarpScale * 2.8) + t * 1.0);

    float calm = mix(1.0, 0.35, center);
    uv.x += ((w1 * 0.6 + w2 * 0.4) * (WarpStrength * 6.0) * s) * calm;
    uv.y += ((w3 * 0.7) * (WarpStrength * 4.0) * s) * calm;

    uv = clamp(uv, 0.0, 1.0);

    vec4 col = texture(DiffuseSampler, uv);

    // subtle contrast kick so it reads as "effect"
    col.rgb = pow(col.rgb, vec3(0.9));

    fragColor = col;
}