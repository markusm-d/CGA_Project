/*#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normals;

out vec3 col;

void main(){

    gl_Position = vec4(position.x, position.y, -position.z, 1.0f);
    col =  normals;

}*/
//Versuch, Shader anzupassen --> Bild schwarz???


#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 normale;
//layout(location = 2) in vec3 normale;

uniform mat4 model_matrix;
uniform mat4 view_matrix;
uniform mat4 proj_matrix;


out struct VertexData
{
    vec3 position;
    vec3 normale;
} vertexData;
//out vec3 col;

void main(){

    mat4 modelView = view_matrix * model_matrix;
    vec4 pos =  modelView * vec4(position, 1.0f);
    vec4 nor = inverse(transpose(modelView)) * vec4(normale, 0.0f);


    gl_Position=proj_matrix*pos;
    vertexData.position=-pos.xyz;
    vertexData.normale=nor.xyz;

    //gl_Position = vec4(position.x, position.y, -position.z, 1.0f);
    //col =  normals;

}