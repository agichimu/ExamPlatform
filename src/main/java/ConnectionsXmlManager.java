import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;

public class ConnectionsXmlManager {
    public static void main(String[] args) {
        writeOutXml();
    }

    public static void writeOutXml() {
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();


            Document document = documentBuilder.newDocument();
            Element rootElement = document.createElement("CONFIG");
            document.appendChild(rootElement);


            Element databaseElement = document.createElement("DATABASE");
            rootElement.appendChild(databaseElement);


            Element databaseTypeElement = document.createElement("DATABASE_TYPE");
            databaseTypeElement.appendChild(document.createTextNode("MYSQL"));
            databaseElement.appendChild(databaseTypeElement);


            Element databaseDriverElement = document.createElement("DATABASE_DRIVER");
            databaseDriverElement.appendChild(document.createTextNode("com.mysql.cj.jdbc.Driver"));
            databaseElement.appendChild(databaseDriverElement);


            Element databaseUrlElement = document.createElement("DATABASE_URL");
            databaseUrlElement.appendChild(document.createTextNode(""));
            databaseElement.appendChild(databaseUrlElement);


            Element databaseNameElement = document.createElement("DATABASE_NAME");
            databaseNameElement.setAttribute("TYPE", "ENCRYPTED");
            databaseNameElement.appendChild(document.createTextNode(""));
            databaseElement.appendChild(databaseNameElement);


            Element usernameElement = document.createElement("USERNAME");
            usernameElement.setAttribute("TYPE", "ENCRYPTED");
            usernameElement.appendChild(document.createTextNode(""));
            databaseElement.appendChild(usernameElement);


            Element passwordElement = document.createElement("PASSWORD");
            passwordElement.setAttribute("TYPE", "ENCRYPTED");
            passwordElement.appendChild(document.createTextNode(""));
            databaseElement.appendChild(passwordElement);


            DOMSource source = new DOMSource(document);


            FileOutputStream outputFile = new FileOutputStream("connections/connections.xml");
            StreamResult result = new StreamResult(outputFile);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");


            transformer.transform(source, result);

            //
            outputFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
