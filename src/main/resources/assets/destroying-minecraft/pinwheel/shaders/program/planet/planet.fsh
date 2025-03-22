#include veil:space_helper
#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

const vec3 PLANET_POS = vec3(-132, 78, 162);

float sdSphere(vec3 p, float s){
    return length(p)-s;
}

float map(vec3 p){
    return sdSphere(p - PLANET_POS, 3);
}

void main() {
    vec4 mainTexture = texture(DiffuseSampler, texCoord);
    vec3 cameraPos = VeilCamera.CameraPosition;
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 playerSpace = screenToLocalSpace(texCoord, depth).xyz;
    float worldDepth = length(playerSpace);

    vec3 rayOrigin = cameraPos;
    vec3 rayDir = viewDirFromUv(texCoord);
    float dist = 0.0;

//    float farPlane = 30.0f;
//    float stepDist = farPlane / ITERATIONS;

    bool hit = false;
    vec3 sphereNormal = vec3(0.0);
    for(int i = 0; i < 30; i++){
        vec3 rayPos = rayOrigin + rayDir * dist;

        float d = map(rayPos);
        dist += d;

        if(d < 0.001){
            sphereNormal = normalize(rayPos - PLANET_POS);
            hit = true;
            break;
        } else if(d > 300 || worldDepth < dist){
            break;
        }
    }
    vec3 color = vec3(dist);

    if(hit) {
        vec3 lightDir = normalize(vec3(cos(GameTime * 500),1,sin(GameTime * 500)));
        float light = dot(sphereNormal, lightDir);
        fragColor = vec4(sphereNormal, 1.0);
    } else {
        fragColor = mainTexture;
    }

//    fragColor = vec4(playerSpace, 1.0);

}