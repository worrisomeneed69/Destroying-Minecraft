uniform sampler2D DiffuseSampler;
uniform sampler2D BHBloomSampler;
uniform sampler2D BHSampler;

uniform vec2 ScreenSize;

in vec2 texCoord;
out vec4 fragColor;

vec3 BloomLod(float scale, vec2 offset){
    vec3 color = vec3(0.0);
    vec2 uv = ((texCoord - offset) * scale);
    if(uv.x > 1.0 || uv.y > 1.0 || uv.x < 0.0 || uv.y < 0.0){
        color = vec3(0.0, 0.0, 0.0);
    } else {
        color += texture(DiffuseSampler, uv).rgb;
    }

    return color;
}

void main() {
    vec4 color = texture(BHSampler, texCoord);

    vec4 highlights = vec4(0.0);
    float scale = 2.0;
    float offset = 0;
    for(int i = 0; i < 6; i++) {
        vec2 uv = (vec2(texCoord.x + offset * scale, texCoord.y)) / scale;
        highlights += texture(DiffuseSampler, uv);
        offset = (1.0 - (1.0/ scale));
        scale *= 2.0;
    }


    fragColor = color + (highlights / 6);


}