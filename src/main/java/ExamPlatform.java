import java.sql.*;


public class ExamPlatform {
    private static org.w3c.dom.Document Document;

    public static void main(String[] args) {

    }

   /* Object pupil_id          = null;
    String teacherIdToSearch = "1";
    displayExamsSetByATeacher (connection, teacherIdToSearch);
    reportOnPupilAnswers (connection, pupil_id);
    reportOnTop5pupils (connection);
    reportSheetForAllPupils (connection);*/

    // Display all the exams set by a teacher.

    public static void displayExamsSetByATeacher(Connection connection, String teacher_id) {
        try {
            String query = "SELECT examination_id, examination_name, examination_time, date_created, date_modified " +
                    "FROM examination_details " +
                    "WHERE teacher_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement (query);
            preparedStatement.setString (1, teacher_id);

            ResultSet resultSet = preparedStatement.executeQuery ();

            System.out.println ("Display all the exams set by a teacher.");

            while (resultSet.next ()) {
                long      examinationId   = resultSet.getLong ("examination_id");
                String    examinationName = resultSet.getString ("examination_name");
                Timestamp examinationTime = resultSet.getTimestamp ("examination_time");
                Timestamp dateCreated     = resultSet.getTimestamp ("date_created");
                Timestamp dateModified    = resultSet.getTimestamp ("date_modified");

                System.out.println ("Examination ID: " + examinationId);
                System.out.println ("Examination Name: " + examinationName);
                System.out.println ("Examination Time: " + examinationTime);
                System.out.println ("Date Created: " + dateCreated);
                System.out.println ("Date Modified: " + dateModified);
            }

            resultSet.close ();
            preparedStatement.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    // Generate a report on the answers provided by a pupil for an exam and their percentage score in that exam.

    public static void reportOnPupilAnswers(Connection connection, Object pupil_id) {
        try {
            String            query             = "SELECT answer_id, choice_id FROM answer_detail WHERE pupil_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement (query);
            preparedStatement.setObject (1, pupil_id);

            ResultSet resultSet = preparedStatement.executeQuery ();

            System.out.println ("Generate a report on the answers provided by a pupil for an exam and their percentage score in that exam.");

            while (resultSet.next ()) {
                long answer_id = resultSet.getLong ("answer_id");
                long choice_id = resultSet.getLong ("choice_id");

                System.out.println ("Answer ID: " + answer_id);
                System.out.println ("Choice Id: " + choice_id);
            }

            resultSet.close ();
            preparedStatement.close ();
        } catch (Exception e) {
            throw new RuntimeException (e);
        }
    }

    // Generate a report on the top 5 pupils with the highest scores in a certain exam.

    public static void reportOnTop5pupils(Connection connection) {
        try {
            String query = "SELECT p.first_name, p.second_name, SUM(ad.scores) AS total_scores, (SUM(ad.scores) / (COUNT(*) * 2)) * 100 AS percentage " +
                    "FROM answer_detail ad " +
                    "JOIN pupils_details p ON ad.pupil_id = p.pupil_id " +
                    "GROUP BY p.pupil_id, p.first_name, p.second_name " +
                    "ORDER BY total_scores DESC LIMIT 5";
            PreparedStatement preparedStatement = connection.prepareStatement (query);

            ResultSet resultSet = preparedStatement.executeQuery ();

            System.out.println ("Generate a report on the top 5 pupils with the highest scores in a certain exam.");

            while (resultSet.next ()) {
                String first_name  = resultSet.getString ("first_name");
                String second_name = resultSet.getString ("second_name");
                long   percentage  = resultSet.getLong ("percentage");

                System.out.println ("P.First_Name: " + first_name);
                System.out.println ("P.Second_Name: " + second_name);
                System.out.println ("Percentage: " + percentage);
            }

            resultSet.close ();
            preparedStatement.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    // Generate a report sheet of the scores for all pupils in each of the exams done and rank them from the highest average score to lowest.

    public static void reportSheetForAllPupils(Connection connection) throws SQLException {
        try {
            String query = "SELECT p.first_name, p.second_name, SUM(ad.scores) AS total_scores, (SUM(ad.scores) / (COUNT(*) * 2)) * 100 AS percentage " +
                    "FROM answer_detail ad " +
                    "JOIN pupils_details p ON ad.pupil_id = p.pupil_id " +
                    "GROUP BY p.pupil_id, p.first_name, p.second_name " +
                    "ORDER BY total_scores DESC";
            PreparedStatement preparedStatement = connection.prepareStatement (query);

            ResultSet resultSet = null;
            try {
                resultSet = preparedStatement.executeQuery ();
            } catch (SQLException e) {
                throw new RuntimeException (e);
            }

            System.out.println ("Generate a report sheet of the scores for all pupils in each of the exams done and rank them from the highest average score to lowest.");

            while (resultSet.next ()) {
                String first_name   = resultSet.getString ("first_name");
                String second_name  = resultSet.getString ("second_name");
                long   total_scores = resultSet.getLong ("total_scores");
                long   percentage   = resultSet.getLong ("percentage");

                System.out.println ("P.First_Name: " + first_name);
                System.out.println ("P.Second_Name: " + second_name);
                System.out.println ("Total Scores: " + total_scores);
                System.out.println ("Percentage: " + percentage);


                resultSet.close ();
                preparedStatement.close ();
            }
        } catch (SQLException e) {
            throw new RuntimeException (e);
        } catch (RuntimeException e) {
            throw new RuntimeException (e);
        }
    }

    public static void setDocument(org.w3c.dom.Document document) {
        Document = document;
    }
}

