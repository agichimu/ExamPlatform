package Utilities;

import encryption.Encryption;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

public class ConnectionsXmlReader {
    private static final Logger logger = LogManager.getLogger(ConnectionsXmlReader.class);

    private static final String CONNECTIONS_XML_PATH = "connections/connections.xml";
    private static BasicDataSource dataSource;

    public static Connection getDbConnection() {
        String databaseType = getDatabaseType();
        String databaseName = getDatabaseName();
        String username = getUsername();
        String password = getPassword();

        Connection con = null;

        try {
            if (databaseType != null) {
                con = getConnection(databaseType, databaseName, username, password);
            }
            System.out.println("Connected to : " + databaseName + " database");
        } catch (SQLException e) {
            System.err.println("Failed to establish a database connection.");
        }

        return con;
    }

    private static Connection getConnection(String databaseType, String databaseName, String username, String password) throws SQLException {
        String connectionString;
        String jdbcDriver;
        String host = getDatabaseHost();
        String port = getDatabasePort();

        connectionString = switch (databaseType.toUpperCase()) {
            case "MYSQL" -> {
                jdbcDriver = "com.mysql.cj.jdbc.Driver";
                yield "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
            }
            case "POSTGRESQL" -> {
                jdbcDriver = "org.postgresql.Driver";
                yield "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
            }
            case "MICROSOFTSQL" -> {
                jdbcDriver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                yield "jdbc:sqlserver://" + host + ":" + port + "/" + databaseName;
            }
            default -> throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        };

        try {
            Class.forName(jdbcDriver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to load JDBC driver for database type: " + databaseType, e);
        }

        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setUrl(connectionString);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setMaxTotal(100);
            dataSource.setMaxIdle(10);
            dataSource.setMinIdle(5);
            dataSource.setMaxWait(Duration.ofSeconds(10));
        }
        return dataSource.getConnection();
    }

    private static String getDatabaseHost() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/HOST";
            Node hostNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (hostNode != null) {
                return hostNode.getTextContent();
            } else {
                System.err.println("Database Host not found");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error while getting database host from XML configuration");
            return null;
        }
    }

    private static String getDatabasePort() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/PORT";
            Node portNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (portNode != null) {
                return portNode.getTextContent();
            } else {
                System.err.println("Database Port not found.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting database port from XML configuration.", e);
            return null;
        }
    }

    private static String getDatabaseType() {
        DocumentBuilder documentBuilder = null;

        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // XPath's expression to get the value of the DATABASE_TYPE element
            String expression = "/CONFIG/DATABASE/DATABASE_TYPE";
            Element databaseTypeElement = (Element) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (databaseTypeElement != null) {
                return databaseTypeElement.getTextContent();
            } else {
                logger.warn("Database Type not found in the configuration.");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error while getting database type from XML configuration.", e);
            return null;
        } finally {
            if (documentBuilder != null) {
                documentBuilder.reset();
            }
        }
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
            Element databaseNameNode = (Element) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (databaseNameNode != null) {
                String databaseName;
                databaseName = databaseNameNode.getTextContent();
                /*Checking if  TYPE = "CLEARTEXT"*/
                return getString(connectionsXmlFile, document, databaseNameNode, databaseName);
            } else {
                System.err.println("Database Name not found.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error while getting database name from XML configuration" + e);
            return null;
        }
    }

    private static String getString(File connectionsXmlFile, Document document, Element databaseNameNode, String databaseName)
            throws TransformerException {
        Element databaseNameElement;
        databaseNameElement = databaseNameNode;
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
            String decryptedDatabaseName;
            decryptedDatabaseName = Encryption.decrypt(databaseName);

            return decryptedDatabaseName;
        } else {
            System.err.println("The TYPE property is unknown.: {}\", typeAttribute)");
            return null;
        }
    }

    public static String getUsername() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
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
                return getString(connectionsXmlFile, document, (Element) usernameNode, username);
            } else {
                System.err.println("Username not found.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to Read password from config" + e);

            return null;
        }
    }

    public static String getPassword() {
        DocumentBuilder documentBuilder = null;
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(connectionsXmlFile);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            String expression = "/CONFIG/DATABASE/PASSWORD";
            Node passwordNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (passwordNode != null) {
                String password = passwordNode.getTextContent();

                /*Check if  TYPE = "CLEAR-TEXT"*/
                return getString(connectionsXmlFile, document, (Element) passwordNode, password);
            } else {
                System.err.println("No password was found.");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Failed to Read password from config");
            return null;
        } finally {
            assert documentBuilder != null;
            documentBuilder.reset();
        }
    }

    public static String getPortRest() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
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
            System.err.println("Port Rest Not Found" + e);
        }

        return null;
    }

    public static String getHostRest() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
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
            System.err.println("Host Rest Not Found" + e);
        }

        return null;
    }

    public static String getBasePathRest() {
        try {
            File connectionsXmlFile = new File(CONNECTIONS_XML_PATH);
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
            System.err.println("BasePathRest Rest Not Found" + e);
        }

        return null;
    }
}
