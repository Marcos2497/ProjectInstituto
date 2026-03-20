package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.Instituto;
import com.facultad.servicios.InstitutoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.stream.Collectors;

public class InstitutoController {
    
    // ========== COMPONENTES FXML ==========
    @FXML private TableView<Instituto> tablaInstitutos;
    @FXML private TableColumn<Instituto, String> colCodigo;
    @FXML private TableColumn<Instituto, String> colDenominacion;
    
    @FXML private TextField txtCodigo;
    @FXML private TextField txtDenominacion;
    @FXML private TextField txtBuscar;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    
    @FXML private Label lblMensaje;
    @FXML private Label lblEstado;
    
    // ========== VARIABLES DE INSTANCIA ==========
    private InstitutoService institutoService;
    private ObservableList<Instituto> institutosObservable;
    private Instituto institutoSeleccionado;
    
    // ========== INICIALIZACIÓN ==========
    
    public InstitutoController() {
        this.institutoService = MainApp.getInstitutoService();
    }
    
    @FXML
    public void initialize() {
        configurarTabla();
        cargarInstitutos();
        configurarListeners();
        actualizarEstado("Sistema listo. " + institutosObservable.size() + " institutos cargados.");
    }
    
    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colDenominacion.setCellValueFactory(new PropertyValueFactory<>("denominacion"));
        tablaInstitutos.setPlaceholder(new Label("No hay institutos registrados"));
    }
    
    private void configurarListeners() {
        // Listener para selección en tabla
        tablaInstitutos.getSelectionModel().selectedItemProperty().addListener(
            (obs, seleccionAnterior, seleccionNueva) -> {
                if (seleccionNueva != null) {
                    seleccionarInstituto(seleccionNueva);
                } else {
                    limpiarSeleccion();
                }
            }
        );
    }
    
    // ========== MÉTODOS CRUD CORREGIDOS ==========
    
    @FXML
    private void guardarInstituto() {
        if (validarFormularioParaNuevo()) {
            try {
                Instituto instituto = new Instituto(
                    txtCodigo.getText().trim(),
                    txtDenominacion.getText().trim()
                );
                
                institutoService.agregarInstituto(instituto);
                institutosObservable.add(instituto);
                
                mostrarMensaje("✅ Instituto guardado correctamente", false);
                limpiarFormulario();
                actualizarEstado("Instituto guardado: " + instituto.getCodigo());
                
            } catch (Exception e) {
                mostrarMensaje(" Error al guardar: " + e.getMessage(), true);
                e.printStackTrace(); // Para debug
            }
        }
    }
    
    @FXML
    private void editarInstituto() {
        if (institutoSeleccionado == null) {
            mostrarMensaje(" No hay instituto seleccionado para editar", true);
            return;
        }
        
        if (validarFormularioParaEdicion()) {
            try {
                // Guardar el código original para el mensaje
                String codigoOriginal = institutoSeleccionado.getCodigo();
                
                // Actualizar datos
                institutoSeleccionado.setCodigo(txtCodigo.getText().trim());
                institutoSeleccionado.setDenominacion(txtDenominacion.getText().trim());
                
                // Persistir cambios
                institutoService.editarInstituto(institutoSeleccionado);
                
                // Refrescar tabla
                tablaInstitutos.refresh();
                
                mostrarMensaje("✅ Instituto actualizado correctamente", false);
                actualizarEstado("Instituto actualizado: " + codigoOriginal + " → " + institutoSeleccionado.getCodigo());
                
            } catch (Exception e) {
                mostrarMensaje(" Error al actualizar: " + e.getMessage(), true);
                e.printStackTrace(); // Para debug
            }
        }
    }
    
    @FXML
    private void eliminarInstituto() {
        if (institutoSeleccionado == null) {
            mostrarMensaje(" No hay instituto seleccionado para eliminar", true);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar instituto?");
        confirmacion.setContentText("Se eliminará: " + institutoSeleccionado);
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Guardar datos para el mensaje antes de eliminar
                    String codigoEliminado = institutoSeleccionado.getCodigo();
                    
                    // Eliminar
                    institutoService.eliminarInstituto(institutoSeleccionado);
                    institutosObservable.remove(institutoSeleccionado);
                    
                    mostrarMensaje("✅ Instituto eliminado correctamente", false);
                    limpiarFormulario();
                    actualizarEstado("Instituto eliminado: " + codigoEliminado);
                    
                } catch (Exception e) {
                    mostrarMensaje(" Error al eliminar: " + e.getMessage(), true);
                    e.printStackTrace(); // Para debug
                }
            }
        });
    }
    
    // ========== MÉTODOS DE NAVEGACIÓN ==========
    
    @FXML
    private void volverAlMenu() {
        try {
            MainApp.setRoot("MainView");
        } catch (Exception e) {
            mostrarMensaje("Error al volver al menú: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void buscarInstitutos() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        
        if (textoBusqueda.isEmpty()) {
            cargarInstitutos();
            return;
        }
        
        List<Instituto> resultados = institutosObservable.stream()
            .filter(instituto -> 
                instituto.getCodigo().toLowerCase().contains(textoBusqueda) ||
                instituto.getDenominacion().toLowerCase().contains(textoBusqueda)
            )
            .collect(Collectors.toList());
        
        ObservableList<Instituto> resultadosObservable = FXCollections.observableArrayList(resultados);
        tablaInstitutos.setItems(resultadosObservable);
        
        // Limpiar selección si el instituto seleccionado no está en los resultados
        if (institutoSeleccionado != null && !resultados.contains(institutoSeleccionado)) {
            limpiarSeleccion();
        }
        
        actualizarEstado("Búsqueda: " + resultados.size() + " resultados para '" + textoBusqueda + "'");
    }
    
    @FXML
    private void cargarInstitutos() {
        try {
            List<Instituto> institutos = institutoService.obtenerTodos();
            institutosObservable = FXCollections.observableArrayList(institutos);
            tablaInstitutos.setItems(institutosObservable);
            
            // Limpiar selección al recargar
            limpiarSeleccion();
            
            actualizarEstado(institutos.size() + " institutos cargados");
            
        } catch (Exception e) {
            mostrarMensaje("Error al cargar institutos: " + e.getMessage(), true);
        }
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    private void seleccionarInstituto(Instituto instituto) {
        this.institutoSeleccionado = instituto;
        txtCodigo.setText(instituto.getCodigo());
        txtDenominacion.setText(instituto.getDenominacion());
        
        btnGuardar.setDisable(true);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
        
        mostrarMensaje("Editando: " + instituto, false);
        actualizarEstado("Instituto seleccionado: " + instituto.getCodigo());
    }
    
    private void limpiarSeleccion() {
        this.institutoSeleccionado = null;
        txtCodigo.clear();
        txtDenominacion.clear();
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        
        // Solo limpiar mensaje si no hay error
        if (!lblMensaje.getText().startsWith("")) {
            lblMensaje.setText("");
        }
    }
    
    @FXML
    private void limpiarFormulario() {
        tablaInstitutos.getSelectionModel().clearSelection();
        limpiarSeleccion();
        actualizarEstado("Formulario limpiado");
    }
    
    // ========== VALIDACIONES MEJORADAS ==========
    
    private boolean validarFormularioParaNuevo() {
        String codigo = txtCodigo.getText().trim();
        String denominacion = txtDenominacion.getText().trim();
        
        if (codigo.isEmpty() || denominacion.isEmpty()) {
            mostrarMensaje(" Todos los campos son obligatorios", true);
            return false;
        }
        
        if (codigo.length() > 20) {
            mostrarMensaje(" El código no puede tener más de 20 caracteres", true);
            return false;
        }
        
        if (denominacion.length() > 100) {
            mostrarMensaje(" La denominación no puede tener más de 100 caracteres", true);
            return false;
        }
        
        // Validar que no exista otro instituto con el mismo código
        if (institutoService.existeInstituto(codigo)) {
            mostrarMensaje(" Ya existe un instituto con el código: " + codigo, true);
            return false;
        }
        
        return true;
    }
    
    private boolean validarFormularioParaEdicion() {
        String codigo = txtCodigo.getText().trim();
        String denominacion = txtDenominacion.getText().trim();
        
        if (codigo.isEmpty() || denominacion.isEmpty()) {
            mostrarMensaje(" Todos los campos son obligatorios", true);
            return false;
        }
        
        if (codigo.length() > 20) {
            mostrarMensaje(" El código no puede tener más de 20 caracteres", true);
            return false;
        }
        
        if (denominacion.length() > 100) {
            mostrarMensaje(" La denominación no puede tener más de 100 caracteres", true);
            return false;
        }
        
        // Si cambió el código, verificar que no exista otro
        if (!codigo.equals(institutoSeleccionado.getCodigo()) && 
            institutoService.existeInstituto(codigo)) {
            mostrarMensaje(" Ya existe otro instituto con el código: " + codigo, true);
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
    
    private void actualizarEstado(String mensaje) {
        lblEstado.setText("Estado: " + mensaje);
    }
}