#veil:buffer veil:camera VeilCamera
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#include veil:space_helper
#include veil:blend

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D DepthComponent;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform vec2 ScreenSize;

in vec2 texCoord;
out vec4 fragColor;

vec3 BLACK_HOLE_POS = vec3(-1154.5, 72.5, 362.5);
float BLACK_HOLE_INNER_RADIUS = 1.0;
float BLACK_HOLE_OUTER_RADIUS = 2;

struct Ray {
    vec3 pos;
    bool hit;
    float distance;
};

// Thanks to https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection.html
// https://www.scratchapixel.com/images/ray-simple-shapes/raysphereisect1.png
void intersectsSphere(in out Ray ray, vec2 uv, vec3 sphereCenter, float sphereRadius) {
    vec3 rayOrigin = VeilCamera.CameraPosition + VeilCamera.CameraBobOffset;
    vec3 rayDir = viewDirFromUv(uv);

    vec3 vecToCenter = sphereCenter - rayOrigin;
    float Tca = dot(rayDir, vecToCenter);
    if (Tca < 0.0) {
        ray.hit = false;
        return;
    }

    float d = length(rayOrigin + rayDir*Tca - sphereCenter);
    if (d > sphereRadius) {
        ray.hit = false;
        return;
    }

    float Thc = sqrt(pow(sphereRadius, 2.0) - pow(d, 2.0));

    if (Thc < 0.0) {
        ray.hit = false;
        return;
    }

    vec3 pointOnShpere = rayOrigin + rayDir * (Tca - Thc);
    ray.hit = true;
    ray.pos = pointOnShpere;
    ray.distance = sphereRadius - d;
}

float clampBetween(float value, float min, float max) {
    return min + value * (max - min);
}

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec3 localPos = screenToLocalSpace(texCoord, depth).xyz;

    Ray ray;
    intersectsSphere(ray, texCoord, BLACK_HOLE_POS + randVec3(GameTime*300000).xyz*0.01, BLACK_HOLE_INNER_RADIUS);


    if (ray.hit) {
        if (length(ray.pos - VeilCamera.CameraPosition) < length(localPos)) {
            color = vec3(0.0);
        }
    } else {
        intersectsSphere(ray, texCoord, BLACK_HOLE_POS, BLACK_HOLE_OUTER_RADIUS);

        if (length(ray.pos - VeilCamera.CameraPosition) < length(localPos)) {
            if (ray.hit) {
                vec2 rayScreenPos = worldToScreenSpace(vec4(ray.pos, 1.0)).xy;
                vec2 blackHoleScreenPos = worldToScreenSpace(vec4(BLACK_HOLE_POS, 1.0)).xy;
                vec2 centerVector = blackHoleScreenPos - rayScreenPos;

                vec2 dir = normalize(centerVector) * (ray.distance)*0.15 * clampBetween(rand(GameTime*1000), 0.98, 1.0);
                color = texture(DiffuseSampler, texCoord+dir).rgb;
            }
        }
    }


    fragColor = vec4(color, 1.0);
}