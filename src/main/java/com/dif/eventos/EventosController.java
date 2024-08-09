package com.dif.eventos;

import com.mongodb.client.FindIterable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.bson.Document;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventosController {
    public static String EVENTOS_COLLECTION = "Eventos";
    public ObservableList<EventoDTO> eventosLista;
    @FXML public TableView<EventoDTO> tableEventos;
    @FXML private TableColumn nombreCol ;
    @FXML private TableColumn fechaCol ;
    @FXML private TableColumn horaCol;
    @FXML private TableColumn lugarCol ;
    @FXML AnchorPane eventosPane;
    public String EVTAST_COLLECTION = "EventoPersona";
    public String SENDER_MAIL = "daxter040798@gmail.com";
    @FXML
    public void initialize() throws UnknownHostException {
        nombreCol.setCellValueFactory(new PropertyValueFactory("nombreDeEvento"));
        fechaCol.setCellValueFactory(new PropertyValueFactory("fechaDeEvento"));
        horaCol.setCellValueFactory(new PropertyValueFactory("horaDeEvento"));
        lugarCol.setCellValueFactory(new PropertyValueFactory("lugarDeEvento"));
        eventosLista = FXCollections.observableArrayList();
        FindIterable<Document> documents = MongoUtils.findAllDocuments(EVENTOS_COLLECTION, null, null, null);
        String id,nombreDeEvento,fechaDeEvento,lugarDeEvento,organizador;

        try{
            for(Document doc : documents){
                fechaDeEvento = "";
                id = doc.containsKey("_id") ? doc.get("_id").toString() : null;
                nombreDeEvento = doc.containsKey("nombreDeEvento") ? doc.get("nombreDeEvento").toString() : null;
                if(doc.containsKey("fechaDeEvento")){
                    fechaDeEvento =  doc.containsKey("fechaDeEvento") ? doc.get("fechaDeEvento").toString() : null;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                Date parsedDate = sdf.parse(fechaDeEvento);

                SimpleDateFormat formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                formattedDate.setTimeZone(TimeZone.getTimeZone("UTC"));
                String dateStr = formattedDate.format(parsedDate);

                //Formatear Fecha
                String date = dateStr.substring(0, dateStr.indexOf(" "));
                String[] fechaPartes = date.split("-");
                String fechaFinal = fechaPartes[2] + "/" + fechaPartes[1] + "/" + fechaPartes[0];

                //Formatear Hora
                String horaCompleta = dateStr.substring(dateStr.indexOf(" ") + 1, dateStr.length());
                String[] horaPartes = horaCompleta.split(":");
                String hora = horaPartes[0] + ":" + horaPartes[1];

                lugarDeEvento =  doc.containsKey("lugarDeEvento") ? doc.get("lugarDeEvento").toString() : null;
                organizador = doc.containsKey("organizadorDeEvento") ? doc.get("organizadorDeEvento").toString() : null;

                EventoDTO evnt = new EventoDTO(id,nombreDeEvento,fechaFinal,hora,lugarDeEvento,organizador);
                eventosLista.add(evnt);
            }
            tableEventos.setItems(eventosLista);
        } catch (Exception ex){
            System.err.println("Exception: " + ex.getMessage());
        }
    }
    @FXML
    public void doCreate() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("eventosForm.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            EventosFormController evtFormCtrl = fxmlLoader.getController();
            evtFormCtrl.setEventList(eventosLista);
            evtFormCtrl.initialize(null, true);

            Stage stage = new Stage();
            stage.setTitle("Crear Evento");
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
        EventoDTO event = new EventoDTO();
        try{
            row = tableEventos.getSelectionModel().getSelectedIndex();
            event = tableEventos.getItems().get(row);
            try {
                modifyInDB(event);
            } catch (UnknownHostException e) {
                Alert alrt = new Alert(Alert.AlertType.ERROR);
                alrt.setTitle("Error");
                alrt.setHeaderText(null);
                alrt.setResizable(false);
                alrt.setContentText("Por favor, seleccione un evento.");
                alrt.show();
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            Alert alrt = new Alert(Alert.AlertType.ERROR);
            alrt.setTitle("Error");
            alrt.setHeaderText(null);
            alrt.setResizable(false);
            alrt.setContentText("Por favor, seleccione un evento.");
            alrt.show();
        }

    }
    public void modifyInDB(EventoDTO evt) throws UnknownHostException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("eventosForm.fxml"));
            fxmlLoader.setLocation(getClass().getResource("eventosForm.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            EventosFormController evtFormCtrl = fxmlLoader.getController();
            evtFormCtrl.setEventList(eventosLista);
            evtFormCtrl.initialize(evt, false);

            Stage stage = new Stage();
            stage.setTitle("Modificar Evento");
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
        a.setTitle("Eliminar evento");
        a.setHeaderText(null);
        a.setResizable(false);
        a.setContentText("¿Está seguro que desea eliminar el evento?");
        a.showAndWait().ifPresent(response -> {
            if (response == aceptarBtn) {
                int row;
                try{
                    row = tableEventos.getSelectionModel().getSelectedIndex();
                    EventoDTO event = tableEventos.getItems().get(row);
                    try {
                        deleteInDB(event);
                    } catch (UnknownHostException e) {
                        Alert alrt = new Alert(Alert.AlertType.ERROR);
                        alrt.setTitle("Error");
                        alrt.setHeaderText(null);
                        alrt.setResizable(false);
                        alrt.setContentText("Por favor, seleccione un evento.");
                        alrt.show();
                    }
                    tableEventos.getItems().remove(row);
                } catch (Exception ex){
                    System.err.println(ex.getMessage());
                }
            }
        });
    }
    public void deleteInDB(EventoDTO evt) throws UnknownHostException {
        boolean deleted = MongoUtils.deleteOne(EVENTOS_COLLECTION,"_id",evt.getId());
        if(deleted){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Resultado exitoso");
            a.setHeaderText(null);
            a.setResizable(false);
            a.setContentText("Se eliminó el evento correctamente.");
            a.show();
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Resultado fallido");
            a.setHeaderText(null);
            a.setResizable(false);
            a.setContentText("No se pudo eliminar el evento.");
            a.show();
        }
    }
    @FXML
    public void addAsistente(){
        int row;
        try{
            row = tableEventos.getSelectionModel().getSelectedIndex();
            EventoDTO event = tableEventos.getItems().get(row);
            System.out.println("id: " + event.getId());
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("eventosAsistentesForm.fxml"));
                Parent root = (Parent) fxmlLoader.load();
                EventosAsistentesFormController evtAstFormCtrl = fxmlLoader.getController();
                evtAstFormCtrl.initialize(event.getId());
                Stage stage = new Stage();
                stage.setTitle("Crear Evento");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
            Alert alrt = new Alert(Alert.AlertType.ERROR);
            alrt.setTitle("Error");
            alrt.setHeaderText(null);
            alrt.setResizable(false);
            alrt.setContentText("Por favor, seleccione un evento.");
            alrt.show();
        }

    }
    @FXML
    public void doNotification(){
        int row;
        EventoDTO event;
        try{
            row = tableEventos.getSelectionModel().getSelectedIndex();
            event = tableEventos.getItems().get(row);

            FindIterable<Document> documents = MongoUtils.findAllDocuments(EVTAST_COLLECTION, "eventoId", event.getId(), null);
            for(Document doc: documents){
                NotificationController.sendEmail(SENDER_MAIL, doc.get("correo").toString(),event);
            }
        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }
    }

}
