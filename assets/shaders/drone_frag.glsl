#version 330 core

//input from vertex shader
in struct VertexData
{
    vec3 position;
    vec2 texture;
    vec3 normale;
    //vec3 toSpotLight;
} vertexData;

uniform sampler2D albedo;
uniform sampler2D emission;
uniform sampler2D metallic;
uniform sampler2D normal;
uniform sampler2D occlusion;
uniform sampler2D roughness;
uniform float shininess;

/*uniform vec3 droneSpotLightColor;
uniform vec3 droneSpotLightAttributeParameter;
uniform vec2 droneSpotLightAngle;
uniform vec3 droneSpotLightDirection;*/

uniform vec3 colorChange;


//fragment shader output
out vec4 color;


/*//Funktionen
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

vec3 spotLightIntensity(vec3 spotlightcolour, float distance, vec3 spotLight, vec3 spotLightDirection){
    float cosTheta = dot(spotLight, normalize(spotLightDirection));
    float cosPhi = cos(droneSpotLightAngle.x);
    float cosGamma = cos(droneSpotLightAngle.y);

    float intensity = (cosTheta-cosGamma)/(cosPhi-cosGamma);
    float clampIntensity = clamp(intensity, 0.0f, 1.0f);

    return spotlightcolour * clampIntensity * attenuate(distance, droneSpotLightAttributeParameter);
}*/

void main(){

    vec3 normale = normalize(vertexData.normale);
    vec3 position = normalize(vertexData.position);

/*    //Lichtversuch...
    float droneSpotLightPositionLength=length(vertexData.toSpotLight);
    vec3 droneSpotLightPosition=vertexData.toSpotLight/droneSpotLightPositionLength;*/

    //Texturverarbeitung
    vec3 albedoCol = texture(albedo, vertexData.texture).rgb;
    vec3 emissionCol = texture(emission, vertexData.texture).rgb;
    vec3 metallicCol = texture(metallic, vertexData.texture).rgb;
    vec3 normalCol = texture(normal, vertexData.texture).rgb;
    vec3 occlusionCol = texture(occlusion, vertexData.texture).rgb;
    vec3 rougnessCol = texture(roughness, vertexData.texture).rgb;

    //Versuch drone sichtbar zu machen
    vec3 colorResult=metallicCol*colorChange;

    //colorResult+=shading(normale,droneSpotLightPosition,position,albedoCol,emissionCol,shininess)*spotLightIntensity(droneSpotLightColor,droneSpotLightPositionLength,droneSpotLightPosition,droneSpotLightDirection);


    color = vec4(colorResult,1.0);

}
