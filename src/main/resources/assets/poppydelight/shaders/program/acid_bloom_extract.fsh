#version 150

uniform sampler2D DiffuseSampler;
uniform float Threshold;
uniform float Knee;

in vec2 texCoord;
out vec4 fragColor;

float luminance(vec3 c) {
    return dot(c, vec3(0.2126, 0.7152, 0.0722));
}

void main() {
    vec3 c = texture(DiffuseSampler, texCoord).rgb;
    float l = luminance(c);

    float soft = clamp((l - Threshold + Knee) / (2.0 * Knee), 0.0, 1.0);
    float w = max(l - Threshold, 0.0) + soft * soft * Knee;

    fragColor = vec4(c * (w / max(l, 1e-5)), 1.0);
}
