package com.tsquare.deviceconnectionnotifier.Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Schema {
    private final List<String> tables = new ArrayList<>();

    public void up() throws SQLException {
        Connection connection = SqliteConnection.getConnection();

        DatabaseMetaData databaseMetaData = connection.getMetaData();

        ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[] {"TABLE"});

        while (resultSet.next()) {
            String name = resultSet.getString("TABLE_NAME");

            tables.add(name);
        }

        resultSet.close();
        connection.close();

        Builder builder = new Builder();

        if (!tables.contains("fields")) {
            builder.createFieldsTable();
        }

        builder.close();
    }
}
