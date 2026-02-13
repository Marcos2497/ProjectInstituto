package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.Docente;
import com.facultad.servicios.DocenteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class DocenteController {
    
    // ========== SERVICIOS ==========
    private DocenteService docenteService;
    
    // ========== COMPONENTES FXML BÁSICOS ==========
    @FXML private TableView<Docente> tablaDocentes;
    @FXML private TableColumn<Docente, Integer> colLegajo;
    @FXML private TableColumn<Docente, String> colApellidos;
    @FXML private TableColumn<Docente, String> colNombres;
    
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    
    @FXML private Label lblMensaje;
    @FXML private Label lblEstado;
    
    // ========== VARIABLES ==========
    private ObservableList<Docente> docentesObservable;
    private Docente docenteSeleccionado;
    
    // ========== INICIALIZACIÓN MEJORADA ==========
    
    @FXML
    public void initialize() {
        System.out.println("🔄 Inicializando DocenteController...");
        
        try {
            // 1. Inicializar servicio (AQUÍ es seguro)
            this.docenteService = MainApp.getDocenteService();
            System.out.println("✅ Servicio obtenido: " + (docenteService != null));
            
            // 2. Configurar tabla (solo columnas básicas por ahora)
            configurarTablaBasica();
            
            // 3. Cargar datos
            cargarDocentes();
            
            // 4. Configurar listeners simples
            configurarListenersBasicos();
            
            // 5. Estado inicial
            actualizarEstado("Sistema listo. " + docentesObservable.size() + " docentes cargados.");
            
            System.out.println("✅ DocenteController inicializado correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en initialize(): " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al inicializar: " + e.getMessage(), true);
        }
    }
    
    private void configurarTablaBasica() {
        System.out.println("🔄 Configurando tabla básica...");
        
        colLegajo.setCellValueFactory(new PropertyValueFactory<>("legajo"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        
        tablaDocentes.setPlaceholder(new Label("No hay docentes registrados"));
        
        System.out.println("✅ Tabla básica configurada");
    }
    
    private void configurarListenersBasicos() {
        // Listener para selección en tabla
        tablaDocentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, seleccionAnterior, seleccionNueva) -> {
                if (seleccionNueva != null) {
                    seleccionarDocente(seleccionNueva);
                } else {
                    limpiarSeleccion();
                }
            }
        );
    }
    
    // ========== MÉTODOS CRUD BÁSICOS ==========
    
    @FXML
    private void guardarDocente() {
        System.out.println("🔄 Ejecutando guardarDocente()...");
        
        if (validarFormularioBasico()) {
            try {
                Docente docente = new Docente(
                    txtDocumento.getText().trim(),
                    txtNombres.getText().trim(),
                    txtApellidos.getText().trim()
                );
                
                docenteService.agregarDocente(docente);
                docentesObservable.add(docente);
                
                mostrarMensaje("✅ Docente guardado correctamente", false);
                limpiarFormulario();
                actualizarEstado("Docente guardado: " + docente.getApellidos() + ", " + docente.getNombres());
                
            } catch (Exception e) {
                mostrarMensaje("❌ Error al guardar: " + e.getMessage(), true);
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void editarDocente() {
        if (docenteSeleccionado == null) {
            mostrarMensaje("❌ No hay docente seleccionado para editar", true);
            return;
        }
        
        if (validarFormularioBasico()) {
            try {
                docenteSeleccionado.setDocumentoUnico(txtDocumento.getText().trim());
                docenteSeleccionado.setNombres(txtNombres.getText().trim());
                docenteSeleccionado.setApellidos(txtApellidos.getText().trim());
                
                docenteService.editarDocente(docenteSeleccionado);
                tablaDocentes.refresh();
                
                mostrarMensaje("✅ Docente actualizado correctamente", false);
                actualizarEstado("Docente actualizado: " + docenteSeleccionado.getApellidos());
                
            } catch (Exception e) {
                mostrarMensaje("❌ Error al actualizar: " + e.getMessage(), true);
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void eliminarDocente() {
        if (docenteSeleccionado == null) {
            mostrarMensaje("❌ No hay docente seleccionado para eliminar", true);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar docente?");
        confirmacion.setContentText("Se eliminará: " + docenteSeleccionado.getApellidos() + ", " + docenteSeleccionado.getNombres());
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String nombreEliminado = docenteSeleccionado.getApellidos();
                    docenteService.eliminarDocente(docenteSeleccionado);
                    docentesObservable.remove(docenteSeleccionado);
                    
                    mostrarMensaje("✅ Docente eliminado correctamente", false);
                    limpiarFormulario();
                    actualizarEstado("Docente eliminado: " + nombreEliminado);
                    
                } catch (Exception e) {
                    mostrarMensaje("❌ Error al eliminar: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    private void seleccionarDocente(Docente docente) {
        this.docenteSeleccionado = docente;
        txtDocumento.setText(docente.getDocumentoUnico());
        txtNombres.setText(docente.getNombres());
        txtApellidos.setText(docente.getApellidos());
        
        btnGuardar.setDisable(true);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
        
        mostrarMensaje("Editando: " + docente.getApellidos() + ", " + docente.getNombres(), false);
        actualizarEstado("Docente seleccionado: " + docente.getLegajo());
    }
    
    private void limpiarSeleccion() {
        this.docenteSeleccionado = null;
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
    }
    
    @FXML
    private void limpiarFormulario() {
        txtDocumento.clear();
        txtNombres.clear();
        txtApellidos.clear();
        tablaDocentes.getSelectionModel().clearSelection();
        limpiarSeleccion();
        actualizarEstado("Formulario limpiado");
    }
    
    @FXML
    private void cargarDocentes() {
        try {
            List<Docente> docentes = docenteService.obtenerTodos();
            docentesObservable = FXCollections.observableArrayList(docentes);
            tablaDocentes.setItems(docentesObservable);
            
            limpiarSeleccion();
            actualizarEstado(docentes.size() + " docentes cargados");
            
        } catch (Exception e) {
            mostrarMensaje("Error al cargar docentes: " + e.getMessage(), true);
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
    
    // ========== VALIDACIONES BÁSICAS ==========
    
    private boolean validarFormularioBasico() {
        String documento = txtDocumento.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        
        if (documento.isEmpty() || nombres.isEmpty() || apellidos.isEmpty()) {
            mostrarMensaje("❌ Documento, Nombres y Apellidos son obligatorios", true);
            return false;
        }
        
        if (!documento.matches("\\d+")) {
            mostrarMensaje("❌ El documento debe contener solo números", true);
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