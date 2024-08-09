package com.dif.eventos;

import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.TimePicker;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.controlsfx.control.SearchableComboBox;
import java.io.IOException;
import java.net.UnknownHostException;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventosFormController {
    public static boolean isCreate;
    public static String EVENT_COLLECTION = "Eventos";
    public static String ORG_COLLECTION = "Personas";
    public static String ERROR_STR = "Error";
    public static String SUCCESS_STR = "Éxito";
    public static Map<String, KeyValuePair> orgMap;
    public static String eventId;
    @FXML
    AnchorPane anPane;
    @FXML
    Label lblTitulo;
    @FXML
    TextField txtNombreEvento;
    @FXML
    TextField txtLugar;
    @FXML
    TimePicker tpHora;
    @FXML
    CalendarPicker cpFecha;
    @FXML
    Button btnCancelar;
    @FXML
    SearchableComboBox <KeyValuePair> cmbOrganizador;
    private ObservableList<EventoDTO> eventosLista ;
    public EventoDTO event;

    public void setEventList(ObservableList<EventoDTO> eventosLista) {
        this.eventosLista = eventosLista;
    }

    @FXML
    public void initialize(EventoDTO evt, Boolean isInsert) throws UnknownHostException {

        orgMap = new HashMap<>();
        isCreate = isInsert;
        fillCmbOrganizador();
        if(isCreate){
            lblTitulo.setText("Crear Evento");
        } else {
            lblTitulo.setText("Modificar Evento");
            txtNombreEvento.setText(evt.getNombreDeEvento());
            txtLugar.setText(evt.getLugarDeEvento());
            String[] horaPartes = evt.getHoraDeEvento().split(":");
            LocalTime localTime = LocalTime.of(Integer.valueOf(horaPartes[0]),Integer.valueOf(horaPartes[1]));
            tpHora.setValue(localTime);
            tpHora.setTime(localTime);
            String[] fechaPartes = evt.getFechaDeEvento().split("/");
            LocalDate localDate = LocalDate.of(Integer.valueOf(fechaPartes[2]),Integer.valueOf(fechaPartes[1]),Integer.valueOf(fechaPartes[0]));
            cpFecha.setValue(localDate);
            cmbOrganizador.setValue(orgMap.get(evt.getOrganizador()));
            eventId = evt.getId();
            System.out.println("eventId: " + evt.getId());

            event = evt;
        }
    }
    public void fillCmbOrganizador(){
        String key = "esOrganizador";
        Boolean value = true;
        String[] qry = {"_id", "nombre","segundoNombre","apellidoPaterno","apellidoMaterno"};
        FindIterable<Document> documents = MongoUtils.findAllDocuments(ORG_COLLECTION, key, value, qry);
        String nombreCompleto = "";
        for(Document doc : documents){
            nombreCompleto = doc.get("nombre").toString() + " ";
            nombreCompleto += !doc.get("segundoNombre").toString().isEmpty() ? doc.get("segundoNombre").toString() + " " : "";
            nombreCompleto += doc.get("apellidoPaterno").toString() + " ";
            nombreCompleto += !doc.get("apellidoMaterno").toString().isEmpty() ? doc.get("apellidoMaterno").toString()  : "";
            KeyValuePair kvp = new KeyValuePair(doc.get("_id").toString(),nombreCompleto);
            cmbOrganizador.getItems().add(kvp);
            orgMap.put(doc.get("_id").toString(), kvp);
        }
    }
    @FXML
    public void doSave(ActionEvent actionEvent) throws IOException {
        List<String> camposFaltantesLst =new ArrayList<>();
        if(txtNombreEvento.getText().isEmpty() || txtNombreEvento.getText().isBlank()){
            camposFaltantesLst.add("Nombre del evento");
        }
        if(txtLugar.getText().isEmpty() || txtLugar.getText().isBlank()){
            camposFaltantesLst.add("Lugar");
        }
        if(tpHora.getTime()== null){
            camposFaltantesLst.add("Hora");
        }
        if(cpFecha.getValue() == null){
            camposFaltantesLst.add("Fecha");
        }
        if(cmbOrganizador.getValue() == null){
            camposFaltantesLst.add("Organizador");
        }
        String camposFaltantes = String.join(", ",camposFaltantesLst);
        if(camposFaltantesLst.isEmpty()){
            String[] fechaPartes = cpFecha.getValue().toString().split("-");
            String fecha = fechaPartes[2] + "/" + fechaPartes[1] + "/" + fechaPartes[0];
            LocalDateTime ldt = LocalDateTime.of(cpFecha.getValue(), tpHora.getTime());
            EventoDTO evento = new EventoDTO("",txtNombreEvento.getText(),fecha,tpHora.getTime().toString(),txtLugar.getText(),cmbOrganizador.getValue().getKey());
            if(isCreate){
                doCreate(ldt, actionEvent, evento);
            } else {
                doModify(ldt,actionEvent, evento);
            }
        } else {
            throwAlert(ERROR_STR, "Por favor, complete los campos: " + camposFaltantes);
        }
    }

    public void doCreate(LocalDateTime localDateTime, ActionEvent actionEvent, EventoDTO evt) throws UnknownHostException {
        Document document = new Document()
                .append("nombreDeEvento",txtNombreEvento.getText())
                .append("fechaDeEvento",localDateTime)
                .append("lugarDeEvento",txtLugar.getText())
                .append("organizadorDeEvento",cmbOrganizador.getSelectionModel().getSelectedItem().getKey());
        String idInserted = MongoUtils.insertDocumentInDB(EVENT_COLLECTION,document);
        if(idInserted != null){
            evt.setId(idInserted);
            eventosLista.add(evt);
            throwAlert(SUCCESS_STR,"Se guardó el registro exitosamente");
            doCancel(actionEvent);
        } else {
            throwAlert(ERROR_STR,"No se pudo guardar el registro");
        }
    }
    public void doModify(LocalDateTime localDateTime, ActionEvent actionEvent, EventoDTO modifiedEvent) throws UnknownHostException {
        modifiedEvent.setId(eventId.toString());

        if(modifiedEvent.toString().equals(event.toString())){
            throwAlert("Sin cambios","No se realizaron cambios en el registro");
            doCancel(actionEvent);
        }else{
            Bson filter = Filters.eq("_id", new ObjectId(eventId));
            Bson updates = Updates.combine(
                    Updates.set("nombreDeEvento",txtNombreEvento.getText()),
                    Updates.set("fechaDeEvento",localDateTime),
                    Updates.set("lugarDeEvento",txtLugar.getText()),
                    Updates.set("organizadorDeEvento",cmbOrganizador.getSelectionModel().getSelectedItem().getKey())
            );
            int indexEvt = eventosLista.indexOf(event);
            Boolean isModified = MongoUtils.updateDocumentInDB(EVENT_COLLECTION,filter,updates);
            if(isModified){
                modifiedEvent.setId(eventId);
                eventosLista.set(indexEvt,modifiedEvent);
                throwAlert(SUCCESS_STR,"Se actualizó el registro exitosamente");
                doCancel(actionEvent);
            } else {
                throwAlert(ERROR_STR,"No se pudo actualizar el registro");
            }
        }

    }
    public void doCancel(ActionEvent actionEvent){
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }
    public void throwAlert(String title, String msg){
        Alert alrt;
        if(title.equals(ERROR_STR)){
            alrt = new Alert(Alert.AlertType.ERROR);
        } else {
            alrt = new Alert(Alert.AlertType.INFORMATION);
        }
        alrt.setTitle(title);
        alrt.setHeaderText(null);
        alrt.setResizable(false);
        alrt.setContentText(msg);
        alrt.show();
    }
}
