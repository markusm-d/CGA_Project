#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
} vertexData;

uniform sampler2D albedo;
uniform sampler2D emission;
uniform sampler2D metallic;
uniform sampler2D normal;
uniform sampler2D occlusion;
uniform sampler2D roughness;


//fragment shader output
out vec4 color;


void main(){

    vec3 n=normalize(vertexData.normale);

    //Texturverarbeitung
    vec3 albedoCol = texture(albedo, vertexData.texture).rgb;
    vec3 emissionCol = texture(emission, vertexData.texture).rgb;
    vec3 metallicCol = texture(metallic, vertexData.texture).rgb;
    vec3 normalCol = texture(normal, vertexData.texture).rgb;
    vec3 occlusionCol = texture(occlusion, vertexData.texture).rgb;
    vec3 rougnessCol = texture(roughness, vertexData.texture).rgb;

    //Versuch drone sichtbar zu machen. Fehlgeschlagen
    //Mal alle Texturen versuchr einzubinden
    vec3 colorResult=emissionCol/**albedoCol*metallicCol*normalCol*occlusionCol*rougnessCol*/;

    color = vec4(colorResult,1.0);

}
