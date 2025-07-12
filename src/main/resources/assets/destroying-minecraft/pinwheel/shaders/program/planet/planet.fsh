#include veil:space_helper
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform isampler2D MaterialSampler;
uniform sampler2D PlanetColor;
uniform sampler2D PebbleDepth;

uniform sampler2D NoiseTexture;

uniform sampler2D PlanetCracks;
uniform sampler2D PlanetSmallCracks;

uniform float GameTime;
uniform float planetFallTimer;

in vec2 texCoord;
out vec4 fragColor;

const float TEXTURE_ZOOM = 0.0007;
const vec3 LIGHT_DIR = normalize(vec3(-0.5,-0.0,1));
const vec3 CONE_OFFSET = vec3(0, -19000, 0);

//vec3 PLANET_POS = vec3(VeilCamera.CameraPosition.x+15000, 25000 - (sin(GameTime * 1000)*0.5+0.5)*7000, VeilCamera.CameraPosition.z);
vec3 PLANET_POS = vec3(VeilCamera.CameraPosition.x+15000, 25000, VeilCamera.CameraPosition.z);

struct Ray {
    vec3 rayPos;
    vec3 origin;
    vec3 direction;
    vec3 normal;
    bool hit;
};

vec3 getSphereTexture(in vec3 rayPos, in vec3 normal, sampler2D textureSampler) {
    return (texture(textureSampler, rayPos.xz * TEXTURE_ZOOM).rgb * normal.y) +
    (texture(textureSampler, rayPos.xy * TEXTURE_ZOOM).rgb * normal.z) +
    (texture(textureSampler, rayPos.yz * TEXTURE_ZOOM).rgb * normal.x);
}

float mapDebrisField(vec3 rayPos, float repetition, float sizeDifference, float chunkNoise) {
    vec3 chunkPos = rayPos;
    float chunkSphere = sdSphere(rayPos, 9000);
    chunkPos = mod(chunkPos, repetition) - repetition/2;

    vec3 id = vec3(floor(abs(rayPos.x) / repetition), floor(abs(rayPos.y) / repetition), floor(abs(rayPos.z) / repetition));
    float fid = id.x*41.6 + id.y*38.7 + id.z*60.2;

    float offset = rand(vec2(fid*352.5235, fid*1.562345));
    float offsetPosition = rand(vec2(fid * 174.42456, -fid * 47.5354));
    float chunks = sdSphere(chunkPos + offsetPosition * 100, offset * sizeDifference) * noise(chunkPos * 0.01);
    return opSmoothIntersection(chunkSphere, chunks - chunkNoise * sizeDifference, 40);
//    return chunkSphere;
}

float map(vec3 rayPos) {
    vec3 p = rayPos - PLANET_POS;

    float mainPlanetBody = sdSphere(p, 19000);

//    float noiseDetail = fbm(p * 0.004, 5);
//    vec3 innerPlanetConePosition = p - CONE_OFFSET;
//    innerPlanetConePosition.xy *= rot2D(45);

//    float innerPlanetCone = sdCappedCone(innerPlanetConePosition, 5000, 9000, 100)+noiseDetail*400;

    vec3 sphereNormal = normalize(vec3(rayPos - PLANET_POS));
//    float craters = getSphereTexture(p*0.3, sphereNormal, PebbleDepth).r * 500;
    float cracks = (getSphereTexture(p*0.2, sphereNormal, PlanetCracks).r) * 500;

//    float planet = opSubtraction(innerPlanetCone, mainPlanetBody) - cracks;
    float planet = mainPlanetBody - cracks;

//    vec3 planePos = p;
//    planePos.yz *= rot2D(120);
//    planePos.xy *= rot2D(20);
//    float plane = sdPlane(planePos, normalize(vec3(0,1,0)), fbm(p * 0.001, 6)*4000);
//
//    float planetChunk1 = opSubtraction(plane, planet);
//
//    plane = sdPlane(planePos, normalize(vec3(0,-1,0)),  fbm(p * 0.001, 6)*4000);
//
//    float planetChunk2 = opSubtraction(plane, planet);

//    vec3 debrisFieldOffset = vec3(0, -10600, 0);
//    float debris = mapDebrisField(p - debrisFieldOffset, 500, 10, noiseDetail);

//    return min(planetChunk1, planetChunk2);
    return planet;

}

