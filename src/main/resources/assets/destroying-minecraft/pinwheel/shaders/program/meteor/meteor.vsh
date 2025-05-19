#veil:buffer veil:camera VeilCamera

layout (location = 0) in vec3 Position;
layout (location = 1) in vec4 Entity_Position;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 entityPos;
out vec3 worldPos;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    entityPos = Entity_Position;
    worldPos = Position + VeilCamera.CameraPosition;
}