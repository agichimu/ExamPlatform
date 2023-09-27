package Utilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;

public class XmlReader {


    public static String getDatabaseName()  {
        try {
            File                   connectionsXmlFile     = new File ("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder ();
            Document               document               = documentBuilder.parse (connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance ();
            XPath        xpath        = xPathFactory.newXPath ();

            String expression       = "/CONFIG/DATABASE/DATABASE_NAME";
            Node   databaseNameNode = (Node) xpath.evaluate (expression, document, XPathConstants.NODE);

            if (databaseNameNode != null) {
                String databaseName = databaseNameNode.getTextContent ();
                //System.out.println ("Database Name: " + databaseName);
                return databaseName;
            } else {
                System.out.println ("Database Name not found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }

    public static String getUsername(){
        try {
            File                   connectionsXmlFile     = new File ("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder ();
            Document               document               = documentBuilder.parse (connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance ();
            XPath        xpath        = xPathFactory.newXPath ();

            String expression   = "/CONFIG/DATABASE/USERNAME";
            Node   usernameNode = (Node) xpath.evaluate (expression, document, XPathConstants.NODE);

            if (usernameNode != null) {
                String username = usernameNode.getTextContent ();
               // System.out.println ("Username: " + username);
                return username;
            } else {
                System.out.println ("Username not found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }
    }
    public static String getPassword() {
        try {
            File                   connectionsXmlFile     = new File ("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder ();
            Document               document               = documentBuilder.parse (connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance ();
            XPath        xpath        = xPathFactory.newXPath ();

            String expression   = "/CONFIG/DATABASE/PASSWORD";
            Node   passwordNode = (Node) xpath.evaluate (expression, document, XPathConstants.NODE);

            if (passwordNode != null) {
                String password = passwordNode.getTextContent ();
                //System.out.println ("Password: " + password);
                return password;
            } else {
                System.out.println ("Error !!! Password not found");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            return null;
        }

    }
    public static void getElementType(){
        try {
            File                   connectionsXmlFile     = new File ("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
            DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder ();
            Document               document               = documentBuilder.parse (connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance ();
            XPath        xpath        = xPathFactory.newXPath ();

            String expression = "/CONFIG/DATABASE/*[@TYPE]";

            NodeList elementsWithTypeAttribute = (NodeList) xpath.evaluate (expression, document, XPathConstants.NODESET);

            for (int i = 0; i < elementsWithTypeAttribute.getLength (); i++) {
                Node   element       = elementsWithTypeAttribute.item (i);
                String elementType   = element.getNodeName ();
                String typeAttribute = element.getAttributes ().getNamedItem ("TYPE").getNodeValue ();
                System.out.println (elementType + " has " + typeAttribute + " attribute" + " and a value of  " + element.getTextContent ());
            }
        } catch (XPathExpressionException | ParserConfigurationException e) {
            throw new RuntimeException (e);
        } catch (IOException e) {
            throw new RuntimeException (e);
        } catch (SAXException e) {
            throw new RuntimeException (e);
        }

    }
    public static void main(String[] args) throws Exception {
        getElementType ();
        getPassword ();
        getDatabaseName ();
        getUsername ();
    }
}
