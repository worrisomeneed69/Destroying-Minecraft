//#include veil:space_helper
//#veil:buffer veil:camera VeilCamera

uniform sampler2D DiffuseSampler;

uniform float GameTime;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec3 color = texture(DiffuseSampler, texCoord).rgb;

    fragColor = vec4(mix(color, vec3(1.0), sin(GameTime * 2000)), 1.0);
}