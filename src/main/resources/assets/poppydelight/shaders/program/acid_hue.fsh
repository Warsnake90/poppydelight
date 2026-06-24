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

    vec3 col = texture(DiffuseSampler, uv).rgb;
    vec3 hsv = rgb2hsv(col);

    float breathA = sin(t * 0.00031) * 0.5 + 0.5;
    float breathB = sin(t * 0.00079 + 1.3) * 0.5 + 0.5;
    float breath  = mix(breathA, breathB, 0.35);

    // -----------------------------------------------------------------------
    // HUE ROTATION
    // 4 overlapping oscillators at different rates create non-repeating
    // colour cycling — the "living colour" quality of the experience.
    // -----------------------------------------------------------------------
    float slowDrift   = sin(t * 0.000157) * 0.12;
    float midSweep    = sin(t * 0.000524 + dist * 2.1) * 0.08;
    float fastFlicker = sin(t * 0.00201  + uv.x * 5.3) * 0.015 * breath;
    float spatialWave = sin(uv.x * 3.7 + uv.y * 2.9 + t * 0.000312) * 0.04;

    float hueShift = slowDrift + midSweep + fastFlicker + spatialWave;

    // Slow random colour cast across the whole scene
    float castT = t * 0.000062;
    float castA = noise1(castT);
    float castB = noise1(castT + 7.3);
    float cast  = mix(castA, castB, 0.5) * 0.06 - 0.03;
    hueShift += cast;

    hsv.x = fract(hsv.x + hueShift);

    // -----------------------------------------------------------------------
    // SATURATION BOOST
    // Low-saturation surfaces get pushed hardest (they become tinted),
    // already-vivid colours get a gentler push to avoid clipping to white.
    // -----------------------------------------------------------------------
    float satBase  = 1.0 + 0.55 * breath;
    float satCurve = 1.0 - hsv.y * 0.4;
    float satPulse = 1.0 + 0.15 * sin(t * 0.000637 + dist * 3.0);
    hsv.y = clamp(hsv.y * satBase * satCurve * satPulse, 0.0, 1.0);

    // -----------------------------------------------------------------------
    // LUMINANCE MODULATION
    // Shadows deepen, highlights bloom, perceived contrast widens.
    // -----------------------------------------------------------------------
    float lumPulse = 1.0 + 0.07 * sin(t * 0.000891 + dist * 4.0) * breath;
    float lumCurve = pow(hsv.z, 0.82);
    hsv.z = clamp(lumCurve * lumPulse, 0.0, 1.0);

    vec3 result = hsv2rgb(hsv);

    // -----------------------------------------------------------------------
    // PHOSPHENE COLOUR OVERLAY
    // A very faint self-luminous colour wash that is strongest at screen
    // edges — like seeing coloured light where there is none.
    // -----------------------------------------------------------------------
    float phospheneT   = t * 0.000048;
    float phospheneHue = fract(phospheneT + dist * 0.3 + uv.x * 0.2 - uv.y * 0.15);
    float ph6 = phospheneHue * 6.0;
    vec3 phospheneCol  = vec3(
        clamp(abs(ph6 - 3.0) - 1.0, 0.0, 1.0),
        clamp(2.0 - abs(ph6 - 2.0), 0.0, 1.0),
        clamp(2.0 - abs(ph6 - 4.0), 0.0, 1.0)
    );
    float phospheneStr = smoothstep(0.2, 0.8, dist) * (0.04 + breath * 0.07);
    result = mix(result, result * phospheneCol + phospheneCol * 0.05, phospheneStr);

    // -----------------------------------------------------------------------
    // COLOUR BANDING AT PEAKS
    // Gradients develop subtle discrete steps — most visible at breath peaks.
    // -----------------------------------------------------------------------
    float bandBreath = breath * breath;
    float bandSteps  = 12.0 + (1.0 - bandBreath) * 18.0;
    vec3 banded      = floor(result * bandSteps + 0.5) / bandSteps;
    result = mix(result, banded, bandBreath * 0.22);

    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}
