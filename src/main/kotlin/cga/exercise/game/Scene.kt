
package cga.exercise.game


import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
import cga.exercise.components.light.PointLight
import cga.exercise.components.light.Spotlight
import cga.exercise.components.shader.ShaderProgram
import cga.exercise.components.texture.Texture2D
import cga.framework.GLError
import cga.framework.GameWindow
import cga.framework.ModelLoader
import cga.framework.OBJLoader
import org.joml.Vector3f
import org.lwjgl.opengl.GL15.*
import kotlin.math.abs
import org.joml.Math
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import kotlin.math.sin


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private var skyboxShader: ShaderProgram

    //ObjectLoader
     //Object laden
    private val resGround : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
     //Mesh für die Daten von Vertex und Index laden
    private val objMeshGround : OBJLoader.OBJMesh = resGround.objects[0].meshes[0]
    //Meshes
    private var planeBottomMesh : Mesh
    private var planeBackMesh : Mesh
    private var planeFrontMesh : Mesh
    private var planeLeftMesh : Mesh
    private var planeRightMesh : Mesh
    private var planeTopMesh : Mesh

    private var cycleRend = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")

    //Renderables
    private var planeBottomRend = Renderable()
    private var planeBackRend = Renderable()
    private var planeFrontRend = Renderable()
    private var planeLeftRend = Renderable()
    private var planeRightRend = Renderable()
    private var planeTopRend = Renderable()


    //Camera
    private var tronCamera  = TronCamera()

    //Lights anlegen
    private var pointLight = PointLight(Vector3f(), Vector3f())
    private var frontSpotLight = Spotlight(Vector3f(), Vector3f())

    //scene setup
    init {
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        skyboxShader = ShaderProgram("assets/shaders/skybox_vert.glsl", "assets/shaders/skybox_frag.glsl")

        //initial opengl state
        //glClearColor(0.6f, 1.0f, 1.0f, 1.0f); GLError.checkThrow()
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        glDisable(GL_CULL_FACE); GLError.checkThrow()
        glEnable(GL_CULL_FACE)
        glFrontFace(GL_CCW)
        glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        //AttributeVertex definieren
        val stride = 8 * 4

        val vertexAttributePosition = VertexAttribute(3, GL_FLOAT, stride, 0)
        val vertexAttributeTexture = VertexAttribute(2, GL_FLOAT, stride, 3*4)
        val vertexAttributeColor = VertexAttribute(3, GL_FLOAT, stride, 5*4)
        //Attribute zusammenfügen
        val vertexAttributes = arrayOf(vertexAttributePosition, vertexAttributeTexture, vertexAttributeColor)

        //Material
         //laden
        /*val emitTex : Texture2D = Texture2D("assets/textures/skybox/bottom.jpg", true)
        val diffTex : Texture2D = Texture2D("assets/textures/ground_diff.png", true)
        val specTex : Texture2D = Texture2D("assets/textures/ground_spec.png", true)*/

        val planeBottomTex = Texture2D("assets/textures/skybox/bottom.jpg", true)
        val planeBackTex = Texture2D("assets/textures/skybox/back.jpg", true)
        val planeFrontTex = Texture2D("assets/textures/skybox/front.jpg", true)
        val planeLeftTex = Texture2D("assets/textures/skybox/left.jpg", true)
        val planeRightTex = Texture2D("assets/textures/skybox/right.jpg", true)
        val planeTopTex = Texture2D("assets/textures/skybox/top.jpg", true)


        //Texturparameter für Objektende
        /* emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)     //Linear = zwischen farbwerten interpolieren
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)*/
        //planeBottomTex.setTexParams(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP, GL_LINEAR)
        //erzeuegn
        val planeBottomMaterial = Material(planeBottomTex,planeBottomTex,planeBottomTex,60.0f, Vector2f(64.0f,64.0f))
        val planeBackMaterial = Material(planeBackTex,planeBackTex,planeBackTex,60.0f, Vector2f(64.0f,64.0f))
        val planeFrontMaterial = Material(planeFrontTex,planeFrontTex,planeFrontTex,60.0f, Vector2f(64.0f,64.0f))
        val planeLeftMaterial = Material(planeLeftTex,planeLeftTex,planeLeftTex,60.0f, Vector2f(64.0f,64.0f))
        val planeRightMaterial = Material(planeRightTex,planeRightTex,planeRightTex,60.0f, Vector2f(64.0f,64.0f))
        val planeTopMaterial = Material(planeTopTex,planeTopTex,planeTopTex,60.0f, Vector2f(64.0f,64.0f))



        //Mesh erzeugen
        planeBottomMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeBottomMaterial)
        planeBackMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeBackMaterial)
        planeFrontMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes,planeFrontMaterial)
        planeLeftMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeLeftMaterial)
        planeRightMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeRightMaterial)
        planeTopMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeTopMaterial)

        //Meshes zu Randerable hinzufügen
        planeBottomRend.meshList.add(planeBottomMesh)
        planeBackRend.meshList.add(planeBackMesh)
        planeFrontRend.meshList.add(planeFrontMesh)
        planeLeftRend.meshList.add(planeLeftMesh)
        planeRightRend.meshList.add(planeRightMesh)
        planeTopRend.meshList.add(planeTopMesh)

        //Bike skalieren
        cycleRend.scaleLocal(Vector3f(0.8f))

        tronCamera.parent = cycleRend
        //Kameratransformationen
        tronCamera.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        tronCamera.translateLocal(Vector3f(0.0f, 0.5f, 4.0f))

        //Lichtertransformationen
        pointLight = PointLight(tronCamera.getWorldPosition(), Vector3f(1f,1f,0f))
        pointLight.parent = cycleRend


        // Spotlight als Frontlicht setzen
        frontSpotLight = Spotlight(Vector3f(0.0f, 0.0f, -2.0f), Vector3f(1.0f))
        frontSpotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f)
        frontSpotLight.parent = cycleRend

        //Planes rotieren/transformieren
        planeTopRend.rotateLocal(Math.toRadians(180.0f), 0f,0f) //ausrichtung
        planeTopRend.translateLocal(Vector3f(0f,-22.83f,0f)) //höhe

        planeLeftRend.rotateLocal(Math.toRadians(90.0f), Math.PI.toFloat(),0f)
        planeLeftRend.translateLocal(Vector3f(0f,-22.38f,22.38f))

        planeRightRend.rotateLocal(Math.toRadians(-90.0f), 0f,0f)
        planeRightRend.translateLocal(Vector3f(0f,-22.38f,22.38f))


        planeFrontRend.rotateLocal(Math.toRadians(90.0f), 0f,Math.toRadians(90.0f))
        planeFrontRend.translateLocal(Vector3f(0f,-22.38f,0f))

        planeBackRend.rotateLocal(Math.toRadians(90.0f), 0f,Math.toRadians(-90.0f))
        planeBackRend.translateLocal(Vector3f(0f,-22.38f,0f))
    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        //shader Benutzung definieren
        staticShader.use()
        //Kamera binden
        tronCamera.bind(staticShader)
        //mesh rendern
        staticShader.setUniform("colorChange", Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3))))
        cycleRend.render(staticShader)
        pointLight.bind(staticShader, "byklePoint")
        frontSpotLight.bind(staticShader, "bykleSpot", tronCamera.getCalculateViewMatrix())
        //staticShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        planeBottomRend.render(staticShader)
        planeTopRend.render(staticShader)
        planeLeftRend.render(staticShader)
        planeRightRend.render(staticShader)
        planeFrontRend.render(staticShader)
        planeBackRend.render(staticShader)


        skyboxShader.use()
        tronCamera.bind(skyboxShader)
        skyboxShader.setUniform("skybox", 0 )
    }



    fun update(dt: Float, t: Float) {
        //Farbe des Motorads wird verändert in Abhängigkeit der Zeit mit sinuswerten
        pointLight.lightColor = Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3)))
        //Bewegung des Motorrads
        if(window.getKeyState(GLFW_KEY_W)){
            cycleRend.translateLocal(Vector3f(0.0f, 0.0f, -5*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                cycleRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                cycleRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }
        if(window.getKeyState(GLFW_KEY_S)){
            cycleRend.translateLocal(Vector3f(0.0f, 0.0f, 5*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                cycleRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                cycleRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}
}
