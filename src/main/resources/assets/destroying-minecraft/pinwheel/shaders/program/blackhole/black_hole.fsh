#veil:buffer veil:camera VeilCamera
#include veil:space_helper
#include veil:blend

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthComponent;
uniform sampler2D RandNoise;
uniform sampler2D PrevSampler;

uniform vec4 ColorModulator;
uniform float GameTime;

uniform mat4 prevProjMat;
uniform mat4 prevViewMat;
uniform vec3 prevCameraPos;




const float BH_SIZE = 0.11;
const float DISK_RADIUS = BH_SIZE + 3.5;
const int ITERATIONS = 150;

const vec3 InDiskColor = vec3(1, 0.9647058823529412, 0.9450980392156862) * 0.5;
const vec3 OutDiskColor = vec3(0.1607843137254902, 0.11764705882352941, 0.09411764705882353) * 5;

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

//https://iquilezles.org/articles/distfunctions/
float opSubtraction( float d1, float d2 ) {
    return max(-d1,d2);
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

float mapDisk(vec3 rayPos, vec3 spherePos) {
    vec3 rotatedRayPos = rayPos - spherePos;
    //BH Rotation
    rotatedRayPos.xy *= rot2D(-9);
    rotatedRayPos.yz *= rot2D(-21);

    float centerHole = sdCylinder(rotatedRayPos, 0.03, BH_SIZE + 0.45);
    float disk = sdCylinder(rotatedRayPos, 0.025, DISK_RADIUS);
//    float centerDisk = sdRoundedCylinder();

    return opSubtraction(centerHole, disk);
}


float mapSphere(vec3 rayPos, vec3 spherePos) {
    return length(rayPos - spherePos) - BH_SIZE;
}

float rand(vec2 coord) {
    return fract(sin(dot(coord, vec2(12.9898, 78.223))) * 43758.5453) * 2.0 - 1.0;
}

void warpSpace(inout vec3 rayPos, inout vec3 rayDir, vec3 BH_POS, in float stepDist) {
    vec3 dirToCenter = normalize(BH_POS - rayPos);
    float dstToCenter = distance(BH_POS, rayPos);

    float force = 1 / (pow(dstToCenter, 2.0));
    rayDir = normalize(mix(rayDir, dirToCenter, force * 3.0 / float(ITERATIONS)));
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

//Attenuation formula https://gamedev.stackexchange.com/questions/56897/glsl-light-attenuation-color-and-intensity-formula
float attenuation(float value, float a, float b){
    return 1 / (1 + a*abs(value) + b*abs(value)*abs(value));
}

void raymarchAccretionDisk(vec3 rayPos, float diskDist, vec3 BH_POS, inout vec4 color) {
    float radius = distance(rayPos, BH_POS);
    vec3 diskPos = rayPos - BH_POS;
    float angle = atan2(diskPos.x, diskPos.z) + GameTime * 500;
    float attenuate = attenuation(radius / 2.5, 0, 4) * (1.0 - diskDist);
//    attenuate *= attenuate;

    float cloud = clamp(fbm(vec3(radius * 15, angle * 5, diskPos.y * attenuate * attenuate)), 0.0, 1.0);
    color.rgb += vec3(cloud * attenuate) * mix(OutDiskColor, InDiskColor, attenuate * attenuate);
//    color.rgb *= texture(RandNoise, vec2(radius, angle) * 2).r;
    color.rgb *= attenuate * attenuate;
    color.a += attenuate * 0.8;
//    color.a += 1.0;
}

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 BH_POS = cameraPos + vec3(0, 1.25, -4.2);
//    vec3 BH_POS = vec3(-96, 80, 156);

//    vec4 color = texture(DiffuseSampler, texCoord) * ColorModulator;
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;

    if(depth >= 1.0){
        //Don't use normal depth here because of ghosting
        vec3 worldSpacePos = screenToWorldSpace(texCoord, 1.0).xyz;
        vec3 playerSpacePos = worldSpacePos - prevCameraPos;
        vec3 prevViewPos = (prevViewMat * vec4(playerSpacePos, 1.0)).xyz;
        vec4 homogenousPos = prevProjMat * vec4(prevViewPos, 1.0);
        vec3 ndcPos = homogenousPos.xyz / homogenousPos.w;
        vec2 prevTexcoord = (ndcPos * 0.5 + 0.5).xy;


        float farPlane = 8.0;
        vec3 ro = (VeilCamera.CameraPosition) + rand(texCoord + GameTime) * 0.01;

        vec3 rayDir = viewDirFromUv(texCoord);
        float stepDist = farPlane / float(ITERATIONS);
        float dist = 0.0;


        bool hit = false;
        vec4 BHcolor = vec4(0.0);
        vec3 rayPos = ro;
        vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
        for(int i = 0; i <= ITERATIONS; i++) {
            rayPos += rayDir * stepDist;


            float diskDist = mapDisk(rayPos, BH_POS);
            dist += stepDist;

            //Warp Space
            warpSpace(rayPos, rayDir, BH_POS, stepDist);


            //Hit Accretion Disk
            if (diskDist <= 0.001) {
                hit = true;
                raymarchAccretionDisk(rayPos, clamp(diskDist * 1000, 0.0, 1.0), BH_POS, BHcolor);
                if (BHcolor.a >= 1.0) {
                    break;
                }
            }
        }

        if(hit) {
            BHcolor.rgb *= 30;
            fragColor = vec4(blend(color, BHcolor), 1.0);
        } else {
            fragColor = color;
        }

        if(prevTexcoord.x >= 0 && prevTexcoord.x <= 1.0 && prevTexcoord.y >= 0 && prevTexcoord.y <= 1.0) {
            fragColor = mix(fragColor, texture(PrevSampler, prevTexcoord), 0.8);
        }
    } else {
        fragColor = texture(DiffuseSampler, texCoord);
    }

}