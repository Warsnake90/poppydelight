#version 150

uniform sampler2D DiffuseSampler;

uniform float Time;
uniform float Intensity;
uniform float WarpStrength;
uniform float WarpScale;
uniform float HueSpeed;
uniform float SatBoost;

in vec2 texCoord;
out vec4 fragColor;

vec3 sat(vec3 c, float s) {
    float l = dot(c, vec3(0.2126, 0.7152, 0.0722));
    return mix(vec3(l), c, s);
}

vec3 hueRotate(vec3 c, float a) {
    float s = sin(a), co = cos(a);
    mat3 m = mat3(
        0.299 + 0.701*co + 0.168*s, 0.587 - 0.587*co + 0.330*s, 0.114 - 0.114*co - 0.497*s,
        0.299 - 0.299*co - 0.328*s, 0.587 + 0.413*co + 0.035*s, 0.114 - 0.114*co + 0.292*s,
        0.299 - 0.300*co + 1.250*s, 0.587 - 0.588*co - 1.050*s, 0.114 + 0.886*co - 0.203*s
    );
    return clamp(m * c, 0.0, 1.0);
}

vec2 kaleido(vec2 p, float blades) {
    float ang = atan(p.y, p.x);
    float rad = length(p);
    float seg = 6.2831853 / blades;
    ang = abs(mod(ang + seg * 0.5, seg) - seg * 0.5);
    return vec2(cos(ang), sin(ang)) * rad;
}

float field(vec2 p, float t, float k) {
    vec2 z = p * (1.4 + 1.0*k);
    float a = 0.0;
    for (int i = 0; i < 20; i++) {
        z = abs(z) / clamp(dot(z,z), 0.18, 4.0) - vec2(0.78 + 0.20*sin(t*0.55), 0.66 + 0.18*cos(t*0.65));
        a += exp(-2.0 * abs(z.x + z.y));
    }
    float m = 1.0 - exp(-a * (0.12 + 0.28*k));
    return clamp(m, 0.0, 1.0);
}

void main() {
    float k = clamp(Intensity, 0.0, 1.0);
    float t = Time;

    vec2 uv = texCoord;
    vec2 p = (uv - vec2(0.5)) * vec2(16.0/9.0, 1.0);

    float breathe = 0.55 + 0.45*sin(t*0.20);
    float w = WarpStrength * (0.55 + 0.85*k) * breathe;

    vec2 swirl = vec2(
        sin(p.y * (2.0*WarpScale) + t*1.0) + sin(p.y * (0.8*WarpScale) - t*0.6),
        cos(p.x * (1.8*WarpScale) - t*0.9) + cos(p.x * (0.7*WarpScale) + t*0.5)
    );
    uv += swirl * w;

    vec3 scene = texture(DiffuseSampler, clamp(uv, 0.0, 1.0)).rgb;

    float blades = 10.0 + 22.0 * k;
    vec2 q = kaleido(p * (0.95 + 0.20*sin(t*0.12)), blades);

    float m = field(q, t, k);

    float bandFreq = 46.0; // increase for tighter geometry
    float bands = sin(m * bandFreq + t * (0.8 + 1.6*k));
    float lines = smoothstep(0.55, 0.98, abs(bands));   // bright thin-ish lines
    float geo = clamp(lines, 0.0, 1.0);

    float u = m + 0.20*sin(t*0.35) + 0.08*sin(q.x*3.0 - q.y*2.0);
    vec3 pal = vec3(
        0.5 + 0.5*sin(6.2831853*(u*1.00 + t*0.06)),
        0.5 + 0.5*sin(6.2831853*(u*1.23 + t*0.05 + 0.33)),
        0.5 + 0.5*sin(6.2831853*(u*1.47 + t*0.04 + 0.67))
    );

    pal = sat(pal, 1.35 + 0.55*k);

    float center = 1.0 - smoothstep(0.0, 0.55, length(p));
    float calm = mix(1.0, 0.55, center);

    float overlayLines = (0.15 + 0.85*k) * geo * calm;      // the bright geometry
    float overlayWash  = (0.06 + 0.22*k) * (0.35 + 0.65*calm); // subtle full-screen color wash

    vec3 col = scene;

    col = mix(col, pal, overlayWash);

    col = mix(col, pal, overlayLines);

    col += pal * (0.08 + 0.28*k) * geo * calm;
    col = sat(col, mix(1.0, SatBoost, k));
    col = hueRotate(col, t * HueSpeed * (0.20 + 1.05*k));

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}