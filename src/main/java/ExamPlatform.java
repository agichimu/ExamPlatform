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

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);

            displayExamsSetByATeacher(connection);
            reportOnPupilAnswers(connection);
            reportOnTop5pupils(connection);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayExamsSetByATeacher(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT examination_id , examination_name , examination_time , date_created , date_modified FROM examination_details  WHERE teacher_id = 1";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                long examinationId = resultSet.getLong("examination_id");
                String examinationName = resultSet.getString("examination_name");
                Timestamp examinationTime = resultSet.getTimestamp("examination_time");
                Timestamp dateCreated = resultSet.getTimestamp("date_created");
                Timestamp dateModified = resultSet.getTimestamp("date_modified");

                System.out.println("Examination ID: " + examinationId);
                System.out.println("Examination Name: " + examinationName);
                System.out.println("Examination Time: " + examinationTime);
                System.out.println("Date Created: " + dateCreated);
                System.out.println("Date Modified: " + dateModified);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void reportOnPupilAnswers(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT answer_id  , choice_id  FROM answer_detail WHERE pupil_id = 1";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                long answer_id = resultSet.getLong("answer_id");
                long choice_id = resultSet.getLong("choice_id");

                System.out.println("Answer ID: " + answer_id);
                System.out.println("Choice Id: " + choice_id);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reportOnTop5pupils(Connection connection){
        try{
            Statement statement = connection.createStatement();
            String query = "SELECT p.first_name , p.second_name , SUM(ad.scores) AS total_scores, (SUM(ad.scores) / (COUNT(*) * 2)) * 100 AS percentage FROM answer_detail ad JOIN pupils_details p ON ad.pupil_id = p.pupil_id  GROUP BY p.pupil_id, p.first_name, p.second_name ORDER BY total_scores DESC LIMIT 5";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()){
                String first_name = resultSet.getString("p.first_name");
                String second_name = resultSet.getString("p.second_name");
                Long percentage = resultSet.getLong("percentage");

                System.out.println("P.First_Name" + first_name);
                System.out.println("P.Second_Name" + second_name);
                System.out.println("Percentage" + percentage);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
