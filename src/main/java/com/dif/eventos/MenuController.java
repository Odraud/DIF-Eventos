package com.dif.eventos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import java.io.IOException;
import java.net.UnknownHostException;

public class MenuController {
    @FXML Pane childPane;
    @FXML
    protected void onEventos() throws UnknownHostException {
        openChild("eventos");
    }
    @FXML
    protected void onAsistentes() throws UnknownHostException {
        openChild("asistentes");
    }
    @FXML
    protected void onEstadisticas() throws UnknownHostException {
        openChild("estadisticas");
    }
    @FXML
    protected void onUsuarios() throws UnknownHostException {
        openChild("usuarios");
    }
    @FXML
    protected void doCerrarSesion() throws IOException {
        StartApplication.setRoot("login");
    }
    public void openChild(String fxml) {
        try {
            if(!childPane.getChildren().isEmpty()){
                childPane.getChildren().clear();
            }
            FXMLLoader childLoader = new FXMLLoader(getClass().getResource(fxml + ".fxml"));
            Pane newLoadedPane = childLoader.load();
            childPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
