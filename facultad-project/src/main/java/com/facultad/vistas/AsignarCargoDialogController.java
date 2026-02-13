package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.Docente;
import com.facultad.modelo.Instituto;
import com.facultad.modelo.CargoDocente;
import com.facultad.servicios.InstitutoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class AsignarCargoDialogController {
    
    @FXML private Label lblDocente;
    @FXML private ComboBox<Instituto> comboInstitutos;
    @FXML private Spinner<Integer> spinnerHoras;
    @FXML private Button btnAsignar;
    @FXML private Button btnCancelar;
    @FXML private Label lblMensaje;
    
    private Docente docente;
    private InstitutoService institutoService;
    private boolean cargoAsignado = false;
    
    public AsignarCargoDialogController() {
        this.institutoService = MainApp.getInstitutoService();
    }
    
    @FXML
    public void initialize() {
        // Configurar spinner de horas (10-60 horas, incremento de 5)
        SpinnerValueFactory<Integer> valueFactory = 
            new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 60, 40, 5);
        spinnerHoras.setValueFactory(valueFactory);
        
        // Cargar institutos en el combo
        cargarInstitutos();
    }
    
    public void setDocente(Docente docente) {
        this.docente = docente;
        if (docente != null) {
            lblDocente.setText(docente.getNombreCompleto());
        }
    }
    
    public boolean isCargoAsignado() {
        return cargoAsignado;
    }
    
    private void cargarInstitutos() {
        try {
            List<Instituto> institutos = institutoService.obtenerTodos();
            ObservableList<Instituto> institutosObservable = FXCollections.observableArrayList(institutos);
            comboInstitutos.setItems(institutosObservable);
            
            if (!institutosObservable.isEmpty()) {
                comboInstitutos.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            mostrarMensaje("Error al cargar institutos: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void asignarCargo() {
        if (validarDatos()) {
            try {
                Instituto instituto = comboInstitutos.getValue();
                Integer horas = spinnerHoras.getValue();
                
                // Crear nuevo cargo
                CargoDocente cargo = new CargoDocente(horas, instituto, docente);
                
                // Agregar cargo al docente (esto se persiste a través del servicio)
                docente.agregarCargo(cargo);
                
                cargoAsignado = true;
                cerrarDialogo();
                
            } catch (Exception e) {
                mostrarMensaje("❌ Error al asignar cargo: " + e.getMessage(), true);
            }
        }
    }
    
    @FXML
    private void cancelar() {
        cargoAsignado = false;
        cerrarDialogo();
    }
    
    private boolean validarDatos() {
        if (comboInstitutos.getValue() == null) {
            mostrarMensaje("❌ Debe seleccionar un instituto", true);
            return false;
        }
        
        // Verificar que el docente no tenga ya un cargo en ese instituto
        Instituto institutoSeleccionado = comboInstitutos.getValue();
        boolean yaTieneCargo = docente.getCargos().stream()
            .anyMatch(c -> c.getInstituto().equals(institutoSeleccionado));
        
        if (yaTieneCargo) {
            mostrarMensaje("❌ El docente ya tiene un cargo en este instituto", true);
            return false;
        }
        
        return true;
    }
    
    private void mostrarMensaje(String mensaje, boolean esError) {
        lblMensaje.setText(mensaje);
        if (esError) {
            lblMensaje.setStyle("-fx-text-fill: #e74c3c;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #27ae60;");
        }
    }
    
    private void cerrarDialogo() {
        Stage stage = (Stage) btnAsignar.getScene().getWindow();
        stage.close();
    }
}