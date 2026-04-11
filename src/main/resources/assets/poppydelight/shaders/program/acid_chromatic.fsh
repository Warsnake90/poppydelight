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

// Hue rotation (fast + good enough)
vec3 hueRotate(vec3 c, float a) {
    float s = sin(a), co = cos(a);
    mat3 m = mat3(
        0.299 + 0.701*co + 0.168*s, 0.587 - 0.587*co + 0.330*s, 0.114 - 0.114*co - 0.497*s,
        0.299 - 0.299*co - 0.328*s, 0.587 + 0.413*co + 0.035*s, 0.114 - 0.114*co + 0.292*s,
        0.299 - 0.300*co + 1.250*s, 0.587 - 0.588*co - 1.050*s, 0.114 + 0.886*co - 0.203*s
    );
    return clamp(m * c, 0.0, 1.0);
}

vec3 sat(vec3 c, float s) {
    float l = dot(c, vec3(0.2126, 0.7152, 0.0722));
    return mix(vec3(l), c, s);
}

void main() {
    float s = clamp(Intensity, 0.0, 1.0);

    // chromatic aberration
    float a = Aberration * (0.6 + 0.4 * sin(Time * AberrationSpeed)) * (0.6 + 1.2*s);
    vec2 dir = normalize(vec2(0.7, 0.4));
    vec2 off = dir * a;

    float r = texture(DiffuseSampler, clamp(texCoord + off, 0.0, 1.0)).r;
    float g = texture(DiffuseSampler, texCoord).g;
    float b = texture(DiffuseSampler, clamp(texCoord - off, 0.0, 1.0)).b;

    vec3 base = vec3(r, g, b);

    // Fantastical “oil slick” rainbow overlay
    float lum = dot(base, vec3(0.2126, 0.7152, 0.0722));

    float field =
        sin(Time * (0.8 + 1.6*s) + texCoord.x * 12.0 + sin(texCoord.y * 9.0 + Time)) +
        cos(Time * (0.6 + 1.2*s) + texCoord.y * 11.0 + cos(texCoord.x * 8.0 - Time));

    float k = (0.5 + 0.5 * sin(field + lum * 6.0));

    vec3 pal = 0.5 + 0.5 * cos(6.28318 * (vec3(0.00, 0.33, 0.67) + k + Time * (0.03 + 0.10*s)));

    // only push palette hard on highlights to keep things readable
    float highlight = smoothstep(0.55, 0.95, lum);
    float palAmt = (0.10 + 0.35*s) * (0.35 + 0.65*highlight);

    vec3 col = mix(base, pal, palAmt);

    col = sat(col, mix(1.0, SatBoost, s));
    col = hueRotate(col, Time * HueSpeed * (0.35 + 0.95*s));

    fragColor = vec4(clamp(col, 0.0, 1.0), 1.0);
}