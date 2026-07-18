#version 150

uniform sampler2D DiffuseSampler;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

float hash(float n) { return fract(sin(n) * 43758.5453); }
float noise1(float x) {
    float i = floor(x); float f = fract(x);
    return mix(hash(i), hash(i+1.0), f*f*(3.0-2.0*f));
}

// Detect local contrast (proxy for edge strength)
float edgeStrength(sampler2D tex, vec2 uv, float radius) {
    vec3 c  = texture(tex, uv).rgb;
    vec3 n  = texture(tex, uv + vec2(0, radius)).rgb;
    vec3 s  = texture(tex, uv - vec2(0, radius)).rgb;
    vec3 e  = texture(tex, uv + vec2(radius, 0)).rgb;
    vec3 w  = texture(tex, uv - vec2(radius, 0)).rgb;
    float d = length(c - n) + length(c - s) + length(c - e) + length(c - w);
    return clamp(d * 1.8, 0.0, 1.0);
}

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

void main() {
    float t = GameTime * 1200.0;

    vec2 uv     = texCoord;
    vec2 center = vec2(0.5);
    vec2 delta  = uv - center;
    float dist  = length(delta);

    float breathA = sin(t * 0.00031) * 0.5 + 0.5;
    float breathB = sin(t * 0.00079 + 1.3) * 0.5 + 0.5;
    float breath  = mix(breathA, breathB, 0.35);

    // -----------------------------------------------------------------------
    // ACTUAL EDGE CHROMATIC ABERRATION
    // -----------------------------------------------------------------------

    float edge = edgeStrength(DiffuseSampler, uv, 0.002);

    vec3 gradX = texture(DiffuseSampler, uv + vec2(0.002, 0)).rgb
               - texture(DiffuseSampler, uv - vec2(0.002, 0)).rgb;
    vec3 gradY = texture(DiffuseSampler, uv + vec2(0, 0.002)).rgb
               - texture(DiffuseSampler, uv - vec2(0, 0.002)).rgb;
    vec2 gradDir = normalize(vec2(
        dot(gradX, vec3(0.299, 0.587, 0.114)) + 1e-5,
        dot(gradY, vec3(0.299, 0.587, 0.114))
    ));

    float baseSplit = 0.0035 + breath * 0.007;
    float timeSway  = noise1(t * 0.00019) * 0.004;
    float spread    = (baseSplit + timeSway) * (0.3 + edge * 0.7);

    vec2 uvR = clamp(uv + gradDir *  spread, 0.0, 1.0);
    vec2 uvG = uv;
    vec2 uvB = clamp(uv - gradDir *  spread, 0.0, 1.0);

    vec2 uvO = clamp(uv + gradDir * (spread * 0.55), 0.0, 1.0);
    vec2 uvV = clamp(uv - gradDir * (spread * 0.55), 0.0, 1.0);

    float r = texture(DiffuseSampler, uvR).r;
    float g = texture(DiffuseSampler, uvG).g;
    float b = texture(DiffuseSampler, uvB).b;

    float rO = texture(DiffuseSampler, uvO).r;
    float bV = texture(DiffuseSampler, uvV).b;

    r = mix(r, mix(r, rO, 0.5), edge);
    b = mix(b, mix(b, bV, 0.5), edge);

    float lumBoost = 1.0 + edge * breath * 0.18;
    g *= lumBoost;

    // -----------------------------------------------------------------------
    // CONTOUR GLOW
    // -----------------------------------------------------------------------
    float glowSpread = spread * 2.5;
    vec3 glowSample  = texture(DiffuseSampler,
                         clamp(uv + gradDir * glowSpread, 0.0, 1.0)).rgb;

    float glowHue  = fract(t * 0.000072 + dist * 0.4);
    float gh6 = glowHue * 6.0;
    vec3 glowTint = vec3(
        clamp(abs(gh6 - 3.0) - 1.0, 0.0, 1.0),
        clamp(2.0 - abs(gh6 - 2.0), 0.0, 1.0),
        clamp(2.0 - abs(gh6 - 4.0), 0.0, 1.0)
    );
    glowSample = mix(glowSample, glowTint, 0.25);

    float glowStr = edge * (0.08 + breath * 0.12);
    vec3 base = vec3(r, g, b);
    vec3 result = mix(base, base + glowSample * glowStr, glowStr);

    // -----------------------------------------------------------------------
    // SURFACE IRIDESCENCE
    // -----------------------------------------------------------------------
    vec3 original = texture(DiffuseSampler, uv).rgb;
    float lum     = dot(original, vec3(0.299, 0.587, 0.114));
    float iriHue  = fract(lum * 1.7 + t * 0.000088 + dist * 0.5);
    float ih6 = iriHue * 6.0;
    vec3 iriCol = vec3(
        clamp(abs(ih6 - 3.0) - 1.0, 0.0, 1.0),
        clamp(2.0 - abs(ih6 - 2.0), 0.0, 1.0),
        clamp(2.0 - abs(ih6 - 4.0), 0.0, 1.0)
    );
    float iriStr = 0.03 + breath * 0.05;
    result = mix(result, iriCol, iriStr * (1.0 - edge));

    // -----------------------------------------------------------------------
    // CONTINUOUS COLOR WARP WAVES
    // Multiple overlapping travelling waves, each with its own spatial
    // frequency/direction/speed, so different regions of the screen warp
    // through hue-space independently rather than uniformly. Saturation
    // and value are pushed up wherever the warp is strongest so the warped
    // colour always reads as bright/vivid rather than dull or dark.
    // -----------------------------------------------------------------------
    vec3 hsvW = rgb2hsv(result);

    float wave1 = sin(uv.x * 6.0  + uv.y * 2.0  + t * 0.00045);
    float wave2 = sin(uv.x * 2.5  - uv.y * 5.0  + t * 0.00071 + 2.0);
    float wave3 = sin(uv.x * 9.0  + uv.y * 9.0  + t * 0.00029 - 1.5);
    float wave4 = sin((uv.x + uv.y) * 4.0 - t * 0.00058 + dist * 3.0);

    float waveSum   = (wave1 + wave2 + wave3 + wave4) * 0.25;
    float waveLocal = wave1 * 0.5 + wave3 * 0.5; // higher-frequency local variation

    // Hue rotates continuously, amount varies per-pixel via the waves
    float warpHue = waveSum * 0.5 + waveLocal * 0.25;
    hsvW.x = fract(hsvW.x + warpHue);

    // Strength of warp also varies spatially/temporally (0..1)
    float warpStrength = smoothstep(-1.0, 1.0, waveSum) * 0.6
                        + smoothstep(-1.0, 1.0, waveLocal) * 0.4;

    // Keep colours bright/vivid wherever the warp kicks in:
    // push saturation up and floor the value so nothing goes dark/dull.
    hsvW.y = clamp(mix(hsvW.y, max(hsvW.y, 0.65), warpStrength), 0.0, 1.0);
    hsvW.z = clamp(max(hsvW.z, 0.55 + 0.25 * warpStrength), 0.0, 1.0);

    result = hsv2rgb(hsvW);

    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}