package com.tsquare.deviceconnectionnotifier.Database;

import java.sql.*;

public class Fields {
    Connection connection = SqliteConnection.getConnection();

    Statement statement = null;

    PreparedStatement preparedStatement = null;

    public Fields() throws SQLException {}

    public void close() throws SQLException {
        if (statement != null && !statement.isClosed()) {
            statement.close();
        }

        if (preparedStatement != null && !preparedStatement.isClosed()) {
            preparedStatement.close();
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public ResultSet getField(String value) throws SQLException {
        String sql = "select * from fields where field_option = ?";

        preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, value);

        return preparedStatement.executeQuery();
    };

    public int insertField(String name, String value) throws SQLException {

        preparedStatement = connection.prepareStatement(
                "insert into fields (field_option, field_value) values (?, ?)"
        );

        preparedStatement.setString(1, name);
        preparedStatement.setString(2, value);

        int rows = preparedStatement.executeUpdate();

        if (rows == 0) {
            return 0;
        }

        int key = 0;
        try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
            if (keys.next()) {
                key = keys.getInt(1);
            }
        }

        return key;
    }

    public void updateField(
            String name,
            String value
    ) throws SQLException {

        preparedStatement = connection.prepareStatement("update fields set field_value = ? where field_option = ?");

        preparedStatement.setString(1, value);
        preparedStatement.setString(2, name);

        preparedStatement.executeUpdate();
    }
}
