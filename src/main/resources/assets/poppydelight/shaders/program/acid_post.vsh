#version 150

in vec3 Position;
in vec2 UV0;

out vec2 texCoord;

void main() {
    vec2 p = Position.xy;

    // If quad is 0..1, convert to clip -1..1
    if (p.x >= 0.0 && p.x <= 1.0 && p.y >= 0.0 && p.y <= 1.0) {
        p = p * 2.0 - 1.0;
    }

    gl_Position = vec4(p, 0.0, 1.0);

    // Prefer UV0 (correct for framebuffer sampling)
    texCoord = UV0;

    // Fallback if UV0 is busted/zeroed
    if (texCoord.x == 0.0 && texCoord.y == 0.0) {
        texCoord = p * 0.5 + 0.5;
    }
}