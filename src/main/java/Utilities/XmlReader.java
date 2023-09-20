package Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;


public class XmlReader {


    public static void getDatabaseName() throws Exception {
        File connectionsXmlFile = new File("connections/connections.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(connectionsXmlFile);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String expression = "/CONFIG/DB/DATABASE_NAME";


        Node databaseNameNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

        if (databaseNameNode != null) {
            String databaseName = databaseNameNode.getTextContent();
            System.out.println("Database Name: " + databaseName);
        } else {
            System.out.println("Database Name not found.");
        }
    }

    public static void getUsername() throws Exception {
        File connectionsXmlFile = new File("connections/connections.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(connectionsXmlFile);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String expression = "/CONFIG/DB/USERNAME";


        Node databaseNameNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

        if (databaseNameNode != null) {
            String Username = databaseNameNode.getTextContent();
            System.out.println("Username: " + Username);
        } else {
            System.out.println("Username not found.");
        }
    }

    public static void getPassword() throws Exception {
        File connectionsXmlFile = new File("connections/connections.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(connectionsXmlFile);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String expression = "/CONFIG/DB/PASSWORD";


        Node databaseNameNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

        if (databaseNameNode != null) {
            String Password = databaseNameNode.getTextContent();
            System.out.println("Password: " + Password);
        } else {
            System.out.println("Password not found.");
        }
    }
    public static String getElementType() throws Exception {
        File connectionsXmlFile = new File("connections/connections.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(connectionsXmlFile);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String expression = "/CONFIG/DB/*[@TYPE]";


        NodeList elementsWithTypeAttribute = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        //System.out.println(" is " + elementsWithTypeAttribute.item(0));

        for (int i = 0; i < elementsWithTypeAttribute.getLength(); i++) {
            Node element = elementsWithTypeAttribute.item(i);

            String elementType = element.getNodeName();

            String typeAttribute = element.getAttributes().getNamedItem("TYPE").getNodeValue();


           System.out.println(elementType + " has " + typeAttribute + " attribute" + " and a value of  " + element.getTextContent());
           
        }


        return expression;
    }

}
