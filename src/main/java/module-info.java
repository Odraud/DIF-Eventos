module com.dif.eventos {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires com.fasterxml.jackson.databind;
    requires com.dlsc.gemsfx;
    requires org.controlsfx.controls;
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.gmail;
    requires com.google.auth.oauth2;
    requires org.apache.commons.codec;
    requires java.mail;
    requires com.google.auth;


    opens com.dif.eventos to javafx.fxml;
    exports com.dif.eventos;
}