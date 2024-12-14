#version 300 es
precision mediump float;

#define iterations 12
#define formuparam 0.57

#define volsteps 10
#define stepsize 0.2

#define zoom   1.200
#define tile   1.0
#define speed  0.010

#define brightness 0.0015
#define darkmatter 1.00
#define distfading 0.730
#define saturation 1.0

uniform vec2 iResolution;
uniform float iTime;
uniform vec2 iMouse;
uniform vec3 blackholeCenter;
uniform float blackholeRadius;
uniform float blackholeIntensity;

out vec4 fragColor;

float iSphere(vec3 ray, vec3 dir, vec3 center, float radius) {
    vec3 rc = ray - center;
    float c = dot(rc, rc) - (radius * radius);
    float b = dot(dir, rc);
    float d = b * b - c;
    float t = -b - sqrt(abs(d));
    float st = step(0.0, min(t, d));
    return mix(-1.0, t, st);
}

vec3 r(vec3 v, vec2 r) {
    vec4 t = sin(vec4(r, r + 1.5707963268));
    float g = dot(v.yz, t.yw);
    return vec3(v.x * t.z - g * t.x, v.y * t.w - v.z * t.y, v.x * t.x + g * t.z);
}

void main() {
    vec2 uv = gl_FragCoord.xy / iResolution.xy - 0.5;
    uv.y *= iResolution.y / iResolution.x;
    vec3 dir = vec3(uv * zoom, 1.0);
    float time = iTime * speed + 0.25;

    vec3 from = vec3(0.0, 0.0, -15.0);
    from = r(from, iMouse / 10.0);
    dir = r(dir, iMouse / 10.0);
    from += blackholeCenter;

    vec3 pos = blackholeCenter - (dot(from - blackholeCenter, dir) * dir + from);
    float intensity = dot(pos, pos);
    if (intensity > blackholeRadius * blackholeRadius) {
        intensity = 1.0 / intensity;
        dir = mix(dir, pos * sqrt(intensity), blackholeIntensity * intensity);

        float s = 0.1, fade = 1.0;
        vec3 v = vec3(0.0);
        for (int r = 0; r < volsteps; r++) {
            vec3 p = from + s * dir * 0.5;
            p = abs(vec3(tile) - mod(p, vec3(tile * 2.0)));
            float pa, a = pa = 0.0;
            for (int i = 0; i < iterations; i++) {
                p = abs(p) / dot(p, p) - formuparam;
                a += abs(length(p) - pa);
                pa = length(p);
            }
            float dm = max(0.0, darkmatter - a * a * 0.001);
            a *= a * a;
            if (r > 6) fade *= 1.0 - dm;
            v += fade;
            v += vec3(s, s * s, s * s * s * s) * a * brightness * fade;
            fade *= distfading;
            s += stepsize;
        }
        v = mix(vec3(length(v)), v, saturation);
        fragColor = vec4(v * 0.01, 1.0);
    } else {
        fragColor = vec4(0.0);
    }
}