#version 330 core

//in vec3 col;
in struct VertexData
{
    vec3 position;
    vec3 normale;
    //vec3 light;
} vertexData;

/*uniform vec3 cloudSpotLightColor;
uniform vec3 cloudSpotLightAttributeParameter;
uniform vec2 cloudSpotLightAngle;
uniform vec3 cloudSpotLightDirection;*/

uniform vec3 colorChange;

//fragment shader output
out vec4 color;

void main(){
    vec3 position = normalize(vertexData.position);
    vec3 normale = normalize(vertexData.normale);

/*    float cloudLightPositionLength=length(vertexData.light);
    vec3 cloudLightPosition=vertexData.light/cloudLightPositionLength;*/

    //color = vec4(col,1.0f);
    //so ist Wolke zumindest Weiß :D
    vec3 colorResult=colorChange;
    //colorResult+=cloudLightPosition;
    //color=vec4(1.0f);
    color=vec4(colorResult,1.0f);
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
