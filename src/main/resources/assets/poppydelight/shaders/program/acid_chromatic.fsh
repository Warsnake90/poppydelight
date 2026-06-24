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
    // On LSD, colour fringing specifically appears at high-contrast edges —
    // a red/orange fringe on one side, blue/violet on the other — like
    // looking through a glass prism pressed against the eye.
    // This pass concentrates the effect exactly on those edges.
    // -----------------------------------------------------------------------

    float edge = edgeStrength(DiffuseSampler, uv, 0.002);

    // Direction of the colour split: perpendicular to edge gradient
    vec3 gradX = texture(DiffuseSampler, uv + vec2(0.002, 0)).rgb
               - texture(DiffuseSampler, uv - vec2(0.002, 0)).rgb;
    vec3 gradY = texture(DiffuseSampler, uv + vec2(0, 0.002)).rgb
               - texture(DiffuseSampler, uv - vec2(0, 0.002)).rgb;
    vec2 gradDir = normalize(vec2(
        dot(gradX, vec3(0.299, 0.587, 0.114)) + 1e-5,
        dot(gradY, vec3(0.299, 0.587, 0.114))
    ));

    // Spread grows with breath and has a slow random sway
    float baseSplit = 0.0035 + breath * 0.007;
    float timeSway  = noise1(t * 0.00019) * 0.004;
    float spread    = (baseSplit + timeSway) * (0.3 + edge * 0.7);

    // Sample three spectral bands: warm, neutral, cool
    // (more sophisticated multi-band is in acid_hue)
    vec2 uvR = clamp(uv + gradDir *  spread, 0.0, 1.0);
    vec2 uvG = uv; // green channel stays
    vec2 uvB = clamp(uv - gradDir *  spread, 0.0, 1.0);

    // Additional micro-split for orange and violet channels
    vec2 uvO = clamp(uv + gradDir * (spread * 0.55), 0.0, 1.0);
    vec2 uvV = clamp(uv - gradDir * (spread * 0.55), 0.0, 1.0);

    float r = texture(DiffuseSampler, uvR).r;
    float g = texture(DiffuseSampler, uvG).g;
    float b = texture(DiffuseSampler, uvB).b;

    // Blend in extra orange warmth on the warm side
    float rO = texture(DiffuseSampler, uvO).r;
    float bV = texture(DiffuseSampler, uvV).b;

    r = mix(r, mix(r, rO, 0.5), edge);
    b = mix(b, mix(b, bV, 0.5), edge);

    // Slight yellow-green luminosity boost at edges (retinal overstimulation)
    float lumBoost = 1.0 + edge * breath * 0.18;
    g *= lumBoost;

    // -----------------------------------------------------------------------
    // CONTOUR GLOW
    // A soft coloured halo radiates slightly beyond each edge.
    // We approximate by sampling with a larger offset and mixing a tinted
    // version with low weight — it reads as the "glowing outline" that is
    // one of the most commonly reported LSD visual phenomena.
    // -----------------------------------------------------------------------
    float glowSpread = spread * 2.5;
    vec3 glowSample  = texture(DiffuseSampler,
                         clamp(uv + gradDir * glowSpread, 0.0, 1.0)).rgb;

    // Tint the glow toward cyan-magenta (complementary to warm edges)
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
    // SURFACE IRIDESCENCE (thin oil-slick sheen on everything)
    // Even non-edge areas get a very faint prismatic shimmer.
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
    result = mix(result, iriCol, iriStr * (1.0 - edge)); // less on edges (already coloured)

    fragColor = vec4(clamp(result, 0.0, 1.0), 1.0);
}
