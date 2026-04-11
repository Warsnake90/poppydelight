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

    float k = clamp(Intensity, 0.0, 1.0);

    // small swim so trails feel alive, not smeary
    vec2 wob = vec2(
        sin(Time * 1.15 + texCoord.y * 22.0),
        cos(Time * 0.95 + texCoord.x * 22.0)
    ) * (0.0015 + 0.0045*k);

    vec4 prev = texture(PrevSampler, clamp(texCoord + wob, 0.0, 1.0));

    // clamp so it never becomes unplayable
    float mixK = clamp(TrailStrength, 0.0, 0.85);

    vec3 outc = mix(cur.rgb, prev.rgb, mixK);
    fragColor = vec4(outc, 1.0);
}
