#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float Radius;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 px = vec2(1.0 / InSize.x, 0.0);
    float r = Radius;

    // 7-tap gaussian-ish
    vec4 sum = vec4(0.0);
    sum += texture(DiffuseSampler, texCoord + px * (-3.0 * r)) * 0.070;
    sum += texture(DiffuseSampler, texCoord + px * (-2.0 * r)) * 0.131;
    sum += texture(DiffuseSampler, texCoord + px * (-1.0 * r)) * 0.190;
    sum += texture(DiffuseSampler, texCoord)                 * 0.218;
    sum += texture(DiffuseSampler, texCoord + px * ( 1.0 * r)) * 0.190;
    sum += texture(DiffuseSampler, texCoord + px * ( 2.0 * r)) * 0.131;
    sum += texture(DiffuseSampler, texCoord + px * ( 3.0 * r)) * 0.070;

    fragColor = sum;
}
