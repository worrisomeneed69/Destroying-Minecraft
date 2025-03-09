#veil:buffer veil:camera VeilCamera
#include veil:space_helper
#include veil:blend

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DepthComponent;
uniform sampler2D Normals;
uniform sampler2D RandNoise;

uniform vec4 ColorModulator;
uniform float GameTime;




const float BH_SIZE = 0.11;
const float DISK_RADIUS = BH_SIZE + 2.5;
const int ITERATIONS = 200;

// Thank you Sonic Ether: https://www.shadertoy.com/view/lstSRS

const float pi = 3.14159265;
float atan2(float y, float x){
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

float opSubtraction( float d1, float d2 ) {
    return max(-d1,d2);
}

float sdCylinder(vec3 p, float h, float r ) {
    vec2 d = abs(vec2(length(p.xz),p.y)) - vec2(r,h);
    return min(max(d.x,d.y),0.0) + length(max(d,0.0));
}

mat2 rot2D(float angle) {
    float rad = (angle * 3.151592)/180.0;
    float s = sin(rad);
    float c = cos(rad);
    return mat2(c, -s, s, c);
}

float mapDisk(vec3 rayPos, vec3 spherePos) {
    vec3 rotatedRayPos = rayPos - spherePos;
    //BH Rotation
    rotatedRayPos.xy *= rot2D(-9);
    rotatedRayPos.yz *= rot2D(-11);

    float centerHole = sdCylinder(rotatedRayPos, 0.05, BH_SIZE + 0.45);
    float disk = sdCylinder(rotatedRayPos, 0.04, DISK_RADIUS);

    return opSubtraction(centerHole, disk);
}


float mapSphere(vec3 rayPos, vec3 spherePos) {
    return length(rayPos - spherePos) - BH_SIZE;
}

float rand(vec2 coord) {
    return fract(sin(dot(coord, vec2(12.9898, 78.223))) * 43758.5453) * 2.0 - 1.0;
}

void warpSpace(inout vec3 rayPos, inout vec3 rayDir, vec3 BH_POS, inout float force, in float stepDist, inout float dstToCentre) {
    vec3 dirToCentre = normalize(BH_POS - rayPos);
    dstToCentre = length(BH_POS - rayPos);

    force = 1 / (pow(dstToCentre, 2.0));
    rayDir = normalize(mix(rayDir, dirToCentre, force * stepDist * 0.1));
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

float fbm(vec3 x) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < 8; ++i) {
        v += a * noise(x);
        x = x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

void raymarchAccretionDisk(vec3 rayPos, float diskDist, vec3 BH_POS, inout vec4 color) {
    float radius = distance(rayPos, BH_POS);
    vec3 diskPos = rayPos - BH_POS;
    float angle = atan2(diskPos.x, diskPos.z) + GameTime * 2000;

    float cloud = clamp(fbm(vec3(radius * 5, angle, diskPos.y * 3)), 0.0, 1.0);
    color += vec4(cloud * (1.0 - (radius * radius)/(DISK_RADIUS * DISK_RADIUS) ) * 0.1);
    color.rgb *= texture(RandNoise, vec2(radius, angle) * 10).rgb;
}

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 BH_POS = cameraPos + vec3(-cameraPos.x * 0.002, -cameraPos.y * 0.002 + 0.8, -4);
//    vec3 BH_POS = vec3(-96, 80, 156);

    vec4 color = texture(DiffuseSampler, texCoord) * ColorModulator;
    float depth = texture(DepthComponent, texCoord).r;
    vec3 localPos = screenToLocalSpace(texCoord, depth).rgb;
    float wordDepth = length(localPos);

    float farPlane = 8.0;
    vec3 ro = VeilCamera.CameraPosition + rand(texCoord + GameTime) * 0.01;
    vec3 rayDir = viewDirFromUv(texCoord);
    vec3 ogRayDir = rayDir;
    float dist = 0.0;


    bool hit = false;
    vec4 BHcolor = vec4(0.0);
    float force = 0.0;
    float dstToCentre = 0.0;
    if(depth >= 1.0) {
        for(int i = 0; i <= ITERATIONS; i++) {
            vec3 rayPos = ro + rayDir * dist;


            float diskDist = mapDisk(rayPos, BH_POS);
            float stepDist = farPlane / float(ITERATIONS);
            dist += stepDist;

            //Warp Space
//            warpSpace(rayPos, rayDir, BH_POS, force, stepDist, dstToCentre);


            //Hit Accretion Disk
            if(diskDist <= 0.001) {
                hit = true;
                raymarchAccretionDisk(rayPos, diskDist, BH_POS, BHcolor);
                if(BHcolor.a >= 1.0){
                    break;
                }
            } else if (dist >= wordDepth || dist > 100){
                hit = false;
                break;
            }
//else if(dstToCentre <= BH_SIZE + 0.05){
//                hit = true;
//                BHcolor = vec4(0.0);
//                break;
//            }
        }

        if(hit){
            fragColor = vec4(blend(color, BHcolor), 1.0);
        } else {
            fragColor = color;
        }
    } else {
        fragColor = color;
    }


}