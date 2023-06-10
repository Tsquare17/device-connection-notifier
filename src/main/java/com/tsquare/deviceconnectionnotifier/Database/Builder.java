package com.tsquare.deviceconnectionnotifier.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Builder {
    Connection connection = SqliteConnection.getConnection();

    public Builder() throws SQLException {}

    public void close() throws SQLException {
        connection.close();
    }

    public void reset() throws SQLException {
        connection.close();

        connection = SqliteConnection.getConnection();
    }

    public void createFieldsTable() throws SQLException {
        String sql = """
            create table if not exists fields
            (
                id     INTEGER
                    primary key autoincrement,
                field_option VARCHAR(255) not null,
                field_value  TEXT not null
            );

            create unique index fields_option
                on fields (field_option);
            """;

        Statement statement = connection.createStatement();

        statement.execute(sql);

        statement.close();
    }
}
