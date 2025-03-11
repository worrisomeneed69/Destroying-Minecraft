
uniform sampler2D DiffuseSampler;
uniform sampler2D HandDepth;
uniform sampler2D DepthComponent;
uniform sampler2D BHSampler;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 color = texture(DiffuseSampler, texCoord);
    float depth = texture(DepthComponent, texCoord).r;
    float handDepth = texture(HandDepth, texCoord).r;
    vec4 blackHole = texture(BHSampler, texCoord);


    if(depth >= 1.0 && handDepth >= 1.0){
        fragColor = blackHole;
    } else {
        fragColor = color;
    }


}