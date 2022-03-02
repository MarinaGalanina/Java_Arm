import java.applet.Applet;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.swing.*;
import java.awt.*;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Transform3D;;
import javax.vecmath.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import javax.vecmath.Color3f;
import java.io.IOException;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;


public class TeksturyApp extends Applet implements  KeyListener, ActionListener
{

    // robot object
    private Robot r;

    // objects responsible for positioning and moving the ball
    private TransformGroup tg;
    private TransformGroup tg_primitive;
    private Transform3D t3d_primitive;
    private BranchGroup primitives = new BranchGroup();
    private boolean isPrimitive = false;

    // object responsible for collision detection with the ball
    private CollisionDetectorGroup cdGroup;

    // objects needed to create a scene
    private BranchGroup scene;
    private Transform3D observer_transform;
    private SimpleUniverse simpleU;
    private TransformGroup alls = new TransformGroup();

    // current robot parameters
    private int tid = 0;  // current active joint
    private double[] degs = new double[] { 0, 0, 0, 0, 0, 0}; //current angle position
    // maximum arm swing in radians
    private double[] limitation = new double [] {-3.0943, 3.0943, -1.5, 0.1, -1, 1.15, -3.316, 3.316, -2.16, 2.16, -6.2832, 6.2832}; // in radians


    // variables responsible for interactions with keyboard buttons
    private boolean[] keys;
    private boolean checked_a = true;
    private boolean checked_s = false;
    private boolean permission = false;

    private Timer timer;

    // Files
    File moves_record = new File("moves.txt");
    FileWriter myWriter;
    boolean saves = false;

    // sounds varaibles
    private boolean is_play = false;
    private Sounds crash_sound = new Sounds("sound/crash.wav", false);
    private Sounds background_sound = new Sounds("sound/background.wav", true);

    // GUI

    // BUTTONS
    private Button sound_button = new Button("Sound");
    private Button start_button = new Button("Start");
    private Button stop_button = new Button("Stop");
    private Button instruction_button = new Button("Instruction");
    private Button send_angles = new Button("Send");

    private Button start_record_button = new Button("Strart record");
    private Button stop_record_sound_button = new Button("Stop record");
    private Button moves_button = new Button("Play");

    // LABELS
    private JLabel limitation_1 = new JLabel("phi: " + rad_to_degs(limitation[0]) + " - "  + rad_to_degs(limitation[1]));
    private JLabel limitation_2 = new JLabel("phi: " + rad_to_degs(limitation[2]) + " - "  + rad_to_degs(limitation[3]));
    private JLabel limitation_3 = new JLabel("phi: " + rad_to_degs(limitation[4]) + " - "  + rad_to_degs(limitation[5]));
    private JLabel limitation_4 = new JLabel("phi: " + rad_to_degs(limitation[6]) + " - "  + rad_to_degs(limitation[7]));
    private JLabel limitation_5 = new JLabel("phi: " + rad_to_degs(limitation[8]) + " - "  + rad_to_degs(limitation[9]));
    private JLabel limitation_6 = new JLabel("phi: " + rad_to_degs(limitation[10]) + " - " + rad_to_degs(limitation[11]));


    // TEXT FILEDS
    private JTextField angle[] =new JTextField[6];

    private JTextField angle_0 = new JTextField(3);
    private JTextField angle_1 = new JTextField(3);
    private JTextField angle_2 = new JTextField(3);
    private JTextField angle_3 = new JTextField(3);
    private JTextField angle_4 = new JTextField(3);
    private JTextField angle_5 = new JTextField(3);


