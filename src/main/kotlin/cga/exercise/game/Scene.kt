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
    //Shader für Drohne und Wolken. Leider funktioniert noch nicht allesso wie es sollte...
    private val droneShader:ShaderProgram
    private val cloudShader:ShaderProgram //TODO: Shader muss noch angepasst werde. Eventuell wegen der ViewMatrix o.ä. schauen?
    //Testshader -> Wolke zu anders?
    private val bigStaticShader:ShaderProgram
    private val bigDroneShader:ShaderProgram
    //private val bigCloudShader:ShaderProgram


    //ObjectLoader

     //Object laden
    private val resGround : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    private val resDrone:OBJLoader.OBJResult=OBJLoader.loadOBJ("assets/models/drone.obj")
    private val resCloud:OBJLoader.OBJResult=OBJLoader.loadOBJ("assets/models/cloud.obj")

     //Mesh für die Daten von Vertex und Index laden
    private val objMeshGround : OBJLoader.OBJMesh = resGround.objects[0].meshes[0]
    private val objMeshDrone:OBJLoader.OBJMesh=resDrone.objects[0].meshes[0]
    private val objMeshCloud:OBJLoader.OBJMesh=resCloud.objects[0].meshes[0]

    //Meshes
    private var groundMesh : Mesh
    private var droneMesh:Mesh
    private val cloudMesh:Mesh

    //TODO: eventuell das Objekt in seperaten Ordner und an ModellLoder anpassen?
    private var cycleRend = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    //private var cloudRend=ModelLoader.loadModel("assets/models/cloud.obj",0.0f,0.0f,0.0f)?: throw IllegalArgumentException("Could not load the model")

    //Renderables
    private var groundRend = Renderable()
    private val droneRend=Renderable()
    private val cloudRend= Renderable()

    //Camera
    private var tronCamera  = TronCamera()

    //Lights anlegen
    private var pointLight = PointLight(Vector3f(), Vector3f())
    private var frontSpotLight = Spotlight(Vector3f(), Vector3f())
    private var droneSpot = Spotlight(Vector3f(), Vector3f())

    private var oldMousePosX : Double = -1.0
    private var oldMousePosY : Double = -1.0
    private var pruefBoolean : Boolean = false

    //scene setup
    init {
        //staticShader = ShaderProgram("assets/shaders/simple_vert.glsl", "assets/shaders/simple_frag.glsl")
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl")
        droneShader= ShaderProgram("assets/shaders/drone_vert.glsl","assets/shaders/drone_frag.glsl")
        //Versuch Drohnen-Shader mit Tron shader zu verbinden. Trotzdem keine Drohne...
        //droneShader= ShaderProgram("assets/shaders/tron_vert.glsl","assets/shaders/drone_frag.glsl")
        cloudShader= ShaderProgram("assets/shaders/cloud_vert.glsl","assets/shaders/cloud_frag.glsl")

        bigStaticShader= ShaderProgram("assets/shaders/bigVertexShader.glsl", "assets/shaders/tron_frag.glsl")
        bigDroneShader=ShaderProgram("assets/shaders/bigVertexShader.glsl","assets/shaders/drone_frag.glsl")
        //bigCloudShader= ShaderProgram("assets/shaders/tron_frag.glsl","assets/shaders/cloud_frag.glsl")

        //initial opengl state
        //glClearColor(0.6f, 1.0f, 1.0f, 1.0f); GLError.checkThrow()
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f); GLError.checkThrow()
        //glDisable(GL_CULL_FACE); GLError.checkThrow()
        //glEnable(GL_CULL_FACE)
        //glFrontFace(GL_CCW)
        //glCullFace(GL_BACK)
        glEnable(GL_DEPTH_TEST); GLError.checkThrow()
        glDepthFunc(GL_LESS); GLError.checkThrow()


        //AttributeVertex definieren
        val stride = 8 * 4
        val cloudStride=6*4

        val vertexAttributePosition = VertexAttribute(3, GL_FLOAT, stride, 0)
        val vertexAttributeTexture = VertexAttribute(2, GL_FLOAT, stride, 3*4)
        val vertexAttributeColor = VertexAttribute(3, GL_FLOAT, stride, 5*4)

        val cloudVertexAttributePosition=VertexAttribute(3, GL_FLOAT,cloudStride,0)
        val cloudVertexAttributeNormals=VertexAttribute(3, GL_FLOAT,cloudStride,3*4)

        //Attribute zusammenfügen
        val vertexAttributes = arrayOf(vertexAttributePosition, vertexAttributeTexture, vertexAttributeColor)
        val cloudVertexAttributes= arrayOf(cloudVertexAttributePosition,cloudVertexAttributeNormals)

        //Material
         //laden
        val emitTex = Texture2D("assets/textures/ground_emit.png", true)
        val diffTex = Texture2D("assets/textures/ground_diff.png", true)
        val specTex = Texture2D("assets/textures/ground_spec.png", true)

        val droneEmTex = Texture2D("assets/textures/drone_Metallic.png", true)
        val droneMeTex = Texture2D("assets/textures/drone_Emission.png", true)
        val droneRoTex = Texture2D("assets/textures/drone_Roughness.png", true)
        val droneAlTex = Texture2D("assets/textures/drone_Albedo.png", true)
        val droneNoTex = Texture2D("assets/textures/drone_Normal.png", true)
        val droneOcTex = Texture2D("assets/textures/drone_Occlusion.png", true)

        //erzeuegn
        val groundMaterial = Material(diffTex, emitTex, specTex, 60.0f, Vector2f(64.0f,64.0f))
         //TODO: Material richtig auf die Drohne laden :D Shader mit mehr Texturvariablen? neues Material?
        val droneMaterial=Material(droneMeTex,droneEmTex,droneRoTex,60.0f, Vector2f(64.0f,64.0f))
        //val droneMaterial=droneMaterial(droneAlTex,droneEmTex,droneMeTex,droneNoTex,droneOcTex,droneRoTex,60.0f, Vector2f(64.0f,64.0f))

        //Texturparameter für Objektende
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)     //Linear = zwischen farbwerten interpolieren
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        //TODO: Auch Prameter setzen müssen für Drohne? Eigentlich nicht, oder?

        //Mesh erzeugen
        groundMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, groundMaterial)
        droneMesh= Mesh(objMeshDrone.vertexData,objMeshDrone.indexData,vertexAttributes,droneMaterial)
        //Material für die Drohne. Ob es funktioniert weiß ich nicht, da die Drohne ständig unsichbar ist...
        //droneMesh= Mesh(objMeshDrone.vertexData,objMeshDrone.indexData,vertexAttributes,null, droneMaterial)
        cloudMesh=Mesh(objMeshCloud.vertexData,objMeshCloud.indexData,cloudVertexAttributes)

        //Meshes zu Randerable hinzufügen
        groundRend.meshList.add(groundMesh)
        droneRend.meshList.add(droneMesh)
        cloudRend.meshList.add(cloudMesh)

        //Bike skalieren
        cycleRend.scaleLocal(Vector3f(0.8f))

        //TODO: Drohne steht seitlich? Warum?
        //TODO: Ist die Drohne für den Shader immer noch zu grpß oder warum sieht man sie nicht?
        // Muss aber da sein, man kann sie bewegen :D 1st Person wäre jetzt einfacher :D
        //Drohne Skalieren/Transformieren
        droneRend.scaleLocal(Vector3f(0.00025f)) //drone ist echt groß :D
        droneRend.translateLocal(Vector3f(0.0f,10000.0f,-1.0f))
        //Rotate funktioniert nicht wirklich? Drohne gedreht, aber auch Kamera und Bewegung verändert...
        //droneRend.rotateAroundPoint(0.0f,Math.toRadians(90.0f),0.0f,droneRend.getWorldPosition())

        //TODO: eventuell einen großen Vertex-Shader????
        //TODO: Cloud ist definitv iw da. Größe muss jetzt noch angepasst werden. bzw. Shaderverarbeitung anpassen?
        cloudRend.scaleLocal(Vector3f(0.0025f))
        cloudRend.translateLocal(Vector3f(0.0f,50.0f,-1.0f))

        tronCamera.parent = cycleRend

        //Kameratransformationen
        tronCamera.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        //Werte für Bike
        tronCamera.translateLocal(Vector3f(0.0f,1.0f,4.0f))
        //Bei drone Werte wegen der Skalierung so hoch
        //tronCamera.translateLocal(Vector3f(0.0f, 1.0f, 4000.0f))

        //Lichtertransformationen
        pointLight = PointLight(tronCamera.getWorldPosition(), Vector3f(1f,1f,0f))
        pointLight.parent = cycleRend


        // Spotlight als Frontlicht setzen
        frontSpotLight = Spotlight(Vector3f(0.0f, 0.0f, -2.0f), Vector3f(1.0f))
        frontSpotLight.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f)
        frontSpotLight.parent = cycleRend

        droneSpot=Spotlight(Vector3f(0.0f, 0.0f, -2.0f), Vector3f(1.0f))
        droneSpot.rotateLocal(Math.toRadians(-10.0f), Math.PI.toFloat(), 0.0f)
        droneSpot.parent = droneRend


    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        //TODO: Problem wegen unterschiedlicher Shader lösen!
