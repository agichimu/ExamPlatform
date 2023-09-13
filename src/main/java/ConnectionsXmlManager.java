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

public class ConnectionsXmlManager {
    public static void main(String[] args) throws Exception {
        //call Encryption class
        Encryption.generateSecretKey (  "brutal");

        Encryption.encrypt ( "online_exams_platform" );
        Encryption.encrypt (  "root");
        Encryption.encrypt (  "@Alexander!123");

        try { // loading the xml file
            File xmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            XPathFactory xPathFactory = XPathFactory.newInstance();//xpath instance
            XPath xPath = xPathFactory.newXPath();


            String expression = "//DATABASE/*[@TYPE='CLEARTEXT']";


            XPathExpression xpathExpr = xPath.compile(expression);


            NodeList elements = (NodeList) xpathExpr.evaluate(doc, XPathConstants.NODESET);


            for (int i = 0; i < elements.getLength(); i++) { //looping through the elements
                Element element = (Element) elements.item(i);


                element.setAttribute("TYPE", "ENCRYPTED");

                String originalValue = element.getTextContent();
                String encryptedValue = Encryption.encrypt ( originalValue );
                element.setTextContent(encryptedValue);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance(); //saving the xml with changed values
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println ("Xml modified Successfully!!");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
