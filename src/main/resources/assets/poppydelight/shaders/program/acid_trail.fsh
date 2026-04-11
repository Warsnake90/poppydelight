#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

uniform float Time;
uniform float Intensity;
uniform float TrailStrength;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 cur = texture(DiffuseSampler, texCoord);

    float s = max(Intensity, 0.0);
    vec2 wob = vec2(
        sin(Time * 1.7 + texCoord.y * 30.0),
        cos(Time * 1.3 + texCoord.x * 30.0)
    ) * (0.0025 + 0.006*s);

    vec4 prev = texture(PrevSampler, clamp(texCoord + wob, 0.0, 1.0));

    float k = clamp(TrailStrength, 0.0, 0.85);
    vec4 outc = mix(cur, prev, k);

    fragColor = vec4(outc.rgb, 1.0);
}