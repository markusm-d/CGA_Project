package cga.exercise.components.geometry

import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import org.lwjgl.opengl.*
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.glEnableVertexAttribArray
import org.lwjgl.opengl.GL20.glVertexAttribPointer

import cga.exercise.components.geometry.Transformable
import cga.exercise.components.camera.*
import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage

class Skybox {

    val skyboxVertecies = floatArrayOf(

        // Coordinates
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f
    )

    val skyboxIndices = intArrayOf(

        // rechts
        1, 2, 6,
        6, 5, 1,
        // links
        0, 4, 7,
        7, 3, 0,
        // oben
        4, 5, 6,
        6, 7, 4,
        // unten
        0, 3, 2,
        2, 1, 0,
        // hinten
        0, 1, 5
    )

    private var skyboxVAO = glGenVertexArrays()
    private var skyboxVBO = glGenBuffers()
    private var skyboxIBO = glGenBuffers()
    private var skyboxEBO = glGenBuffers()
    private var indexcount = 0

    init {
        glBindVertexArray(skyboxVAO)
        glGenBuffers(skyboxVBO)
        glGenBuffers(skyboxEBO)
        glBindVertexArray(skyboxVAO)
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO)
        glBufferData(GL_ARRAY_BUFFER, skyboxVertecies, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, skyboxEBO)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, skyboxIndices, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * skyboxVertecies, (null), 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }


    var facesCubemap: Array<String> = arrayOf(

        "/assets/textures/skyrender0001.bmp",
        "assets/textures/skyrender0002.bmp",
        "assets/textures/skyrender_top.bmp",
        "assets/textures/skyrender_bottom.bmp",
        "assets/textures/skyrender0004.bmp",
        "assets/textures/skyrender0005.bmp"
    )

    init {
        var cubemapTexture = glGenTextures()
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

        var width: Int = 512
        var height: Int = 512
        var nrChannels: Int = 4


        for (i in facesCubemap.indices) {
            var data = STBImage.stbi_load(facesCubemap[i], width, height, nrChannels, 0)
            if (data){
                STBImage.stbi_set_flip_vertically_on_load(false)
                GL11.glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0,
                    GL_RGB,
                    width,
                    height,
                    0,
                    GL_RGBA,
                    GL_UNSIGNED_BYTE,
                    data
                )
                STBImage.stbi_image_free(data)
            } else {
                println("Failed to load texture: ")
            }
        }
    }

}