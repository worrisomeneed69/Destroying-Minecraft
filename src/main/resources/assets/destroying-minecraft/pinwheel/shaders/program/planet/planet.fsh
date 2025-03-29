#include veil:space_helper
#include destroying-minecraft:ray_march
#veil:buffer veil:camera VeilCamera

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D PlanetColor;
uniform sampler2D PebbleDepth;

uniform sampler2D NoiseTexture;

uniform sampler2D PlanetCracks;
uniform sampler2D PlanetSmallCracks;

uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

const vec3 PLANET_POS = vec3(-132, 1700, -3300);
const float ZOOM = 0.001;
const vec3 LIGHT_DIR = normalize(vec3(1, 1, 1));



vec3 getSphereTexture(in vec3 rayPos, in vec3 normal, sampler2D textureSampler) {
    return (texture(textureSampler, rayPos.xz * ZOOM).rgb * normal.y) +
           (texture(textureSampler, rayPos.xy * ZOOM).rgb * normal.z) +
           (texture(textureSampler, rayPos.yz * ZOOM).rgb * normal.x);

//    return texture(textureSampler, rayPos.xy * ZOOM).rgb;
}

vec3 getPlanetNormal(vec3 rayPos){
    return normalize(rayPos - PLANET_POS);
}

float noise3D(vec3 p){
    float z = p.z;
    vec2 z1 = (floor(z) * OFFSET + p.xy)/5.0;
    vec2 z2 = ((floor(z) + 2.0) * OFFSET + p.xy)/5.0;
    float n1 = texture(NoiseTexture, z1).r;
    float n2 = texture(NoiseTexture, z2).r;
    float ratio = fract(z);
    return mix(n1, n2, ratio);
}

float fbm2(vec3 x, int iterations) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < iterations; ++i) {
        v += a * noise3D(x);
        x = x * 2.0 + shift;
        a *= 0.5;
    }
    return v;
}

float map(in vec3 p) {
    vec3 rayPos = p - PLANET_POS;
    vec3 coneRayPos = rayPos - (vec3(0.5, -1, 1) * 800);
    coneRayPos.xy *= rot2D(145);
    coneRayPos.yz *= rot2D(-55);

    float dist = opSubtraction(sdCappedCone(coneRayPos, 1033.333333, 66.6667, 806.6667) - fbm(coneRayPos * 0.01, 4) * 200, sdSphere(rayPos, 2000));
//    float dist = sdSphere(rayPos, 3);


    vec3 sphereNormal = getPlanetNormal(p);
    float center = clamp(dot(sphereNormal, normalize(vec3(1,-1,1))) * 0.2, 0.0, 1.0);
    float cracks = getSphereTexture(p, sphereNormal, PlanetSmallCracks).r * center;
    float displacement = getSphereTexture(p, sphereNormal, PebbleDepth).r - cracks;


    return dist + displacement * 20;
//    return sdSphere(rayPos, 3000);
}

vec3 getRaymarchNormal(in vec3 point){
    vec2 e = vec2(0.01, 0.0);
    float d = map(point);
    return normalize(vec3(map(point + e.xyy), map(point + e.yxy), map(point + e.yyx)) - d);
}

void rayMarch(in out vec3 rayPos, in vec3 rayOrigin, in vec3 rayDir, in float worldDepth, out bool hit, out float steps, in float maxDist, in int iterations){
    float dist = 0.0;
    hit = false;
    rayPos = rayOrigin;
    steps = 1.0;
    for(int i = 0; i < iterations; i++) {
        rayPos = rayOrigin + rayDir * dist;
        float d = map(rayPos);

        steps = min(steps, 8*d/dist);

        dist += d;
        if(d < 0.001){
            rayPos = rayOrigin + rayDir * dist;
            hit = true;
            break;
        } else if(d > maxDist || (worldDepth < 256.0 && worldDepth < dist)){
            rayPos = rayOrigin + rayDir * dist;
            break;
        }
    }
}

void main() {
//    vec3 LIGHT_DIR = normalize(vec3(1));
    vec4 mainTexture = texture(DiffuseSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 playerSpace = screenToLocalSpace(texCoord, depth).xyz;
    float worldDepth = length(playerSpace);

    if(depth >= 1.0){
        vec3 rayPos;
        vec3 rayOrigin = VeilCamera.CameraPosition;
        vec3 rayDir = viewDirFromUv(texCoord);
        bool hit;
        float steps;
        rayMarch(rayPos, rayOrigin, rayDir, worldDepth, hit, steps, 30000, 300);

        vec3 planetNormal = getRaymarchNormal(rayPos);

        bool shadowHit;
        //shadows
        rayMarch(rayPos, rayPos + planetNormal * 0.02, LIGHT_DIR, worldDepth, shadowHit, steps, 30000, 250);

    //    vec3 rayOrigin = cameraPos;
    //    vec3 rayDir = viewDirFromUv(texCoord);
    //    float dist = 0.0;
    //
    //
    //    bool hit = false;
    //    vec3 sphereNormal = vec3(0.0);
    //    vec3 rayPos = rayOrigin;
    //    float step = 0.0;
    //    for(int i = 0; i < 150; i++) {
    //        rayPos = rayOrigin + rayDir * dist;
    //        step += 1.0;
    //        float d = map(rayPos);
    //        dist += d;
    //
    //        if(d < 0.001){
    //            rayPos = rayOrigin + rayDir * dist;
    //            hit = true;
    //            break;
    //        } else if(d > 300 || worldDepth < dist){
    //            rayPos = rayOrigin + rayDir * dist;
    //            break;
    //        }
    //    }


        if(hit) {
            vec3 planetColor = getSphereTexture(rayPos, abs(normalize(planetNormal * planetNormal)), PlanetColor);

            float lighting = dot(normalize(planetNormal), LIGHT_DIR);
            fragColor = vec4(planetColor * lighting, 1.0);

            if(!shadowHit){
                fragColor.rgb *= vec3(steps);
            } else {
                fragColor.rgb = vec3(0.0);
            }

            fragColor = mix(fragColor, mainTexture, 0.4);
        } else {
            fragColor = mainTexture;
        }
    } else {
        fragColor = mainTexture;
    }




}