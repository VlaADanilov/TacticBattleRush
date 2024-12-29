module ru.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;

    opens org.example.client to javafx.fxml;
    exports org.example.client;
    exports org.example.client.connectors;
    exports org.example.client.protocol;
    opens org.example.client.connectors to javafx.fxml;
    exports org.example.client.controllers;
    opens org.example.client.controllers to javafx.fxml;
}