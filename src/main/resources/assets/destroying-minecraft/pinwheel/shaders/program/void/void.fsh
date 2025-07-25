
uniform float GameTime;

in vec3 pos;

out vec4 fragColor;
layout (location = 1) out vec4 Albedo;
layout (location = 2) out vec3 OutNormal;
layout (location = 3) out vec2 LightUV;
layout (location = 4) out vec3 LightColor;
layout (location = 7) out vec3 Bloom;

vec4 plane = vec4(1.0, -1.0, 0.0, 1.0);

void main() {
//307 338
    vec3 offset = vec3(mix(307, 338, sin(GameTime * 3000) * 0.5 + 0.5),0.0, 0.0);

//    float sideOfPlane = clamp(dot(pos + offset, plane.xyz), 0.0, 0.6);
    float sideOfPlane = 0.6;

    fragColor = vec4(vec3(sideOfPlane), 1.0);
    Albedo = vec4(vec3(sideOfPlane), 1.0);
    OutNormal = vec3(1.0);
    LightUV = vec2(1, 1);
    Bloom = vec3(sideOfPlane);
}