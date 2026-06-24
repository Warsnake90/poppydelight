#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

float hash(float n) { return fract(sin(n) * 43758.5453); }
float noise1(float x) {
    float i = floor(x); float f = fract(x);
    return mix(hash(i), hash(i+1.0), f*f*(3.0-2.0*f));
}

vec2 hash2(vec2 p) {
    p = vec2(dot(p, vec2(127.1,311.7)), dot(p, vec2(269.5,183.3)));
    return fract(sin(p) * 43758.5453);
}

float noise2(vec2 p) {
    vec2 i = floor(p); vec2 f = fract(p);
    vec2 u = f*f*(3.0-2.0*f);
    float a = dot(hash2(i),           f);
    float b = dot(hash2(i+vec2(1,0)), f-vec2(1,0));
    float c = dot(hash2(i+vec2(0,1)), f-vec2(0,1));
    float d = dot(hash2(i+vec2(1,1)), f-vec2(1,1));
    return mix(mix(a,b,u.x), mix(c,d,u.x), u.y) * 0.5 + 0.5;
}

void main() {
    float t = GameTime * 1200.0;

    vec2 uv     = texCoord;
    vec2 center = vec2(0.5);
    vec2 delta  = uv - center;
    float dist  = length(delta);

    // Shared breath oscillator
    float breathA = sin(t * 0.00031) * 0.5 + 0.5;
    float breathB = sin(t * 0.00079 + 1.3) * 0.5 + 0.5;
    float breath  = mix(breathA, breathB, 0.35);

    vec3 current = texture(DiffuseSampler, uv).rgb;

    // -----------------------------------------------------------------------
    // TRAIL / AFTERIMAGE — the most characteristic LSD visual effect
    //
    // Moving objects leave "comet tails" of themselves. The previous frame
    // is blended back but sampled at a slightly OFFSET UV so trails appear
    // to lag behind the direction of motion rather than just ghosting in place.
    //
    // Key realism points:
    //  1. The offset direction rotates slowly so trails curve, not just smear.
    //  2. Retention decays non-linearly — bright afterimages linger longest.
    //  3. The trail is slightly hue-shifted from the original (LSD trails
    //     have a distinctive blue-violet tinge from cone fatigue).
    //  4. Retention strength pulses with breath — stronger at peak.
    // -----------------------------------------------------------------------

    // Slowly rotating trail lag direction
    float lagAngle  = t * 0.000063 + sin(t * 0.000031) * 0.8;
    float lagAmp    = 0.004 + breath * 0.010;
    vec2  lagOffset = vec2(cos(lagAngle), sin(lagAngle)) * lagAmp;

    // Additional noise-driven micro-drift — makes trails "swim" organically
    float driftT  = t * 0.000097;
    float driftX  = noise2(uv * 4.0 + vec2(driftT, 0.0)) - 0.5;
    float driftY  = noise2(uv * 4.0 + vec2(0.0, driftT + 3.7)) - 0.5;
    lagOffset    += vec2(driftX, driftY) * 0.003 * breath;

    vec2 prevUV = clamp(uv + lagOffset, 0.0, 1.0);
    vec3 prev   = texture(PrevSampler, prevUV).rgb;

    // Hue-shift the previous frame toward blue-violet (cone fatigue tinge)
    // Quick approximation: rotate in RGB space
    float tinge  = 0.08 + breath * 0.06;
    vec3 prevTinted = vec3(
        prev.r * (1.0 - tinge) + prev.b * tinge,
        prev.g * (1.0 - tinge * 0.5),
        prev.b * (1.0 - tinge) + prev.r * tinge * 0.3 + prev.g * tinge * 0.5
    );

    // Non-linear retention: bright areas persist longer than dark areas
    float prevLum    = dot(prev, vec3(0.299, 0.587, 0.114));
    float retention  = 0.70 + breath * 0.12;           // base retention 70-82%
    retention       *= (0.6 + prevLum * 0.4);           // bright→longer trail
    retention        = clamp(retention, 0.0, 0.92);     // hard cap to prevent burn-in

    // Edge areas have stronger trails (peripheral vision is more affected)
    float edgeFactor = smoothstep(0.1, 0.6, dist);
    retention = mix(retention * 0.85, retention, edgeFactor);

    vec3 trailed = mix(current, prevTinted, retention);

    // -----------------------------------------------------------------------
    // ECHO GHOSTS
    // At higher doses, multiple discrete "echoes" of moving objects appear
    // stacked behind them. We simulate this with 2 additional lag samples
    // at larger offsets, each progressively more faint and further tinted.
    // -----------------------------------------------------------------------
    float echo1Amp = lagAmp * 2.2;
    float echo2Amp = lagAmp * 3.8;

    vec2 echo1UV = clamp(uv + lagOffset * (echo1Amp / lagAmp), 0.0, 1.0);
    vec2 echo2UV = clamp(uv + lagOffset * (echo2Amp / lagAmp), 0.0, 1.0);

    vec3 echo1 = texture(PrevSampler, echo1UV).rgb;
    vec3 echo2 = texture(PrevSampler, echo2UV).rgb;

    // Echoes tinted progressively more toward violet
    echo1 = mix(echo1, vec3(echo1.b * 0.4, echo1.g * 0.7, echo1.r * 0.3 + echo1.b * 0.6), 0.3);
    echo2 = mix(echo2, vec3(echo2.b * 0.3, echo2.g * 0.5, echo2.r * 0.2 + echo2.b * 0.7), 0.5);

    float echo1Str = 0.08 + breath * 0.10;
    float echo2Str = 0.04 + breath * 0.05;

    trailed = mix(trailed, echo1, echo1Str * edgeFactor);
    trailed = mix(trailed, echo2, echo2Str * edgeFactor);

    // -----------------------------------------------------------------------
    // TEMPORAL SMEAR
    // Fast motion produces an additional blur along the direction of lag.
    // We approximate by sampling the current frame at several points along
    // the lag vector and averaging — creating a velocity-blur feel without
    // actual motion vectors.
    // -----------------------------------------------------------------------
    int SMEAR = 5;
    vec3 smearAccum = current;
    float smearW = 1.0;
    for (int i = 1; i <= SMEAR; i++) {
        float fi    = float(i) / float(SMEAR);
        float w     = 1.0 - fi * 0.7;
        vec2  sUV   = clamp(uv + lagOffset * fi * 1.5, 0.0, 1.0);
        smearAccum += texture(DiffuseSampler, sUV).rgb * w;
        smearW     += w;
    }
    vec3 smeared = smearAccum / smearW;

    float smearStr = 0.20 + breath * 0.15;
    trailed = mix(trailed, smeared, smearStr * edgeFactor * 0.5);

    // -----------------------------------------------------------------------
    // RETINAL BURN — very bright areas leave a complementary-coloured
    // ghost even after the stimulus is gone (like staring at a lamp).
    // -----------------------------------------------------------------------
    float currentLum = dot(current, vec3(0.299, 0.587, 0.114));
    float burnMask   = smoothstep(0.75, 1.0, currentLum) * breath;

    // Complementary colour of the bright region
    vec3 burnComp = vec3(1.0) - current;
    float burnStr = burnMask * 0.12;
    trailed = mix(trailed, trailed + burnComp * burnStr, burnStr);

    // -----------------------------------------------------------------------
    // NOISE GRAIN — very light organic film noise to stop the trail from
    // looking too digital. Matches the visual "static" that appears in
    // peripheral vision during a strong experience.
    // -----------------------------------------------------------------------
    float grainSeed = fract(uv.x * 521.3 + uv.y * 1371.9 + t * 0.00031);
    float grain     = (fract(sin(grainSeed * 127.3) * 43758.5) - 0.5) * 0.018;
    trailed        += grain * (0.5 + breath * 0.5);

    fragColor = vec4(clamp(trailed, 0.0, 1.0), 1.0);
}
