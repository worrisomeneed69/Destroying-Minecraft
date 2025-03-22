uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform sampler2D Sampler3;

in vec2 texCoord;
in vec3 normal;
in float brightness;
flat in int textureType;

out vec4 fragColor;
layout (location = 1) out vec4 Albedo;
layout (location = 2) out vec3 OutNormal;
layout (location = 3) out vec2 LightUV;
layout (location = 4) out vec3 LightColor;

void main() {
	vec4 color = vec4(0.0);
	if(textureType == 0) {
		color = texture(Sampler0, texCoord);
	} else if(textureType == 1) {
		color = texture(Sampler1, texCoord);
	} else if(textureType == 2) {
		color = texture(Sampler2, texCoord);
	} else {
		color = texture(Sampler3, texCoord);
	}

	color *= brightness;

	fragColor = color;
	Albedo = color;
	OutNormal = normal;
	LightUV = vec2(0.0, 1.0);
	LightColor = vec3(1.0);
}