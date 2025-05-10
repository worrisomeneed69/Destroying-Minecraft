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

//const vec2 centerPos = vec2(-1341, 1351);
const vec2 centerPos = vec2(0, 0);
const float TEXTURE_SIZE = 0.5;
//const float HOLE_SIZE = 0.01;

float getNoise(vec3 pos, float HOLE_SIZE) {
    float noise = fbm(pos * TEXTURE_SIZE, 7) * 2.0 - 1.0;
    float noise2 = -noise;

        return min(max(noise, noise2), HOLE_SIZE+0.01);
//    return noise;
}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    if(depth < 1.0) {
        vec3 worldPos = screenToWorldSpace(texCoord, depth).xyz;
        float time = abs(sin(GameTime*300)) * 100;
//        float time = 5;
//        float HOLE_SIZE = smoothstep(0, (100 - time)*0.1, 1 - distance(centerPos, worldPos.xz)/time);
        float HOLE_SIZE = pow(1 - distance(centerPos, worldPos.xz)/time, 3);
        float noise = getNoise(worldPos, HOLE_SIZE);



        if (noise < HOLE_SIZE) {
            //rayMarch
            vec3 rayPos = worldPos;
            vec3 rayDir = viewDirFromUv(texCoord);
            vec3 step = rayDir * 0.05;


            vec3 magmaColor = vec3(0.0);
            for(int i = 0; i < 50; i++) {
                rayPos += step;
                noise = getNoise(vec3(rayPos.x, worldPos.y, rayPos.z), HOLE_SIZE);


                magmaColor += vec3(1, 0.4, 0)*0.03;

                if(noise > HOLE_SIZE) {
                    break;
                }

            }

            color = magmaColor;
        }
    }

    fragColor = vec4(color, 1.0);
}