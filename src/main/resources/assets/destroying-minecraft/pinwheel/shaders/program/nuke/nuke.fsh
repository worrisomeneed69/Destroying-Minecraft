#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform float GameTime;
uniform float flashTimer;
uniform float smokeRiseTimer;

in vec2 texCoord;
out vec4 fragColor;

//vec3(-124, 72, 157);
const vec3 NUKE_POS = vec3(-1364, 67, 1603);
//const float CONTRAST = -1;
const float ZOOM = 0.1;
const int ITERATIONS = 5;
const float ABSORPTION = 0.5;
const float SEETH = 500;

const vec3 fireColor = vec3(0.807843137254902, 0.44313725490196076, 0.09019607843137255);

float contrast(float color, float cont){
    return cont * (color - 0.5) + 0.5;
}

float map(vec3 p){
    vec3 nukePos = p - NUKE_POS;
    float cylinder = sdCylinder(nukePos - vec3(0, 40*smokeRiseTimer, 0), 40*smokeRiseTimer, 10);
    float torus = sdTorus(nukePos - vec3(0, 80*smokeRiseTimer,0), vec2(30*smokeRiseTimer, 20));
    float cone = sdCappedCone(nukePos - vec3(0, 3, 0), 3, 80*smokeRiseTimer, 1);

    float shockWave = sdTorus(nukePos, vec2(200*smokeRiseTimer, 20));

    float distortion = (fbm(nukePos + (GameTime*1000), 7) - fbm(nukePos*0.2 + (GameTime*100), 2)*2)*3;



    return min(opSmoothUnion(torus, opSmoothUnion(cylinder, cone, 20), 0), shockWave) - distortion;
}

float lqMap(vec3 p){
    vec3 nukePos = p - NUKE_POS;
    float cylinder = sdCylinder(nukePos - vec3(0, 40, 0), 44, 12);
    float torus = sdTorus(nukePos - vec3(0, 80,0), vec2(34, 24));


    return opSmoothUnion(torus, cylinder, 0);
}

vec3 rayMarchCloud(inout vec3 color, in vec3 viewPos, in vec3 rayOrigin, in vec3 rayDir) {
    vec3 step = rayDir * 0.05;

    vec3 rayPos = rayOrigin;
    float accumulation = 0.0;
    for(int i = 0; i < 100; i++){
        rayPos += step;

        float noise = fbm4d(vec4((rayPos + vec3(0,40,0)*sin(GameTime*0))*ZOOM, GameTime * SEETH), ITERATIONS);

        float d = map(rayPos);
        if(noise > 0.0){
            accumulation += noise * 0.05 + max(-d*0.1, 0.0);
        }



        if(d > 1.2 || accumulation >= 1.0){
            break;
        }

        else if(length(rayOrigin - rayPos) > 10){
            break;
        }
    }
    float dist = length(rayOrigin - rayPos);

    float fireNoise = fbm4d(vec4((rayPos + vec3(0,40,0)*smokeRiseTimer)*0.1, GameTime * 100), ITERATIONS);
    fireNoise = contrast(fireNoise, -15);
    float glow = (4 + 20*(1.0 - smokeRiseTimer));
    float ring = length(rayPos.xz - NUKE_POS.xz) > 50 ? 0.0 : 1.0;
    vec3 fire = fireColor * max((1.0 - exp(-dist * 0.5)) * fireNoise, 0.0) * glow;
    return vec3(accumulation * (1.0 - exp(-dist * 0.1))) + fire * ring;
}

void rayMarchNuke(inout vec3 color, in vec3 viewPos, inout vec3 normal, inout bool hit) {
    vec3 rayOrigin = VeilCamera.CameraPosition + VeilCamera.CameraBobOffset;
    vec3 rayDir = viewDirFromUv(texCoord);

    float dist = 0.0;
    vec3 rayPos = rayOrigin;
    for(int i = 0; i < 50; i++){
        rayPos = rayOrigin + rayDir * dist;

//        float d = lqMap(rayPos);
//        if(d < 1){
//            d = map(rayPos);
//        }
        float d = map(rayPos);
        dist += d;

        if(d <= 0.001) {
            hit = true;
            break;
        }

        else if(dist > length(viewPos) || dist > 300){
            break;
        }
    }

    if(hit) {
        color = rayMarchCloud(color, viewPos, rayPos, rayDir);
    }

}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 viewPos = screenToViewSpace(texCoord, depth).xyz;
    vec3 normal = vec3(0.0);
    bool hit = false;

    if(smokeRiseTimer > 0.0){
        rayMarchNuke(color, viewPos, normal, hit);
    }

    if(hit) {
        fragColor = vec4(color, 1.0);
    } else {
        fragColor = texture(DiffuseSampler, texCoord);
    }
    float flash = flashTimer > 0 ? 0.6 * (1.0-min(flashTimer, 1.0)) : 0.0;
    fragColor += flash;
}