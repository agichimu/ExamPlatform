import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ExamPlatform {
    public static void main(String[] args) {
        try {

            File xmlFile = new File("/home/agichimu/IdeaProjects/ExamPlatform/connections/connections.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();


            String driver = doc.getElementsByTagName("entry").item(0).getTextContent();
            String url = doc.getElementsByTagName("entry").item(1).getTextContent();
            String username = doc.getElementsByTagName("entry").item(2).getTextContent();
            String password = doc.getElementsByTagName("entry").item(3).getTextContent();

            //forName() method of Class class is used to register the driver class
            Class.forName(driver);


            Connection connection = DriverManager.getConnection(url, username, password);


            Statement statement = connection.createStatement();
            String query = "SELECT * FROM examination_details";
            ResultSet resultSet = statement.executeQuery(query);


            while (resultSet.next()) {
                long examinationId = resultSet.getLong("examination_id");
                String instructions = resultSet.getString("instructions");
                int teacherId = resultSet.getInt("teacher_id");
                String examinationName = resultSet.getString("examination_name");
                int subjectId = resultSet.getInt("subject_id");
                Timestamp examinationTime = resultSet.getTimestamp("examination_time");
                long questionId = resultSet.getLong("question_id");
                Timestamp dateCreated = resultSet.getTimestamp("date_created");
                Timestamp dateModified = resultSet.getTimestamp("date_modified");


                System.out.println("Examination ID: " + examinationId);
                System.out.println("Instructions: " + instructions);
                System.out.println("Teacher ID: " + teacherId);
                System.out.println("Examination Name: " + examinationName);
                System.out.println("Subject ID: " + subjectId);
                System.out.println("Examination Time: " + examinationTime);
                System.out.println("Question ID: " + questionId);
                System.out.println("Date Created: " + dateCreated);
                System.out.println("Date Modified: " + dateModified);


            }
            
            resultSet.close();
            statement.close();


            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
