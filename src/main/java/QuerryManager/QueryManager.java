package QuerryManager;

import Utilities.ConnectionsXmlReader;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryManager {

    private ResultSetMetaData metaData;

    public Connection establishConnection() {
        return ConnectionsXmlReader.getDbConnection();
    }


    public List<LinkedHashMap<String, Object>> select(String sqlQuery, Map<Integer, Object> values) throws ClassNotFoundException, SQLException {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = statement.executeQuery()) {

            metaData = resultSet.getMetaData(); /* Setting the metadata */
            int count = metaData.getColumnCount();
            List<LinkedHashMap<String, Object>> results = new ArrayList<>();

            while (resultSet.next()) {
                LinkedHashMap<String, Object> result = new LinkedHashMap<>();
                for (int i = 1; i <= count; i++) {
                    result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                results.add(result);
            }
            return results;
        }
    }

    public int insert(String sqlQuery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                statement.setObject(Integer.parseInt(key), value);
            }

            return statement.executeUpdate();
        }
    }

    public int update(String sqlQuery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                statement.setObject(Integer.parseInt(key), value);
            }

            return statement.executeUpdate();
        }
    }

    public int delete(String sqlQuery, LinkedHashMap<String, Object> values) throws ClassNotFoundException, SQLException {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            for (Map.Entry<String, Object> entry : values.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                statement.setObject(Integer.parseInt(key), value);
            }

            return statement.executeUpdate();
        }
    }

    public ResultSetMetaData getMetaData() {
        return this.metaData;
    }
}
