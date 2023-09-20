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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionsManager {
    private static final String ENCRYPTED_DATABASE_NAME = "encryptedDatabaseName";
    private static final String ENCRYPTED_USERNAME = "encryptedUsername";
    private static final String ENCRYPTED_PASSWORD = "encryptedPassword";



    public static void readAndEncryptConnectionXmlFile() {
        try {
            XmlReader.getElementType();

            NodeList clearTextElements = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < clearTextElements.getLength(); i++) {
                Element element = (Element) clearTextElements.item(i);
                String plainTextValue = element.getTextContent();

                String encryptedValue = Encryption.encrypt(plainTextValue);
                element.setTextContent(encryptedValue);


                element.setAttribute("TYPE", "ENCRYPTED");

            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(connectionsXmlFile);
            transformer.transform(source, result);

            System.out.println("Encrypted connection information in XML file.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void createDatabaseConnection() {
        try {
            File connectionsXmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            String databaseName = Encryption.decrypt(getElementValueById(document, ENCRYPTED_DATABASE_NAME));
            String username = Encryption.decrypt(getElementValueById(document, ENCRYPTED_USERNAME));
            String password = Encryption.decrypt(getElementValueById(document, ENCRYPTED_PASSWORD));

            String jdbcUrl = "jdbc:mysql://localhost:3306/" + databaseName;

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                connection.beginRequest();
                System.out.println("Connected to the database.");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String getElementValueById(Document document, String id) {
        Element element = document.getElementById(id);
        if (element != null) {
            return element.getTextContent();
        } else {
            throw new RuntimeException("Encrypted database connection information not found.");
        }
    }
}
