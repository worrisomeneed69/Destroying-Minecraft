#include veil:space_helper
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
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
const int FBM_ITERATIONS = 5;
const int SHADOW_FBM_ITERATIONS = 3;

float totalFBMIterations = 0.0;



vec3 getSphereTexture(in vec3 rayPos, in vec3 normal, sampler2D textureSampler) {
    return (texture(textureSampler, rayPos.xz * ZOOM).rgb * normal.y) +
           (texture(textureSampler, rayPos.xy * ZOOM).rgb * normal.z) +
           (texture(textureSampler, rayPos.yz * ZOOM).rgb * normal.x);
}

vec3 getPlanetNormal(vec3 rayPos){
    return normalize(rayPos - PLANET_POS);
}

float fbm2(vec3 x, int iterations) {
    float v = 0.0;
    float a = 0.5;
    vec3 shift = vec3(100);
    for (int i = 0; i < iterations; ++i) {
        v += a * noise(x);
        x = x * 2.0 + shift;
        a *= 0.5;
        totalFBMIterations += 1.0;
    }
    return v;
}

float map(in vec3 p, int iterations) {
    vec3 rayPos = p - PLANET_POS;
    vec3 coneRayPos = rayPos - (vec3(0.5, -1, 1) * 800);
//    vec3 chunkPos = p;
    coneRayPos.xy *= rot2D(145);
    coneRayPos.yz *= rot2D(-55);

    float chunkNoise = fbm2(coneRayPos * 0.01, iterations);
    float cone = sdCappedCone(coneRayPos, 1033.333333, 66.6667, 806.6667) - chunkNoise * 200;

    float sphere = sdSphere(rayPos, 2000);

//    float repetition = 200;
//    chunkPos = mod(chunkPos, repetition) - repetition/2;
//
//    vec3 id = vec3(floor(abs(p.x) / repetition), floor(abs(p.y) / repetition), floor(abs(p.z) / repetition));
//    float fid = id.x*41.6 + id.y*38.7 + id.z*60.2;
//
//    float offset = rand(vec2(fid * 1.42456, fid * 47.5354));
//    float chunk1 = sdSphere(chunkPos + offset * 50, 30 + offset * 20);
//    float chunk2 = ;
//    float chunk3 = ;

    float dist = opSubtraction(cone, sphere);


    vec3 sphereNormal = getPlanetNormal(p);
    float center = clamp(dot(sphereNormal, normalize(vec3(1,-1,1))) * 0.2, 0.0, 1.0);
    float cracks = getSphereTexture(p, sphereNormal, PlanetSmallCracks).r * center;
    float displacement = getSphereTexture(p, sphereNormal, PebbleDepth).r - cracks;
//    float displacement = sin(p.x * 0.1) * sin(p.y * 0.1) * sin(p.z * 0.1);


    return dist + displacement * 20;
//    return chunk1 + displacement * 00;
}

float lqMap(in vec3 p) {
    vec3 rayPos = p - PLANET_POS;
    vec3 coneRayPos = rayPos - (vec3(0.5, -1, 1) * 800);
    coneRayPos.xy *= rot2D(145);
    coneRayPos.yz *= rot2D(-55);

    float cone = sdCappedCone(coneRayPos, 1033.333333, 66.6667, 806.6667);
    float sphere = sdSphere(rayPos, 2000);

    float dist = opSubtraction(cone, sphere);

    return dist;
}

vec3 getRaymarchNormal(in vec3 point){
    vec2 e = vec2(0.01, 0.0);
    float d = map(point, FBM_ITERATIONS);
    return normalize(vec3(map(point + e.xyy, FBM_ITERATIONS), map(point + e.yxy, FBM_ITERATIONS), map(point + e.yyx, FBM_ITERATIONS)) - d);
}

void rayMarch(in out vec3 rayPos, in vec3 rayOrigin, in vec3 rayDir, in float worldDepth, out bool hit, out float shadow,in float minDist, in float maxDist, in int iterations, in int fbmIterations, out float steps){
    float dist = 0.0;
    hit = false;
    rayPos = rayOrigin;
    shadow = 1.0;
    steps = 0.0;
    for(int i = 0; i < iterations; i++) {
        rayPos = rayOrigin + rayDir * dist;

        float d = lqMap(rayPos);
        if(d < 20){
           d = map(rayPos, fbmIterations);
        }
        steps += 1.0;

        shadow = min(shadow, 8*d/dist);

        dist += d;
        if(d < minDist){
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
        float shadow;
        float steps;
        rayMarch(rayPos, rayOrigin, rayDir, worldDepth, hit, shadow, 8, 3000, 150, FBM_ITERATIONS, steps);

        vec3 planetNormal = vec3(0.0);
        if(hit) {
            planetNormal = getRaymarchNormal(rayPos);
        }

        //SHADOWS
        bool shadowHit;
        float steps2;

        //No point in calculating shadows if the surface is facing away
        if(dot(planetNormal, LIGHT_DIR) > -0.1){
            rayMarch(rayPos, rayPos + planetNormal * 20, LIGHT_DIR, worldDepth, shadowHit, shadow, 1, 1000, 50, SHADOW_FBM_ITERATIONS, steps2);
        }



        if(hit) {
            vec3 planetColor = getSphereTexture(rayPos, abs(normalize(planetNormal * planetNormal)), PlanetColor);

            float lighting = clamp(dot(normalize(planetNormal), LIGHT_DIR), 0.0, 1.0);
            fragColor = vec4(planetColor * lighting, 1.0);

            if(!shadowHit) {
                fragColor.rgb *= vec3(shadow);
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