import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

// class describing the robot frame
// It contains the kinematic diagram, the D_H table,
// the appearance of the various moving parts,
// the methods responsible for interacting with the arm and its formation

public class Robot {

    // Class to read a model from an XML file
    private ReadXML xml_reader;

    // cinematic structure
    private TransformGroup leg;
    private TransformGroup box;
    private TransformGroup base;
    private TransformGroup joint1;
    private TransformGroup joint2;
    private TransformGroup joint3;
    private TransformGroup joint4;
    private TransformGroup joint5;
    private TransformGroup joint6;

    // current position of the robot
    private double[] degs = new double[] { 0, 0, 0, 0, 0, 0};

    // maximum swing of individual arms
    private double[] limitation = new double [] {-3.0943, 3.0943, -1.5, 0.1, -1, 1.15, -3.316, 3.316, -2.16, 2.16, -6.2832, 6.2832}; // in radians

    // D-H parameters
    private double[] d = new double[] {0, 0, 0, 0, 0, 0, 0};
    private double[] a = new double[] {0, 0, 0, 0, 0, 0, 0};
    private double[][] DH = new double[][] {d,a};


    // the file names that contain the robot geometry
    private String[] robot_files = new String[7];
    // point clouds of 3d shapes
    private Point3f[][] robot_Coordinate = new Point3f[7][];

    // the type of robot taken from the file
    private double factor;
    private int id;

    // the variables needed to add the primitive to the end of the arm
    private BranchGroup primitive;
    private BranchGroup root;

    // constructor
    public Robot(BranchGroup objRoot, String file) throws ParserConfigurationException
    {
        // XML file initialization
        xml_reader = new ReadXML(file);

        // BranchGroup
        root = objRoot;

        // initialization of the robot
        Robot_init();

    }
    // method responsible for setting the rotation angles
    public void set_angles(double deg, int joint)
    {
        double radians = deg;
       // double radians = degs_to_radians(deg);

        // set the rotation in the selected degree of freedom
        degs[joint] = radians;

        // function that updates the robot's position
        update_position(joint + 1);
    }

    // method adds a primitive to the end of the robot's kinematic chain
    // takes as parameter the BranchGroup in which the primitive is located
    public void add_primitive(BranchGroup p)
    {
        primitive = new BranchGroup();
        primitive = p;
        joint6.addChild(primitive);
    }

    // removes a primitive from a kinematic chain
    public void remove_primitive()
    {
        joint6.removeChild(primitive);
    }

    // the method calculates how far from the base the primitive is supposed to fall after leaving it
    public double calc_distance()
    {
        double distance = -a[3] *Math.sin(degs[1]);
        distance = Math.abs(distance);
        distance = distance + (d[4] + d[6]) * Math.cos(degs[2]) * Math.cos(degs[1]) - (d[4] + d[6]) * Math.sin(degs[1]) * Math.sin(degs[2]);
        distance = distance + a[2];

        return distance;
    }

    // Inside are the functions that make up the entire robot arm
    private void Robot_init()
    {

        // adding angle type table from XML file
        set_angle_type();
        // adding DH table from XML file
        set_DH_parameters();
        // creating a kinematic chain
        create_cinematic_structure();
        // adding shapes 3D to the different parts of the kinematic chain
        create_robot_geometry();
    }

    // adding angle type table from XML file
    private void set_angle_type()
    {
        id = xml_reader.read_angle_type();
    }

    // read DH table from XML file
    private  void set_DH_parameters()
    {
        xml_reader.read_dh_parameters(DH);
    }

    // creating a kinematic chain
    private void create_cinematic_structure()
    {

        // Creating a robot leg
        leg = new TransformGroup();

        Transform3D leg_transform = new Transform3D();
        Transform3D leg_rot = new Transform3D();

        // rotating the robot's leg vertically upward
        leg_rot.rotY(Math.PI/2);
        leg_transform.set(new Vector3f(0.0f,0.352f,0.0f));
        leg_transform.mul(leg_rot);
        leg.setTransform(leg_transform);



        // adding a leg to the BranchGroup
        root.addChild(leg);

        // create basic
        box = new TransformGroup();

        Transform3D box_transform = new Transform3D();
        Transform3D box_rot = new Transform3D();

        // rotating and moving the base to the center of the stage
        box_rot.rotX(-Math.PI/2);
        box_transform.set(new Vector3f(0.0f,0.35f,0.0f));
        box_transform.mul(box_rot);
        box.setTransform(box_transform);

        // adding the ability to edit during the program
        box.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        leg.addChild(box);

        // creating another part of the kinematic chain
        joint1 = new TransformGroup();
        base = new TransformGroup();

        base.setCapability(BranchGroup.ALLOW_DETACH);
        base.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        base.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        base.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);


        // adding the ability to edit during the program
        joint1.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint1.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        box.addChild(base);
        base.addChild(joint1);


        // creating another part of the kinematic chain
        joint2 = new TransformGroup();

        // adding the ability to edit during the program
        joint2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint2.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        joint1.addChild(joint2);

