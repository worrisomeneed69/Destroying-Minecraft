#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D MainTexture;
uniform sampler2D MainDepth;
uniform sampler2D BlurredTexture;

uniform float centerDepth;
uniform float GameTime;
uniform int enabled;

const int ITERATIONS = 5;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 color = texture(MainTexture, texCoord).rgb;

    if(enabled == 0) {
        fragColor = vec4(color, 1.0);
        return;
    }

    vec3 blurredTexture = texture(BlurredTexture, texCoord).rgb;
    float depth = texture(MainDepth, texCoord).r;
    vec3 playerPos = screenToLocalSpace(texCoord, depth).xyz;
    depth = length(playerPos) / 100;

//    float smoothCenterDepth = smoothstep(0, 1, centerDepth);

    float dist = max(abs(depth - centerDepth), 0.0);
//    dist = dist * dist;

    float blur = smoothstep(0.0, 0.15, dist);
//    blur *= blur * blur;

//    float steps = 0.0;
//    if(blur > 0.0){
//        for(int i = -ITERATIONS; i <= ITERATIONS; i++) {
//            for(int j = -ITERATIONS; j <= ITERATIONS; j++) {
//                vec2 offset = vec2(blur*0.0007) * ivec2(i, j);
//                float sampleDepth = texture(MainDepth, texCoord + offset).r;
//                float sampleDist = max(abs(sampleDepth - centerDepth), 0.0);
//                float sampleBlur = smoothstep(0, 1, sampleDist);
//                if(sampleBlur > 0.00) {
//                    color += texture(DiffuseSampler, texCoord + offset).rgb;
//                    steps += 1.0;
//                }
//            }
//        }
//        color /= steps;
//    }


    fragColor = vec4(mix(color, blurredTexture, blur), 1.0);
//    fragColor = vec4(blurredTexture, 1.0);

}