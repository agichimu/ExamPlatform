package Utilities;

import encryption.Encryption;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;


public class ConnectionsXmlReader {
    public static String getPortRest() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/API/UNDERTOW/PORT[@REST]"; //predicates
            Node portRestNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (portRestNode != null) {
                return portRestNode.getAttributes().getNamedItem("REST").getNodeValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public static String getHostRest() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/API/UNDERTOW/HOST[@REST]";
            Node hostRestNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (hostRestNode != null) {
                return hostRestNode.getAttributes().getNamedItem("REST").getNodeValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getBasePathRest() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/API/UNDERTOW/BASE_PATH[@REST]";
            Node basePathRestNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (basePathRestNode != null) {
                return basePathRestNode.getAttributes().getNamedItem("REST").getNodeValue();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static String getDatabaseName() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/DATABASE_NAME";
            Node databaseNameNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (databaseNameNode != null) {
                String databaseName = databaseNameNode.getTextContent();

                /*Checking if  TYPE = "CLEARTEXT"*/
                Element databaseNameElement = (Element) databaseNameNode;
                String typeAttribute = databaseNameElement.getAttribute("TYPE");

                if ("CLEARTEXT".equalsIgnoreCase(typeAttribute)) {
                    /*Encrypting && Updating connections.xml file*/
                    String encryptedDatabaseName = Encryption.encrypt(databaseName);
                    databaseNameElement.setTextContent(encryptedDatabaseName);
                    databaseNameElement.setAttribute("TYPE", "ENCRYPTED");

                    /*Updating*/

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(document);
                    StreamResult result = new StreamResult(connectionsXmlFile);
                    transformer.transform(source, result);

                    return encryptedDatabaseName;
                } else if ("ENCRYPTED".equalsIgnoreCase(typeAttribute)) {
                    String decryptedDatabaseName = Encryption.decrypt(databaseName);

                    return decryptedDatabaseName;
                } else {
                    System.out.println("The TYPE property is unknown.: " + typeAttribute);
                    return null;
                }
            } else {
                System.out.println("⚠\uFE0F Database Name not found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getUsername() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/USERNAME";
            Node usernameNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (usernameNode != null) {
                String username = usernameNode.getTextContent();
                /*Check if  TYPE = "CLEARTEXT"*/
                Element usernameElement = (Element) usernameNode;
                String typeAttribute = usernameElement.getAttribute("TYPE");

                if ("CLEARTEXT".equalsIgnoreCase(typeAttribute)) {
                    String encryptedUsername = Encryption.encrypt(username);
                    usernameElement.setTextContent(encryptedUsername);
                    usernameElement.setAttribute("TYPE", "ENCRYPTED");

                    /*Updating the configurations.xml file*/
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(document);
                    StreamResult result = new StreamResult(connectionsXmlFile);
                    transformer.transform(source, result);

                   // System.out.println("Username: " + encryptedUsername);
                    return encryptedUsername;
                } else if ("ENCRYPTED".equalsIgnoreCase(typeAttribute)) {
                    String decryptedUsername = Encryption.decrypt(username);
                   // System.out.println("Username (Already Encrypted): " + decryptedUsername);
                    return decryptedUsername;
                } else {
                    System.out.println("The TYPE property is unknown.: " + typeAttribute);
                    return null;
                }
            } else {
                System.out.println("⚠\uFE0F Username not found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPassword() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/PASSWORD";
            Node passwordNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (passwordNode != null) {
                String password = passwordNode.getTextContent();

                /*Check if  TYPE = "CLEARTEXT"*/
                Element passwordElement = (Element) passwordNode;
                String typeAttribute = passwordElement.getAttribute("TYPE");

                if ("CLEARTEXT".equalsIgnoreCase(typeAttribute)) {

                    String encryptedPassword = Encryption.encrypt(password);
                    passwordElement.setTextContent(encryptedPassword);
                    passwordElement.setAttribute("TYPE", "ENCRYPTED");


                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(document);
                    StreamResult result = new StreamResult(connectionsXmlFile);
                    transformer.transform(source, result);

                    //System.out.println("Password: " + encryptedPassword);
                    return encryptedPassword;
                } else if ("ENCRYPTED".equalsIgnoreCase(typeAttribute)) {
                    String decryptedPassword = Encryption.decrypt(password);
                    //System.out.println("Password (Already Encrypted): " + decryptedPassword);
                    return decryptedPassword;
                } else {
                    System.out.println("The TYPE property is unknown.: " + typeAttribute);
                    return null;
                }
            } else {
                System.out.println("Error ⚠\uFE0F No password was found.");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getDatabaseURL() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(connectionsXmlFile);
            doc.getDocumentElement().normalize();

            NodeList databaseURLNodeList = doc.getElementsByTagName("DATABASE_URL");
            if (databaseURLNodeList.getLength() > 0) {
                Element databaseURLElement = (Element) databaseURLNodeList.item(0);
                return databaseURLElement.getTextContent();
            } else {
                throw new IllegalArgumentException("DATABASE_URL not found in the configuration.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}