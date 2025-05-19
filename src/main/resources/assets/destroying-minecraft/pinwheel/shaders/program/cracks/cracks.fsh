#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform isampler2D MaterialSampler;
uniform sampler2D HandDepthSampler;

uniform sampler2D DirtTexture;

uniform float GameTime;
uniform float flashTimer;
uniform float smokeRiseTimer;

in vec2 texCoord;
out vec4 fragColor;

const vec2 centerPos = vec2(-1709, 1575);
//const vec2 centerPos = vec2(0.5, 0.5);
const float TEXTURE_SIZE = 0.5;

float getNoise(vec3 pos, float HOLE_SIZE) {
    float noise = fbm(pos * TEXTURE_SIZE, 7) * 2.0 - 1.0;
    float noise2 = -noise;

        return min(max(noise, noise2), HOLE_SIZE+0.01);
//    return noise;
}

float map(vec3 p) {
    vec3 rayPos = p;
    float d = sdInfCylinder(rayPos - vec3(centerPos.x, 0, centerPos.y), vec3(0, 0, 0.5));
    d -= (sin(p.y*1 + rand(vec2(GameTime*1000, 745))*100)*0.5 + 0.5)*0.1;
    return d;

}

void rayMarchLaser(in out vec3 color, vec3 playerPos, bool cracks, float crackDepth) {
    vec3 rayOrigin = VeilCamera.CameraPosition + VeilCamera.CameraBobOffset;
    vec3 rayDir = viewDirFromUv(texCoord);
    float dist = 0.0;

    for(int i = 0; i < 200; i++) {
        vec3 rayPos = rayOrigin + rayDir * dist;
        float d = map(rayPos);
        dist += d;

        if(d < 0.01) {
            color = vec3(10.0, 2.0, 2.0);
            break;
        } else if((dist > length(playerPos) && dist > length(playerPos) + crackDepth) || dist > 500.0) {
            break;
        }

    }

}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float handDepth = texture(HandDepthSampler, texCoord).r;
    uint material = texture(MaterialSampler, texCoord).r;
    vec3 worldPos = screenToWorldSpace(texCoord, depth).xyz;
    float crackDepth = 0.0;
    bool cracks = false;

    if (depth < 1.0 && material == 2 && handDepth >= 1.0) {

        worldPos.y = 4;
//        float time = abs(sin(GameTime*300)) * 60;
        float time = 10.0;
//        float HOLE_SIZE = smoothstep(0, (40 - time)*0.1, 1 - distance(centerPos, worldPos.xz)/time);
        float HOLE_SIZE = pow(1 - distance(centerPos, worldPos.xz)/time, 3);
        float noise = getNoise(worldPos, HOLE_SIZE);



        if (noise < HOLE_SIZE) {
            cracks = true;
            //rayMarch
            vec3 rayPos = worldPos + rand(texCoord + GameTime) * 0.01;
            vec3 ogRayPos = rayPos;
            vec3 rayDir = viewDirFromUv(texCoord);
            vec3 step = rayDir * 0.05;


            vec3 magmaColor = vec3(0.0);
            for(int i = 0; i < 100; i++) {
                rayPos += step;
                HOLE_SIZE = pow(1 - distance(centerPos, worldPos.xz)/time, 3);
                noise = getNoise(vec3(rayPos.x, worldPos.y, rayPos.z), HOLE_SIZE);


                magmaColor += vec3(1, 0.4, 0)*0.03;

                if(noise > HOLE_SIZE) {
                    break;
                }

            }

            crackDepth += distance(rayPos, worldPos);

            color = magmaColor*2;
        }
    }

    vec3 playerPos = screenToLocalSpace(texCoord, depth).xyz;

    rayMarchLaser(color, playerPos, cracks, crackDepth);


    fragColor = vec4(color, 1.0);
}