/*        droneShader.use()
        droneShader.setUniform("colorChange", Vector3f(1.0f))
        droneRend.render(droneShader)*/

        //shader Benutzung definieren
        staticShader.use()
        //Kamera binden
        tronCamera.bind(staticShader)
        //mesh rendern
         //Eventuell anderer Shader nötig. Wird dargestellt, aber nicht korrekt :D
        staticShader.setUniform("colorChange", Vector3f(1.0f))
        droneRend.render(staticShader)
        staticShader.setUniform("colorChange", Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3))))
        cycleRend.render(staticShader)
        pointLight.bind(staticShader, "byklePoint")
        frontSpotLight.bind(staticShader, "bykleSpot", tronCamera.getCalculateViewMatrix())
        staticShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        groundRend.render(staticShader)
        //So kann man tatsächlich auch die Wolke sehen :D
        staticShader.setUniform("colorChange", Vector3f(1.0f,0.0f,0.0f))
        cloudRend.render(staticShader)

        //TODO: Ja, da ist jetzt was, aber richtig sieht es nicht aus :D bzw. gerade sieht man nichts...
        // nur was mit dem staticShader gerendert wird ist sichtbar???
        //eventuell was mit dem Licht zu tun?
/*        cloudShader.use()
        cloudShader.setUniform("colorChange", Vector3f(1.0f))
        cloudRend.render(cloudShader)*/

