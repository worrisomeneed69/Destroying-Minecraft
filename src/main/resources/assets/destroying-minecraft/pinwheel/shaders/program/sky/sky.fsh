#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D StarsTexture;

uniform float GameTime;
uniform mat4 sunMat;
uniform float supernovaTimer;
uniform float flashTimer;
uniform float explosionTimer;
uniform float laserLength;
uniform int flashFrame;


in vec2 texCoord;
out vec4 fragColor;


vec3 getLightAngle() {
    vec3 lightangle = mat3(sunMat) * vec3(0.0, 0.0, 1.0);
    return normalize(lightangle);
}

float easeInExpo(float x) {
    return x == 0 ? 0 : pow(2, 200 * x - 200);
}

const vec3 SkyColor = vec3(0.3,0.55,1.4);

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

const vec3 centerPos = vec3(-1685, 80.9, 1573.5);

//Attenuation formula https://gamedev.stackexchange.com/questions/56897/glsl-light-attenuation-color-and-intensity-formula
float attenuation(float value, float a, float b) {
    return 1 / (1 + a*abs(value) + b*abs(value)*abs(value));
}

float map(vec3 rayPos, float radius, float time) {
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    float cone1 = clamp(-sdCappedCone(rayPos + vec3(0,1,0), time, 0.75, 0), 0.0, 1.0);
    float cone2 = clamp(-sdCappedCone(rayPos - vec3(0,1,0), time, 0, 0.75), 0.0, 1.0);
    float cone = max(cone1, cone2);
//    return cone;
    return max(cone, clamp(-sdCylinder(rayPos, smoothstep(0.0, 10.0, 3 - radius) + 0.2, time * 3), 0.0, 1.0));
}

float getCone(vec3 rayPos, float time) {
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    float cone1 = clamp(-sdCappedCone(rayPos + vec3(0,1,0), time, 0.75, 0), 0.0, 1.0);
    float cone2 = clamp(-sdCappedCone(rayPos - vec3(0,1,0), time, 0, 0.75), 0.0, 1.0);
    return max(cone1, cone2);
}

float map2(vec3 rayPos, float radius, float time) {
    rayPos.yz *= rot2D(-15);
    rayPos.xy *= rot2D(-35);

    return clamp(-sdCylinder(rayPos, time * 1.5, time * 3), 0.0, 1.0);
}

float getBrightness(vec3 color) {
    return (color.r + color.g + color.b) / 3;
}

float getLuminance(vec3 color) {
    return dot(color, vec3(0.2126, 0.7152, 0.0722));
}

float contrast(float color) {
    return CONTRAST * (color - 0.5) + 0.5;
}

vec3 rayMarchSupernova() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 centerPos = cameraPos + getLightAngle() * 3;
//    vec3 centerPos = vec3(-811, 80, 234);

    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 playerSpace = screenToLocalSpace(texCoord, depth).xyz;
    float worldDepth = length(playerSpace);

    float stepSize = farPlane / ITERATIONS;

    vec3 rd = viewDirFromUv(texCoord) * stepSize;
    vec3 rayPos = cameraPos + rand(texCoord + GameTime) * 0.01;

    vec3 fog = vec3(0.0);
//    bool hit = false;
    for(int i = 0; i < ITERATIONS; i++) {
        rayPos += rd;
        float time = min(explosionTimer, 1.0)*2;
//        float time = 0.5f*2;

        vec3 diskRayPos = rayPos - centerPos;
        float radius = (2.0 - time) * length(diskRayPos);
        vec3 polarPos = vec3(radius * 2, atan2(diskRayPos.x, diskRayPos.z) * 0.5, diskRayPos.y * 2);
        float attenuate = attenuation(radius / 2.5, 2, 4);


        float disk = map(diskRayPos, radius, time) * attenuate;
        float disk2 = map2(diskRayPos, radius, time) * attenuate * attenuate * 0.2;
        float cone = getCone(diskRayPos, 1);

        if(disk > 0.01 || disk2 > 0.01 || cone > 0.01) {
            float noise = fbm(polarPos * 4, 4);
            float red = clamp(contrast(fbm(vec3(noise)* RED_STRENGTH, 4)), 0.0, 1.0);
            float green = clamp(contrast(fbm(vec3(noise) * GREEN_STRENGTH, 4)), 0.0, 1.0);
            float blue = clamp(contrast(fbm(vec3(noise) * BLUE_STRENGTH, 4)), 0.0, 1.0);
            vec3 noise1 = ((red * color1) + (green * color2) + (blue * color3)) * attenuate;

            vec3 noise2 = (blue * color4) * attenuate * attenuate;

            fog += noise1 * disk*0.7;
            fog += noise2 * cone*0.7;
            fog += noise1 * disk2 * 0.6;
//            hit = true;
        }
        float dist = length(cameraPos - rayPos);


        if(getBrightness(fog) >= 1.0 || worldDepth < dist){
            break;
        }

    }

    color = fog * 5;


    return color;
}

float mapLaser(vec3 p, vec3 sunDir) {
    vec3 rayPos = p;
    float length = laserLength * 20000;

    if (length <= 0.0) return 1.0;
    rayPos -= vec3(centerPos.x, centerPos.y, centerPos.z);
    float angle = atan2(sunDir.x, sunDir.y);
    rayPos.xy *= rot2D(angle * 180/3.141592);
    rayPos.y -= length;
    float d = sdCylinder(rayPos, length, 0.5);
    d -= (sin(p.y*1 + rand(vec2(GameTime*1000, 745))*100)*0.5 + 0.5)*0.1;
    return d;
}

void rayMarchLaser(in out vec3 color, vec3 playerPos, vec3 sunDir) {
    vec3 rayOrigin = VeilCamera.CameraPosition + VeilCamera.CameraBobOffset;
    vec3 rayDir = viewDirFromUv(texCoord);
    float dist = 0.0;

    for(int i = 0; i < 200; i++) {
        vec3 rayPos = rayOrigin + rayDir * dist;
        float d = mapLaser(rayPos, sunDir);
        dist += d;

        if(d < 0.01) {
            color = vec3(10.0, 2.0, 2.0);
            break;
        } else if(dist > length(playerPos) || dist > 500.0) {
            break;
        }

    }

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
        color = mix(vec3(SkyColor - rd.y * 0.7), texture(StarsTexture, viewDirFromUv(texCoord).zy*0.5).rgb*(0.75 - explosionTimer), time)*2;
        color += vec3(light * 10);



        if (flashTimer > 0.0){
//            fragColor = vec4(max(dot(rd, sunDir), 0.1) * mix(vec3(10.0), color + rayMarchSupernova(), min(flashTimer, 1.0)), 1.0);
            fragColor = vec4(mix(vec3(10.0), color + rayMarchSupernova(), min(flashTimer, 1.0)), 1.0);
        } else {
            fragColor = vec4(color, 1.0);
        }

    } else {
        fragColor = vec4(color, 1.0);
    }

    if (laserLength > 0.0) {
        vec3 playerPos = screenToLocalSpace(texCoord, depth).rgb;
        rayMarchLaser(fragColor.rgb, playerPos, sunDir);
    }

    if (flashFrame == 1) {
        if (getBrightness(fragColor.rgb) > 0.3) {
            fragColor.rgb = vec3(0.0);
        } else {
            fragColor.rgb = vec3(1.0);
        }
    }
//    fragColor.rgb = vec3(getLuminance(fragtColor.rgb));
//    fragColor.rgb = 1.0 - fragColor.rgb;
}