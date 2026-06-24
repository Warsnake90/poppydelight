#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float Intensity;
uniform float Aberration;
uniform float AberrationSpeed;
uniform float HueSpeed;
uniform float SatBoost;

in vec2 texCoord;
out vec4 fragColor;

// ═══════════════════════════════════════════════════════════════
//  NOISE
// ═══════════════════════════════════════════════════════════════

float hash1v(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 19.19);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(hash1v(i),             hash1v(i + vec2(1,0)), u.x),
        mix(hash1v(i + vec2(0,1)), hash1v(i + vec2(1,1)), u.x),
        u.y
    );
}

float fbm(vec2 p, int oct) {
    float v = 0.0, amp = 0.5, total = 0.0;
    for (int i = 0; i < oct; i++) {
        v     += vnoise(p) * amp;
        total += amp;
        p     *= 2.07;
        amp   *= 0.48;
    }
    return v / total;
}

// ═══════════════════════════════════════════════════════════════
//  SMOOTH HUE CYCLING
//  Instead of a single hue rotation angle, we build a position
//  along a smooth colour journey. The journey uses three
//  overlapping sine waves at different speeds so the path
//  through hue space is never periodic — it never exactly
//  repeats and never snaps.
// ═══════════════════════════════════════════════════════════════

vec3 hueRotate(vec3 c, float a) {
    float s = sin(a), co = cos(a);
    mat3 m = mat3(
        0.299 + 0.701*co + 0.168*s, 0.587 - 0.587*co + 0.330*s, 0.114 - 0.114*co - 0.497*s,
        0.299 - 0.299*co - 0.328*s, 0.587 + 0.413*co + 0.035*s, 0.114 - 0.114*co + 0.292*s,
        0.299 - 0.300*co + 1.250*s, 0.587 - 0.588*co - 1.050*s, 0.114 + 0.886*co - 0.203*s
    );
    return clamp(m * c, 0.0, 1.0);
}

vec3 sat(vec3 c, float factor) {
    float l = dot(c, vec3(0.2126, 0.7152, 0.0722));
    return mix(vec3(l), c, factor);
}

// Smooth multi-speed hue angle — three incommensurate frequencies
// so the cycle length is effectively infinite
float smoothHueAngle(float t, float speed, float s) {
    float a = sin(t * speed * 0.041) * 1.00;
    float b = sin(t * speed * 0.067) * 0.55;
    float c = sin(t * speed * 0.113) * 0.28;
    return (a + b + c) * (0.4 + 0.9 * s);
}

// ═══════════════════════════════════════════════════════════════
//  PHOSPHENE EDGE GLOW
//  Bright edges in the scene get a coloured halo — mimics the
//  edge-enhancement and colour fringing of visual cortex
//  overstimulation. Detected via a simple Sobel-like sample.
// ═══════════════════════════════════════════════════════════════

vec3 phospheneEdge(vec2 uv, float s, float t, vec3 base) {
    float px = 0.0015;

    // Sample neighbours
    vec3 n  = texture(DiffuseSampler, uv + vec2(0,  px)).rgb;
    vec3 so = texture(DiffuseSampler, uv + vec2(0, -px)).rgb;
    vec3 e  = texture(DiffuseSampler, uv + vec2( px, 0)).rgb;
    vec3 w  = texture(DiffuseSampler, uv + vec2(-px, 0)).rgb;

    // Luminance gradient magnitude (edge strength)
    float lN  = dot(n,  vec3(0.299, 0.587, 0.114));
    float lS  = dot(so, vec3(0.299, 0.587, 0.114));
    float lE  = dot(e,  vec3(0.299, 0.587, 0.114));
    float lW  = dot(w,  vec3(0.299, 0.587, 0.114));
    float edge = length(vec2(lE - lW, lN - lS)) * 3.5;
    edge = clamp(edge, 0.0, 1.0);

    // Halo colour cycles slowly through spectrum — different speed
    // from global hue so edges and surfaces shift independently
    float haloAngle = smoothHueAngle(t, 0.6, s) + 0.9;
    vec3  haloCol   = 0.5 + 0.5 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + haloAngle));

    // Pulse the halo brightness with a slow noise so it shimmers
    float shimmer = 0.6 + 0.4 * fbm(vec2(t * 0.14, dot(uv, vec2(3.1, 7.3))), 3);

    return mix(base, base + haloCol * edge * shimmer * 0.45, s * 0.7);
}

// ═══════════════════════════════════════════════════════════════
//  GEOMETRIC PATTERN GHOSTING
//  Faint mandala/geometric shapes float over the scene —
//  closed-eye geometric visuals overlaid at low opacity.
//  Built entirely from distance functions so no textures needed.
// ═══════════════════════════════════════════════════════════════

float hexDist(vec2 p) {
    p = abs(p);
    return max(dot(p, normalize(vec2(1.0, 1.73))), p.x);
}

