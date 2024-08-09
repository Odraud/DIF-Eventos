package com.dif.eventos;

import com.mongodb.client.FindIterable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.UnknownHostException;

public class AsistentesController {
    public static String ASISTENTES_COLLECTION = "Personas";
    public ObservableList<Persona> asistLista;
    @FXML
    public TableView<Persona> tableAsistentes;
    @FXML private TableColumn nombreAsistenteCol ;
    @FXML private TableColumn sNombreAsistenteCol ;
    @FXML private TableColumn apellidoPAsistenteCol;
    @FXML private TableColumn apellidoMAsistenteCol;
    @FXML private TableColumn correoAsistenteCol;
    @FXML private TableColumn organizadorCol;
    @FXML private TableColumn asistenteCol;
    @FXML
    AnchorPane eventosPane;

    @FXML
    public void initialize() throws UnknownHostException {
        nombreAsistenteCol.setCellValueFactory(new PropertyValueFactory("pNombre"));
        sNombreAsistenteCol.setCellValueFactory(new PropertyValueFactory("sNombre"));
        apellidoPAsistenteCol.setCellValueFactory(new PropertyValueFactory("pApellido"));
        apellidoMAsistenteCol.setCellValueFactory(new PropertyValueFactory("mApellido"));
        correoAsistenteCol.setCellValueFactory(new PropertyValueFactory("correo"));
        organizadorCol.setCellValueFactory(new PropertyValueFactory("esOrganizador"));
        asistenteCol.setCellValueFactory(new PropertyValueFactory("esAsistente"));

        asistLista = FXCollections.observableArrayList();
        FindIterable<Document> documents = MongoUtils.findAllDocuments(ASISTENTES_COLLECTION, null, null, null);
        String id,pNombre,sNombre,pApellido,mApellido,correo;
        Boolean esAsist,esOrg;

        try{
            for(Document doc : documents){
                id = doc.containsKey("_id") ? doc.get("_id").toString() : null;
                pNombre = doc.containsKey("nombre") ? doc.get("nombre").toString() : null;
                sNombre = doc.containsKey("segundoNombre") ? doc.get("segundoNombre").toString() : null;
                pApellido = doc.containsKey("apellidoPaterno") ? doc.get("apellidoPaterno").toString() : null;
                mApellido = doc.containsKey("apellidoMaterno") ? doc.get("apellidoMaterno").toString() : null;
                correo = doc.containsKey("correo") ? doc.get("correo").toString() : null;
                esAsist = (Boolean)(doc.get("esAsistente"));
                esOrg = (Boolean)(doc.get("esOrganizador"));

                Persona persona = new Persona(id,pNombre,sNombre,pApellido,mApellido,correo,esAsist,esOrg);
                asistLista.add(persona);
            }
            tableAsistentes.setItems(asistLista);
        } catch (Exception ex){
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    @FXML
    public void doCreate() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("asistentesForm.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            AsistentesFormController astFormCtrl = fxmlLoader.getController();
            astFormCtrl.setAsistentesLista(asistLista);
            astFormCtrl.initialize(null, true);
            Stage stage = new Stage();
            stage.setTitle("Crear Asistente");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
    @FXML
    public void doModify(){
        int row;
        try{
            row = tableAsistentes.getSelectionModel().getSelectedIndex();
            Persona ast = tableAsistentes.getItems().get(row);
            try {
                modifyInDB(ast);
            } catch (UnknownHostException e) {
                Alert alrt = new Alert(Alert.AlertType.ERROR);
                alrt.setTitle("Error");
                alrt.setHeaderText(null);
                alrt.setResizable(false);
                alrt.setContentText("Por favor, seleccione a un asistente.");
                alrt.show();
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            Alert alrt = new Alert(Alert.AlertType.ERROR);
            alrt.setTitle("Error");
            alrt.setHeaderText(null);
            alrt.setResizable(false);
            alrt.setContentText("Por favor, seleccione a un asistente.");
            alrt.show();
        }

    }
    public void modifyInDB(Persona ast) throws UnknownHostException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("asistentesForm.fxml"));
            fxmlLoader.setLocation(getClass().getResource("asistentesForm.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            AsistentesFormController astFormCtrl = fxmlLoader.getController();
            astFormCtrl.setAsistentesLista(asistLista);
            astFormCtrl.initialize(ast, false);

            Stage stage = new Stage();
            stage.setTitle("Modificar Asistente");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
    @FXML
    public void doDelete(){
        ButtonType aceptarBtn = new ButtonType("Aceptar");
        ButtonType cancelarBtn = new ButtonType("Cancelar");
        Alert a = new Alert(Alert.AlertType.INFORMATION, "",aceptarBtn,cancelarBtn);
        a.setTitle("Eliminar Asistente");
        a.setHeaderText(null);
        a.setResizable(false);
        a.setContentText("¿Está seguro que desea eliminar este asistente?");
        a.showAndWait().ifPresent(response -> {
            if (response == aceptarBtn) {
                int row;
                try{
                    row = tableAsistentes.getSelectionModel().getSelectedIndex();
                    Persona ast = tableAsistentes.getItems().get(row);
                    try {
                        deleteInDB(ast);
                    } catch (UnknownHostException e) {
                        Alert alrt = new Alert(Alert.AlertType.ERROR);
                        alrt.setTitle("Error");
                        alrt.setHeaderText(null);
                        alrt.setResizable(false);
                        alrt.setContentText("Por favor, seleccione un asistente.");
                        alrt.show();
                    }
                    tableAsistentes.getItems().remove(row);
                } catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
            }
        });
    }
    public void deleteInDB(Persona ast) throws UnknownHostException {
        boolean deleted = MongoUtils.deleteOne(ASISTENTES_COLLECTION,"_id",ast.getId());
        if(deleted){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Resultado exitoso");
            a.setHeaderText(null);
            a.setResizable(false);
            a.setContentText("Se eliminó el asistente correctamente.");
            a.show();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Resultado fallido");
            a.setHeaderText(null);
            a.setResizable(false);
            a.setContentText("No se pudo eliminar al asistente.");
            a.show();
        }
    }
}
