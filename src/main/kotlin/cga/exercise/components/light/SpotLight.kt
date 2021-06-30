package cga.exercise.components.light

import cga.exercise.components.shader.ShaderProgram
import org.joml.*

// Übergabe der Winkel für Spotlight 
open class Spotlight(lightPosition: Vector3f, lightColor: Vector3f, attributeParameter: Vector3f = Vector3f(0.5f, 0.05f, 0.01f),
                     var lightAngle: Vector2f = Vector2f(Math.toRadians(15.0f), Math.toRadians(30.0f))) : PointLight(lightPosition, lightColor, attributeParameter) {

    fun bind(shaderProgram: ShaderProgram, name: String, viewMatrix: Matrix4f) {
        super.bind(shaderProgram, name)
        shaderProgram.setUniform(name + "LightAngle", lightAngle)
        shaderProgram.setUniform(name + "LightDirection", getWorldZAxis().negate().mul(Matrix3f(viewMatrix)))
    }
}