vec3 geometricGhost(vec2 uv, float s, float t) {
    // Slowly drifting and rotating polar coordinate space
    vec2  c     = uv - 0.5;
    float rad   = length(c);
    float ang   = atan(c.y, c.x) + t * 0.04;

    // Multiple ring frequencies layered — gives mandala feel
    float rings = 0.0;
    for (int i = 1; i <= 5; i++) {
        float fi   = float(i);
        float r    = fract(rad * (2.5 + fi * 0.8) - t * (0.05 + fi * 0.012));
        rings     += smoothstep(0.08, 0.0, abs(r - 0.5)) * (1.0 / fi);
    }

    // Angular symmetry — 6-fold like a mandala
    float sym = 6.0;
    float angSym = fract(ang / (6.28318 / sym)) * (6.28318 / sym);
    float angSig = abs(angSym - (6.28318 / sym) * 0.5);
    float petals = smoothstep(0.38, 0.0, abs(angSig - 0.4)) * (0.4 + 0.6 * sin(t * 0.07));

    float pattern = clamp(rings * 0.6 + petals * rings * 0.5, 0.0, 1.0);

    // Pattern colour — slow smooth cycle, offset from global hue
    float ghostHue = smoothHueAngle(t, 0.4, s) + 1.8 + rad * 2.0;
    vec3  ghostCol = 0.5 + 0.5 * cos(6.28318 * (vec3(0.0, 0.33, 0.67) + ghostHue));

    // Fade toward center and edges — appears in midfield of vision
    float radMask = smoothstep(0.05, 0.18, rad) * smoothstep(0.52, 0.38, rad);

    // Very low opacity — these are ghostly overlays not solid shapes
    float opacity = pattern * radMask * s * 0.18;
    return ghostCol * opacity;
}

vec3 colourDrain(vec3 col, vec2 uv, float s, float t) {

    // Slow irregular trigger — fbm so it never pulses on a clean beat
    float drainNoise = fbm(vec2(t * 0.031, t * 0.019), 5);
    float drainTrig  = smoothstep(0.58, 0.72, drainNoise) * s;

    // Spatial variation — drain hits one side of the screen before
    // the other, creating an asymmetric creeping desaturation
    float spatialDelay = fbm(uv * 1.4 + vec2(t * 0.02, 0.0), 3);
    float localDrain   = clamp(drainTrig - spatialDelay * 0.4, 0.0, 1.0);

    // Target colour: sickly desaturated grey-green
    float lum      = dot(col, vec3(0.2126, 0.7152, 0.0722));
    vec3  sickGrey = vec3(lum * 0.82, lum * 0.88, lum * 0.74);

    // Pull toward grey-green during drain
    col = mix(col, sickGrey, localDrain * 0.78);

    // As colour returns it comes back wrong — slight hue skew
    // so things look subtly off even after the drain passes
    float returnSkew = drainTrig * 0.3 * s;
    col.r = mix(col.r, col.r * 0.88, returnSkew);
    col.b = mix(col.b, col.b * 1.12, returnSkew);

    return col;
}

// ═══════════════════════════════════════════════════════════════
//  MAIN
// ═══════════════════════════════════════════════════════════════

void main() {
    float s  = clamp(Intensity, 0.0, 1.0);
    float t  = Time;
    vec2  uv = texCoord;

    // ── Radial chromatic aberration ───────────────────────────────
    vec2  toCenter = uv - 0.5;
    float dist     = length(toCenter);
    vec2  radDir   = normalize(toCenter + 0.0001);

    // Aberration breathes via smooth noise — no clean sine snap
    float breathe = fbm(vec2(t * AberrationSpeed * 0.28, 0.5), 4);
    float aberMag  = Aberration * (0.3 + 0.7 * breathe) * dist * (0.4 + 1.6 * s);

    float r = texture(DiffuseSampler, clamp(uv + radDir * aberMag * 1.0,  0.0, 1.0)).r;
    float g = texture(DiffuseSampler, clamp(uv + radDir * aberMag * 0.0,  0.0, 1.0)).g;
    float b = texture(DiffuseSampler, clamp(uv + radDir * aberMag * -1.0, 0.0, 1.0)).b;

    vec3 col = vec3(r, g, b);
    float lum = dot(col, vec3(0.2126, 0.7152, 0.0722));

    // ── Phosphene edge glow ───────────────────────────────────────
    col = phospheneEdge(uv, s, t, col);

    // ── Cone fatigue: per-pixel hue shift driven by slow noise ────
    float fatigueField = fbm(uv * 1.6 + vec2(t * 0.09, t * 0.06), 4);
    // Smooth journey through hue — no snap, no threshold crossing
    float fatigueAngle = (fatigueField * 2.0 - 1.0)
                       * smoothHueAngle(t, 0.9, s) * 0.8;
    float highlightMask = smoothstep(0.15, 0.85, lum);
    col = hueRotate(col, fatigueAngle * (0.25 + 0.75 * highlightMask));

    // ── Global smooth hue drift ───────────────────────────────────
    // Three-wave angle means colour journey never repeats or snaps
    float driftField = fbm(uv * 0.8 + vec2(t * 0.05, t * -0.035), 4);
    float globalAngle = smoothHueAngle(t, HueSpeed, s)
                      + driftField * 0.9 * s;
    col = hueRotate(col, globalAngle);

    // ── Saturation: smooth bloom, highlights more than shadows ────
    float satFactor = mix(1.0, SatBoost, s * (0.35 + 0.65 * highlightMask));
    col = sat(col, satFactor);

    // Luminance bloom
    col = mix(col, col * (1.0 + 0.28 * lum), s * 0.45);

    // ── Geometric ghost overlay ───────────────────────────────────
    col += geometricGhost(uv, s, t);

    col = colourDrain(col, uv, s, t);

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}