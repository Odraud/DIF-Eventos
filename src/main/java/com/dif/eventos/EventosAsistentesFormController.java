package com.dif.eventos;

import com.mongodb.client.FindIterable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.bson.Document;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import static com.dif.eventos.EventosFormController.ERROR_STR;
import static com.dif.eventos.EventosFormController.SUCCESS_STR;

public class EventosAsistentesFormController {
    public String eventoId;
    public String AST_COLLECTION = "Personas";
    public String EVTAST_COLLECTION = "EventoPersona";
    public static Map<String, String> astMap = new HashMap<>();
    @FXML
    SearchableComboBox<KeyValuePair> cmbAsistente;
    public void initialize(String evtId) throws UnknownHostException {
        eventoId = evtId;
        String key = "esAsistente";
        Boolean value = true;
        String[] qry = {"_id", "nombre","segundoNombre","apellidoPaterno","apellidoMaterno","correo"};
        FindIterable<Document> documents = MongoUtils.findAllDocuments(AST_COLLECTION, key, value, qry);
        String nombreCompleto = "";
        for(Document doc : documents){
            nombreCompleto = doc.get("nombre").toString() + " ";
            nombreCompleto += !doc.get("segundoNombre").toString().isEmpty() ? doc.get("segundoNombre").toString() + " " : "";
            nombreCompleto += doc.get("apellidoPaterno").toString() + " ";
            nombreCompleto += !doc.get("apellidoMaterno").toString().isEmpty() ? doc.get("apellidoMaterno").toString()  : "";
            KeyValuePair kvp = new KeyValuePair(doc.get("_id").toString(),nombreCompleto);
            cmbAsistente.getItems().add(kvp);
            astMap.put(doc.get("_id").toString(), doc.get("correo").toString());
        }
    }
    @FXML
    public void doSave(ActionEvent actionEvent) throws IOException {
        boolean faltaAsistente = true;

        if(cmbAsistente.getValue() != null){
            faltaAsistente = false;
        }
        if(!faltaAsistente){
            doCreate(actionEvent);
        } else {
            throwAlert(ERROR_STR,"Por favor, ingrese al asistente");
        }

    }
    public void doCreate(ActionEvent actionEvent) throws UnknownHostException {
        Document document = new Document()
                .append("eventoId",eventoId)
                .append("asistenteId",cmbAsistente.getValue().getKey())
                .append("correo",astMap.get(cmbAsistente.getValue().getKey()));
        String idInserted = MongoUtils.insertDocumentInDB(EVTAST_COLLECTION,document);
        if(idInserted != null){
            throwAlert(SUCCESS_STR,"Se guardó el registro exitosamente");
            doCancel(actionEvent);
        } else {
            throwAlert(ERROR_STR,"No se pudo guardar el registro");
        }
    }
    public void doCancel(ActionEvent actionEvent){
        Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
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
