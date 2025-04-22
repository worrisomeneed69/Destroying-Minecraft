#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D CloudsTexture;

uniform float GameTime;
uniform mat4 sunMat;
uniform float supernovaTimer;
uniform float flashTimer;
uniform float explosionTimer;


in vec2 texCoord;
out vec4 fragColor;

const vec3 SkyColor = vec3(0.5,0.75,1.1);

vec3 getLightAngle(){
    vec3 lightangle = mat3(sunMat) * vec3(0.0, 0.0, 1.0);
    return normalize(lightangle);
}

float easeInExpo(float x) {
    return x == 0 ? 0 : pow(2, 200 * x - 200);
}

const int ITERATIONS = 75;
const float CONTRAST = 5;
const float farPlane = 5.0;

const vec3 RED_STRENGTH   = vec3(4,3,3);
const vec3 GREEN_STRENGTH = vec3(3,1,4);
const vec3 BLUE_STRENGTH  = vec3(2.6,2,2);

const vec3 color1 = vec3(0.4627450980392157, 0.06274509803921569, 0.11764705882352941);
const vec3 color2 = vec3(0.7607843137254902, 0.8666666666666667, 0.8941176470588236);
const vec3 color3 = vec3(0.07450980392156863, 0.21568627450980393, 0.4117647058823529);
const vec3 color4 = vec3(0.788235294117647, 0.21568627450980393, 0.2980392156862745);

//Attenuation formula https://gamedev.stackexchange.com/questions/56897/glsl-light-attenuation-color-and-intensity-formula
float attenuation(float value, float a, float b){
    return 1 / (1 + a*abs(value) + b*abs(value)*abs(value));
}

float map(vec3 rayPos, float radius, float time){
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    float cone1 = clamp(-sdCappedCone(rayPos + vec3(0,1,0), time, 1, 0), 0.0, 1.0);
    float cone2 = clamp(-sdCappedCone(rayPos - vec3(0,1,0), time, 0, 1), 0.0, 1.0);
    float cone = max(cone1, cone2);
//    return cone;
    return max(cone, clamp(-sdCylinder(rayPos, smoothstep(0.0, 10.0, 3 - radius) + 0.1, time * 3), 0.0, 1.0));
}

float getCone(vec3 rayPos, float time){
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    float cone1 = clamp(-sdCappedCone(rayPos + vec3(0,1,0), time, 1, 0), 0.0, 1.0);
    float cone2 = clamp(-sdCappedCone(rayPos - vec3(0,1,0), time, 0, 1), 0.0, 1.0);
    return max(cone1, cone2);
}

float map2(vec3 rayPos, float radius, float time){
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    return clamp(-sdCylinder(rayPos, time * 1.5, time * 3), 0.0, 1.0);
}

float getBrightness(vec3 color){
    return (color.r + color.g + color.b) / 3;
}

float contrast(float color){
    return CONTRAST * (color - 0.5) + 0.5;
}