float lowQualityMap(vec3 rayPos) {
    vec3 p = rayPos - PLANET_POS;

    float mainPlanetBody = sdSphere(p, 19000);

    vec3 innerPlanetConePosition = p - CONE_OFFSET;
    innerPlanetConePosition.xy *= rot2D(45);
    float innerPlanetCone = sdCappedCone(innerPlanetConePosition, 5000, 9000, 100);

    float planet = opSubtraction(innerPlanetCone, mainPlanetBody);

    vec3 planePos = p;
    planePos.yz *= rot2D(120);
    planePos.xy *= rot2D(20);
    float plane = sdPlane(planePos, normalize(vec3(0,-1,0)), 0);

    float planetChunk1 = opSubtraction(plane, planet);

    plane = sdPlane(planePos, normalize(vec3(0,1,0)), 0);

    float planetChunk2 = opSubtraction(plane, planet);

//    vec3 debrisFieldOffset = vec3(0, -10600, 0);
//    float debris = mapDebrisField(p - debrisFieldOffset, 500, 10, 0);

//    return min(debris, planet);
    return planetChunk1;
}

vec3 getRaymarchNormal(in vec3 point) {
    vec2 e = vec2(0.1, 0.0);
    float d = map(point);
    return normalize(vec3(map(point + e.xyy), map(point + e.yxy), map(point + e.yyx)) - d);
}

void rayMarch(in out vec3 outColor, int iterations, in out Ray ray) {
    float dist = 0.0;

    for (int i = 0; i < iterations; i++) {
        ray.rayPos = ray.origin + ray.direction * dist;

//        float d = lowQualityMap(ray.rayPos);
//        if (d <= 10) {
            float d = map(ray.rayPos);
//        }

        dist += d;

        if (d < 5) {
            ray.normal = getRaymarchNormal(ray.rayPos);
            ray.hit = true;
            float light = clamp(dot(ray.normal, LIGHT_DIR), 0.0, 1.0);
            outColor = getSphereTexture(ray.rayPos, abs(ray.normal), PlanetColor) * light;
            break;
        } else if (d > 40000) {
            ray.hit = false;
            break;
        }
    }

}

float getBrightness(vec3 color) {
    return (color.r + color.g + color.b) / 3;
}


void main() {
    vec4 mainTexture = texture(DiffuseSampler, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    uint material = texture(MaterialSampler, texCoord).r;

    vec3 playerSpace = screenToLocalSpace(texCoord, depth).xyz;
    float worldDepth = length(playerSpace);

    fragColor = mainTexture;

    if (depth >= 1.0) {
        Ray ray;
        ray.origin = VeilCamera.CameraPosition;
        ray.direction = viewDirFromUv(texCoord);

        //Normal Raymarch
        rayMarch(fragColor.rgb, 100, ray);

        bool notAlreadyInShadow = dot(ray.normal, LIGHT_DIR) >= 0.0;
        if (ray.hit && notAlreadyInShadow) {
            ray.origin = ray.rayPos + ray.normal * 100;
            ray.direction = LIGHT_DIR;
            ray.hit = false;

            //Raymarch Shadows
            rayMarch(fragColor.rgb, 70, ray);

            if (ray.hit) {
                fragColor.rgb *= 0.15;
            }
        }

//        fragColor.rgb = mix(mainTexture.rgb, fragColor.rgb, 1);
    }

    if (material == 5) {
        fragColor *= 10;
    } else if (material == 6) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }


}