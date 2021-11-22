#version 330 core
out vec4 FragColor;
in vec3 ourNormal;
in vec2 texCoord;
uniform sampler2D tex;
uniform vec4 color;
uniform vec4 noTex;
void main(){
    vec4 texel = texture(tex, texCoord);
    if(texel.a<0.1)discard;
    FragColor = (texel+noTex)*color;
}