module ru.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires java.xml;
    requires ru.itis.prot;
    requires java.desktop;
    requires javafx.media;

    opens org.example.client to javafx.fxml;
    exports org.example.client;
    exports org.example.client.connectors;
    opens org.example.client.connectors to javafx.fxml;
    exports org.example.client.controllers;
    opens org.example.client.controllers to javafx.fxml;
    exports org.example.client.animaitedPanes to javafx.fxml;
}