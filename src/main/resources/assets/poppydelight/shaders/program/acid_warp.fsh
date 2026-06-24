#version 150

uniform sampler2D DiffuseSampler;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

// --- Noise helpers ---
vec2 hash2(vec2 p) {
    p = vec2(dot(p, vec2(127.1, 311.7)), dot(p, vec2(269.5, 183.3)));
    return -1.0 + 2.0 * fract(sin(p) * 43758.5453123);
}

float smoothNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);

    float a = dot(hash2(i + vec2(0,0)), f - vec2(0,0));
    float b = dot(hash2(i + vec2(1,0)), f - vec2(1,0));
    float c = dot(hash2(i + vec2(0,1)), f - vec2(0,1));
    float d = dot(hash2(i + vec2(1,1)), f - vec2(1,1));

    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

// Layered noise for organic feel
float fbm(vec2 p) {
    float val = 0.0;
    float amp  = 0.5;
    float freq = 1.0;
    for (int i = 0; i < 5; i++) {
        val  += amp * smoothNoise(p * freq);
        freq *= 2.13;
        amp  *= 0.48;
    }
    return val;
}

void main() {
    float t = GameTime * 1200.0; // internal clock

    vec2 uv = texCoord;
    vec2 center = vec2(0.5, 0.5);
    vec2 delta  = uv - center;
    float dist  = length(delta);

    // --- 1. BREATHING ---
    // The world slowly expands and contracts, like walls inhaling.
    // Two overlapping sine waves at slightly different speeds give a
    // non-mechanical, lung-like rhythm.
    float breathSlow = sin(t * 0.00031) * 0.5 + 0.5;
    float breathFast = sin(t * 0.00079 + 1.3) * 0.5 + 0.5;
    float breath     = mix(breathSlow, breathFast, 0.35);

    float breathAmp  = 0.018 + breath * 0.024;   // 1.8% – 4.2% scale swing
    float breathFreq = 0.7 + breath * 0.6;
    float breathWave = sin(dist * breathFreq * 12.0 - t * 0.0012) * breathAmp;

    uv += normalize(delta + vec2(0.0001)) * breathWave;

    // --- 2. SURFACE CRAWL ---
    // Flat surfaces appear to ripple as if seen through thin water.
    // Noise is sampled at two slightly offset times and blended so the
    // motion never fully repeats — matching the "almost-pattern" quality
    // that characterises psychedelic texture hallucinations.
    float crawlTime1 = t * 0.00018;
    float crawlTime2 = t * 0.00011 + 5.7;

    vec2 noiseCoord = uv * 3.5;
    float n1 = fbm(noiseCoord + vec2(crawlTime1, crawlTime1 * 0.7));
    float n2 = fbm(noiseCoord + vec2(-crawlTime2 * 0.6, crawlTime2));
    float n  = mix(n1, n2, 0.5 + 0.5 * sin(t * 0.00043));

    float crawlAmp = 0.006 + breath * 0.010;
    uv.x += n  * crawlAmp;
    uv.y += fbm(noiseCoord.yx + vec2(crawlTime1 * 0.8, -crawlTime2)) * crawlAmp;

    // --- 3. PERIPHERAL WARP ---
    // Edges of vision distort more than the centre — accurate to how
    // peripheral hallucinations dominate while central focus stays
    // relatively intact early in the experience.
    float edgeFactor = smoothstep(0.15, 0.72, dist);
    float edgeAngle  = atan(delta.y, delta.x);
    float edgeWave   = sin(edgeAngle * 3.0 + t * 0.00055) * 0.5
                     + sin(edgeAngle * 7.0 - t * 0.00033) * 0.25;
    float edgeAmp    = edgeFactor * (0.012 + breath * 0.018);

    uv += normalize(delta + vec2(0.0001)) * edgeWave * edgeAmp;

    // --- 4. FRACTAL ZOOM PULSE ---
    // Occasionally the scene feels like it is zooming in on itself —
    // a radial pulse that originates near centre and rolls outward.
    float pulsePeriod = 9.5; // seconds between pulses
    float pulsePhase  = mod(t * 0.001, pulsePeriod) / pulsePeriod;
    float pulseRing   = smoothstep(0.0, 0.15, pulsePhase)
                      * smoothstep(1.0, 0.6,  pulsePhase);
    float pulseRad    = pulsePhase * 1.4;
    float pulseMask   = exp(-abs(dist - pulseRad) * 14.0) * pulseRing;
    float pulseAmp    = 0.022 * pulseMask;

    uv -= normalize(delta + vec2(0.0001)) * pulseAmp;

    // --- 5. MICRO-JITTER ---
    // A subtle high-frequency tremble that makes edges look "alive".
    float jitterAmp = 0.0008 + breath * 0.0012;
    float jt = t * 0.0035;
    uv.x += sin(uv.y * 140.0 + jt * 2.3) * jitterAmp;
    uv.y += sin(uv.x * 140.0 + jt * 1.9) * jitterAmp;

    // Clamp to avoid sampling outside texture
    uv = clamp(uv, 0.0, 1.0);

    fragColor = texture(DiffuseSampler, uv);
}
