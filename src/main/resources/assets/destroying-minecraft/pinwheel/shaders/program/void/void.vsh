#veil:buffer veil:camera VeilCamera
#include veil:space_helper

layout(location = 0) in vec3 Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform int renderingShadow;

out vec3 pos;

vec3 distort(in vec3 shadowPosition) {
    const float bias0 = 0.95;
    const float bias1 = 1.0 - bias0;

    float factorDistance = length(shadowPosition.xy);

    float distortFactor = factorDistance * bias0 + bias1;

    return shadowPosition * vec3(vec2(1.0 / distortFactor), 0.2);
}

void main() {
//    vec3 cameraPos = VeilCamera.CameraPosition;
//    pos = viewToWorldSpace(vec4(Position, 1.0)).xyz;
    pos = Position + VeilCamera.CameraPosition;

    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    if (renderingShadow == 1) {
        gl_Position.xyz = distort(gl_Position.xyz);
    }
}