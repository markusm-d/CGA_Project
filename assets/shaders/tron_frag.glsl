#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
    vec3 toPointLight;
    vec3 toFrontSpotLight;

} vertexData;

//Material
uniform sampler2D diff;
uniform sampler2D emit;
uniform sampler2D specular;
uniform float shininess;

uniform vec3 byklePointLightColor;
uniform vec3 byklePointLightAttributeParameter;

uniform vec3 bykleSpotLightColor;
uniform vec3 bykleSpotLightAttributeParameter;
uniform vec2 bykleSpotLightAngle;
uniform vec3 bykleSpotLightDirection;

uniform vec3 colorChange;


//fragment shader output
out vec4 color;

//Funktionen
//Wertübergabe diff, specular, shininess
vec3 shading(vec3 normaleS, vec3 lightVec, vec3 posS, vec3 diff, vec3 spec, float shine){
    //Kreuzprodukt normale & lightVec
    vec3 diffuse =  diff * max(0.0, dot(normaleS,lightVec));            //0.0 damit nur positive Lichtwerte gelten
    vec3 refelctionDirection = reflect(-lightVec, normaleS);
    float cos = max(dot(posS, refelctionDirection), 0.0);

    vec3 specular = spec * pow(cos, shine);

    return diffuse+specular;
}

//Attenuation, Parameter für nachlassendes Licht
float attenuate(float distance, vec3 attributeParameter){
    return 1.0/(attributeParameter.x + attributeParameter.y * distance + attributeParameter.z * distance * distance);
}
//Lichtintensitätsberechnung
vec3 pointLightIntensity(vec3 lightcolour, float distance){
    return lightcolour * attenuate(distance, byklePointLightAttributeParameter);
}

vec3 spotLightIntensity(vec3 spotlightcolour, float distance, vec3 spotLight, vec3 spotLightDirection){
    float cosTheta = dot(spotLight, normalize(spotLightDirection));
    float cosPhi = cos(bykleSpotLightAngle.x);
    float cosGamma = cos(bykleSpotLightAngle.y);

    float intensity = (cosTheta-cosGamma)/(cosPhi-cosGamma);
    float clampIntensity = clamp(intensity, 0.0f, 1.0f);

    return spotlightcolour * clampIntensity * attenuate(distance, bykleSpotLightAttributeParameter);
}

void main(){
    vec3 normale = normalize(vertexData.normale);
    vec3 position = normalize(vertexData.position);
    //Licht
    float pointLightPositionLength = length(vertexData.toPointLight);
    vec3 pointLightPosition = vertexData.toPointLight/pointLightPositionLength;
    float frontSpotLightPositionLength = length(vertexData.toFrontSpotLight);
    vec3 frontSpotLightPosition = vertexData.toFrontSpotLight/frontSpotLightPositionLength;

    //Texturverarbeitung
    vec3 diffCol = texture(diff, vertexData.texture).rgb;
    vec3 emitCol = texture(emit, vertexData.texture).rgb;
    vec3 specularCol = texture(specular, vertexData.texture).rgb;

    //Farbdefinition
    vec3 colorResult = emitCol*colorChange;

    //Colorberechnungen
    colorResult += shading(normale, pointLightPosition, position, diffCol, specularCol, shininess) * pointLightIntensity(byklePointLightColor, pointLightPositionLength);
    colorResult += shading(normale, frontSpotLightPosition, position, diffCol, specularCol, shininess) * spotLightIntensity(bykleSpotLightColor, frontSpotLightPositionLength, frontSpotLightPosition, bykleSpotLightDirection);
    //finale Color
    color = vec4(colorResult, 1.0);
}