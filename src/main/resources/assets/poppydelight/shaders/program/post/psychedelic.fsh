#version 150

uniform sampler2D InSampler;
uniform float Time;
uniform float Intensity;

in vec2 texCoord;
out vec4 fragColor;

// Convert RGB to HSV
vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

// Convert HSV to RGB
vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

// Smooth noise function
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = fract(sin(dot(i, vec2(127.1, 311.7))) * 43758.5453);
    float b = fract(sin(dot(i + vec2(1.0, 0.0), vec2(127.1, 311.7))) * 43758.5453);
    float c = fract(sin(dot(i + vec2(0.0, 1.0), vec2(127.1, 311.7))) * 43758.5453);
    float d = fract(sin(dot(i + vec2(1.0, 1.0), vec2(127.1, 311.7))) * 43758.5453);
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

void main() {
    vec2 uv = texCoord;

    // --- Wavy UV distortion ---
    float waveX = sin(uv.y * 12.0 + Time * 2.5) * 0.008 * Intensity;
    float waveY = cos(uv.x * 10.0 + Time * 2.0) * 0.008 * Intensity;
    vec2 warpedUV = uv + vec2(waveX, waveY);

    // --- Chromatic aberration (RGB channel split) ---
    float aberration = 0.006 * Intensity;
    float aberrationX = aberration * sin(Time * 1.3);
    float aberrationY = aberration * cos(Time * 1.7);

    float r = texture(InSampler, warpedUV + vec2(aberrationX, aberrationY)).r;
    float g = texture(InSampler, warpedUV).g;
    float b = texture(InSampler, warpedUV - vec2(aberrationX, aberrationY)).b;

    vec3 color = vec3(r, g, b);

    // --- Hue rotation over time ---
    vec3 hsv = rgb2hsv(color);
    hsv.x = fract(hsv.x + Time * 0.15 * Intensity);
    // Pump saturation for psychedelic look
    hsv.y = clamp(hsv.y * (1.0 + 0.6 * Intensity), 0.0, 1.0);
    color = hsv2rgb(hsv);

    // --- Vignette pulse ---
    vec2 center = uv - 0.5;
    float dist = length(center);
    float vignettePulse = 1.0 - dist * (0.8 + 0.3 * sin(Time * 3.0)) * Intensity;
    color *= max(vignettePulse, 0.1);

    // --- Noise-based luminance shimmer ---
    float shimmer = noise(uv * 8.0 + Time * 0.5) * 0.1 * Intensity;
    color += shimmer;

    fragColor = vec4(clamp(color, 0.0, 1.0), 1.0);
}
