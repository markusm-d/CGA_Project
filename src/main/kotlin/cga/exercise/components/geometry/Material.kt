package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import org.joml.Vector2f

class Material(var diff: Texture2D,
               var emit: Texture2D,
               var specular: Texture2D,
               var shininess: Float = 50.0f,
               var tcMultiplier : Vector2f = Vector2f(1.0f)){

    fun bind(shaderProgram: ShaderProgram) {
        //Textureanteile binden
        emit.bind(0)
        diff.bind(1)
        specular.bind(2)

        //Uniforms für Shader setzen
        shaderProgram.setUniform("emit", 0)
        shaderProgram.setUniform("diff", 1)
        shaderProgram.setUniform("specular", 2)
        shaderProgram.setUniform("tcMultiplier", tcMultiplier)
        shaderProgram.setUniform("shininess", shininess)
    }
}