#version 330 core

layout(location = 0) in vec3 position;
layout(location =  1) in vec2 tc;
layout(location = 2) in vec3 normale;

//uniforms
// translation object to world
uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 proj_matrix;
//für Textur
uniform vec2 tcMultiplier;

//Lichtpositionen
uniform vec3 byklePointLightPosition;
uniform vec3 bykleSpotLightPosition;

//uniform vec3 droneSpotLightPosition;

out struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
    vec3 toPointLight;
    vec3 toFrontSpotLight;
    //vec3 toSpotLight;
} vertexData;



void main(){
    mat4 modelView = view_matrix * model_matrix;
    vec4 pos =  modelView * vec4(position, 1.0f);
    vec4 nor = inverse(transpose(modelView)) * vec4(normale, 0.0f);

    // Pointlight im Camera Space platzieren
    // Berechnung der toLight Vektors
    // Lichtrichtung
    vec4 lp = view_matrix * vec4(byklePointLightPosition, 1.0);
    vertexData.toPointLight = (lp - pos).xyz;                          //von p zu lp (licht im camera space)

    vec4 lp2 = view_matrix * vec4(bykleSpotLightPosition, 1.0);
    vertexData.toFrontSpotLight = (lp2 - pos).xyz;

    //vec4 droneLightPosition=view_matrix*vec4(droneSpotLightPosition,1.0);
    //vertexData.toSpotLight=(droneLightPosition-pos).xyz;

    gl_Position = proj_matrix * pos;
    vertexData.position = -pos.xyz;
    vertexData.texture = tc * tcMultiplier;
    vertexData.normale = nor.xyz;
}