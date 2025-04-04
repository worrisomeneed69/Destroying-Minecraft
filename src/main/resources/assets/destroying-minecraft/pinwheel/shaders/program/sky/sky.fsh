#include veil:space_helper
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

#define OFFSET vec2(0.1965249, 0.6546237)

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;

uniform float GameTime;
uniform mat4 sunMat;
uniform float supernovaTimer;
uniform float flash;


in vec2 texCoord;
out vec4 fragColor;

const vec3 SkyColor = vec3(0.5,0.75,1.1);

vec3 getLightAngle(){
    vec3 lightangle = mat3(sunMat) * vec3(0.0, 0.0, 1.0);
    return normalize(lightangle);
}

float easeInExpo(float x) {
    return x == 0 ? 0 : pow(2, 200 * x - 200);
}

void main() {
    vec3 color = vec3(0.0);
    float depth = texture(DiffuseDepthSampler, texCoord).r;

    vec3 sunDir = getLightAngle();
    vec3 rd = viewDirFromUv(texCoord);
    float time = supernovaTimer;

    float light = smoothstep(0.998 + 0.002 * time, 1.0, dot(rd, sunDir));
    rd += rand(texCoord + GameTime) * 0.01;
    if(depth >= 1.0){
        color = mix(vec3(SkyColor - rd.y * 0.9), vec3(0.0), time);
        color += vec3(light * 10);
    }

    if(flash > 0.0){
        fragColor = vec4(color, 1.0);
    } else {
        fragColor = vec4(vec3(1.0), 1.0);
    }
}