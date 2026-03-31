#version 150

uniform sampler2D Sampler0;
uniform float Time;
uniform float WarpStrength;
uniform float Saturation;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;

    uv.x += sin(uv.y * 10.0 + Time) * WarpStrength;
    uv.y += cos(uv.x * 10.0 + Time) * WarpStrength;

    vec4 texColor = texture(Sampler0, uv);

    vec3 color = texColor.rgb * vertexColor.rgb;
    float alpha = texColor.a * vertexColor.a;

    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, Saturation);

    fragColor = vec4(color, alpha);
}