#veil:buffer veil:camera VeilCamera
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#include veil:space_helper
#include veil:blend

uniform sampler2D DiffuseSampler;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D HandDepth;
uniform sampler2D WorldMap;
uniform sampler2D CloudsTexture;
uniform sampler2D StarsTexture;

uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

const vec3 cloudLayerSize = vec3(1.02*10);
const float cloudZoom = 0.5/10;

const vec3 earthLayerSize = vec3(1.0*10);
const float earthZoom = 0.5/10;

bool cube = false;

vec3 centerPos = vec3(-692, 69, 552);
vec3 lightDir = normalize(vec3(0, 0, -1));

vec3 getTransformation(vec3 pos) {
    vec3 rot = pos - centerPos;
    rot.xy *= rot2D(-25.0);
    rot.xz *= rot2D(15);

    return rot;
}



float unrotatedMap(vec3 rayPos, bool clouds) {
    vec3 p = rayPos;

    vec3 size = clouds ? cloudLayerSize : earthLayerSize;

    float d = cube ? sdBox(p, size) : sdSphere(p, size.r);
    return d;
}

vec3 getNormal(vec3 rayPos, bool clouds) {
    vec2 offset = vec2(0.001, 0.0);
    float d = unrotatedMap(rayPos, clouds);
    return normalize(d - vec3(
    unrotatedMap(rayPos - offset.xyy, clouds),
    unrotatedMap(rayPos - offset.yxy, clouds),
    unrotatedMap(rayPos - offset.yyx, clouds)
    ));
}

vec4 getColor(vec3 normal, vec3 rayPos, bool clouds) {

    vec3 uv = clouds ? rayPos * cloudZoom : rayPos * earthZoom;
    return !clouds ?  (texture(WorldMap, uv.xy) * normal.z) +
    (texture(WorldMap, uv.xz) * normal.y) +
    (texture(WorldMap, uv.yz) * normal.x) :

    (texture(CloudsTexture, uv.xy) * normal.z) +
    (texture(CloudsTexture, uv.xz) * normal.y) +
    (texture(CloudsTexture, uv.yz) * normal.x) ;
}

float map(vec3 rayPos, bool clouds) {
    vec3 p = getTransformation(rayPos);

    vec3 size = clouds ? cloudLayerSize : earthLayerSize;

    float d = cube ? sdBox(p, size) : sdSphere(p, size.r);
    vec3 normal = abs(getNormal(p, clouds));

    d -= clouds ? getColor(normal, p, clouds).r*0.01 : 0.0;
    return d;
}



void rayMarch(inout vec4 color, out vec3 normal, float depth) {
    vec3 rayOrigin = VeilCamera.CameraPosition;
    vec3 rayDir = viewDirFromUv(texCoord);
    float maxDepth = length(screenToViewSpace(texCoord, depth).xyz);
    float dist = 0.0;

    for(int i = 0; i < 100; i++) {
        vec3 rayPos = rayOrigin + rayDir * dist;

        float d = map(rayPos, false);
        dist += d;


        if (d < 0.002) {
            vec3 pos = getTransformation(rayPos);

            normal = getNormal(pos, false);
            color = getColor(abs(normal), pos, false);

            lightDir.xy *= rot2D(-25.0);
            lightDir.xz *= rot2D(15);

            color.rgb *= cube ? dot(normal, lightDir) * 0.5 + 0.5 : max(dot(normal, lightDir), 0.03);
            break;
        } else if (dist > 100.0) {
            break;
        }

    }
}

void rayMarchCloudLayer(inout vec4 color, out vec3 normal, float depth) {
    vec3 rayOrigin = VeilCamera.CameraPosition;
    vec3 rayDir = viewDirFromUv(texCoord);
    float maxDepth = length(screenToViewSpace(texCoord, depth).xyz);
    float dist = 0.0;

    for(int i = 0; i < 100; i++) {
        vec3 rayPos = rayOrigin + rayDir * dist;

        float d = map(rayPos, true);
        dist += d;


        if (d < 0.002) {
            vec3 pos = getTransformation(rayPos);

            vec3 tempNormal = getNormal(pos, true);
            vec4 tempColor = getColor(abs(tempNormal), pos, true);

            if(tempColor.a > 0.1) {
                color = mix(tempColor*2, color, 0.75);

                normal = tempNormal;

                lightDir.xy *= rot2D(-25.0);
                lightDir.xz *= rot2D(15);

                color.rgb *= cube ? dot(normal, lightDir) * 0.5 + 0.5 : max(dot(normal, lightDir), 0.03);
            }

            break;
        } else if (dist > 100.0) {
            break;
        }

    }
}

void main() {
    vec3 cameraPos = VeilCamera.CameraPosition;
    vec3 rayDir = viewDirFromUv(texCoord);
    vec4 color = texture(StarsTexture, rayDir.xy*0.75);

    vec3 normal = vec3(0.0);

    float depth = texture(DiffuseDepthSampler, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;

    float light = 1 / (length((rayDir) - lightDir)*20+0.1);

    color += (light) ;

    rayMarch(color, normal, depth);
    rayMarchCloudLayer(color, normal, depth);
    lightDir = normalize(vec3(0, 0, -1));


    fragColor = color;
}