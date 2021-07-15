package cga.exercise.components.geometry


import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

//Versuch das Material für die Drohne erstellen zu können. geht theoretisch, allerding müsste auch ein neues Mesh erstellt werden, denke ich...
// da das bestehende kein droneMaterial übernehmen kann. Oder nur einen ANteil der PNG´s nutzen? Aber ich wüsste jetzt nicht genau wie...
class droneMaterial (var albedo: Texture2D,
                     var emission: Texture2D,
                     var metallic: Texture2D,
                     var normal: Texture2D,
                     var occlusion: Texture2D,
                     var roughness: Texture2D,
                     var shininess: Float = 50.0f,
var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        //Textureanteile binden
        albedo.bind(0)
        emission.bind(1)
        metallic.bind(2)
        normal.bind(3)
        occlusion.bind(4)
        roughness.bind(5)

        //Uniforms für Shader setzen
        shaderProgram.setUniform("albedo", 0)
        shaderProgram.setUniform("emission", 1)
        shaderProgram.setUniform("metallic", 2)
        shaderProgram.setUniform("normal",3)
        shaderProgram.setUniform("occlusion",4)
        shaderProgram.setUniform("roughness",5)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("shininess", shininess)

    }

}