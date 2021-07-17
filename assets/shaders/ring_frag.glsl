#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
} vertexData;

uniform sampler2D col;
uniform sampler2D disp;
uniform sampler2D metalness;
uniform sampler2D nrm;
uniform sampler2D roughness;
uniform float shininess;


uniform vec3 colorChange;


//fragment shader output
out vec4 color;

void main(){

    vec3 normale = normalize(vertexData.normale);
    vec3 position = normalize(vertexData.position);

    //Texturverarbeitung
    vec3 colCol = texture(col, vertexData.texture).rgb;
    vec3 dispCol = texture(disp, vertexData.texture).rgb;
    vec3 metalnessCol = texture(metalness, vertexData.texture).rgb;
    vec3 rougnessCol = texture(roughness, vertexData.texture).rgb;

    //Versuch drone sichtbar zu machen
    vec3 colorResult=metalnessCol*colorChange;



    color = vec4(colorResult,1.0);

}
