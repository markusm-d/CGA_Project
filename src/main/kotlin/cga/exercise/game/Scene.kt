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
import org.joml.Math
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*


/**
 * Created by Fabian on 16.09.2017.
 */
class Scene(private val window: GameWindow) {
    private val staticShader: ShaderProgram
    private val droneShader:ShaderProgram
    private val cloudShader:ShaderProgram
    private val ringShader:ShaderProgram


    //ObjectLoader

     //Object laden
    private val resGround : OBJLoader.OBJResult = OBJLoader.loadOBJ("assets/models/ground.obj")
    private val resCloud:OBJLoader.OBJResult=OBJLoader.loadOBJ("assets/cloud/cloud.obj")
    private val resRing:OBJLoader.OBJResult=OBJLoader.loadOBJ("assets/ring/checkpoint ring2.obj")

     //Mesh für die Daten von Vertex und Index laden
    private val objMeshGround : OBJLoader.OBJMesh = resGround.objects[0].meshes[0]
    private val objMeshCloud:OBJLoader.OBJMesh=resCloud.objects[0].meshes[0]
    private val objMeshRing:OBJLoader.OBJMesh=resRing.objects[0].meshes[0]

    //Meshes
    private var groundMesh : Mesh
    private val cloudMesh:Mesh

    private var cycleRend = ModelLoader.loadModel("assets/Light Cycle/Light Cycle/HQ_Movie cycle.obj", Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private var droneRend = ModelLoader.loadModel("assets/drone/drone.obj",Math.toRadians(0.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private var ringRend=ModelLoader.loadModel("assets/ring/checkpoint ring2.obj",Math.toRadians(0.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private var ring1Rend=ModelLoader.loadModel("assets/ring/checkpoint ring2.obj",Math.toRadians(-90.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private var ring2Rend=ModelLoader.loadModel("assets/ring/checkpoint ring2.obj",Math.toRadians(0.0f),Math.toRadians(0.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")

    //Renderables
    private var groundRend = Renderable()
    private val cloudRend= Renderable()

    //Camera
    private var tronCamera  = TronCamera()

    //Lights anlegen
    private var pointLight = PointLight(Vector3f(), Vector3f())
    private var frontSpotLight = Spotlight(Vector3f(), Vector3f())

    private var oldMousePosX : Double = -1.0
    private var oldMousePosY : Double = -1.0
    private var pruefBoolean : Boolean = false

    //scene setup
    init {
        staticShader = ShaderProgram("assets/shaders/tron_vert.glsl", "assets/shaders/tron_frag.glsl",0)
        droneShader= ShaderProgram("assets/shaders/drone_vert.glsl","assets/shaders/drone_frag.glsl",1)
        cloudShader= ShaderProgram("assets/shaders/cloud_vert.glsl","assets/shaders/cloud_frag.glsl",2)
        ringShader= ShaderProgram("assets/shaders/ring_vert.glsl","assets/shaders/ring_frag.glsl",3)

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

        val cloudTex=Texture2D("assets/cloud/white.tga",true)

        //erzeuegn
        val groundMaterial = Material(diffTex, emitTex, specTex, 60.0f, Vector2f(64.0f,64.0f))
        val cloudMaterial=Material(cloudTex,cloudTex,cloudTex,60.0f, Vector2f(64.0f,64.0f))

        //Texturparameter für Objektende
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)     //Linear = zwischen farbwerten interpolieren
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        //Mesh erzeugen
        groundMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, groundMaterial)
        cloudMesh=Mesh(objMeshCloud.vertexData,objMeshCloud.indexData,cloudVertexAttributes, cloudMaterial)


        //Meshes zu Randerable hinzufügen
        groundRend.meshList.add(groundMesh)
        cloudRend.meshList.add(cloudMesh)


        //Bike skalieren
        cycleRend.scaleLocal(Vector3f(0.8f))

        //Drohne Skalieren/Transformieren
        droneRend.scaleLocal(Vector3f(0.00025f))
        droneRend.translateLocal(Vector3f(0.0f,10000.0f,-1.0f))

        ringRend.scaleLocal(Vector3f(0.00025f))
        ringRend.translateLocal(randomPosition())
        ring1Rend.scaleLocal(Vector3f(0.00025f))
        ring1Rend.translateLocal(randomPosition())
        ring2Rend.scaleLocal(Vector3f(0.00025f))
        ring2Rend.translateLocal(randomPosition())

        cloudRend.scaleLocal(Vector3f(0.0025f))
        cloudRend.translateLocal(Vector3f(500.0f,50.0f,-1.0f))

        tronCamera.parent = droneRend

        //Kameratransformationen
        tronCamera.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        //Werte für Bike
        //tronCamera.translateLocal(Vector3f(0.0f,1.0f,4.0f))
        //Bei drone Werte wegen der Skalierung so hoch
        tronCamera.translateLocal(Vector3f(0.0f, 1.0f, 4000.0f))

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
        tronCamera.bind(droneShader)
        droneShader.setUniform("colorChange", Vector3f(1.0f,1.0f,1.0f))
        droneRend.render(droneShader)

        ringShader.use()
        tronCamera.bind(ringShader)
        ringShader.setUniform("colorChange", Vector3f(0.0f,1.0f,1.0f))
        ringRend.render(ringShader)
        ringShader.setUniform("colorChange", Vector3f(1.0f,1.0f,1.0f))
        ring1Rend.render(ringShader)
        ringShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        ring2Rend.render(ringShader)

/*        //shader Benutzung definieren
        staticShader.use()
        //Kamera binden
        tronCamera.bind(staticShader)
        //mesh rendern
        staticShader.setUniform("colorChange", Vector3f(1.0f))
        staticShader.setUniform("colorChange", Vector3f(abs(sin(t)),abs(sin(t/2)),abs(sin(t/3))))
        cycleRend.render(staticShader)
        pointLight.bind(staticShader, "byklePoint")
        frontSpotLight.bind(staticShader, "bykleSpot", tronCamera.getCalculateViewMatrix())
        staticShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        groundRend.render(staticShader)*/

        cloudShader.use()
        tronCamera.bind(cloudShader)
        cloudShader.setUniform("colorChange", Vector3f(1.0f))
        cloudRend.render(cloudShader)

    }


    fun randomPosition(): Vector3f {
        var randomPositionX = (-10000..10000).random()
        var randomPositionY = (-10000..10000).random()
        var randomPositionZ = (-10000..10000).random()
        return Vector3f(randomPositionX.toFloat(),randomPositionY.toFloat(),randomPositionZ.toFloat())
    }
    //Theorie: Position von zwei Objekten abfragen und wenn die gleich ist,
    // dann wird die Drohne an eine zufällige Position gesetzt
    //TODO: komplette Wolkentextur muss iw erkannt werden, um dann mit der Drohne auch richtig zu kollidieren. Eventuell iw Objektgröße abfragen?
    fun collisionDetectionCloud(drone:Renderable,cloud:Renderable){
          //so passiert nix
          if (drone.getPosition()==cloud.getPosition()){
          //so macht der das immmer???
 /*       if(drone.getXAxis()==cloud.getXAxis()){
            if (drone.getYAxis()==cloud.getYAxis()){
                if (drone.getZAxis()==cloud.getZAxis()){

                }
            }*/
            val randomPositionX = (-10000..10000).random()
            val randomPositiony = (-10000..10000).random()
            val randomPositionz = (-10000..10000).random()
            //Drohne wird bei Wolkenkollision an einen zufälligen PLatz gesetzt
            droneRend.translateLocal(Vector3f(randomPositionX.toFloat(),randomPositiony.toFloat(), randomPositionz.toFloat()))
        }
    }

    //Kollision für Ringe-->sollen dann gelöscht werden
    fun collisionDetectionRing(drone:Renderable,ring:Renderable){
        //Positionsabfrage und Vergleich

        //Löschen des Ringes

    }

    fun cloudMovement(cloud: Renderable/*, stop:Float*/){
        //SO dreht sich die Wolke zumindest schonmal auf der Stelle ;)
        cloud.rotateAroundPoint(Math.toRadians(1.0f),0.0f,0.0f,cloud.getPosition())
/*        val tranform=cloud.getPosition()
        val increment=1.0f
        //Bewegung lässt sich nicht stoppen :D
        if (tranform.x<stop) {
            cloud.translateLocal(Vector3f(tranform.x + increment, tranform.y, tranform.z))
        }else{
            cloud.translateLocal(Vector3f(tranform.x - 10.0f, tranform.y, tranform.z))
        }*/
    }

/*    fun update(dt: Float, t: Float) {
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
    }*/

    fun update(dt: Float, t: Float) {
        cloudMovement(cloudRend)
        //Bewegung der Drohne
        //Drohne sinkt ab
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)){
            droneRend.translateLocal(Vector3f(0.0f,-5000.0f*dt,0.0f))
            collisionDetectionCloud(droneRend,cloudRend)
        }
        //Drohne steigt auf
        if (window.getKeyState(GLFW_KEY_SPACE)){
            droneRend.translateLocal(Vector3f(0.0f,5000.0f*dt,0.0f))
            collisionDetectionCloud(droneRend,cloudRend)
        }
        if(window.getKeyState(GLFW_KEY_W)){
            //z-Wert muss je nach drone-Größe angepasst werden
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, -5000*dt))
            collisionDetectionCloud(droneRend,cloudRend)
        }
        if(window.getKeyState(GLFW_KEY_S)){
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, 5000*dt))
            collisionDetectionCloud(droneRend,cloudRend)
        }
        if(window.getKeyState(GLFW_KEY_A)){
            droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            collisionDetectionCloud(droneRend,cloudRend)
        }
        if(window.getKeyState(GLFW_KEY_D)){
            droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            collisionDetectionCloud(droneRend,cloudRend)
        }
    }

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
