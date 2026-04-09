#version 150

uniform sampler2D InSampler;
uniform float Time;
uniform float Strength; // How much twist (e.g. 1.5)

in vec2 texCoord;
out vec4 fragColor;

#define PI 3.14159265358979

void main() {
    vec2 uv = texCoord - 0.5;

    float radius = length(uv);
    float angle = atan(uv.y, uv.x);

    // Twist amount falls off with distance (tighter in center)
    float twist = Strength * exp(-radius * 3.5) * sin(Time * 0.8);

    angle += twist;

    vec2 warpedUV = vec2(cos(angle), sin(angle)) * radius + 0.5;
    warpedUV = clamp(warpedUV, 0.001, 0.999);

    fragColor = texture(InSampler, warpedUV);
}
