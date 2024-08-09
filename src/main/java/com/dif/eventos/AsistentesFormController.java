package com.dif.eventos;

import com.dlsc.gemsfx.EmailField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class AsistentesFormController {
    public static boolean isCreate;
    public static String AST_COLLECTION = "Personas";
    public static String ERROR_STR = "Error";
    public static String SUCCESS_STR = "Éxito";
    public static String astId;

    @FXML
    Label lblTituloPer;
    @FXML
    TextField pNombreAst;
    @FXML
    TextField sNombreAst;
    @FXML
    TextField pApellidoAst;
    @FXML
    TextField mApellidoAst;
    @FXML
    EmailField correoAst;
    @FXML
    CheckBox chbEsOrg;
    @FXML
    CheckBox chbEsAst;

    private ObservableList<Persona> asistentesLista ;
    public Persona asistente;

    public void setAsistentesLista(ObservableList<Persona> asistentesLista) {
        this.asistentesLista = asistentesLista;
    }

    @FXML
    public void initialize(Persona ast, Boolean isInsert) throws UnknownHostException {
        isCreate = isInsert;
        if(isCreate){
            lblTituloPer.setText("Crear Registro de Persona");
        } else {
            lblTituloPer.setText("Modificar Registro de Persona");
            pNombreAst.setText(ast.getPNombre());
            sNombreAst.setText(ast.getSNombre());
            pApellidoAst.setText(ast.getPApellido());
            mApellidoAst.setText(ast.getMApellido());
            correoAst.setEmailAddress(ast.getCorreo());
            chbEsOrg.setSelected(ast.getEsOrganizador());
            chbEsAst.setSelected(ast.getEsAsistente());
            astId = ast.getId();
            asistente = ast;
        }
    }

    @FXML
    public void doSave(ActionEvent actionEvent) throws IOException {
        List<String> camposFaltantesLst = new ArrayList<>();
        if(pNombreAst.getText().isEmpty() || pNombreAst.getText().isBlank()){
            camposFaltantesLst.add("Primer Nombre");
        }
        if(pApellidoAst.getText().isEmpty() || pApellidoAst.getText().isBlank()){
            camposFaltantesLst.add("Apellido Paterno");
        }
        if(mApellidoAst.getText().isEmpty() || mApellidoAst.getText().isBlank()){
            camposFaltantesLst.add("Apellido Materno");
        }
        if(correoAst.getEmailAddress().isEmpty() || correoAst.getEmailAddress().isBlank()){
            camposFaltantesLst.add("Correo electrónico");
        }
        if(!chbEsAst.isSelected() && !chbEsOrg.isSelected()){
            camposFaltantesLst.add("Seleccione si es Asistente u Organizador/a");
        }

        String camposFaltantes = String.join(", ",camposFaltantesLst);

        if(camposFaltantesLst.isEmpty()){
            Persona persona = new Persona("",pNombreAst.getText(),sNombreAst.getText(),pApellidoAst.getText(),mApellidoAst.getText(),correoAst.getEmailAddress(),chbEsAst.isSelected(), chbEsOrg.isSelected());

            if(isCreate){
                doCreate(actionEvent, persona);
            } else {
                doModify(actionEvent, persona);
            }
        } else {
            throwAlert(ERROR_STR, "Por favor, complete los campos: " + camposFaltantes);
        }
    }

    public void doCreate(ActionEvent actionEvent, Persona ast) throws UnknownHostException {
        Document document = new Document()
                .append("nombre",ast.getPNombre())
                .append("segundoNombre",ast.getSNombre())
                .append("apellidoPaterno",ast.getPApellido())
                .append("apellidoMaterno",ast.getMApellido())
                .append("correo",ast.getCorreo())
                .append("esOrganizador",ast.getEsOrganizador())
                .append("esAsistente",ast.getEsAsistente());
        String idInserted = MongoUtils.insertDocumentInDB(AST_COLLECTION,document);
        if(idInserted != null){
            ast.setId(idInserted);
            asistentesLista.add(ast);
            throwAlert(SUCCESS_STR,"Se guardó el registro exitosamente");
            doCancel(actionEvent);
        } else {
            throwAlert(ERROR_STR,"No se pudo guardar el registro");
        }
    }
    public void doModify(ActionEvent actionEvent, Persona modifiedAst) throws UnknownHostException {
        modifiedAst.setId(astId);
        if(modifiedAst.toString().equals(asistente.toString())) {
            throwAlert("Sin cambios", "No se realizaron cambios en el registro");
            doCancel(actionEvent);
        } else {
            Bson filter = Filters.eq("_id", new ObjectId(astId));
            Bson updates = Updates.combine(
                    Updates.set("nombre",modifiedAst.getPNombre()),
                    Updates.set("segundoNombre",modifiedAst.getSNombre()),
                    Updates.set("apellidoPaterno",modifiedAst.getPApellido()),
                    Updates.set("apellidoMaterno",modifiedAst.getMApellido()),
                    Updates.set("correo",modifiedAst.getCorreo()),
                    Updates.set("esOrganizador",modifiedAst.getEsOrganizador()),
                    Updates.set("esAsistente",modifiedAst.getEsAsistente())
            );
            int indexEvt = asistentesLista.indexOf(asistente);
            Boolean isModified = MongoUtils.updateDocumentInDB(AST_COLLECTION,filter,updates);
            if(isModified){
                asistentesLista.set(indexEvt,modifiedAst);
                throwAlert(SUCCESS_STR,"Se actualizó el registro exitosamente");
                doCancel(actionEvent);
            } else {
                throwAlert(ERROR_STR,"No se pudo actualizar el registro");
            }
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
