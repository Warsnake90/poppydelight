#version 150

uniform sampler2D DiffuseSampler;

uniform float Time;
uniform float HueShift;
uniform float HueDriftSpeed;
uniform float Saturation;
uniform float Contrast;
uniform float Exposure;
uniform float Vignette;

in vec2 texCoord;
out vec4 fragColor;

vec3 rgb2hsv(vec3 c) {
    vec4 K = vec4(0.0, -1.0/3.0, 2.0/3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0*d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0/3.0, 1.0/3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float vignetteMask(vec2 uv) {
    vec2 p = uv - vec2(0.5);
    float r = dot(p, p);
    // 0 center -> darker corners
    float edge = smoothstep(0.12, 0.55, r);
    return 1.0 - edge;
}

void main() {
    vec4 src = texture(DiffuseSampler, texCoord);
    vec3 rgb = src.rgb;

    vec3 hsv = rgb2hsv(rgb);
    float h = HueShift + Time * HueDriftSpeed;
    hsv.x = fract(hsv.x + h);
    hsv.y = clamp(hsv.y * Saturation, 0.0, 1.0);
    rgb = hsv2rgb(hsv);

    rgb *= Exposure;

    rgb = (rgb - 0.5) * Contrast + 0.5;

    float vm = vignetteMask(texCoord);
    rgb *= mix(1.0, vm, clamp(Vignette, 0.0, 1.0));

    fragColor = vec4(clamp(rgb, 0.0, 1.0), src.a);
}