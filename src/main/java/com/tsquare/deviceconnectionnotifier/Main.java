package com.tsquare.deviceconnectionnotifier;

import com.tsquare.deviceconnectionnotifier.Database.Schema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Main.setup();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 480);
        stage.setTitle("Device Connection Notifier");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    protected static void setup() throws SQLException {
        File dir = new File(System.getProperty("user.home") + "/.device-connection-notifier");
        if(!dir.exists()) {
            dir.mkdir();
        }

        Schema schema = new Schema();
        schema.up();
    }
}