#include veil:light
#veil:buffer veil:camera VeilCamera

layout(location = 0) in vec3 Position;
layout(location = 1) in vec2 UV0;
layout(location = 2) in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;
uniform mat3 NormalMat;
uniform vec3 offset;

out vec2 texCoord;
out vec3 normal;
out float brightness;
out int textureType;

// Thanks to https://www.shadertoy.com/view/WttXWX
uint triple32(uint x) {
    x ^= x >> 17;
    x *= 0xed5ad4bbU;
    x ^= x >> 11;
    x *= 0xac4c1b51U;
    x ^= x >> 15;
    x *= 0x31848babU;
    x ^= x >> 14;
    return x;
}

float hash(uint x){
    return float( triple32(x) ) / float( 0xffffffffU );
}

vec4 worldToViewSpace(vec4 pos) {
    vec4 viewSpacePos = VeilCamera.ViewMat * (pos);
    return viewSpacePos / viewSpacePos.w;
}

mat2 rot2D(float angle) {
    float rad = (angle * 3.151592)/180.0;
    float s = sin(rad);
    float c = cos(rad);
    return mat2(c, -s, s, c);
}

void main() {
    float distToBlackHole = fract(GameTime * (hash(gl_InstanceID * 5345) + 0.5) * 10);
    vec3 pos = Position;


    //Size
    pos -= vec3(0.5);
    pos *= 1.0 - (distToBlackHole * distToBlackHole * distToBlackHole);

    //Random Rotation
    pos.xz *= rot2D(GameTime * 200000 * hash(gl_InstanceID * 1324));
    pos.xy *= rot2D(GameTime * 200000 * hash(gl_InstanceID * 674));
    pos.yz *= rot2D(-GameTime * 20000 * hash(gl_InstanceID * 94));
    pos += vec3(0.5);

    //Random Position
    float randSpread = hash(gl_InstanceID * 634564) * 2.0 - 1.0;
    vec3 randDir = vec3(0 - randSpread, 1, -2);
    randDir = normalize(randDir);
    randDir *= 70;
    pos = pos + randDir * distToBlackHole * 20;

    vec3 offsetFromCamera = vec3(randSpread * 500, -VeilCamera.CameraPosition.y + 50,(hash(gl_InstanceID * 5345) * -100) - 200);
    gl_Position = VeilCamera.ProjMat * VeilCamera.ViewMat * vec4((pos + offsetFromCamera), 1.0);

    texCoord = UV0;

    float Brightness = 1.0;

    if(Normal.y == -1){
        Brightness = 0.5;
    } else if(Normal.z == 1){
        Brightness = 0.8;
    } else if(Normal.x == 1){
        Brightness = 0.6;
    }
    brightness = Brightness;

    textureType = int(floor(mod(float(gl_InstanceID), 4.0)));


    normal = worldToViewSpace(vec4(Normal, 1.0)).xyz;
}