    public TeksturyApp() throws ParserConfigurationException, IOException {

        setLayout(new BorderLayout());

        // graphics card settings
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        // canvas
        Canvas3D canvas3D = new Canvas3D(config);

        // adding canvas in the center of the screen
        add("Center", canvas3D);
        // waiting for keys to be pressed
        canvas3D.addKeyListener(this);

        // initialization of 10 keys
        keys = new boolean[10];
        timer = new Timer(10, this);

        // GUI deployment function
        create_GUI();

        // listening for button presses
        sound_button.addActionListener(this);
        start_button.addActionListener(this);
        stop_button.addActionListener(this);
        instruction_button.addActionListener(this);
        send_angles.addActionListener(this);
        start_record_button.addActionListener(this);
        stop_record_sound_button.addActionListener(this);
        moves_button.addActionListener(this);



        simpleU = new SimpleUniverse(canvas3D);

        scene = createScene();

        BoundingSphere bounds = new BoundingSphere(new Point3d(0,0,0), 1000);

        // adding camera movement
        OrbitBehavior orbit = new OrbitBehavior(canvas3D, OrbitBehavior.REVERSE_ALL);
        orbit.setZoomFactor(-1d);

        orbit.setSchedulingBounds(bounds);
        ViewingPlatform vp = simpleU.getViewingPlatform();
        vp.setViewPlatformBehavior(orbit);

        simpleU.getViewer().getView().setBackClipDistance(10000);

        // initial viewpoint setting
        observer_transform = new Transform3D();
        observer_transform.set(new Vector3f(0f, 0.5f, 18.0f));
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(observer_transform);
        simpleU.addBranchGraph(scene);


    }

    @Override
    public void keyTyped(KeyEvent e) {;}

