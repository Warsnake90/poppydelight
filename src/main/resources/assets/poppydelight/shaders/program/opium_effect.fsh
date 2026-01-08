#version 150

uniform sampler2D DiffuseSampler;
uniform float Saturation;
uniform float Contrast;
uniform float WhitePoint;

in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 color) {
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);

    float luma = luminance(color.rgb);
    vec3 gray = vec3(luma);
    color.rgb = mix(gray, color.rgb, Saturation);

    color.rgb = (color.rgb - 0.5) * Contrast + 0.5;

    color.rgb = clamp(color.rgb / WhitePoint, 0.0, 1.0);

    fragColor = color;
}