#version 330 core

in vec3 col;

//fragment shader output
out vec4 color;

void main(){
    //color = vec4(col,1.0f);
    //so ist Wolke zumindest Weiß :D
    color=vec4(1.0f);
}
//Versuch, Shader anzupassen --> Bild schwarz???
/*    #version 330 core

//in vec3 col;
in struct VertexData
{
    vec3 position;
    vec3 normals;
} vertexData;
//fragment shader output
out vec4 color;

void main(){

    vec3 n=normalize(vertexData.normals);

    color = vec4(vertexData.normals,1.0f);
}*/
