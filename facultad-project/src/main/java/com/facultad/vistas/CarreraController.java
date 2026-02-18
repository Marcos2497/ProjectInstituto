package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.Carrera;
import com.facultad.servicios.CarreraService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CarreraController {
    
    @FXML private TableView<Carrera> tablaCarreras;
    @FXML private TableColumn<Carrera, String> colCodigo;
    @FXML private TableColumn<Carrera, String> colNombre;
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtBuscar;
    
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    
    @FXML private Label lblMensaje;
    @FXML private Label lblEstado;
    
    private CarreraService carreraService;
    private ObservableList<Carrera> carrerasObservable;
    private Carrera carreraSeleccionada;
    
    @FXML
    public void initialize() {
        System.out.println("🔄 Inicializando CarreraController...");
        
        try {
            carreraService = MainApp.getCarreraService();
            configurarTabla();
            cargarCarreras();
            configurarListeners();
            
            System.out.println("✅ CarreraController inicializado");
        } catch (Exception e) {
            System.err.println("❌ Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tablaCarreras.setPlaceholder(new Label("No hay carreras registradas"));
    }
    
    private void configurarListeners() {
        tablaCarreras.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newSelection) -> {
                if (newSelection != null) {
                    seleccionarCarrera(newSelection);
                } else {
                    limpiarSeleccion();
                }
            }
        );
    }
    
    private void seleccionarCarrera(Carrera carrera) {
        this.carreraSeleccionada = carrera;
        txtCodigo.setText(carrera.getCodigo());
        txtNombre.setText(carrera.getNombre());
        
        txtCodigo.setDisable(true);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
    }
    
    private void limpiarSeleccion() {
        this.carreraSeleccionada = null;
        txtCodigo.setDisable(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }
    
    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtCodigo.setDisable(false);
        carreraSeleccionada = null;
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        tablaCarreras.getSelectionModel().clearSelection();
    }
    
    @FXML
    private void guardarCarrera() {
        if (!validarFormulario()) return;
        
        try {
            Carrera carrera = new Carrera(
                txtCodigo.getText().trim().toUpperCase(),
                txtNombre.getText().trim()
            );
            
            carreraService.agregarCarrera(carrera);
            cargarCarreras();
            limpiarFormulario();
            mostrarMensaje("✅ Carrera guardada correctamente", false);
            actualizarEstado("Carrera agregada: " + carrera.getNombre());
            
        } catch (Exception e) {
            mostrarMensaje("❌ Error: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void editarCarrera() {
        if (carreraSeleccionada == null) {
            mostrarMensaje("❌ Seleccione una carrera para editar", true);
            return;
        }
        
        if (!validarNombre()) return;
        
        try {
            carreraSeleccionada.setNombre(txtNombre.getText().trim());
            
            carreraService.editarCarrera(carreraSeleccionada);
            cargarCarreras();
            limpiarFormulario();
            mostrarMensaje("✅ Carrera actualizada correctamente", false);
            
        } catch (Exception e) {
            mostrarMensaje("❌ Error: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void eliminarCarrera() {
        if (carreraSeleccionada == null) {
            mostrarMensaje("❌ Seleccione una carrera para eliminar", true);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar carrera?");
        confirmacion.setContentText("Se eliminará: " + carreraSeleccionada.getNombre());
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String nombreEliminado = carreraSeleccionada.getNombre();
                    carreraService.eliminarCarrera(carreraSeleccionada);
                    cargarCarreras();
                    limpiarFormulario();
                    mostrarMensaje("✅ Carrera eliminada: " + nombreEliminado, false);
                } catch (Exception e) {
                    mostrarMensaje("❌ Error: " + e.getMessage(), true);
                }
            }
        });
    }
    
    @FXML
    private void cargarCarreras() {
        try {
            carrerasObservable = FXCollections.observableArrayList(
                carreraService.obtenerTodos()
            );
            tablaCarreras.setItems(carrerasObservable);
            actualizarEstado(carrerasObservable.size() + " carreras cargadas");
        } catch (Exception e) {
            mostrarMensaje("Error al cargar: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void buscarCarreras() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarCarreras();
            return;
        }
        
        try {
            carrerasObservable = FXCollections.observableArrayList(
                carreraService.buscarPorNombre(criterio)
            );
            tablaCarreras.setItems(carrerasObservable);
            actualizarEstado(carrerasObservable.size() + " resultados para '" + criterio + "'");
        } catch (Exception e) {
            mostrarMensaje("Error en búsqueda: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void volverAlMenu() {
        try {
            MainApp.setRoot("MainView");
        } catch (Exception e) {
            mostrarMensaje("Error al volver: " + e.getMessage(), true);
        }
    }
    
    private boolean validarFormulario() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarMensaje("❌ El código es obligatorio", true);
            return false;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("❌ El nombre es obligatorio", true);
            return false;
        }
        return true;
    }
    
    private boolean validarNombre() {
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("❌ El nombre es obligatorio", true);
            return false;
        }
        return true;
    }
    
    private void mostrarMensaje(String mensaje, boolean error) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(error ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
    }
    
    private void actualizarEstado(String mensaje) {
        lblEstado.setText("Estado: " + mensaje);
    }
}