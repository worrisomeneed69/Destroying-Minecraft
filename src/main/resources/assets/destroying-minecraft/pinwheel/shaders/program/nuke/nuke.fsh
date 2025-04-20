#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

//const vec3 LIGHT_DIR = vec3(1, 1, 1);
const vec3 NUKE_POS = vec3(-124, 72, 157);
const float CONTRAST = -10;

float contrast(float color){
    return CONTRAST * (color - 0.5) + 0.5;
}

float map(vec3 p){
    vec3 nukePos = p - NUKE_POS;
//    float cylinder = sdCylinder(nukePos - vec3(0, 40, 0), 40, 10);
//    float torus = sdTorus(nukePos - vec3(0, 80,0), vec2(30, 20));
//    float sphere = sdSphere(nukePos - vec3(0,2,0), 2);
    float cube = sdBox(nukePos - vec3(0,2,0), vec3(2));



//    return opSmoothUnion(torus, cylinder, 0);
//    return cube - fbm(nukePos + (GameTime*1000), 7) - fbm(nukePos + (GameTime*1000), 1);
    return cube;
}


vec3 getRaymarchNormal(vec3 point){
    vec2 e = vec2(0.01, 0.0);
    float d = map(point);
    return normalize(vec3(map(point + e.xyy), map(point + e.yxy), map(point + e.yyx)) - d);
}

float rayMarchShadow(in vec3 rayPos, vec3 LIGHT_DIR){
    vec3 shadowRayPos = rayPos;
    vec3 step = LIGHT_DIR * 0.05;

    float shadow = 0.0;
    for(int i = 0; i < 50; i++){
        shadowRayPos += step;
        float d = max(-map(shadowRayPos), 0.0);

        float noise = clamp(contrast(fbm(shadowRayPos*1, 9)), 0.0, 1.0);

        shadow += clamp(1 * d * noise, 0.0, 1.0);

        if(shadow >= 1.0){
            break;
        } else if(map(shadowRayPos) > 0.9){
            break;
        }
    }

    return shadow;
}

float rayMarchCloud(vec3 rayPos, vec3 rd, vec3 viewPos, vec3 LIGHT_DIR) {
    vec3 step = rd * 0.05;
    vec3 ogRayPos = rayPos;

    float cloud = 0.0;
    float shadow = 1.0;
    for(int i = 0; i < 50; i++){
        rayPos += step;

        if(map(rayPos) > 0.9){
            break;
        }

        float d = max(-map(rayPos), 0.0);

        float noise = clamp(contrast(fbm(rayPos*1, 9)), 0.0, 1.0);
        float density = (10 * d * noise);

        shadow -= rayMarchShadow(rayPos, LIGHT_DIR);

        cloud += clamp(density, 0.0, 1.0) * exp(length(ogRayPos - rayPos) * 0.1);

        if(cloud >= 1.0){
            break;
        }
    }

    return shadow;
//    return cloud * (shadow);
}

vec3 rayMarchNuke(vec3 color, vec3 viewPos, out vec3 normal, out bool hit, vec3 LIGHT_DIR){
    vec3 rayOrigin = (VeilCamera.CameraPosition + VeilCamera.CameraBobOffset) + rand(texCoord + GameTime) * 0.01;
    vec3 rayDir = viewDirFromUv(texCoord);

    float dist = 0.0;
    vec3 rayPos = rayOrigin;
    for(int i = 0; i < 100; i++){
        rayPos = rayOrigin + rayDir * dist;

        float d = map(rayPos);
        dist += d;

        if(d <= 0.01){
            hit = true;
            normal = getRaymarchNormal(rayOrigin + rayDir * dist);
            break;
        }

        if(d > 100 || length(viewPos) < dist){
            break;
        }
    }

    if(hit){
        return vec3(rayMarchCloud(rayPos, rayDir, viewPos, LIGHT_DIR));
//        return blend(vec4(color, 1.0), vec4(rayMarchCloud(rayPos, rayDir, viewPos, LIGHT_DIR)));
//        return vec3(0.3);
    }

    return color;
}

void lighting(inout vec3 color, in vec3 normal){
//    vec3 lightDir = vec3(sin(GameTime * 1000),0,cos(GameTime * 1000));
    vec3 lightDir = vec3(1.0);
    color = color * (dot(normal, lightDir) * 0.5 + 0.5);
}

void main() {
    vec3 nukePos = vec3(-124, 72, 157);
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 viewPos = screenToViewSpace(texCoord, depth).xyz;
    vec3 normal = vec3(0.0);
    bool hit = false;

    vec3 LIGHT_DIR = vec3(sin(GameTime * 1000), 1, cos(GameTime * 1000));
//    vec3 LIGHT_DIR = vec3(1);
    color = rayMarchNuke(color, viewPos, normal, hit, LIGHT_DIR);

//    if(hit){
//        lighting(color, normal);
//    }

    fragColor = vec4(color, 1.0);
}