package cga.exercise.game


import cga.exercise.components.camera.TronCamera
import cga.exercise.components.geometry.*
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
    private var droneRend = ModelLoader.loadModel("assets/drone/drone.obj",Math.toRadians(0.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")
    private var ringRend=ModelLoader.loadModel("assets/ring/checkpoint ring2.obj",Math.toRadians(0.0f),Math.toRadians(90.0f), 0.0f) ?: throw IllegalArgumentException("Could not load the model")

     //Mesh für die Daten von Vertex und Index laden
    private val objMeshGround : OBJLoader.OBJMesh = resGround.objects[0].meshes[0]
    private val objMeshCloud:OBJLoader.OBJMesh=resCloud.objects[0].meshes[0]

    //Meshes
    //private var groundMesh : Mesh
    private val cloudMesh:Mesh
    private var planeBottomMesh : Mesh
    private var planeBackMesh : Mesh
    private var planeFrontMesh : Mesh
    private var planeLeftMesh : Mesh
    private var planeRightMesh : Mesh
    private var planeTopMesh : Mesh



    //Renderables
    private var groundRend = Renderable()
    private val cloudRend = Renderable()
    private val cloudRend1 = Renderable()
    private val cloudRend2 = Renderable()
    private val cloudRend3 = Renderable()
    private val cloudRend4 = Renderable()

    //Planes
    private var planeBottomRend = Renderable()
    private var planeBackRend = Renderable()
    private var planeFrontRend = Renderable()
    private var planeLeftRend = Renderable()
    private var planeRightRend = Renderable()
    private var planeTopRend = Renderable()


    //Camera
    private var tronCamera  = TronCamera()

    //Mausposition für Bewegung
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

        // Plane Material
        val planeBottomTex = Texture2D("assets/textures/skybox/bottom.jpg", true)
        val planeBackTex = Texture2D("assets/textures/skybox/back.jpg", true)
        val planeFrontTex = Texture2D("assets/textures/skybox/front.jpg", true)
        val planeLeftTex = Texture2D("assets/textures/skybox/left.jpg", true)
        val planeRightTex = Texture2D("assets/textures/skybox/right.jpg", true)
        val planeTopTex = Texture2D("assets/textures/skybox/top.jpg", true)

        //erzeuegn
        //val groundMaterial = Material(diffTex, emitTex, specTex, 60.0f, Vector2f(64.0f,64.0f))
        val cloudMaterial=Material(cloudTex,cloudTex,cloudTex,60.0f, Vector2f(64.0f,64.0f))
        val planeBottomMaterial = Material(planeBottomTex,planeBottomTex,planeBottomTex,60.0f, Vector2f(64.0f,64.0f))
        val planeBackMaterial = Material(planeBackTex,planeBackTex,planeBackTex,60.0f, Vector2f(64.0f,64.0f))
        val planeFrontMaterial = Material(planeFrontTex,planeFrontTex,planeFrontTex,60.0f, Vector2f(64.0f,64.0f))
        val planeLeftMaterial = Material(planeLeftTex,planeLeftTex,planeLeftTex,60.0f, Vector2f(64.0f,64.0f))
        val planeRightMaterial = Material(planeRightTex,planeRightTex,planeRightTex,60.0f, Vector2f(64.0f,64.0f))
        val planeTopMaterial = Material(planeTopTex,planeTopTex,planeTopTex,60.0f, Vector2f(64.0f,64.0f))

        //Texturparameter für Objektende
        emitTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)     //Linear = zwischen farbwerten interpolieren
        diffTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)
        specTex.setTexParams(GL_REPEAT, GL_REPEAT, GL11.GL_LINEAR_MIPMAP_LINEAR, GL_LINEAR)

        //Mesh erzeugen
        //groundMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, groundMaterial)
        cloudMesh=Mesh(objMeshCloud.vertexData,objMeshCloud.indexData,cloudVertexAttributes, cloudMaterial)

        planeBottomMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeBottomMaterial)
        planeBackMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeBackMaterial)
        planeFrontMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes,planeFrontMaterial)
        planeLeftMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeLeftMaterial)
        planeRightMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeRightMaterial)
        planeTopMesh = Mesh(objMeshGround.vertexData, objMeshGround.indexData, vertexAttributes, planeTopMaterial)


        //Meshes zu Randerable hinzufügen
        //groundRend.meshList.add(groundMesh)
        cloudRend.meshList.add(cloudMesh)
        cloudRend1.meshList.add(cloudMesh)
        cloudRend2.meshList.add(cloudMesh)
        cloudRend3.meshList.add(cloudMesh)
        cloudRend4.meshList.add(cloudMesh)

        planeBottomRend.meshList.add(planeBottomMesh)
        planeBackRend.meshList.add(planeBackMesh)
        planeFrontRend.meshList.add(planeFrontMesh)
        planeLeftRend.meshList.add(planeLeftMesh)
        planeRightRend.meshList.add(planeRightMesh)
        planeTopRend.meshList.add(planeTopMesh)


        //Drohne Skalieren/Transformieren
        droneRend.scaleLocal(Vector3f(0.00025f))
        droneRend.translateLocal(Vector3f(0.0f,10000.0f,-1.0f))

        //Ring Skalieren/Tranformieren
        ringRend.scaleLocal(Vector3f(0.00025f))
        ringRend.translateLocal(randomPositionCloud())

        //Wolken Skalieren/Transformieren
        cloudRend.scaleLocal(Vector3f(0.0025f))
        cloudRend.translateLocal(randomPositionCloud())
        cloudRend1.scaleLocal(Vector3f(0.0025f))
        cloudRend1.translateLocal(Vector3f(-100.0f, 50.0f,-100.0f))
        cloudRend2.scaleLocal(Vector3f(0.0025f))
        cloudRend2.translateLocal(randomPositionCloud())
        cloudRend3.scaleLocal(Vector3f(0.0025f))
        cloudRend3.translateLocal(Vector3f(500.0f,50.0f,-1.0f))
        cloudRend4.scaleLocal(Vector3f(0.0025f))
        cloudRend4.translateLocal(randomPositionCloud())

        //Planes rotieren/transformieren
        planeTopRend.rotateLocal(Math.toRadians(180.0f), 0f,0f) //ausrichtung
        planeTopRend.translateLocal(Vector3f(0f,-32.83f,0f)) //höhe

        planeLeftRend.rotateLocal(Math.toRadians(90.0f), Math.PI.toFloat(),0f)
        planeLeftRend.translateLocal(Vector3f(0f,-22.38f,22.38f))

        planeRightRend.rotateLocal(Math.toRadians(-90.0f), 0f,0f)
        planeRightRend.translateLocal(Vector3f(0f,-22.38f,22.38f))


        planeFrontRend.rotateLocal(Math.toRadians(90.0f), 0f,Math.toRadians(90.0f))
        planeFrontRend.translateLocal(Vector3f(0f,-22.38f,-22.38f))

        planeBackRend.rotateLocal(Math.toRadians(90.0f), 0f,Math.toRadians(-90.0f))
        planeBackRend.translateLocal(Vector3f(0f,-22.38f,-22.38f))

        tronCamera.parent = droneRend

        //Kameratransformationen
        tronCamera.rotateLocal(Math.toRadians(-35.0f), 0.0f, 0.0f)
        //Bei drone Werte wegen der Skalierung so hoch
        tronCamera.translateLocal(Vector3f(0.0f, 1.0f, 4000.0f))

    }


    fun render(dt: Float, t: Float) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        droneShader.use()
        tronCamera.bind(droneShader)
        droneShader.setUniform("colorChange", Vector3f(1.0f,1.0f,1.0f))
        droneRend.render(droneShader)

        planeBottomRend.render(droneShader)
        planeTopRend.render(droneShader)
        planeLeftRend.render(droneShader)
        planeRightRend.render(droneShader)
        planeFrontRend.render(droneShader)
        planeBackRend.render(droneShader)

        ringShader.use()
        tronCamera.bind(ringShader)
        ringShader.setUniform("colorChange", Vector3f(0.0f,1.0f,0.0f))
        ringRend.render(ringShader)

        cloudShader.use()
        tronCamera.bind(cloudShader)
        cloudShader.setUniform("colorChange", Vector3f(1.0f))
        cloudRend.render(cloudShader)
        cloudRend2.render(cloudShader)
        cloudRend4.render(cloudShader)
        cloudRend1.render(cloudShader)
        cloudRend3.render(cloudShader)

        //staticShader.use()


    }

    //Wolken Position --> Da sie eine andere Größe haben, brauchen sie eine eigene Funktion
    fun randomPositionCloud(): Vector3f {
        var randomPositionX = (-2000..2000).random()
        var randomPositionY = (0..2000).random()
        var randomPositionZ = (-2000..2000).random()
        return Vector3f(randomPositionX.toFloat(),randomPositionY.toFloat(),randomPositionZ.toFloat())
    }

    fun randomPosition(): Vector3f {
        var randomPositionX = (-10000..10000).random()
        var randomPositionY = (0..10000).random()
        var randomPositionZ = (-10000..10000).random()
        return Vector3f(randomPositionX.toFloat(),randomPositionY.toFloat(),randomPositionZ.toFloat())
    }

    //AllgemeinesPrüfen auf Kollision
    fun collisionCheck(drone:Renderable, object2:Renderable): Float{
        val xDistance=drone.getPosition().x-object2.getPosition().x
        val yDistance=drone.getPosition().y-object2.getPosition().y
        val zDistance=drone.getPosition().z-object2.getPosition().z

        return Math.sqrt((xDistance*xDistance).toDouble()+
                (yDistance*yDistance).toDouble()+(zDistance*zDistance).toDouble()).toFloat()
    }

    //Kollisionsverhalten mit Wolke
    fun collisionDetectionCloud(drone:Renderable,cloud:Renderable){
        //Distanzen prüfen
        if (collisionCheck(drone,cloud)<=0.2){
            //wenn getroffen, Drohne neue Position
            drone.translateLocal(randomPosition())
        }
    }

    //Kollisionverhalten für Ringe
    fun collisionDetectionRing(drone:Renderable,ring:Renderable){
        //Distanze prüfen
        if (collisionCheck(drone,ring)<=0.2){
            ring.translateLocal(randomPosition())
        }
    }

    //Abfrage der einzelnen möglichen Kollisionen
    fun collisionDetection(){
        collisionDetectionRing(droneRend,ringRend)
        collisionDetectionCloud(droneRend,cloudRend)
        collisionDetectionCloud(droneRend,cloudRend1)
        collisionDetectionCloud(droneRend,cloudRend2)
        collisionDetectionCloud(droneRend,cloudRend3)
        collisionDetectionCloud(droneRend,cloudRend4)
    }

    //unterschiedliche Wolkenbewegungen
    fun cloudMoveLeft(cloud: Renderable){
        cloud.translateLocal(Vector3f(cloud.getPosition().x-1.0f,cloud.getPosition().y,cloud.getPosition().z))
    }
    fun cloudMoveRight(cloud: Renderable){
        cloud.translateLocal(Vector3f(cloud.getPosition().x+1.0f,cloud.getPosition().y,cloud.getPosition().z))
    }
    fun cloudMoveRotate(cloud: Renderable, rotation:Vector3f){
        cloud.rotateAroundPoint(rotation.x,rotation.y,rotation.z,cloud.getPosition())
    }

    //zufällige Wahl der Wolkenbewegung
    fun cloudRandomMovement(cloud:Renderable){
        var randomMove = (0..2).random()
        if(randomMove==0){
            cloudMoveRotate(cloud,Vector3f(0.0f,Math.toRadians(1.0f),0.0f))
        }else if (randomMove==1){
            cloudMoveLeft(cloud)
        }else if (randomMove==2){
            cloudMoveRight(cloud)
        }
    }

    fun update(dt: Float, t: Float) {
        //Wolkenbewegung
        cloudRandomMovement(cloudRend1)
        cloudRandomMovement(cloudRend3)
        cloudMoveRotate(cloudRend2,Vector3f(0.0f,Math.toRadians(1.0f),0.0f))
        //Bewegung der Drohne
        //Drohne sinkt ab
        if (window.getKeyState(GLFW_KEY_LEFT_SHIFT)){
            droneRend.translateLocal(Vector3f(0.0f,-5000.0f*dt,0.0f))
            collisionDetection()
        }
        //Drohne steigt auf
        if (window.getKeyState(GLFW_KEY_SPACE)){
            droneRend.translateLocal(Vector3f(0.0f,5000.0f*dt,0.0f))
            collisionDetection()

        }
        if(window.getKeyState(GLFW_KEY_W)){
            //z-Wert muss je nach drone-Größe angepasst werden
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, -5000*dt))
            collisionDetection()

        }
        if(window.getKeyState(GLFW_KEY_S)){
            droneRend.translateLocal(Vector3f(0.0f, 0.0f, 5000*dt))
            collisionDetection()

        }
        if(window.getKeyState(GLFW_KEY_A)){
            droneRend.rotateLocal(0.0f, 2f*dt, 0.0f)
            collisionDetection()

        }
        if(window.getKeyState(GLFW_KEY_D)){
            droneRend.rotateLocal(0.0f, -2f*dt, 0.0f)
            collisionDetection()

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
