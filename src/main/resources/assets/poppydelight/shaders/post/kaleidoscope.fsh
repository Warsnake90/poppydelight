#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Segments;

in vec2 texCoord;
out vec4 fragColor;

#define PI 3.14159265358979

void main() {
    // Shift UV to be centered at 0,0
    vec2 uv = texCoord - 0.5;

    // Convert to polar coordinates
    float angle = atan(uv.y, uv.x);
    float radius = length(uv);

    // Slow rotation over time
    angle += Time * 0.2;

    // Divide into N segments and fold
    float segAngle = PI / Segments;
    angle = mod(angle, 2.0 * segAngle);
    if (angle > segAngle) {
        angle = 2.0 * segAngle - angle;
    }

    // Convert back to Cartesian and sample
    vec2 sampledUV = vec2(cos(angle), sin(angle)) * radius + 0.5;
    sampledUV = clamp(sampledUV, 0.001, 0.999);

    fragColor = texture(DiffuseSampler, sampledUV);
}