/*        //TODO: Ja, ein großer VertexShader funktioniert auch nicht besser, Wolke kann den eh nicht nutzen!?!
        bigDroneShader.use()
        droneRend.render(bigDroneShader)
        bigStaticShader.use()
        tronCamera.bind(bigStaticShader)
        bigStaticShader.setUniform("colorChange", Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3))))
        cycleRend.render(bigStaticShader)
        pointLight.bind(bigStaticShader, "byklePoint")
        frontSpotLight.bind(bigStaticShader, "bykleSpot", tronCamera.getCalculateViewMatrix())
        bigStaticShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        groundRend.render(bigStaticShader)
        //bigCloudShader.use()
        //cloudRend.render(bigCloudShader)*/

    }



    fun update(dt: Float, t: Float) {
        //Farbe des Motorads wird verändert in Abhängigkeit der Zeit mit sinuswerten
        pointLight.lightColor = Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3)))
        //Bewegung des Motorrads
         //Für Drohnensuche :D
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)){
            cycleRend.translateLocal(Vector3f(0.0f,-5.0f*dt,0.0f))
        }
        if (window.getKeyState(GLFW_KEY_SPACE)){
            cycleRend.translateLocal(Vector3f(0.0f,5.0f*dt,0.0f))
        }
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

/*
    fun update(dt: Float, t: Float) {
        //TODO: FLughöhe der Drohne wie verändern?
        //Bewegung der Drohne
        //Drohne sinkt ab
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)){
            droneRend.translateLocal(Vector3f(0.0f,-5000.0f*dt,0.0f))
        }
        //Drohne steigt auf
        if (window.getKeyState(GLFW_KEY_SPACE)){
            droneRend.translateLocal(Vector3f(0.0f,5000.0f*dt,0.0f))
        }
        if(window.getKeyState(GLFW_KEY_W)){
            //z-Wert muss je nach drone-Größe angepasst werden
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, -5000*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }
        if(window.getKeyState(GLFW_KEY_S)){
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, 5000*dt))
            if(window.getKeyState(GLFW_KEY_A)){
                droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            }
            if(window.getKeyState(GLFW_KEY_D)){
                droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            }
        }
    }
*/

    fun onKey(key: Int, scancode: Int, action: Int, mode: Int) {}

    fun onMouseMove(xpos: Double, ypos: Double) {
        //Bewegung in x Richtung durch Differenz zwischen alter und neuer Position
        var deltaX : Double = xpos - oldMousePosX
        var deltaY : Double = ypos - oldMousePosY
        oldMousePosX = xpos
        oldMousePosY = ypos

        if(pruefBoolean){
            tronCamera.rotateAroundPoint(0.0f, Math.toRadians(deltaX.toFloat()*0.05f), 0.0f, Vector3f(0.0f))
            tronCamera.rotateAroundPoint(Math.toRadians(deltaY.toFloat()*-0.05f),0.0f,0.0f, Vector3f(0.0f))
            //Hier Position der Drohne anpassen, bezüglich der Höhe? rotate oder translate? oder anders?
        }
        pruefBoolean = true
    }

    fun cleanup() {}
}
