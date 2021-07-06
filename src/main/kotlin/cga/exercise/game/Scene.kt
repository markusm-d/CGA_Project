
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
import org.lwjgl.opengl.GL11
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
    //ältere Shaderversion versucht, um die Drohne zu laden. Leider kein Erfolg
    private val droneShader:ShaderProgram

    //ObjectLoader
    //TODO: für die Wolken ist definitv ein eigener Shader notwendig, da sie nur über Positions- und Normal-Vertecies verfügen!!
     //Object laden
    //Beide Versionen für das Laden von Objekten versucht, bei beiden kein Problem beim Compile, aber keine Drohne zu sehen...
    private val resGround : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    //private val resDrone:OBJLoader.OBJResult=OBJLoader.loadOBJ("Assets/models/drone.obj")
     //Mesh für die Daten von Vertex und Index laden
    private val objMeshGround : OBJLoader.OBJMesh = resGround.objects[0].meshes[0]
    //private val objMeshDrone:OBJLoader.OBJMesh=resDrone.objects[0].meshes[0]
    //Meshes
    private var groundMesh : Mesh
    //private var droneMesh:Mesh
    private var cycleRend = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    //Drohne laden
    private var droneRend=ModelLoader.loadModel("assets/models/drone.obj",Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")


    //Renderables
    private var groundRend = Renderable()
    //private val droneRend=Renderable()

    //Camera
    private var tronCamera  = TronCamera()

    //Lights anlegen
    private var pointLight = PointLight(Vector3f(), Vector3f())
    private var frontSpotLight = Spotlight(Vector3f(), Vector3f())

    //scene setup
    init {
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        droneShader= ShaderProgram("assets/shaders/drone_vert.glsl","assets/shaders/drone_frag.glsl")
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
        val emitTex = Texture2D("assets/textures/ground_emit.png", true)
        val diffTex = Texture2D("assets/textures/ground_diff.png", true)
        val specTex = Texture2D("assets/textures/ground_spec.png", true)

        val droneEmTex = Texture2D("assets/textures/drone_Metallic.png", true)
        val droneMeTex = Texture2D("assets/textures/drone_Emission.png", true)
        val droneRoTex = Texture2D("assets/textures/drone_Roughness.png", true)

        //erzeuegn
        val groundMaterial = Material(diffTex, emitTex, specTex, 60.0f, Vector2f(64.0f,64.0f))
        //val droneMaterial=Material(droneMeTex,droneEmTex,droneRoTex,60.0f, Vector2f(64.0f,64.0f))

        //Texturparameter für Objektende
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)     //Linear = zwischen farbwerten interpolieren
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        //Mesh erzeugen
        groundMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, groundMaterial)
        //droneMesh= Mesh(objMeshDrone.vertexData,objMeshDrone.indexData,vertexAttributes,droneMaterial)
        //Meshes zu Randerable hinzufügen
        groundRend.meshList.add(groundMesh)
        //droneRend.meshList.add(droneMesh)

        //Bike skalieren
        cycleRend.scaleLocal(Vector3f(0.8f))

        droneRend.scaleLocal(Vector3f(0.8f))


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
    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        droneShader.use()
        droneRend.render(staticShader)

        //shader Benutzung definieren
        staticShader.use()
        //Kamera binden
        tronCamera.bind(staticShader)
        //mesh rendern
        //Eventuell anderer Shader nötig?
        staticShader.setUniform("colorChange", Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3))))
        //Drohne nicht zu sehen. Warum?
        cycleRend.render(staticShader)
        pointLight.bind(staticShader, "byklePoint")
        frontSpotLight.bind(staticShader, "bykleSpot", tronCamera.getCalculateViewMatrix())
        staticShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        groundRend.render(staticShader)
    }



   /* fun update(dt: Float, t: Float) {
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
    */


    //geht nicht, da die Drohne nicht wirklich da ist???
    fun update(dt: Float, t: Float) {
        //Bewegung der Drohne
        if(window.getKeyState(GLFW_KEY_W)){
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, -5*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }
        if(window.getKeyState(GLFW_KEY_S)){
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, 5*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }


    }

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {}

    fun cleanup() {}
}
