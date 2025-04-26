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
uniform float planetFallTimer;

in vec2 texCoord;
out vec4 fragColor;

vec3 PLANET_OFFSET = vec3(0, 100 * planetFallTimer, 0);
vec3 PLANET_POS = vec3(-132, 1700, -3300) - PLANET_OFFSET;
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

float mapDebrisField(vec3 coneRayPos, vec3 p, float radius, float repetition, float sizeDifference, float chunkNoise){
    vec3 chunkPos = p;
    float chunkSphere = sdSphere(coneRayPos, radius);
    chunkPos = mod(chunkPos, repetition) - repetition/2;

    vec3 id = vec3(floor(abs(p.x) / repetition), floor(abs(p.y) / repetition), floor(abs(p.z) / repetition));
    float fid = id.x*41.6 + id.y*38.7 + id.z*60.2;

    float offset = rand(vec2(fid, fid));
    float offsetPosition = rand(vec2(fid * 174.42456, -fid * 47.5354));
    float chunks = sdSphere(chunkPos + offsetPosition * 20, 1 + offset * sizeDifference) * noise(chunkPos * 0.01);
    return opSmoothIntersection(chunkSphere, chunks - chunkNoise * sizeDifference, 40);
}

float map(in vec3 p, int iterations) {
    vec3 pos = p + PLANET_OFFSET;
    vec3 rayPos = p - PLANET_POS;
    vec3 coneRayPos = rayPos - (vec3(0.5, -1, 1) * 800);
    coneRayPos.xy *= rot2D(145);
    coneRayPos.yz *= rot2D(-55);

    float chunkNoise = fbm2(pos * 0.03, iterations);
    float cone = sdCappedCone(coneRayPos, 1033.333333, 66.6667, 1206.6667) + chunkNoise * 200;

    float sphere = sdSphere(rayPos, 2000);

    float debrisField3 = sdCappedCone(coneRayPos - vec3(200, 1300, 300), 200, 50, 200) + chunkNoise * 30;
    float dist = min(opSubtraction(cone, sphere), debrisField3);


    vec3 sphereNormal = getPlanetNormal(pos);
    float crackDir = clamp(dot(sphereNormal, normalize(vec3(1,-1,1))) * 0.6, 0.0, 1.0);
    float cracks = getSphereTexture(pos, sphereNormal, PlanetSmallCracks).r * crackDir;
    float displacement = getSphereTexture(pos, sphereNormal, PebbleDepth).r - cracks;

    float debrisField1 = mapDebrisField(coneRayPos, pos, 1500, 500, 30, chunkNoise);
//    float debrisField2 = mapDebrisField(coneRayPos, p, 1200, 400, 20, chunkNoise);
//
//    float finalDebrisField = min(debrisField1, debrisField2);

    return min(debrisField1, dist + displacement * 20);
}

float lqMap(in vec3 p) {
    vec3 rayPos = p - PLANET_POS;
    vec3 coneRayPos = rayPos - (vec3(0.5, -1, 1) * 800);
    coneRayPos.xy *= rot2D(145);
    coneRayPos.yz *= rot2D(-55);

    float cone = sdCappedCone(coneRayPos, 1033.333333, 66.6667, 806.6667);
    float sphere = sdSphere(rayPos, 2000);
    float debrisSphere = sdSphere(coneRayPos, 1500);

    return min(opSubtraction(cone, sphere), debrisSphere);
}

vec3 getRaymarchNormal(in vec3 point){
    vec2 e = vec2(0.1, 0.0);
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
        rayMarch(rayPos, rayOrigin, rayDir, worldDepth, hit, shadow, 8, 2000, 150, FBM_ITERATIONS, steps);

        vec3 planetNormal = vec3(0.0);
        if(hit) {
            planetNormal = getRaymarchNormal(rayPos);
        }

        //SHADOWS
        bool shadowHit;
        float steps2;
        vec3 shadowRayPos;

        //No point in calculating shadows if the surface is facing away
        if(dot(planetNormal, LIGHT_DIR) > -0.1){
            rayMarch(shadowRayPos, rayPos + planetNormal * 20, LIGHT_DIR, worldDepth, shadowHit, shadow, 1, 1000, 50, SHADOW_FBM_ITERATIONS, steps2);
        }



        if(hit) {
            vec3 planetColor = getSphereTexture(rayPos + PLANET_OFFSET, abs(normalize(planetNormal * planetNormal)), PlanetColor);

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