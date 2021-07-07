#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
    vec3 toPointLight;
} vertexData;

uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D specular;
uniform float shininess;

//fragment shader output
out vec4 color;


void main(){

    vec3 n=normalize(vertexData.normale);

    //Texturverarbeitung
    vec3 diffCol = texture(diff, vertexData.texture).rgb;
    vec3 emitCol = texture(emit, vertexData.texture).rgb;
    vec3 specularCol = texture(specular, vertexData.texture).rgb;

    //Versuch drone sichtbar zu machen. Fehlgeschlagen
    vec3 colorChange=emitCol*diffCol*specularCol;

    color = vec4(colorChange,1.0);

}
