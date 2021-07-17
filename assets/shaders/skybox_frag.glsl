version 330 core
out vec4 FragColor;

in vec3 tecCoords;
uniform samplerCube skybox;

void main(){
    FragColor = texture(skybox, texCoords);
}