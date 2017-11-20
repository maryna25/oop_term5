import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.util.SkyFactory;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.SimpleWaterProcessor;

public class Game extends SimpleApplication {

    private Node mainNode;
    private Geometry water;
    protected Spatial torpedo;
    protected Spatial ship;

    private Boolean moveShip = false;
    private Vector2f prevMousePos = new Vector2f(0.0f,0.0f);

    private float shSpeed = 2f;
    private float tSpeed = 2f;

    MotionPath Tpath;
    MotionEvent Tcontrol;

    protected Vector3f shVector;

    private boolean collided = false;

    public static void main(String[] args) {
        Game app = new Game();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //Initialize all components
        mainNode = new Node();

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-1.0f,-1.0f,-1.0f));

        cam.setLocation(new Vector3f(300,500,300));
        cam.lookAt(new Vector3f(100,0, 100), new Vector3f(0,1,0));
        flyCam.setEnabled(false);

        water = createWater();
        shipInit();
        torpedoInit();

        mainNode.attachChild(ship);
        mainNode.attachChild(torpedo);
        mainNode.addLight(sun);
        mainNode.attachChild(SkyFactory.createSky(getAssetManager(), "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap));
        rootNode.attachChild(mainNode);
        rootNode.attachChild(water);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(!collided)
        {
            if(areCollided())
            {
                collided = true;
            }else{
                //for moving ship
                Vector3f v = calculateShVector();
                double angle;
                if(v.length() == 0 || shVector.length() == 0)
                    angle = 0;
                else
                    angle = Math.acos((v.x * shVector.x + v.z * shVector.z) / (v.length() * shVector.length()));
                if(Double.isNaN(angle))
                    angle = 0;
                shVector.set(v);
                if (moveShip)
                    ship.move(new Vector3f(shSpeed * shVector.x, 0.0f, shSpeed * shVector.z));
                ship.rotate(new Quaternion().fromAngleAxis((float)(angle), Vector3f.UNIT_Y));

                //for moving torpedo
                Vector2f nextPoint = new Vector2f(ship.getLocalTranslation().x - torpedo.getLocalTranslation().x,
                        ship.getLocalTranslation().z - torpedo.getLocalTranslation().z);
                Tpath.addWayPoint(new Vector3f(torpedo.getLocalTranslation().x + nextPoint.x,
                        torpedo.getLocalTranslation().y, torpedo.getLocalTranslation().z + nextPoint.y));
                Tcontrol.play();
            }

        }
        else //if torpedo and ship are collided
        {
            guiNode.detachAllChildren();
            guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
            BitmapText text = new BitmapText(guiFont, false);
            text.setSize(1.5f * guiFont.getCharSet().getRenderedSize());
            text.setColor(ColorRGBA.Red);
            text.setText("Oops. You are dead now.");
            text.setLocalTranslation((cam.getWidth() - text.getLineWidth()) / 2, cam.getHeight(), 0);
            guiNode.attachChild(text);
        }
    }

    private void shipInit(){
        ship = assetManager.loadModel("Models/Ship/ship.obj");
        ship.setLocalTranslation(new Vector3f(-100f,20f,200f));
        Material mat_ship = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_ship.setTexture("ColorMap",
                assetManager.loadTexture("Textures/ship.jpg"));
        ship.setMaterial(mat_ship);
        ship.scale(0.1f);
        shVector = new Vector3f((float)(-1),0,(float)(0));

    }

    private void torpedoInit(){
        torpedo = assetManager.loadModel("Models/Torpedo/torpedo.obj");
        TangentBinormalGenerator.generate(torpedo);
        Material mat_torpedo = new Material(
                assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_torpedo.setTexture("ColorMap",
                assetManager.loadTexture("Textures/Torpedo.jpg"));
        torpedo.setMaterial(mat_torpedo);
        torpedo.scale(5.5f);

        torpedo.setLocalTranslation(new Vector3f(-100f,0f,-100f));

        Tpath = new MotionPath();
        Tpath.addWayPoint(torpedo.getLocalTranslation());
        Vector2f nextPoint = new Vector2f(ship.getLocalTranslation().x, ship.getLocalTranslation().z);
        Tpath.addWayPoint(new Vector3f(nextPoint.x, torpedo.getLocalTranslation().y, nextPoint.y));
        Tpath.setCycle(false);

        Tcontrol = new MotionEvent(torpedo, Tpath);
        Tcontrol.setLookAt(ship.getLocalTranslation(), Vector3f.UNIT_Y);
        Tcontrol.setDirectionType(MotionEvent.Direction.LookAt);
        Tcontrol.setSpeed(tSpeed);
    }

    private Geometry createWater(){
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(mainNode);
        Vector3f waterLocation = new Vector3f(0f,-10f,0f);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        viewPort.addProcessor(waterProcessor);

        waterProcessor.setWaterDepth(400f);
        waterProcessor.setDistortionScale(0.8f);
        waterProcessor.setWaveSpeed(.04f);

        Quad bank = new Quad(6000f,6000f);
        bank.scaleTextureCoordinates(new Vector2f(6.0f,6.0f));

        Geometry w = new Geometry("water", bank);
        w.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        w.setLocalTranslation(-3000f, -10f, 2000f);
        w.setShadowMode(ShadowMode.Receive);
        w.setMaterial(waterProcessor.getMaterial());
        return w;
    }

    private double hypotenuse(Vector3f v){
        return Math.sqrt(Math.pow(v.x, 2) + Math.pow(v.z, 2));
    }

    private Vector3f calculateShVector(){
        //Get 2D cursor coordinates
        Vector2f cursorPos2D = inputManager.getCursorPosition().clone();
        //Convert 2D screen coordinates to their 3D equivalent
        Vector3f cursorPos3D = cam.getWorldCoordinates(new Vector2f(cursorPos2D.x, cursorPos2D.y), 0f).clone();
        //Aim the ray from the clicked 3D location forwards into the scene
        Vector3f direction = cam.getWorldCoordinates(new Vector2f(cursorPos2D.x, cursorPos2D.y), 1f)
                                .subtractLocal(cursorPos3D).normalizeLocal();
        //Collect intersections between ray and all nodes into a results list
        Ray ray = new Ray(cursorPos3D, direction);
        CollisionResults results = new CollisionResults();
        water.collideWith(ray, results);
        Vector3f v = results.getClosestCollision().getContactPoint();
        v.y = 0;

        if(v.x == prevMousePos.x && v.z == prevMousePos.y)
            moveShip = false;
        else moveShip = true;

        prevMousePos.x = v.x;
        prevMousePos.y = v.z;
        //Calculate vector for ship rotation
        return new Vector3f((float)(v.x / hypotenuse(v)), 0, (float)(v.z / hypotenuse(v)));
    }

    private boolean areCollided(){
        if((torpedo.getLocalTranslation().x < ship.getLocalTranslation().x + 5) &&
           (torpedo.getLocalTranslation().x > ship.getLocalTranslation().x - 5) &&
           (torpedo.getLocalTranslation().z < ship.getLocalTranslation().z + 5) &&
           (torpedo.getLocalTranslation().z > ship.getLocalTranslation().z - 5))
                return true;
        return false;
    }
}