    @Override
    public void keyPressed(KeyEvent e)
    {

        switch(e.getKeyCode()) {
            case KeyEvent.VK_1:
                keys[0] = true;
                break;
            case KeyEvent.VK_2:
                keys[1] = true;
                break;
            case KeyEvent.VK_3:
                keys[2] = true;
                break;
            case KeyEvent.VK_4:
                keys[3] = true;
                break;
            case KeyEvent.VK_5:
                keys[4] = true;
                break;
            case KeyEvent.VK_6:
                keys[5] = true;
                break;
            case KeyEvent.VK_LEFT:
                keys[6] = true;
                break;
            case KeyEvent.VK_RIGHT:
                keys[7] = true;
                break;
            case KeyEvent.VK_A:
                keys[8] = true;
                break;
            case KeyEvent.VK_S:
                keys[9] = true;
                break;
            case KeyEvent.VK_I:
                show_instruction("tekst/instrukcja.txt");
                break;
            case KeyEvent.VK_R:
                reset_primitive();
                break;
            case KeyEvent.VK_Y:
                observer_transform.set(new Vector3f(0f, 0.5f, 18.0f));
                simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(observer_transform);
                break;
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

            switch(e.getKeyCode()) {
                case KeyEvent.VK_1:
                    keys[0] = false;
                    break;
                case KeyEvent.VK_2:
                    keys[1] = false;
                    break;
                case KeyEvent.VK_3:
                    keys[2] = false;
                    break;
                case KeyEvent.VK_4:
                    keys[3] = false;
                    break;
                case KeyEvent.VK_5:
                    keys[4] = false;
                    break;
                case KeyEvent.VK_6:
                    keys[5] = false;
                    break;
                case KeyEvent.VK_LEFT:
                    keys[6] = false;
                    break;
                case KeyEvent.VK_RIGHT:
                    keys[7] = false;
                    break;
                case KeyEvent.VK_A:
                    keys[8] = false;
                    break;
                case KeyEvent.VK_S:
                    keys[9] = false;
                    break;
            }
    }

    // program response to user interaction
    @Override
    public void actionPerformed(ActionEvent e)
    {
        // listening for buttons
        if(e.getSource() == start_button)
        {
            // permissions the robot to move
            permission = true;
            if(!is_play) {
                background_sound.play();
                is_play =! is_play;
            }

            // Starts the clock
            if (!timer.isRunning())
            {
                timer.start();
            }
        }
        else if(e.getSource() == stop_button)
        {
            // unable the robot to move
            permission = false;

            background_sound.stop();
            is_play = false;

            // Stops the clock
            if (timer.isRunning())
            {
                timer.stop();
            }
        }
        // Displays instructions in a new window
        else if(e.getSource() == instruction_button)
        {
            // the instruction is read from a text file
            show_instruction("tekst/instrukcja.txt");
        }


        // set the angles for inverse kinematics
        //reads text from a text field, converts to double,
        // converts degrees to radians and sets the angle
        else if(e.getSource() == send_angles)
        {
            for(int i =0; i <= 5; i++)
            {
                String ang = angle[i].getText().toString();
                double ang_ = Double.parseDouble(ang);
                degs[i] = degs_to_rad(ang_);

                // checking if the specified angle is within limits
                // if not, it sets the limit value of the range
                if(degs[i] > limitation[2 * i + 1])
                    r.set_angles(limitation[2 * i + 1], i);
                else if(degs[i] < limitation[2 * i])
                    r.set_angles(limitation[2 * i], i);
                else
                    r.set_angles(degs[i], i);
            }
        }

        else if(e.getSource() == start_record_button)
        {
            saves = true;
            try {
                myWriter = new FileWriter("moves.txt");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        else if(e.getSource() == stop_record_sound_button)
        {
            saves = false;
            try {
                myWriter.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        else if(e.getSource() == moves_button)
        {
            saves = false;
            try {
                myWriter.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

            robot_moves("moves.txt");

        }

        // If the start button is pressed
        if(permission)
        {
            if(e.getSource() == sound_button)
            {
                if(is_play)
                    background_sound.stop();
                else
                    background_sound.play();
                is_play =! is_play;
            }

            // change joint
            if (keys[0])
                tid = 0;
            if (keys[1] )
                tid = 1;
            if (keys[2])
                tid = 2;
            if (keys[3])
                tid = 3;
            if (keys[4])
                tid = 4;
            if (keys[5])
                tid = 5;

            if (keys[6])    // VK_LEFT
            {
                if (degs[tid] > limitation[2 * tid])
                {
                    degs[tid] -= Math.PI/60;
                    if(saves)
                    {
                        try {
                            myWriter.write(degs[0] + " " + degs[1] +" " + degs[2] + " " + degs[3] + " " + degs[4] + " " + degs[5] + "\n");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }

            if (keys[7])    // VK_RIGHT
            {
                if (degs[tid] < limitation[2 * tid + 1])
                {
                    degs[tid] += Math.PI/60;
                    if(saves) {
                        try {
                            myWriter.write(degs[0] + " " + degs[1] +" " + degs[2] + " " + degs[3] + " " + degs[4] + " " + degs[5] + "\n");
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                }
            }

            // If the robot touches the ball and button "a" is pressed
            if(keys[8] && cdGroup.isInCollision() && !isPrimitive && checked_a)
            {
                checked_a = false;
                checked_s = true;
                isPrimitive = true;

                try
                {
                    Thread.sleep(10);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

                // remove the primitive from the scene and add it to the robot
                alls.removeChild(primitives);
                r.add_primitive(primitives);

                // setting the ball in the robot effector
                t3d_primitive.setTranslation(new Vector3d(0,1.85,2.27));
                tg_primitive.setTransform(t3d_primitive);

            }

            // if the ball is in the effector and the "s" button is pressed
            else if(keys[9] && isPrimitive && checked_s)
            {
                crash_sound.play();

                checked_s = false;
                checked_a = true;
                isPrimitive = false;

                // detach the ball from the robot and add it to the scene
                r.remove_primitive();
                alls.addChild(primitives);


                // putting a point where the ball should fall
                double distance = r.calc_distance();
                t3d_primitive.setTranslation(new Vector3d(-distance*Math.sin(degs[0]),distance*Math.cos(degs[0]),0.33));
                tg_primitive.setTransform(t3d_primitive);
            }

            // update the angles in the robot
            if(tid == 0) {
                r.set_angles(degs[0], 0);
            }
            else if(tid == 1) {
                r.set_angles(degs[1], 1);
            }
            else if(tid == 2) {
                r.set_angles(degs[2], 2);
            }
            else if(tid ==3) {
                r.set_angles(degs[3], 3);
            }
            else if(tid == 4) {
                r.set_angles(degs[4], 4);
            }
            else if(tid == 5) {
                r.set_angles(degs[5], 5);
            }
        }
    }

    public  BranchGroup createScene() throws ParserConfigurationException, IOException {


        BranchGroup wezel_scena = new BranchGroup();


        // create robot
        r = new Robot(wezel_scena, "data/Motoman_MH5F.xml");

        // It is possible to simulate a different robot model.
        //To do this, comment out the previous robot and uncomment the line below

        //r = new Robot(wezel_scena, "data/Staubli_TX40.xml");


        // function to create a scene lighting
        createLight(wezel_scena);

        // function creating ground
        wezel_scena.addChild(create_ground());

        // creating and adding a ball
        primitives = createPrimitives();
        alls.addChild(primitives);


        // putting the ball in the right position
        Transform3D transform = new Transform3D();
        Transform3D rotate = new Transform3D();
        rotate.rotY(Math.PI/2);
        transform.mul(rotate, transform);
        rotate.rotZ(Math.PI/2);
        transform.mul(rotate, transform);
        alls.setTransform(transform);

        // permission to manipulate with the ball
        alls.setCapability(BranchGroup.ALLOW_DETACH);
        alls.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        alls.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        alls.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        wezel_scena.addChild(alls);

        wezel_scena.compile();

        return wezel_scena;
    }


    // function that describes the appearance and material of the ball
    private Sphere createSphere(float radius)
    {
        // coloring and shading
        Appearance myAppearance = new Appearance();
        ColoringAttributes coloringAttributes = new ColoringAttributes(new Color3f(100f, 0.3f, 0.5f), ColoringAttributes.NICEST);
        myAppearance.setColoringAttributes(coloringAttributes);

        // setting up the material and the light reflection on it
        Material material = new Material();
        material.setDiffuseColor(0.9f,0.3f,0.3f);
        material.setShininess(50f);

        myAppearance.setMaterial(material);

        // created spheres
        Sphere sphere = new Sphere(radius, myAppearance);
        BoundingSphere bounds2 = new BoundingSphere(new Point3d(0,0,0), 0.0002);
        sphere.setBounds(bounds2);

        return sphere;

    }

    private BranchGroup createPrimitives()
    {
        // Create a transform group node to scale and position the object.
        BranchGroup objRoot = new BranchGroup();

        //  permission to manipulate with the ball
        tg = new TransformGroup();
        tg.setCapability(BranchGroup.ALLOW_DETACH);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        tg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        objRoot.setCapability(BranchGroup.ALLOW_DETACH);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        objRoot.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

        tg_primitive = new TransformGroup();
        t3d_primitive = new Transform3D();
        tg_primitive.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // giving the ball a starting position
        t3d_primitive.setTranslation(new Vector3d(1.3, 0.5, 0.33));
        tg_primitive.setTransform(t3d_primitive);

        Sphere primitive = createSphere(0.3f);

        // setting the ball collision volume
        BoundingSphere bounds2 = new BoundingSphere(new Point3d(0,0,0), 0.3);
        primitive.setBoundsAutoCompute(false);
        primitive.setBounds(bounds2);
        primitive.setCollisionBounds(bounds2);

        tg_primitive.addChild(primitive);

        // setting a collision detector on the ball
        cdGroup = new CollisionDetectorGroup(tg_primitive);
        cdGroup.setSchedulingBounds(bounds2);


        tg.addChild(tg_primitive);
        tg.addChild(cdGroup);

        objRoot.addChild(tg);
        objRoot.compile();

        return objRoot;
    }

    // creates surfaces in the form of flat planes
    public TransformGroup create_ground()
    {
        TransformGroup two_side_ground = new TransformGroup();

        // Downloading a grass texture from a file
        Appearance wyglad_ziemia = new Appearance();
        TextureLoader loader = new TextureLoader("obrazki/trawka.gif",null);
        ImageComponent2D image = loader.getImage();

        Texture2D trawka = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA,
                image.getWidth(), image.getHeight());

        trawka.setImage(0, image);
        trawka.setBoundaryModeS(Texture.WRAP);
        trawka.setBoundaryModeT(Texture.WRAP);

        wyglad_ziemia.setTexture(trawka);


        // setting the dimensions and center of the ground
        Point3f[]  coords = new Point3f[4];
        for(int i = 0; i< 4; i++)
            coords[i] = new Point3f();

        Point2f[]  tex_coords = new Point2f[4];
        for(int i = 0; i< 4; i++)
            tex_coords[i] = new Point2f();

        coords[0].y = 0.0f;
        coords[1].y = 0.0f;
        coords[2].y = 0.0f;
        coords[3].y = 0.0f;

        coords[0].x = 10f;
        coords[1].x = 10f;
        coords[2].x = -10f;
        coords[3].x = -10f;

        coords[0].z = 10f;
        coords[1].z = -10f;
        coords[2].z = -10f;
        coords[3].z = 10f;

        tex_coords[0].x = 0.0f;
        tex_coords[0].y = 0.0f;

        tex_coords[1].x = 20.0f;
        tex_coords[1].y = 0.0f;

        tex_coords[2].x = 0.0f;
        tex_coords[2].y = 20.0f;

        tex_coords[3].x = 20.0f;
        tex_coords[3].y = 20.0f;

        QuadArray qa_ziemia = new QuadArray(4, GeometryArray.COORDINATES|
                GeometryArray.TEXTURE_COORDINATE_2);
        qa_ziemia.setCoordinates(0,coords);
        qa_ziemia.setTextureCoordinates(0, tex_coords);


        Shape3D ziemia = new Shape3D(qa_ziemia);
        ziemia.setAppearance(wyglad_ziemia);

        QuadArray qa_ziemia2 = new QuadArray(4, GeometryArray.COORDINATES|
                GeometryArray.TEXTURE_COORDINATE_2);
        qa_ziemia2.setCoordinates(0,coords);
        qa_ziemia2.setTextureCoordinates(0, tex_coords);
        two_side_ground.addChild(ziemia);


        // creation of ground geometry
        Shape3D ziemia2 = new Shape3D(qa_ziemia);
        ziemia2.setAppearance(wyglad_ziemia);

        // transform the grass to the right position
        TransformGroup trans_ziemii = new TransformGroup();
        Transform3D przesuniecie_ziemii = new Transform3D();
        przesuniecie_ziemii.set(new Vector3f(0.0f, 0.0f,0.0f));
        przesuniecie_ziemii.rotX(Math.PI);
        trans_ziemii.setTransform(przesuniecie_ziemii);

        trans_ziemii.addChild(ziemia2);
        two_side_ground.addChild(trans_ziemii);

        return two_side_ground;
    }

    // adding two directional lights to the scene
    private void createLight(BranchGroup b)
    {

        DirectionalLight lightA = new DirectionalLight();
        lightA.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
        lightA.setDirection(new Vector3f(5.0f, 0.0f, 3.0f));
        lightA.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        b.addChild(lightA);

        DirectionalLight lightB = new DirectionalLight();
        lightB.setInfluencingBounds(new BoundingSphere(new Point3d(), 10000.0));
        lightB.setDirection(new Vector3f(-5.0f, 0.0f, -3.0f));
        lightB.setColor(new Color3f(1.0f, 1.0f, 1.0f));
        b.addChild(lightB);

    }

    // function reads instructions from a text file
    public String read_instruction(String file)
    {
        BufferedReader reader = null;

        // variable storing instructions
        String message = new String();
        try {
            // use buffered reader to read line by line
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));

            String line = null;


            // read line by line till end of file
            while ((line = reader.readLine()) != null) {
                message += line;
                message += '\n';
            }

        } catch (IOException h) {
            System.err.println("Exception:" + h.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException h) {
                    System.err.println("Exception:" + h.toString());
                }
            }
        }
        return message;
    }

    public void show_instruction(String file)
    {
        // downloading instructions from a file
        String message = read_instruction(file);

        // create a new window with a text field
        JFrame frame = new JFrame("Instruction");
        JTextArea instruction = new JTextArea();

        // writing instructions in a text field
        instruction.append(message);

        // editing of the window by the user is prohibited
        instruction.setEditable(false);

        frame.setLayout(new BorderLayout());
        frame.add(instruction,BorderLayout.CENTER);

        // setting dimensions and visibility of the window
        frame.setSize(500, 550);
        frame.setVisible(true);
    }

    // sets the ball to the default position
    public void reset_primitive()
    {
        // If the ball is held by the robot, it lets go
        if( isPrimitive && checked_s)
        {
            checked_s = false;
            checked_a = true;
            isPrimitive = false;


            try {
                Thread.sleep(10);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            r.remove_primitive();
            alls.addChild(primitives);
        }

        // setting the ball in a default position
        t3d_primitive.setTranslation(new Vector3d(1.5f,0.5f,0.33));
        tg_primitive.setTransform(t3d_primitive);


    }

    private void create_GUI()
    {
        // top panel
        JPanel panelUp = new JPanel();
        panelUp.add(start_button);
        panelUp.add(stop_button);
        panelUp.add(sound_button);
        panelUp.add(instruction_button);

        panelUp.add(start_record_button);
        panelUp.add(stop_record_sound_button);
        panelUp.add(moves_button);

        add(""+"North",panelUp);

        // initialization of variables associated with a text field for inverse kinematics
        angle[0] = angle_0;
        angle[1] = angle_1;
        angle[2] = angle_2;
        angle[3] = angle_3;
        angle[4] = angle_4;
        angle[5] = angle_5;

        // setting a zero angle in each cell
        for(int i=0; i<=5; i++)
            angle[i].setText("0");

            // location of buttons and text fields in the right panel for inverse kinematics

            JPanel panelRight = new JPanel(new GridBagLayout());

            GridBagConstraints constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = new Insets(10, 10, 10, 10);

            constraints.gridx = 0;
            constraints.gridy = 0;
            panelRight.add(limitation_1, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[0], constraints);

            constraints.gridx = 0;
            constraints.gridy = 1;
            panelRight.add(limitation_2, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[1], constraints);

            constraints.gridx = 0;
            constraints.gridy = 2;
            panelRight.add(limitation_3, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[2], constraints);

            constraints.gridx = 0;
            constraints.gridy = 3;
            panelRight.add(limitation_4, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[3], constraints);

            constraints.gridx = 0;
            constraints.gridy = 4;
            panelRight.add(limitation_5, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[4], constraints);


            constraints.gridx = 0;
            constraints.gridy = 5;
            panelRight.add(limitation_6, constraints);

            constraints.gridx = 1;
            panelRight.add(angle[5], constraints);


            constraints.gridx = 0;
            constraints.gridy = 6;
            panelRight.add(send_angles, constraints);


            add("" + "East", panelRight);

    }

    // converting radians to degrees
    private String rad_to_degs(double rad)
    {
        double deg = (rad * 180)/Math.PI;
        return String.format("%4.0f", deg);
    }
    // converting degrees to radians
    private double degs_to_rad(double deg)
    {
        double rad = deg * Math.PI/180;
        return rad;
    }

    // read recorded robot moves from txt file
    private void robot_moves(String file)
    {

        // I do not know the size of the files.
        // The values will be stored in a dynamic array
        ArrayList<float[]> list = new ArrayList<float[]>();
        float[][] ps=null;

        BufferedReader reader = null;

        try {
            // use buffered reader to read line by line
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));


            float x, y, z, x1, x2, x3;
            String line = null;
            String[] numbers = null;
            // read line by line till end of file
            while ((line = reader.readLine()) != null) {
                // split each line based on regular expression having
                // "any digit followed by one or more spaces".

                numbers = line.split("\\d\\s+");


                x = Float.valueOf(numbers[0].trim());
                y = Float.valueOf(numbers[1].trim());
                z = Float.valueOf(numbers[2].trim());
                x1 = Float.valueOf(numbers[2].trim());
                x2 = Float.valueOf(numbers[2].trim());
                x3 = Float.valueOf(numbers[2].trim());

                r.set_angles(x, 0);
                r.set_angles(y, 1);
                r.set_angles(z, 2);
                r.set_angles(x1, 3);
                r.set_angles(x2, 4);
                r.set_angles(x3, 5);


                try
                {
                    Thread.sleep(25);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

            }

        } catch (IOException e) {
            System.err.println("Exception:" + e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Exception:" + e.toString());
                }
            }
        }

    }

    public static void main(String args[]) throws ParserConfigurationException, IOException {
        TeksturyApp animacja = new TeksturyApp();
        animacja.addKeyListener(animacja);
        MainFrame mf = new MainFrame(animacja, 800,600);
    }

}
