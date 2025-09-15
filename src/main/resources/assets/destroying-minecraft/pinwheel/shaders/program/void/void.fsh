#include destroying-minecraft:noise
uniform float GameTime;

vec3 BLACK_HOLE_POS = vec3(-18.5, 282.5, -14.5);

in vec3 pos;
in vec3 color;

out vec4 fragColor;
layout (location = 1) out vec4 Albedo;
layout (location = 2) out vec3 OutNormal;
layout (location = 3) out vec2 LightUV;
layout (location = 4) out vec3 LightColor;
layout (location = 6) out ivec4 Material;
layout (location = 7) out vec4 Bloom;

void main() {
    vec3 blockPos = floor(vec3(pos.x, pos.y + 1, pos.z) * 0.5);

//    vec3 sideOfPlane = vec3(10.0f);
//    if (noise((blockPos + (GameTime)) * 523.523) > distance(blockPos, BLACK_HOLE_POS * 0.5)/5) {
//        float noise = perlin_noise(vec4(blockPos.x, blockPos.y, blockPos.z, GameTime*noise(blockPos) * 10000)) * 0.5 + 0.8;
//        sideOfPlane = vec3(noise - 0.5);
//    }

    vec3 sideOfPlane = color;

//    vec3 sideOfPlane = mix(vec3(noise), vec3(1.0), sin(GameTime*2000) * 0.5 + 0.5);

    fragColor = vec4(sideOfPlane, 1.0);
    Albedo = vec4(sideOfPlane, 1.0);
    OutNormal = vec3(1.0);
    LightUV = vec2(1, 1);
    Material = ivec4(7,0,0,1);
    Bloom = vec4(sideOfPlane * 0.1, 1.0);
}