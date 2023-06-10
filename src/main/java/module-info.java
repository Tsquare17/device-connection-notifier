module com.tsquare.deviceconnectionnotifier {
    requires javafx.controls;
    requires javafx.fxml;

    requires twilio;

    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.tsquare.deviceconnectionnotifier to javafx.fxml, twilio;
    exports com.tsquare.deviceconnectionnotifier;
}