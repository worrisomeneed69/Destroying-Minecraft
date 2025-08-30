#veil:buffer veil:camera VeilCamera
#include veil:space_helper
#include destroying-minecraft:noise

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int renderingShadow;
uniform float GameTime;

out vec3 pos;
out vec3 color;

vec3 distort(in vec3 shadowPosition) {
    const float bias0 = 0.95;
    const float bias1 = 1.0 - bias0;

    float factorDistance = length(shadowPosition.xy);

    float distortFactor = factorDistance * bias0 + bias1;

    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    pos = Position + cameraPos;

    vec3 blockPos = floor(vec3(pos.x, pos.y + 1, pos.z) * 0.5);
    float noise = snoise(vec4(blockPos.x, blockPos.y, blockPos.z, GameTime*3000)) * 0.5 + 0.1;

    color = Color.rgb;
//    color = vec3(noise);

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    if (renderingShadow == 1) {
        gl_Position.xyz = distort(gl_Position.xyz);
    }
}