package parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import utils.Task;
import utils.TaskPool;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.io.File;

public class WorkflowParser {

    public static void run(String pathToXML) {
        try {
            File file = new File(pathToXML);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList jobsList = doc.getElementsByTagName("job");
            NodeList usesList;
            Node usesNode;
            for (int itr = 0; itr < jobsList.getLength(); itr++) {
                Long input=0L;
                Long output=0L;
                Node jobNode = jobsList.item(itr);
                NamedNodeMap nnm= jobNode.getAttributes();
                int id=Integer.parseInt(nnm.getNamedItem("id").getTextContent().substring(2));
                Long runtime=Math.round(Double.parseDouble(nnm.getNamedItem("runtime").getTextContent())*1000);
                Element eElement = (Element) jobNode;
                usesList=eElement.getElementsByTagName("uses");
                input=0L;
                output=0L;
                for (int itr2 = 0; itr2 < usesList.getLength(); itr2++) {
                    usesNode = usesList.item(itr2);
                    String link=usesNode.getAttributes().getNamedItem("link").getTextContent();
                    switch (link) {
                        case "input":
                            input+=Long.parseLong(usesNode.getAttributes().getNamedItem("size").getTextContent());
                            break;
                        case "output":
                            output+=Long.parseLong(usesNode.getAttributes().getNamedItem("size").getTextContent());
                            break;
                        default:
                            System.out.println("Error in parse xml. input/output attribute");
                            break;
                    }
                }
                UtilizationModel utilizationModel = new UtilizationModelFull();
                Task t=new Task(id, runtime, 1, input, output, utilizationModel, utilizationModel, utilizationModel);
                TaskPool.getTaskPool().addTask(t);
            }

            NodeList childsList = doc.getElementsByTagName("child");
            for (int i = 0; i < childsList.getLength(); i++) {
                Node childNode = childsList.item(i);
                NamedNodeMap nnm= childNode.getAttributes();
                int childID=Integer.parseInt(nnm.getNamedItem("ref").getTextContent().substring(2));
                Element eElement = (Element) childNode;
                NodeList parentsList=eElement.getElementsByTagName("parent");
                for (int j = 0; j < parentsList.getLength(); j++) {
                    Node parentNode = parentsList.item(j);
                    int parentID=Integer.parseInt(parentNode.getAttributes().getNamedItem("ref").getTextContent().substring(2));
                    TaskPool.getTaskPool().getTaskByID(childID).addParent(parentID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}