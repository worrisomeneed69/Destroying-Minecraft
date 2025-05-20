#veil:buffer veil:camera VeilCamera
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#include veil:space_helper
#include veil:blend

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthComponent;
uniform sampler2D RandNoise;
uniform sampler2D PrevSampler;
uniform sampler2D PrevDepth;
uniform sampler2D StarsTexture;

uniform vec4 ColorModulator;
uniform float GameTime;

uniform mat4 prevProjMat;
uniform mat4 prevViewMat;
uniform vec3 prevCameraPos;

in vec2 texCoord;
out vec4 fragColor;


const float BH_SIZE = 0.15;
const float DISK_RADIUS = BH_SIZE + 3.5;
const int ITERATIONS = 150;

const vec3 InDiskColor = vec3(1, 0.9647058823529412, 0.9450980392156862) * 0.5;
const vec3 OutDiskColor = vec3(0.1607843137254902, 0.11764705882352941, 0.09411764705882353) * 5;


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

void warpSpace(inout vec3 rayPos, inout vec3 rayDir, vec3 BH_POS, in float stepDist) {
    vec3 dirToCenter = normalize(BH_POS - rayPos);
    float dstToCenter = distance(BH_POS, rayPos);

    float force = 1 / (pow(dstToCenter, 2.0));
    rayDir = normalize(mix(rayDir, dirToCenter, force * 3.0 / float(ITERATIONS)));
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

    float cloud = clamp(fbm(vec3(radius * 15, angle * 5, diskPos.y * attenuate * attenuate), 8), 0.0, 1.0);
    color.rgb += vec3(cloud * attenuate) * mix(OutDiskColor, InDiskColor, attenuate * attenuate);
//    color.rgb *= texture(RandNoise, vec2(radius, angle) * 2).r;
    color.rgb *= attenuate * attenuate;
    color.a += attenuate * 0.8;
//    color.a += 1.0;
}

vec3 projectAndDivide(mat4 projMat, vec3 position){
    vec4 homogeneousPos = projMat * vec4(position, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 BH_POS = cameraPos + vec3(0, 1, -3.2);
//    vec3 BH_POS = vec3(-96, 80, 156);

//    vec4 color = texture(DiffuseSampler, texCoord) * ColorModulator;
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;

    if(depth >= 1.0) {
        vec3 worldSpacePos = screenToWorldSpace(texCoord, depth).xyz;


        vec3 playerSpacePos = worldSpacePos - prevCameraPos;
        vec3 prevViewPos = (prevViewMat * vec4(playerSpacePos, 1.0)).xyz;
        vec4 homogenousPos = prevProjMat * vec4(prevViewPos, 1.0);
        vec3 prevNDCPos = homogenousPos.xyz / homogenousPos.w;
        vec2 prevTexcoord = (prevNDCPos * 0.5 + 0.5).xy;
        float prevDepth = texture(PrevDepth, prevTexcoord).r;

        float farPlane = 8.0;
        vec3 ro = (VeilCamera.CameraPosition) + rand(texCoord + GameTime) * 0.02;

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
//            dist += stepDist;

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
//            color = texture(StarsTexture, rayDir.xy*0.75)*3;
        }

        if(hit) {
            BHcolor.rgb *= 30;
            fragColor = vec4(blend(color, BHcolor), 1.0);
        } else {
            fragColor = color;
        }


        if(prevTexcoord.x >= 0 && prevTexcoord.x <= 1.0 && prevTexcoord.y >= 0 && prevTexcoord.y <= 1.0) {
            if(prevDepth >= 1.0){
                fragColor = mix(fragColor, texture(PrevSampler, prevTexcoord), 0.9);
            }
        }

    } else {
        fragColor = texture(DiffuseSampler, texCoord);
    }

    gl_FragDepth = depth;
}