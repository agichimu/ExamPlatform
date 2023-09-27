package configurations;

import Utilities.XmlReader;
import encryption.Encryption;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class ConnectionsManager {

    /*public static void main(String[] args) {
        readAndProcessConnectionXmlFile();
    }*/

    public static void readAndProcessConnectionXmlFile() {
        try {
            /*loading and parsing the connections.xml file*/

            File connectionsXmlFile = new File("connections/connections.xml");
            Document document = parseXmlFile(connectionsXmlFile);

            if (!isXmlEncrypted(document)) {

                String originalDatabaseName = XmlReader.getDatabaseName();
                String encryptedDatabaseName = Encryption.encrypt(originalDatabaseName);
                updateElementValue(document, "DATABASE_NAME", encryptedDatabaseName);

                String originalUsername = XmlReader.getUsername();
                String encryptedUsername = Encryption.encrypt(originalUsername);
                updateElementValue(document, "USERNAME", encryptedUsername);

                String originalPassword = XmlReader.getPassword();
                String encryptedPassword = Encryption.encrypt(originalPassword);
                updateElementValue(document, "PASSWORD", encryptedPassword);

                savingXml(document, connectionsXmlFile);

                System.out.println("Encryption complete !!");
            } else {
                String encryptedDatabaseName = getEncryptedElementValue(document, "DATABASE_NAME");
                String decryptedDatabaseName = Encryption.decrypt(encryptedDatabaseName);

                String encryptedUsername = getEncryptedElementValue(document, "USERNAME");
                String decryptedUsername = Encryption.decrypt(encryptedUsername);

                String encryptedPassword = getEncryptedElementValue(document, "PASSWORD");
                String decryptedPassword = Encryption.decrypt(encryptedPassword);


                System.out.println("Decrypted Database Name: " + decryptedDatabaseName);
                System.out.println("Decrypted Username: " + decryptedUsername);
                System.out.println("Decrypted Password: " + decryptedPassword);

                System.out.println("Decryption complete \uD83D\uDE1E");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Document parseXmlFile(File file) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(file);
    }

    private static boolean isXmlEncrypted(Document document) {
        Element databaseNameElement = (Element) document.getElementsByTagName("DATABASE_NAME").item(0);
        Element usernameElement = (Element) document.getElementsByTagName("USERNAME").item(0);
        Element passwordElement = (Element) document.getElementsByTagName("PASSWORD").item(0);

        return (databaseNameElement != null && "ENCRYPTED".equalsIgnoreCase(databaseNameElement.getAttribute("TYPE"))) &&
                (usernameElement != null && "ENCRYPTED".equalsIgnoreCase(usernameElement.getAttribute("TYPE"))) &&
                (passwordElement != null && "ENCRYPTED".equalsIgnoreCase(passwordElement.getAttribute("TYPE")));
    }

   /* private static void encryptingXml(Document document) {
        String originalDatabaseName = XmlReader.getDatabaseName();
        String encryptedDatabaseName = Encryption.encrypt(originalDatabaseName);
        updateElementValue(document, "DATABASE_NAME", encryptedDatabaseName);

        String originalUsername = XmlReader.getUsername();
        String encryptedUsername = Encryption.encrypt(originalUsername);
        updateElementValue(document, "USERNAME", encryptedUsername);

        String originalPassword = XmlReader.getPassword();
        String encryptedPassword = Encryption.encrypt(originalPassword);
        updateElementValue(document, "PASSWORD", encryptedPassword);
    }*/

    /*public static void decryptingXml(Document document) {
        String encryptedDatabaseName = getEncryptedElementValue(document, "DATABASE_NAME");
        String decryptedDatabaseName = Encryption.decrypt(encryptedDatabaseName);

        String encryptedUsername = getEncryptedElementValue(document, "USERNAME");
        String decryptedUsername = Encryption.decrypt(encryptedUsername);

        String encryptedPassword = getEncryptedElementValue(document, "PASSWORD");
        String decryptedPassword = Encryption.decrypt(encryptedPassword);


        System.out.println("Decrypted Database Name: " + decryptedDatabaseName);
        System.out.println("Decrypted Username: " + decryptedUsername);
        System.out.println("Decrypted Password: " + decryptedPassword);
    }*/

    private static String getEncryptedElementValue(Document document, String elementName) {
        NodeList nodeList = document.getElementsByTagName(elementName);
        if (nodeList.getLength() > 0) {
            Element element = (Element) nodeList.item(0);
            return element.getTextContent();
        }
        return null;
    }

    private static void updateElementValue(Document document, String elementName, String newValue) {
        NodeList nodeList = document.getElementsByTagName(elementName);
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodeList.item(i).setTextContent(newValue);
            Element element = (Element) nodeList.item(i);
            element.setAttribute("TYPE", "ENCRYPTED"); // Mark as encrypted
        }
    }

    private static void savingXml(Document document, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}
