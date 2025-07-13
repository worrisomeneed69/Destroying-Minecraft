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

const float TEXTURE_ZOOM = 0.001;
const float NOISE_ZOOM = 0.0015;
const vec3 LIGHT_DIR = normalize(vec3(-0.1,-0.2,1));
const vec3 CONE_OFFSET = vec3(0, -19000, 0);
const vec3 CRACK_DIR = normalize(vec3(-1, -1.2, 0));

//vec3 PLANET_POS = vec3(VeilCamera.CameraPosition.x+15000, 25000 - (sin(GameTime * 1000)*0.5+0.5)*7000, VeilCamera.CameraPosition.z);
vec3 PLANET_POS = vec3(VeilCamera.CameraPosition.x+20000, 20000, VeilCamera.CameraPosition.z);

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

float map(vec3 rayPos) {
    vec3 p = rayPos - PLANET_POS;

    vec3 sphereNormal = normalize(vec3(rayPos - PLANET_POS));
    float cracks = getSphereTexture(p*0.3, sphereNormal, PebbleDepth).r * 50;

    float planet = sdSphere(p, 19000) - cracks;

    return planet;
}

vec3 getRaymarchNormal(in vec3 point) {
    vec2 e = vec2(0.1, 0.0);
    float d = map(point);
    return normalize(vec3(map(point + e.xyy), map(point + e.yxy), map(point + e.yyx)) - d);
}

float getNoise(vec3 pos) {
    float noise = (fbm(pos * NOISE_ZOOM, 5)) * 2.0 - 1.0;
    float noise2 = -noise;

    return min(max(noise, noise2), 1);
}

void displaceObject(in out vec3 outColor, int iterations, in out Ray ray) {
    vec3 step = ray.direction * 20;
    vec3 origin = ray.rayPos;
    float depth = 0.0;
    bool inHole = false;
    vec3 surfacePoint;

    for (int i = 0; i < iterations; i++) {
        ray.rayPos += step;

        depth = distance(origin, ray.rayPos);

        vec3 dirToCenter = ray.rayPos - PLANET_POS;
        float distToCenter = length(dirToCenter);
        dirToCenter = normalize(dirToCenter);
        surfacePoint = (ray.rayPos + dirToCenter * (19000 - distToCenter)) - PLANET_POS;

        float pointOfCracking = 1.0 - smoothstep(0.94, 1.0, dot(normalize(surfacePoint - PLANET_POS), CRACK_DIR)*0.5+0.5);

        float noise = getNoise(surfacePoint) + pointOfCracking;


        if (noise > 0.2) {
//                outColor = vec3(noise);
//            outColor = vec3(depth/1000);

            break;
        }
        //If it wasn't in the hole, it would break immediately
        inHole = true;
    }
    vec3 planetWallColor = getSphereTexture(surfacePoint * 0.1, ray.normal, PlanetColor) + 0.5;
    vec3 crackWallColor = (1.0 - vec3(depth/1000)) * dot(normalize(ray.rayPos - PLANET_POS), LIGHT_DIR);
    outColor = inHole ? crackWallColor * planetWallColor : outColor;
}

void rayMarch(in out vec3 outColor, int iterations, in out Ray ray) {
    float dist = 0.0;

    for (int i = 0; i < iterations; i++) {
        ray.rayPos = ray.origin + ray.direction * dist;

        float d = map(ray.rayPos);

        dist += d;

        //Hit
        if (d < 10) {
            ray.normal = getRaymarchNormal(ray.rayPos);
            ray.hit = true;
            float light = clamp(dot(ray.normal, LIGHT_DIR), 0.0, 1.0);
            outColor = getSphereTexture(ray.rayPos, abs(ray.normal), PlanetColor) * light;
//            outColor = vec3(getNoise(ray.rayPos));
            displaceObject(outColor, 40, ray);
            break;
        }
        //Miss
        else if (d > 30000) {
            ray.hit = false;
            break;
        }
    }
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

        rayMarch(fragColor.rgb, 70, ray);
    }

    if (material == 5) {
        fragColor *= 10;
    } else if (material == 6) {
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    }


}