import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.*;



public class ExamPlatform {
    public static void main(String[] args) {
        try {

            File xmlFile = new File("connections/connections.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element databaseElement = (Element) doc.getElementsByTagName("DATABASE").item(0);

            String driver = databaseElement.getElementsByTagName("DATABASE_DRIVER").item(0).getTextContent();
            String url = databaseElement.getElementsByTagName("DATABASE_URL").item(0).getTextContent();
            String username = databaseElement.getElementsByTagName("CLEARTEXT USERNAME").item(0).getTextContent();
            String password = databaseElement.getElementsByTagName("CLEARTEXT PASSWORD").item(0).getTextContent();

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url, username, password);

            String teacherIdToSearch = String.valueOf(1);
            Object teacher_id = null;
            displayExamsSetByATeacher(connection, null);
            reportOnPupilAnswers(connection);
            reportOnTop5pupils(connection);
            reportSheetForAllPupils(connection);

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   //Display all the exams set by a teacher.
    public static void displayExamsSetByATeacher(Connection connection, Object teacher_id) throws Exception{
        try {
            // add code to check connection
            PreparedStatement preparedStatementStatement= connection.prepareStatement("SELECT examination_id , " +
                    "examination_name , " +
                    "examination_time , " +
                    "date_created , " +
                    "date_modified " +
                    "FROM examination_details  " +
                    "WHERE teacher_id = ?");
            preparedStatementStatement.setString(1 ,"teacher_id");
            ResultSet resultSet = ((PreparedStatement) preparedStatementStatement).executeQuery();

            System.out.println("Display all the exams set by a teacher.");

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
            preparedStatementStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Generate a report on the answers provided by a pupil for an exam and their percentage score in that exam.
    public static void reportOnPupilAnswers(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT answer_id  , choice_id  FROM answer_detail WHERE pupil_id = ?";
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Task 2: Generate a report on the answers provided by a pupil for an exam and their percentage score in that exam.");

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

    //Generate a report on the top 5 pupils with the highest scores in a certain exam.
    public static void reportOnTop5pupils(Connection connection){
        try{
            Statement statement = connection.createStatement();
            String query = "SELECT p.first_name , p.second_name , SUM(ad.scores) AS total_scores, (SUM(ad.scores) / (COUNT(*) * 2)) * 100 AS percentage FROM answer_detail ad JOIN pupils_details p ON ad.pupil_id = p.pupil_id  GROUP BY p.pupil_id, p.first_name, p.second_name ORDER BY total_scores DESC LIMIT 5";
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Generate a report on the top 5 pupils with the highest scores in a certain exam.");

            while (resultSet.next()){
                String first_name = resultSet.getString("p.first_name");
                String second_name = resultSet.getString("p.second_name");
                long percentage = resultSet.getLong("percentage");


                System.out.println("P.First_Name" + first_name);
                System.out.println("P.Second_Name" + second_name);
                System.out.println("Percentage" + percentage);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Generate a report sheet of the scores for all pupils in each of the exams done and rank them from the highest average score to lowest.
    public static void reportSheetForAllPupils(Connection connection) {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT p.first_name, p.second_name, SUM(ad.scores) AS total_scores, (SUM(ad.scores) / (COUNT(*) * 2)) * 100 AS percentage FROM answer_detail ad JOIN pupils_details p ON ad.pupil_id = p.pupil_id GROUP BY p.pupil_id, p.first_name, p.second_name ORDER BY total_scores DESC ";
            ResultSet resultSet = statement.executeQuery(query);


            System.out.println("Generate a report sheet of the scores for all pupils in each of the exams done and rank them from the highest average score to lowest.");

            while (resultSet.next()) {
                String first_name = resultSet.getString("first_name");
                String second_name = resultSet.getString("second_name");
                long total_scores = resultSet.getLong("total_scores");
                long percentage = resultSet.getLong("percentage");

                System.out.println("P.First_Name: " + first_name);
                System.out.println("P.Second_Name: " + second_name);
                System.out.println("Total Scores: " + total_scores);
                System.out.println("Percentage: " + percentage + "%");

            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
