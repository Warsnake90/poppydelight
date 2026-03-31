#version 150

uniform sampler2D Sampler0;

uniform float BlurStrength;
uniform float Saturation;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    vec2 offset = vec2(BlurStrength, 0.0);

    vec4 c0 = texture(Sampler0, uv);
    vec4 c1 = texture(Sampler0, uv + offset);
    vec4 c2 = texture(Sampler0, uv - offset);

    vec4 blurred = (c0 * 0.5) + (c1 * 0.25) + (c2 * 0.25);

    vec3 color = blurred.rgb * vertexColor.rgb;
    float alpha = blurred.a * vertexColor.a;

    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, Saturation);

    fragColor = vec4(color, alpha);
}