vec3 rayMarchSupernova(){
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 centerPos = cameraPos + getLightAngle() * 3;

    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 playerSpace = screenToLocalSpace(texCoord, depth).xyz;
    float worldDepth = length(playerSpace);

    if(depth >= 1.0){
        float stepSize = farPlane / ITERATIONS;

        vec3 sunDir = getLightAngle();
        vec3 rd = viewDirFromUv(texCoord) * stepSize;
        vec3 rayPos = cameraPos + rand(texCoord + GameTime) * 0.01;

        vec3 fog = vec3(0.0);

        for(int i = 0; i < ITERATIONS; i++){
            rayPos += rd;
//            float time = abs(sin(GameTime * 100) * 2);
            float time = min(explosionTimer, 1.0)*2;
//            float time = 0.2f;

            vec3 diskRayPos = rayPos - centerPos;
            float radius = (2.0 - time) * length(diskRayPos);
            vec3 polarPos = vec3(radius * 3, atan2(diskRayPos.x, diskRayPos.z) * 0.5, diskRayPos.y * 2);
            float attenuate = attenuation(radius / 2.5, 2, 4);


            float disk = map(diskRayPos, radius, time) * attenuate;
            float disk2 = map2(diskRayPos, radius, time) * attenuate * attenuate * 0.1;
            float cone = getCone(diskRayPos, time);

            float sphere = clamp(-sdSphere(diskRayPos, time*1.5), 0.0, 1.0)* attenuate * 0.3;

            if(disk > 0.01 || disk2 > 0.01 || cone > 0.01) {
                float noise = fbm(polarPos * 3, 4);
                float red = clamp(contrast(fbm(vec3(noise)* RED_STRENGTH, 4)), 0.0, 1.0);
                float green = clamp(contrast(fbm(vec3(noise) * GREEN_STRENGTH, 4)), 0.0, 1.0);
                float blue = clamp(contrast(fbm(vec3(noise) * BLUE_STRENGTH, 4)), 0.0, 1.0);
                vec3 noise1 = ((red * color1) + (green * color2) + (blue * color3)) * attenuate;

                vec3 noise2 = (blue * color4) * attenuate * attenuate;

                fog += noise1 * disk*0.7;
                fog += noise2 * cone*0.7;
                fog += noise1 * disk2 * 0.6;
            }
            float dist = length(cameraPos - rayPos);


            if(getBrightness(fog) >= 1.0 || worldDepth < dist){
                break;
            }

        }
        color = fog * 5;

    }

    return color;
}

float noise3D(vec3 p){
    float z = p.z*5.0;
    vec2 z1 = (floor(z) * OFFSET + p.xz)/5.0;
    vec2 z2 = ((floor(z) + 1.0) * OFFSET + p.xz)/5.0;
    float n1 = texture(CloudsTexture, z1).r;
    float n2 = texture(CloudsTexture, z2).r;
    float ratio = fract(z);
    return mix(n1, n2, ratio);
}

vec3 getClouds(vec4 color, float depth){
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    vec4 clouds = vec4(0.0);
    if(rd.y > 0) {
        vec2 uv = (rd.xz / rd.y);
        vec2 xz = (rd.xz * (200 - cameraPos.y))/rd.y;
//        vec3 rayOrigin = vec3(uv.x, 100 + cameraPos.y, uv.y) + cameraPos;
        vec3 rayOrigin = vec3(xz.x, 0, xz.y) + cameraPos;

        float dist = 0.0;
        for(int i = 0; i < 50; i++){
            vec3 rayPos = rayOrigin + rd * dist;
            dist += 1;

//            clouds += 0.01 * fbm(vec3(fbm(rayPos, 1)), 5);
            clouds += 0.05 * max(fbm(rayPos*0.1, 3) * fbm(rayPos*0.03, 1), 0.0);

            if(clouds.r >= 1.0){
                break;
            }

        }
//        clouds = texture(CloudsTexture, uv);
    }


    return blend(color, clouds);
}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 sunDir = getLightAngle();
    vec3 rd = viewDirFromUv(texCoord);
    float time = supernovaTimer;

    float light = smoothstep(0.998 + 0.002 * time, 1.0, dot(rd, sunDir));
    rd += rand(texCoord + GameTime) * 0.01;
    if(depth >= 1.0){
        color = mix(vec3(SkyColor - rd.y * 0.9), vec3(0.0), time);
        color += vec3(light * 10);



        if (flashTimer > 0.0){
            fragColor = vec4(max(dot(rd, sunDir), 0.0) * mix(vec3(10.0), rayMarchSupernova(), min(flashTimer, 1.0)), 1.0);
        } else {
            fragColor = vec4(color, 1.0);
        }

//        fragColor.rgb = getClouds(fragColor, depth);
    } else {
        fragColor = vec4(color, 1.0);
    }



//    fragColor = vec4(1.0);
}