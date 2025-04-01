package com.ejem.ejem;

import jakarta.annotation.PostConstruct;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class Controller implements Initializable {

    @Autowired
    private Repository repo;

    private int id;

    private Model item;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ListView<Model> listViewDatos;

    @FXML
    private TextField txtId, txtName, txtTelef;

    @FXML
    private Button btnDelete, btnUpdate, btnClear, btnCreate;

    @FXML
    private Label lblError, lblTotal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtId.setDisable(true);
        list();
        listViewDatos.getSelectionModel().selectedItemProperty()
                .addListener((obs,  old,  newValue) -> {
                    if (newValue != null){
                        id = newValue.getId();
                        item = item();
                        txtId.setText(String.valueOf(item.getId()));
                        txtName.setText(item.getNombre());
                        txtTelef.setText(item.getTelefono());
                        btnCreate.setVisible(false);
                        btnClear.setVisible(true);
                        btnDelete.setVisible(true);
                        btnUpdate.setVisible(true);
                        lblError.setVisible(false);

                    }
            }
        );
    }

    public Model item(){
        return repo.findById(id).get();
    }
    public void list(){
        listViewDatos.setItems(FXCollections.observableArrayList(repo.findAll()));
        lblTotal.setText(String.valueOf(listViewDatos.getItems().size()));
    }

    @FXML
    public void create(){
        if(!txtName.getText().trim().isEmpty()){
            String nombre = txtName.getText().trim();
            String telefono = txtTelef.getText().trim();

            // Validar nombre
            if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
                lblError.setText("El nombre solo puede contener letras y espacios");
                lblError.setVisible(true);
                return;
            }

            // Validar teléfono
            if (!telefono.matches("\\d{9}")) {
                lblError.setText("El teléfono debe tener exactamente 9 dígitos");
                lblError.setVisible(true);
                return;
            }

            // Si las validaciones son correctas, crea el modelo
            Model model = new Model();
            model.setNombre(nombre);
            model.setTelefono(telefono);
            repo.save(model);

            list(); // Actualiza la lista
            clear(); // Limpia los campos
        }else{
            lblError.setVisible(true);
        }
    }

    @FXML
    public void delete(){
        repo.delete(item);
        clear();
        list();
    }

    @FXML
    public void update(){
        String nombre = txtName.getText().trim();
        String telefono = txtTelef.getText().trim();

        // Validar nombre
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            lblError.setText("El nombre solo puede contener letras y espacios");
            lblError.setVisible(true);
            return;
        }

        // Validar teléfono
        if (!telefono.matches("\\d{9}")) {
            lblError.setText("El teléfono debe tener exactamente 9 dígitos");
            lblError.setVisible(true);
            return;
        }

        // Si las validaciones son correctas, actualiza el modelo
        item.setNombre(nombre);
        item.setTelefono(telefono);
        repo.save(item);

        list(); // Actualiza la lista
    }

    @FXML
    public void clear(){
        anchorPane.getChildren().forEach( node -> {
            if (node instanceof TextField)((TextField) node).clear();
        });
        btnCreate.setVisible(true);
        btnClear.setVisible(false);
        btnUpdate.setVisible(false);
        btnDelete.setVisible(false);
    }

    @PostConstruct
    public void testConnection() {
        try {
            long count = repo.count();
            System.out.println("Conexión exitosa. Número de registros: " + count);
        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

}
