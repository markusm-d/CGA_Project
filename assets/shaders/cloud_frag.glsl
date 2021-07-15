#version 330 core

//in vec3 col;
in struct VertexData
{
    vec3 position;
    vec3 normale;
} vertexData;

//fragment shader output
out vec4 color;

void main(){
    vec3 position = normalize(vertexData.position);
    vec3 normale = normalize(vertexData.normale);

    //color = vec4(col,1.0f);
    //so ist Wolke zumindest Weiß :D
    color=vec4(1.0f);
}
//Versuch, Shader anzupassen --> Bild schwarz???
 /*   #version 330 core

//in vec3 col;
in struct VertexData
{
    vec3 position;
    vec3 normale;
} vertexData;
//fragment shader output
out vec4 color;

void main(){

    vec3 normale = normalize(vertexData.normale);
    vec3 position = normalize(vertexData.position);

    color = vec4(vertexData.normale,1.0f);
}*/
