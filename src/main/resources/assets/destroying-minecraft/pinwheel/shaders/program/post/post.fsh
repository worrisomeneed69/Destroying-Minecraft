#include veil:space_helper
#include veil:blend
#include destroying-minecraft:ray_march
#include destroying-minecraft:noise
#veil:buffer veil:camera VeilCamera

uniform sampler2D MainTexture;
uniform sampler2D MainDepth;
uniform sampler2D BlurredTexture;

uniform float centerDepth;
uniform float GameTime;
uniform int enabledDepthOfField;
uniform int enabledBlackScreen;

const int ITERATIONS = 5;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 color = texture(MainTexture, texCoord).rgb;

    if (enabledBlackScreen == 1) {
        fragColor = vec4(0.0);
        return;
    }

    if (enabledDepthOfField == 0) {
        fragColor = vec4(color, 1.0);
        return;
    }

    vec3 blurredTexture = texture(BlurredTexture, texCoord).rgb;
    float depth = texture(MainDepth, texCoord).r;
    vec3 playerPos = screenToLocalSpace(texCoord, depth).xyz;
    depth = length(playerPos) / 100;

    float dist = max(depth - centerDepth, 0.0);

    float blur = smoothstep(0.0, 0.2, dist);


    fragColor = vec4(mix(color, blurredTexture, blur), 1.0);

}