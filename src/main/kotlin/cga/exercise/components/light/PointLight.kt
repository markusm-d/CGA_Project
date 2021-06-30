package cga.exercise.components.light

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.shader.ShaderProgram
import org.joml.Vector3f

open class PointLight(var lightPosition : Vector3f = Vector3f(), var lightColor : Vector3f = Vector3f(),
                      var attributeParameter : Vector3f = Vector3f(1.0f, 0.5f, 0.1f)): Transformable(), IPointLight{

    //PointLight muss im Weltkoordinatensystem platziert werden
    init {
        //Dazu Lichtposition nach Global translatieren
        translateGlobal(lightPosition)
    }


    override fun bind(shaderProgram: ShaderProgram, name: String) {
        //Lichtparameter an Shader übergeben
        shaderProgram.setUniform(name + "LightPosition", getWorldPosition())
        shaderProgram.setUniform(name + "LightColor", lightColor)
        shaderProgram.setUniform(name + "LightAttributeParameter", attributeParameter)
    }

}