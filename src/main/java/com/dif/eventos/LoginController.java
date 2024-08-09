package com.dif.eventos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import java.net.UnknownHostException;

public class LoginController {

    @FXML
    private TextField userField;
    @FXML
    private TextField passField;
    public static String COLLECTION_NAME = "Usuarios";
    @FXML
    protected void doLogin() throws UnknownHostException {
        MongoDatabase db = MongoUtils.connect();
        String password = passField.getText();
        String username = userField.getText();

        if(username.isBlank() || username.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese su usuario");
            alert.showAndWait();
        } else if (password.isBlank() || password.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, ingrese su contraseña");
            alert.showAndWait();

        } else {
            String[] qry = {"username", "password"};
            String user = MongoUtils.findOneDocument(db,COLLECTION_NAME,"username",username, qry);
            if(user == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("El usuario no existe. Póngase en contacto con el Administrador.");
                alert.showAndWait();
            } else {
                try{
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(user);
                    String dbPassword = jsonNode.get("password").asText();
                    if(password.equals(dbPassword)){
                        StartApplication.setRoot("menu");
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("La contraseña es incorrecta.");
                        alert.showAndWait();
                    }
                }
                catch(Exception ex){
                    System.err.println("Exception: " + ex.getMessage() + " | " + ex.getCause());
                }
            }
        }

    }
}