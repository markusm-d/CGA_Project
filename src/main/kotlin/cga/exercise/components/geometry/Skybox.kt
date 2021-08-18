package cga.exercise.components.geometry
import cga.exercise.components.shader.ShaderProgram
import cga.framework.GLError
import org.lwjgl.opengl.*
import org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray
import org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays
import org.lwjgl.opengl.GL15.*
import cga.exercise.components.geometry.Transformable
import cga.exercise.components.camera.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import org.lwjgl.stb.STBImage


class Skybox {
    val skyboxVertecies = floatArrayOf(
        // Coordinates
        -1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,

        -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,

        -1.0f,  1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f,

        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f
    )
    val cubeVertices = floatArrayOf(
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
        0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
    )

    private var cubeVAO = glGenVertexArrays()
    private var cubeVBO = glGenBuffers()

    private var skyboxVAO = glGenVertexArrays()
    private var skyboxVBO = glGenBuffers()
    private var skyboxIBO = glGenBuffers()
    private var skyboxEBO = glGenBuffers()
    private var indexcount = 0

    init {

        glBindVertexArray(cubeVAO)
        glBindBuffer(GL_ARRAY_BUFFER, cubeVBO)
        GL15.glBufferData(GL_ARRAY_BUFFER, cubeVertices, GL_STATIC_DRAW )
        glEnableVertexAttribArray(0)
        //glVertexAttribPointer(0,3, GL_FLOAT, GL_FALSE, 5,Float, (null), 0)
        glEnableVertexAttribArray(1)


        glBindVertexArray(skyboxVAO)
        var skyboxVBO = glGenBuffers()
        var skyboxIBO = glGenBuffers()
        glBindVertexArray(skyboxVAO)
        glBindBuffer(GL_ARRAY_BUFFER, skyboxVBO)
        glBufferData(GL_ARRAY_BUFFER, skyboxVertecies, GL_STATIC_DRAW)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, skyboxEBO)
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, skyboxIndices, GL_STATIC_DRAW)
        //glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 3 * skyboxVertecies, (null), 0)
        glEnableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    var facesCubemap: Array<String> = arrayOf(
        "assets/textures/skybox/right.jpeg",
        "assets/textures/skybox/left.jpeg",
        "assets/textures/skybox/top.jpeg",
        "assets/textures/skybox/bottom.jpeg",
        "assets/textures/skybox/front.jpeg",
        "assets/textures/skybox/back.jpeg"
    )

    init {
        var cubemapTexture = glGenTextures()
        GL11.glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        GL11.glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)



        for (i in facesCubemap.indices) {

            //So sind es BufferObjekte
            val width = BufferUtils.createIntBuffer(1)
            val height = BufferUtils.createIntBuffer(1)
            val nrChannels = BufferUtils.createIntBuffer(1)
            val data = STBImage.stbi_load(facesCubemap[i], width, height, nrChannels, 4)
            if (data != null) {
                STBImage.stbi_set_flip_vertically_on_load(false)
                GL11.glTexImage2D(
                    GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                    0,
                    GL_RGB,
                    width.get(),
                    height.get(),
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
        glBindVertexArray(skyboxVAO)
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_CUBE_MAP, cubemapTexture)
        glDrawArrays(GL_TRIANGLES, 0, 36)
        glBindVertexArray(0)
    }
}

