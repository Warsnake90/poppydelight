#version 150

uniform sampler2D DiffuseSampler;
uniform float Contrast;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    // Apply contrast
    color.rgb = (color.rgb - 0.5) * Contrast + 0.5;

    fragColor = color;
}