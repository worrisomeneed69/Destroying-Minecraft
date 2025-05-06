#include veil:space_helper
#veil:buffer veil:camera VeilCamera

#define SHADOW_SAMPLES 1

#define SHADOW_STRENGTH 0.4

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D AlbedoSampler;
uniform sampler2D ShadowMap;
uniform sampler2D NormalSampler;
uniform sampler2D HandDepth;
uniform sampler2D LightUVSampler;
uniform sampler2D LightMapSampler;
uniform sampler2D VanillaLightMapTexture;

uniform sampler2D NoiseTex;

uniform vec2 ScreenSize;
uniform mat4 shadowViewMatrix;
uniform mat4 IShadowViewMatrix;
uniform mat4 shadowProjMat;
uniform float supernovaTimer;
uniform float flashTimer;
uniform float explosionTimer;

in vec2 texCoord;
out vec4 fragColor;

vec3 projectAndDivide(mat4 projMat, vec3 pos){
    vec4 homogeneousPos = projMat * vec4(pos, 1.0);
    return homogeneousPos.xyz / homogeneousPos.w;
}

vec3 distort(in vec3 shadowPosition) {
    const float bias0 = 0.95;
    const float bias1 = 1.0 - bias0;

    float factorDistance = length(shadowPosition.xy);

    float distortFactor = factorDistance * bias0 + bias1;

    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}

vec3 viewToWorldSpaceDir(vec3 direction) {
    return (VeilCamera.IViewMat * vec4(direction, 0.0)).xyz;
}

mat2 randRotMat(vec2 coord){
    float randomAngle = texture(NoiseTex, coord * 20.0).r * 180.0;
    float cosTheta = cos(randomAngle);
    float sinTheta = sin(randomAngle);
    return mat2(cosTheta, -sinTheta, sinTheta, cosTheta) / 2048.0;
}

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 BH_POS = cameraPos + vec3(0, 0, -4.2);

    vec3 BH_DIR = normalize(mat3(IShadowViewMatrix) * vec3(0.0,0.0,1.0));
    vec3 worldNormal = viewToWorldSpaceDir(texture(NormalSampler, texCoord).rgb);

    vec4 color = texture(DiffuseSampler, texCoord);
    vec3 albedoColor = texture(AlbedoSampler, texCoord).rgb;

    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;


    vec3 flash = flashTimer > 0 ? vec3(0.6) * (1.0-min(flashTimer, 1.0)) : vec3(0.0);
    if(depth >= 1.0) {
        fragColor = color;
        return;
    }

    vec3 viewPos = screenToViewSpace(texCoord, depth).rgb;


    vec3 localSpace = screenToLocalSpace(texCoord, depth).xyz;
    vec3 adjustedLocalSpace = localSpace + 0.02 * worldNormal * length(viewPos);
    vec3 shadowViewSpace = (shadowViewMatrix * vec4(adjustedLocalSpace, 1.0)).xyz;

    vec3 shadowNDCPos = distort(projectAndDivide(shadowProjMat, shadowViewSpace));

    vec3 shadowScreenPos = shadowNDCPos * 0.5 + 0.5;
    float shadowDepth = shadowScreenPos.z;
//    shadowScreenPos.z -= 0.0001;
    float initialShadowSampler = texture(ShadowMap, shadowScreenPos.xy).r;

    vec2 lightUV = texture(LightUVSampler, texCoord).rg;

    vec3 blockLight = texture(VanillaLightMapTexture, vec2(lightUV.x, 1.0/32.0)).rgb;
    vec3 skyLight = texture(VanillaLightMapTexture, vec2(1.0/32.0, lightUV.y)).rgb;

    float lightDir = dot(worldNormal, BH_DIR);

    float shadowSum = SHADOW_STRENGTH;

    if(lightDir > -0.02){
        mat2 randRotation = randRotMat(texCoord);
        for (int x = -SHADOW_SAMPLES; x <= SHADOW_SAMPLES; x++){
            for (int y = -SHADOW_SAMPLES; y <= SHADOW_SAMPLES; y++){
                vec2 offset = randRotation * vec2(x, y) * 1;
                float shadowSampler = texture(ShadowMap, shadowScreenPos.xy + offset).r;

                if (shadowDepth < shadowSampler){
                    shadowSum += 1.0;
                }
            }
        }

        shadowSum /= pow(2.0 * SHADOW_SAMPLES + 1.0, 2.0);
    }

//    vec3 ambientLight = (blockLight + 0.2*skyLight) * clamp(dot(worldNormal, worldNormal), 0.0, 1.0);
    vec3 outputColor;
    if (flashTimer > 0.0) {
        outputColor = color.rgb * (blockLight + skyLight * max(shadowSum, SHADOW_STRENGTH)) + flash;
    } else {
        outputColor = color.rgb * (blockLight + skyLight * max(shadowSum, SHADOW_STRENGTH)*(1.0 - supernovaTimer));
    }


    if(handDepth < 1.0){
        outputColor = color.rgb;
    }


    fragColor = vec4(outputColor, 1.0);





}