        // creating another part of the kinematic chain
        joint3 = new TransformGroup();

        // adding the ability to edit during the program
        joint3.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint3.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        joint2.addChild(joint3);

        // creating another part of the kinematic chain
        joint4 = new TransformGroup();

        // adding the ability to edit during the program
        joint4.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint4.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        joint3.addChild(joint4);

        // creating another part of the kinematic chain
        joint5 = new TransformGroup();

        // adding the ability to edit during the program
        joint5.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint5.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        joint4.addChild(joint5);

        // creating another part of the kinematic chain
        joint6 = new TransformGroup();

        // adding the ability to edit during the program
        joint6.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        joint6.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        joint5.addChild(joint6);

        // adding the possibility to add a child during the program
        joint6.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        joint6.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        joint6.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
    }

    // method to create robot leg geometry
    private Cylinder create_leg(float r, float d , Color3f color)
    {
        // setting the color and type of shadows
        Appearance myAppearance = new Appearance();
        ColoringAttributes coloringAttributes = new ColoringAttributes(color, ColoringAttributes.NICEST);
        myAppearance.setColoringAttributes(coloringAttributes);

        // setting up reflective material
        Material material = new Material();
        material.setDiffuseColor(color);
        material.setShininess(30f);
        myAppearance.setMaterial(material);

        // creating a cylinder as a leg
        Cylinder cylinder = new Cylinder(r, d, myAppearance);

        return cylinder;
    }

    private void create_robot_geometry()
    {
        // set file names that contain the robot geometry from XML file
        xml_reader.read_geometry_file(robot_files);

        // saving the point cloud for each part of the kinematic chain
        for(int i = 0; i <= 6; i++)
        {
            robot_Coordinate[i] = read_file(robot_files[i]);
        }

        // add leg geometry
        leg.addChild(create_leg(0.38f, 0.7f, new Color3f(Color.orange)));


        // create the geometry of an object using the "my_shape()" function
        // and position it at the appropriate point in the kinematic chain

        Color3f color = new Color3f(Color.orange);
        Shape3D part0 = my_shape(robot_Coordinate[0], color);
        box.addChild(part0);

        color = new Color3f(Color.gray);
        Shape3D part1 = my_shape(robot_Coordinate[1],color);
        joint1.addChild(part1);

        color = new Color3f(Color.orange);
        Shape3D part2 = my_shape(robot_Coordinate[2],color);
        joint2.addChild(part2);

        color = new Color3f(Color.gray);
        Shape3D part3 = my_shape(robot_Coordinate[3],color);
        joint3.addChild(part3);

        color = new Color3f(Color.orange);
        Shape3D part4 = my_shape(robot_Coordinate[4],color);
        joint4.addChild(part4);

        color = new Color3f(Color.gray);
        Shape3D part5 = my_shape(robot_Coordinate[5],color);
        joint5.addChild(part5);

        color = new Color3f(2f, 2f, 2f);
        Shape3D part6 = my_shape(robot_Coordinate[6],color);
        joint6.addChild(part6);
    }

    // method that creates a geometric object from a point cloud
    // and gives it an appropriate appearance
    private Shape3D my_shape(Point3f[] my_Coordinate, Color3f col)
    {

        // setting the color and type of shadows
        Appearance myAppearance = new Appearance();

        ColoringAttributes coloringAttributesGreen = new
                ColoringAttributes();
        coloringAttributesGreen.setColor(col);
        coloringAttributesGreen.setShadeModel(ColoringAttributes.NICEST);

        myAppearance.setColoringAttributes(coloringAttributesGreen);

        // setting up reflective material
        Material material = new Material();
        material.setDiffuseColor(col);
        material.setShininess(30f);
        myAppearance.setMaterial(material);

        // create vector array
        TriangleArray myLines = new TriangleArray(my_Coordinate.length,
                GeometryArray.COORDINATES);
        myLines.setCoordinates(0, my_Coordinate);

        // NormalGenerator adds normal values to geometry without normals.
        GeometryInfo geometryInfo = new GeometryInfo(myLines);
        NormalGenerator nG = new NormalGenerator();
        nG.generateNormals(geometryInfo);
        GeometryArray resultGA = geometryInfo.getGeometryArray();

        // fthe final shape, to which we give color and material
        Shape3D myShape = new Shape3D(resultGA, myAppearance);
        return myShape;
    }

    // the method sets the desired angles for the individual degrees of freedom
    // the axes of rotation of the different degrees of freedom vary according to the type of angles read from the XML

    //  Each geometric shape rotates about the point where the TransformGroup "box" is placed.
    //  In order for the model to display properly, you must move the shape appropriately.
    //  This is done by the "change_rotation_point()" function, which receives as parameters the DH
    private void update_position(int joint)
    {
        if(id == 3)
        {
           if(joint == 1)
           {
               joint1.setTransform(change_rotation_point('Z', degs[0], new Vector3d(0.0, 0.0, 0.0),
                       new Vector3d(0.0, 0.0, 0.0)));
           }
           if(joint == 2)
           {
               joint2.setTransform(change_rotation_point('X',degs[1],new Vector3d(0.0, -a[2], -d[1]),
                       new Vector3d(0.0, a[2], d[1])));
           }
            if(joint == 3)
            {
                joint3.setTransform(change_rotation_point('X', degs[2], new Vector3d(0.0, -a[2], -d[1]-a[3]),
                        new Vector3d(0.0, a[2], d[1]+a[3])));
            }
            if(joint == 4)
            {
                joint4.setTransform(change_rotation_point('Y', degs[3], new Vector3d(0.0, -a[2], -d[1]-a[3]-a[4]),
                        new Vector3d(0.0, a[2], d[1]+a[3]+a[4]) ));
            }
            if(joint == 5)
            {
                joint5.setTransform(change_rotation_point('X', degs[4], new Vector3d(0.0, -a[2]-d[4], -d[1]-a[3]-a[4]),
                        new Vector3d(0.0, a[2]+d[4], d[1]+a[3]+a[4])));
            }
            if(joint == 6)
            {
                joint6.setTransform(change_rotation_point('Y', degs[5], new Vector3d(0.0, -a[2]-d[4]-d[6], -d[1]-a[3]-a[4]),
                        new Vector3d(0.0, a[2]+d[4]+d[6], d[1]+a[3]+a[4])));
            }
        }
        else if(id == 1)
        {
            if(joint == 1)
            {
                joint1.setTransform(change_rotation_point('Z', degs[0], new Vector3d(0.0, 0.0, 0.0),
                        new Vector3d(0.0, 0.0, 0.0)));
            }
            if(joint == 2)
            {
                joint2.setTransform(change_rotation_point('Y',degs[1],new Vector3d(0.0, -d[2], -d[1]),
                        new Vector3d(0.0, d[2], d[1])));
            }
            if(joint == 3)
            {
                joint3.setTransform(change_rotation_point('Y', degs[2], new Vector3d(0.0, -d[2], -d[1]-a[3]),
                        new Vector3d(0.0, d[2], d[1]+a[3])));
            }
            if(joint == 4)
            {
                joint4.setTransform(change_rotation_point('Z', degs[3], new Vector3d(0.0, -d[2], -d[1]-a[3]),
                        new Vector3d(0.0, d[2], d[1]+a[3]) ));
            }
            if(joint == 5)
            {
                joint5.setTransform(change_rotation_point('Y', degs[4], new Vector3d(0.0, -d[2], -d[1]-a[3]-d[4]),
                        new Vector3d(0.0,  d[2], d[1]+a[3]+d[4])));
            }
            if(joint == 6)
            {
                joint6.setTransform(change_rotation_point('Z', degs[5], new Vector3d(0.0, -d[2], -d[1]-a[3]-d[4]-d[6]),
                        new Vector3d(0.0,  d[2], d[1]+a[3]+d[4]+d[6])));
            }
        }
    }


    //In order for the model to display correctly, the shape must be moved appropriately.
    // This is done by the following function, which receives the DH array and the angle and axis of rotation as parameters.
    //The model is moved to the place of rotation, rotated by the appropriate angle and wiped at the old position,
    // but already properly rotated
    private Transform3D change_rotation_point(char axis, double rad, Vector3d trans1, Vector3d trans2)
    {
        // creating transformations
        Transform3D transform = new Transform3D();

        // shift by an appropriate vector
        Transform3D translate1 = new Transform3D();
        translate1.setTranslation(trans1);
        transform.mul(translate1, transform);

        //rotate by an appropriate vector
        Transform3D rotate = new Transform3D();
        if(axis == 'X')
            rotate.rotX(rad);
        else if(axis == 'Y')
            rotate.rotY(rad);
        else if(axis == 'Z')
            rotate.rotZ(rad);
        transform.mul(rotate, transform);

        // backshift
        Transform3D translate2 = new Transform3D();
        translate2.setTranslation(trans2);
        transform.mul(translate2, transform);

        return transform;
    }


    // The file consists of 3 columns with the x, y, z values of the points in floating point form.
    // The method converts each row into a vector and returns an array of vectors
    private Point3f[] read_file(String file)
    {

        // I do not know the size of the files.
        // The values will be stored in a dynamic array
        ArrayList<float[]> list = new ArrayList<float[]>();
        float[][] ps=null;

        BufferedReader reader = null;

        try {
            // use buffered reader to read line by line
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));


            float x, y, z;
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

                list.add(new float[] { x, y, z});

            }

            // add values to the list
            ps = new float[list.size()][];
            for (int i = 0; i < ps.length; i++)
                ps[i] = list.get(i);

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

        // creating an array of vectors
        Point3f[] my_Coordinates = new Point3f[ps.length];
        for(int i=0; i<ps.length; i++)
        {
            my_Coordinates[i] = new Point3f(ps[i][0]/300, ps[i][1]/300, ps[i][2]/300);
        }

        return my_Coordinates;
    }

}
