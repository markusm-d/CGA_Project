#version 330 core

in struct VertexData
{
    vec3 position;
    vec3 normale;
} vertexData;

uniform vec3 colorChange;

//fragment shader output
out vec4 color;

void main(){
    vec3 position = normalize(vertexData.position);
    vec3 normale = normalize(vertexData.normale);

    vec3 colorResult=colorChange;

    color=vec4(colorResult,1.0f);
}
