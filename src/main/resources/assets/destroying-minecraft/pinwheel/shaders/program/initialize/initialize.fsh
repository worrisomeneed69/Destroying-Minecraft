#veil:buffer veil:camera VeilCamera
#include destroying-minecraft:noise
#include veil:space_helper

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D HandDepth;

uniform vec2 ScreenSize;
uniform float GameTime;
uniform float initTimer;
uniform float skyTimer;

in vec2 texCoord;
out vec4 fragColor;

vec3 projectAndDivide(mat4 projMat, vec3 pos){
    vec4 homogeneousPos = projMat * vec4(pos, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

//    vec3 ndcPos = vec3(texCoord, depth) * 2.0 - 1.0;
//    vec3 viewPos = projectAndDivide(VeilCamera.IProjMat, ndcPos);
//    vec3 worldPos = (VeilCamera.IViewMat * vec4(viewPos, 1.0)).xyz + VeilCamera.CameraPosition;

    vec3 worldPos = screenToLocalSpace(vec3(texCoord, depth)).xyz + VeilCamera.CameraPosition;
//    vec3 blockPos = floor(vec3(worldPos.x, worldPos.y, worldPos.z) * 0.500001);
    vec3 blockPos = floor(vec3(worldPos.x, worldPos.y - 1, worldPos.z) * 0.50001) * 0.4;

    float noise1 = perlin_noise(vec4(blockPos.x, blockPos.y, blockPos.z, GameTime * noise(blockPos) * 10000)) * 0.5 + 0.4;
    float noise2 = (perlin_noise(vec4(blockPos.x, blockPos.y, blockPos.z, 112494)) * 0.5 + 0.5);

    noise1 = mix(1.0, noise1, clamp((initTimer - noise2) + initTimer*1.3, 0.0, 1.0));

    if (initTimer < noise2) {
        color.rgb = vec3(noise1);
    }

    if (depth >= 0.9999) {
        float count = 10;
        float ratio = ScreenSize.y / ScreenSize.x;
        vec2 screenPos = floor(texCoord * vec2(count, count * ratio));

        float noise3 = clamp(perlin_noise(vec4(screenPos, 5235, GameTime * noise(vec3(screenPos, 5235)) * 10000)) * 0.5 + 0.8, 0.0, 1.0);
        float noise4 = (perlin_noise(vec4(screenPos*0.3, 23463, 112494)) * 0.5 + 0.5);

        float skyNoise = mix(1.0, noise3, clamp((skyTimer - noise4) + skyTimer*1.3, 0.0, 1.0));

        if (skyTimer < skyNoise) {
            color.rgb = vec3(skyNoise);
        }

    }


    fragColor = vec4(color, 1.0);
}