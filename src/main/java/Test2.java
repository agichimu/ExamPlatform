/*
import configurations.ConnectionsManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test2 {
    public static void main(String[] args) {

       // ConnectionsManager.processAndDecryptData ();

        try {
            // Decrypted values from the XML
            String[] decryptedValues = processAndDecryptData();

            if (decryptedValues != null && decryptedValues.length == 3) {
                String decryptedDatabaseName = decryptedValues[0];
                String decryptedUsername = decryptedValues[1];
                String decryptedPassword = decryptedValues[2];

                // Construct the JDBC URL, username, and password
                String jdbcUrl = "jdbc:mysql://localhost:3306/" + decryptedDatabaseName;
                String dbUsername = decryptedUsername;
                String dbPassword = decryptedPassword;

                // Register JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Open a connection to the database
                Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);

                // Now you have a connection to the database, and you can perform database operations using this connection.

                // For example, you can create a statement and execute queries.
                // Statement statement = connection.createStatement();
                // ResultSet resultSet = statement.executeQuery("SELECT * FROM your_table");

                // Don't forget to close the connection when you are done with it.
                // connection.close();

                System.out.println("Database connection established successfully.");
            } else {
                System.err.println("Failed to retrieve decrypted values.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static String[] processAndDecryptData() {
        // Process and decrypt data here as needed
        // Retrieve the decrypted values from the XML configuration
        String encryptedDatabaseName = getEncryptedElementValue(document, "DATABASE_NAME");
        String decryptedDatabaseName = Encryption.decrypt(encryptedDatabaseName);

        String encryptedUsername = getEncryptedElementValue(document, "USERNAME");
        String decryptedUsername = Encryption.decrypt(encryptedUsername);

        String encryptedPassword = getEncryptedElementValue(document, "PASSWORD");
        String decryptedPassword = Encryption.decrypt(encryptedPassword);

        // Return the decrypted values as an array
        return new String[]{decryptedDatabaseName, decryptedUsername, decryptedPassword};
    }
}
*/
