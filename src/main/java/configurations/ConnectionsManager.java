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
    public static void main(String[] args) {
        readAndEncryptConnectionXmlFile ();
    }

    public static void readAndEncryptConnectionXmlFile() {
        try {
            // Load and parsing the XML file

            File     connectionsXmlFile = new File ("connections/connections.xml");
            Document document           = parseXmlFile (connectionsXmlFile);

            // Check if the XML is already marked as encrypted

            if (!isXmlMarkedAsEncrypted (document)) {

                /*db name*/

                String databaseName = XmlReader.getDatabaseName ();
                String encryptedDatabaseName = Encryption.encrypt (databaseName);
                String decryptedDatabase = Encryption.decrypt (encryptedDatabaseName);
                System.out.println (decryptedDatabase);


                /*user-name*/

                String username     = XmlReader.getUsername ();
                String encryptedUsername     = Encryption.encrypt (username);

                /*password*/

                String password     = XmlReader.getPassword ();
                String encryptedPassword     = Encryption.encrypt (password);

                // Update the XML elements with encrypted data

                updateElementValue (document, "DATABASE_NAME", encryptedDatabaseName);
                updateElementValue (document, "USERNAME", encryptedUsername);
                updateElementValue (document, "PASSWORD", encryptedPassword);

                /*Saving back the updated Connections.xml file*/

                saveXmlDocument (document, connectionsXmlFile);

                System.out.println ("Encryption complete !! ");
            } else {
                System.out.println ("Already encrypted \uD83D\uDE1E");
            }
        } catch (Exception e) {
            e.printStackTrace ();

        }
    }

    private static Document parseXmlFile(File file) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance ();
        DocumentBuilder        documentBuilder        = documentBuilderFactory.newDocumentBuilder ();
        return documentBuilder.parse (file);
    }

    private static boolean isXmlMarkedAsEncrypted(Document document) {
        Element databaseNameElement = (Element) document.getElementsByTagName ("DATABASE_NAME").item (0);
        Element usernameElement     = (Element) document.getElementsByTagName ("USERNAME").item (0);
        Element passwordElement     = (Element) document.getElementsByTagName ("PASSWORD").item (0);

        return (databaseNameElement != null && "ENCRYPTED".equalsIgnoreCase (databaseNameElement.getAttribute ("TYPE"))) &&
                (usernameElement != null && "ENCRYPTED".equalsIgnoreCase (usernameElement.getAttribute ("TYPE"))) &&
                (passwordElement != null && "ENCRYPTED".equalsIgnoreCase (passwordElement.getAttribute ("TYPE")));
    }

    private static void updateElementValue(Document document, String elementName, String newValue) {
        NodeList nodeList = document.getElementsByTagName (elementName);
        for (int i = 0; i < nodeList.getLength (); i++) {
            nodeList.item (i).setTextContent (newValue);
            if (true) {
                // Add the "TYPE" attribute as "ENCRYPTED"
                Element element = (Element) nodeList.item (i);
                element.setAttribute ("TYPE", "ENCRYPTED");
            } else {
                // Remove the "TYPE" attribute if present
                Element element = (Element) nodeList.item (i);
                element.removeAttribute ("TYPE");
            }
        }
    }
    private static void saveXmlDocument(Document document, File file) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance ();
        Transformer        transformer        = transformerFactory.newTransformer ();
        DOMSource          source             = new DOMSource (document);
        StreamResult       result             = new StreamResult (file);
        transformer.transform (source, result);
    }
}
