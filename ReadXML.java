import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
//import java.io.InputStream;

//  class responsible for retrieving attributes from an XML file
class ReadXML
{

    private String FILEPATH;

    // the file path is passed to the constructor
   public ReadXML(String file) throws ParserConfigurationException
   {
        FILEPATH = file;
    }

    // reading "DH" parameters from an XML file
    public void read_dh_parameters(double [][]DH)
    {
        // creation of a new instance
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // opening the selected document
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(FILEPATH));
            doc.getDocumentElement().normalize();

            // attributes will be read from "axis" calls
            NodeList list = doc.getElementsByTagName("axis");


            // iterating over all "axis" nodes
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // extraction of attributes named "a" and "d"
                    String a = element.getAttribute("a");
                    String d = element.getAttribute("d");


                    // string to double conversion
                        double dd = Double.parseDouble(d);
                        dd =dd /300;

                        double ad = Double.parseDouble(a);
                        ad = ad / 300;


                        // writing the read parameters to the array
                        DH[0][temp] = dd;
                        DH[1][temp] = ad;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    //method reads the names of the files that contain the robot geometry
    //writing the read parameters to the array

    public void read_geometry_file(String[] files)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            // creation of a new instance
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);


            // opening the selected document
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(FILEPATH));
            doc.getDocumentElement().normalize();


            // iterating over all "geometry" nodes
            NodeList list = doc.getElementsByTagName("geometry");

            // iterating over all "geometry" nodes
            for (int temp = 0; temp < list.getLength(); temp++) {
                Node node = list.item(temp);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // extraction of attributes named "geo"
                    String attribute = element.getAttribute("geo");

                    // file path completion based on XML
                    String file = "data/" + attribute + ".stl";

                    // adding a path to the table
                    files[temp] = file;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public int read_angle_type() {

        int angle_id = 0;

        // creation of a new instance
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // opening the selected document
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(FILEPATH));
            doc.getDocumentElement().normalize();

            // attributes will be read from "Angle_Typed" calls
            NodeList list = doc.getElementsByTagName("Angle_Type");


            Node node = list.item(0);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // extraction of attributes named "id"

                String id = element.getAttribute("id");

                // string to int conversion
                angle_id = Integer.parseInt(id);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
       return angle_id;
    }
}
