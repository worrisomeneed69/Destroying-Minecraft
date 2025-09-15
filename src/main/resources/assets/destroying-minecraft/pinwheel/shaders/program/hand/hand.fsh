uniform sampler2D DiffuseSampler1;
uniform sampler2D DiffuseSampler2;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D MainTexture;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler2, texCoord);
    vec4 mainColor = texture(MainTexture, texCoord);
    gl_FragDepth = texture(DiffuseDepthSampler, texCoord).r;

    fragColor = color;
}