//https://iquilezles.org/articles/distfunctions/

const float pi = 3.14159265;
float dot2( in vec2 v ) { return dot(v,v); }
float dot2( in vec3 v ) { return dot(v,v); }

float opSubtraction( float d1, float d2 ) {
    return max(-d1,d2);
}

float sdSphere(vec3 p, float s){
    return length(p)-s;
}

float sdCappedCone( vec3 p, float h, float r1, float r2 ) {
    vec2 q = vec2( length(p.xz), p.y );
    vec2 k1 = vec2(r2,h);
    vec2 k2 = vec2(r2-r1,2.0*h);
    vec2 ca = vec2(q.x-min(q.x,(q.y<0.0)?r1:r2), abs(q.y)-h);
    vec2 cb = q - k1 + k2*clamp( dot(k1-q,k2)/dot2(k2), 0.0, 1.0 );
    float s = (cb.x<0.0 && ca.y<0.0) ? -1.0 : 1.0;
    return s*sqrt( min(dot2(ca),dot2(cb)) );
}

float sdCylinder(vec3 p, float h, float r ) {
    vec2 d = abs(vec2(length(p.xz),p.y)) - vec2(r,h);
    return min(max(d.x,d.y),0.0) + length(max(d,0.0));
}

float sdRoundedCylinder( vec3 p, float ra, float rb, float h ){
    vec2 d = vec2( length(p.xz)-2.0*ra+rb, abs(p.y) - h );
    return min(max(d.x,d.y),0.0) + length(max(d,0.0)) - rb;
}

mat2 rot2D(float angle) {
    float rad = (angle * 3.151592)/180.0;
    float s = sin(rad);
    float c = cos(rad);
    return mat2(c, -s, s, c);
}

// Thank you Sonic Ether: https://www.shadertoy.com/view/lstSRS
float atan2(float y, float x) {
    if (x > 0.0) {
        return atan(y / x);
    }
    else if (x == 0.0) {
        if (y > 0.0) {
            return pi / 2.0;
        }
        else if (y < 0.0) {
            return -(pi / 2.0);
        }
        else {
            return 0.0;
        }
    }
    else { //(x < 0.0)
           if (y >= 0.0) {
               return atan(y / x) + pi;
           }
           else {
               return atan(y / x) - pi;
           }
    }
}

//Thank you https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83 for the noise functions
float mod289(float x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 mod289(vec4 x){return x - floor(x * (1.0 / 289.0)) * 289.0;}
vec4 perm(vec4 x){return mod289(((x * 34.0) + 1.0) * x);}

float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);

    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);

    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);

    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));

    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);

    return o4.y * d.y + o4.x * (1.0 - d.y);
}

float fbm(vec3 x, int iterations) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < iterations; ++i) {
        v += a * noise(x);
        x = x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

float hash(float x) { return fract(x + 1.3215 * 1.8152); }

float hash3(vec3 a) { return fract((hash(a.z * 42.8883) + hash(a.y * 36.9125) + hash(a.x * 65.4321)) * 291.1257); }

vec3 rehash3(float x) { return vec3(hash(((x + 0.5283) * 59.3829) * 274.3487), hash(((x + 0.8192) * 83.6621) * 345.3871), hash(((x + 0.2157f) * 36.6521f) * 458.3971f)); }

float sqr(float x) {return x*x;}
float fastdist(vec3 a, vec3 b) { return sqr(b.x - a.x) + sqr(b.y - a.y) + sqr(b.z - a.z); }

vec2 eval(float x, float y, float z) {
    vec4 p[27];
    for (int _x = -1; _x < 2; _x++) for (int _y = -1; _y < 2; _y++) for(int _z = -1; _z < 2; _z++) {
        vec3 _p = vec3(floor(x), floor(y), floor(z)) + vec3(_x, _y, _z);
        float h = hash3(_p);
        p[(_x + 1) + ((_y + 1) * 3) + ((_z + 1) * 3 * 3)] = vec4((rehash3(h) + _p).xyz, h);
    }
    float m = 9999.9999, w = 0.0;
    for (int i = 0; i < 27; i++) {
        float d = fastdist(vec3(x, y, z), p[i].xyz);
        if(d < m) { m = d; w = p[i].w; }
    }
    return vec2(m, w);
}