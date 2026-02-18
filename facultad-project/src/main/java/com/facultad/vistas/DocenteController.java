package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.CargoDocente;
import com.facultad.modelo.Docente;
import com.facultad.servicios.DocenteService;
import com.facultad.servicios.InstitutoService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class DocenteController {
    
    // ========== SERVICIOS ==========
    private DocenteService docenteService;
    private InstitutoService institutoService;  // Necesario para cargar institutos en diálogo de cargos
    
    // ========== COMPONENTES DEL FORMULARIO ==========
    @FXML private TextField txtDocumento;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dateFechaNacimiento;
    @FXML private TextField txtDireccion;
    
    // ========== TABLA PRINCIPAL DE DOCENTES ==========
    @FXML private TableView<Docente> tablaDocentes;
    @FXML private TableColumn<Docente, Integer> colLegajo;
    @FXML private TableColumn<Docente, String> colDocumento;
    @FXML private TableColumn<Docente, String> colApellidos;
    @FXML private TableColumn<Docente, String> colNombres;
    @FXML private TableColumn<Docente, Integer> colEdad;
    @FXML private TableColumn<Docente, Integer> colCantidadCargos;
    
    // ========== FILTROS DE BÚSQUEDA ==========
    @FXML private TextField txtBuscarApellido;
    @FXML private TextField txtBuscarDocumento;
    
    // ========== DETALLES DEL DOCENTE SELECCIONADO ==========
    @FXML private Label lblDetalleLegajo;
    @FXML private Label lblDetalleDocumento;
    @FXML private Label lblDetalleNombreCompleto;
    @FXML private Label lblDetalleFechaNacimiento;
    @FXML private Label lblDetalleDireccion;
    
    // ========== TABLA DE CARGOS ==========
    @FXML private TableView<CargoDocente> tablaCargos;
    @FXML private TableColumn<CargoDocente, String> colInstitutoCargo;
    @FXML private TableColumn<CargoDocente, Integer> colHorasCargo;
    @FXML private TableColumn<CargoDocente, Void> colAccionesCargo;  // Opcional para botones en la tabla
    
    // ========== BOTONES ==========
    @FXML private Button btnGuardar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnQuitarCargo;  // Botón para quitar cargo seleccionado
    
    // ========== LABELS DE MENSAJES ==========
    @FXML private Label lblMensaje;
    @FXML private Label lblEstado;
    
    // ========== VARIABLES DE ESTADO ==========
    private ObservableList<Docente> docentesObservable;
    private ObservableList<CargoDocente> cargosObservable;
    private Docente docenteSeleccionado;
    
    // ========== INICIALIZACIÓN ==========
    @FXML
    public void initialize() {
        System.out.println("🔄 Inicializando DocenteController...");
        
        try {
            // Obtener servicios
            this.docenteService = MainApp.getDocenteService();
            this.institutoService = MainApp.getInstitutoService();  // Asegúrate de tener este getter en MainApp
            
            // Configurar tablas
            configurarTablaDocentes();
            configurarTablaCargos();
            
            // Cargar datos
            cargarDocentes();
            
            // Configurar listeners
            configurarListeners();
            
            // Estado inicial
            actualizarEstado("Sistema listo. " + docentesObservable.size() + " docentes cargados.");
            
            System.out.println("✅ DocenteController inicializado correctamente");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en initialize(): " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al inicializar: " + e.getMessage(), true);
        }
    }
    
    // ========== CONFIGURACIÓN DE TABLAS ==========
    private void configurarTablaDocentes() {
        colLegajo.setCellValueFactory(new PropertyValueFactory<>("legajo"));
        colDocumento.setCellValueFactory(new PropertyValueFactory<>("documentoUnico"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        
        // Columna Edad (calculada)
        colEdad.setCellValueFactory(cellData -> {
            LocalDate fechaNac = cellData.getValue().getFechaNacimiento();
            if (fechaNac != null) {
                int edad = Period.between(fechaNac, LocalDate.now()).getYears();
                return new javafx.beans.property.SimpleIntegerProperty(edad).asObject();
            }
            return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
        });
        
        // Columna Cantidad de Cargos
        colCantidadCargos.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getCargos().size()
            ).asObject()
        );
        
        tablaDocentes.setPlaceholder(new Label("No hay docentes registrados"));
    }
    
    private void configurarTablaCargos() {
        colInstitutoCargo.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInstituto().getDenominacion())
        );
        
        colHorasCargo.setCellValueFactory(new PropertyValueFactory<>("dedicacionHoras"));
        
        // Si tienes columna de acciones (botones), configúrala aquí
        if (colAccionesCargo != null) {
            // Ejemplo: agregar botón de eliminar directamente en la tabla
        }
        
        tablaCargos.setPlaceholder(new Label("No hay cargos asignados"));
    }
    
    private void configurarListeners() {
        // Listener para selección en tabla de docentes
        tablaDocentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, seleccionAnterior, seleccionNueva) -> {
                if (seleccionNueva != null) {
                    seleccionarDocente(seleccionNueva);
                } else {
                    limpiarSeleccion();
                }
            }
        );
        
        // Listener para selección en tabla de cargos (habilita botón Quitar)
        tablaCargos.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newSelection) -> {
                btnQuitarCargo.setDisable(newSelection == null);
            }
        );
    }
    
    // ========== MÉTODOS CRUD ==========
    @FXML
    private void guardarDocente() {
        if (!validarFormulario()) return;
        
        try {
            Docente docente = new Docente();
            docente.setDocumentoUnico(txtDocumento.getText().trim());
            docente.setNombres(txtNombres.getText().trim());
            docente.setApellidos(txtApellidos.getText().trim());
            docente.setFechaNacimiento(dateFechaNacimiento.getValue());
            docente.setDireccionNotificaciones(txtDireccion.getText().trim());
            
            docenteService.agregarDocente(docente);
            
            cargarDocentes();
            limpiarFormulario();
            mostrarMensaje("✅ Docente guardado correctamente", false);
            actualizarEstado("Docente guardado: " + docente.getApellidos() + ", " + docente.getNombres());
            
        } catch (Exception e) {
            mostrarMensaje("❌ Error al guardar: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void editarDocente() {
        if (docenteSeleccionado == null) {
            mostrarMensaje("❌ Seleccione un docente para editar", true);
            return;
        }
        
        if (!validarFormulario()) return;
        
        try {
            docenteSeleccionado.setDocumentoUnico(txtDocumento.getText().trim());
            docenteSeleccionado.setNombres(txtNombres.getText().trim());
            docenteSeleccionado.setApellidos(txtApellidos.getText().trim());
            docenteSeleccionado.setFechaNacimiento(dateFechaNacimiento.getValue());
            docenteSeleccionado.setDireccionNotificaciones(txtDireccion.getText().trim());
            
            docenteService.editarDocente(docenteSeleccionado);
            
            tablaDocentes.refresh();  // Actualizar datos en la tabla
            mostrarDetalles(docenteSeleccionado); // Actualizar panel de detalles
            cargarCargosDocente(); // Recargar cargos (por si cambió algo)
            
            mostrarMensaje("✅ Docente actualizado correctamente", false);
            actualizarEstado("Docente actualizado: " + docenteSeleccionado.getApellidos());
            
        } catch (Exception e) {
            mostrarMensaje("❌ Error al actualizar: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void eliminarDocente() {
        if (docenteSeleccionado == null) {
            mostrarMensaje("❌ Seleccione un docente para eliminar", true);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Eliminar docente?");
        confirmacion.setContentText("Se eliminará: " + docenteSeleccionado.getApellidos() + ", " + docenteSeleccionado.getNombres());
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String nombreEliminado = docenteSeleccionado.getApellidos() + ", " + docenteSeleccionado.getNombres();
                    docenteService.eliminarDocente(docenteSeleccionado);
                    
                    docentesObservable.remove(docenteSeleccionado);
                    limpiarFormulario();
                    
                    mostrarMensaje("✅ Docente eliminado correctamente", false);
                    actualizarEstado("Docente eliminado: " + nombreEliminado);
                    
                } catch (Exception e) {
                    mostrarMensaje("❌ Error al eliminar: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }
    
    // ========== MÉTODOS DE BÚSQUEDA ==========
    @FXML
    private void buscarDocentes() {
        String apellido = txtBuscarApellido.getText().trim();
        String documento = txtBuscarDocumento.getText().trim();
        
        List<Docente> resultados;
        
        if (!apellido.isEmpty() && !documento.isEmpty()) {
            // Búsqueda combinada: por apellido y documento
            resultados = docenteService.obtenerTodos().stream()
                .filter(d -> d.getApellidos().toLowerCase().contains(apellido.toLowerCase()))
                .filter(d -> d.getDocumentoUnico().contains(documento))
                .toList();
        } else if (!apellido.isEmpty()) {
            resultados = docenteService.buscarPorApellido(apellido);
        } else if (!documento.isEmpty()) {
            resultados = docenteService.buscarPorDocumento(documento);
        } else {
            cargarDocentes();
            return;
        }
        
        docentesObservable = FXCollections.observableArrayList(resultados);
        tablaDocentes.setItems(docentesObservable);
        actualizarEstado(resultados.size() + " resultados para la búsqueda");
    }
    
    @FXML
    private void cargarDocentes() {
        try {
            List<Docente> docentes = docenteService.obtenerTodos();
            docentesObservable = FXCollections.observableArrayList(docentes);
            tablaDocentes.setItems(docentesObservable);
            
            limpiarFormulario();
            actualizarEstado(docentes.size() + " docentes cargados");
            
        } catch (Exception e) {
            mostrarMensaje("Error al cargar docentes: " + e.getMessage(), true);
        }
    }
    
    // ========== MÉTODOS DE SELECCIÓN Y DETALLES ==========
    private void seleccionarDocente(Docente docente) {
        this.docenteSeleccionado = docente;
        
        // Cargar datos en el formulario
        txtDocumento.setText(docente.getDocumentoUnico());
        txtNombres.setText(docente.getNombres());
        txtApellidos.setText(docente.getApellidos());
        dateFechaNacimiento.setValue(docente.getFechaNacimiento());
        txtDireccion.setText(docente.getDireccionNotificaciones());
        
        // Mostrar detalles en el panel
        mostrarDetalles(docente);
        
        // Cargar cargos del docente
        cargarCargosDocente();
        
        // Cambiar estado de botones
        btnGuardar.setDisable(true);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
    }
    
    private void mostrarDetalles(Docente docente) {
        lblDetalleLegajo.setText(docente.getLegajo().toString());
        lblDetalleDocumento.setText(docente.getDocumentoUnico());
        lblDetalleNombreCompleto.setText(docente.getApellidos() + ", " + docente.getNombres());
        lblDetalleFechaNacimiento.setText(docente.getFechaNacimiento() != null ? docente.getFechaNacimiento().toString() : "No especificada");
        lblDetalleDireccion.setText(docente.getDireccionNotificaciones() != null ? docente.getDireccionNotificaciones() : "No especificada");
    }
    
    private void limpiarSeleccion() {
        this.docenteSeleccionado = null;
        btnGuardar.setDisable(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        btnQuitarCargo.setDisable(true);
        
        // Limpiar tabla de cargos
        if (cargosObservable != null) {
            cargosObservable.clear();
        }
    }
    
    @FXML
    private void limpiarFormulario() {
        txtDocumento.clear();
        txtNombres.clear();
        txtApellidos.clear();
        dateFechaNacimiento.setValue(null);
        txtDireccion.clear();
        
        // Limpiar detalles
        lblDetalleLegajo.setText("");
        lblDetalleDocumento.setText("");
        lblDetalleNombreCompleto.setText("");
        lblDetalleFechaNacimiento.setText("");
        lblDetalleDireccion.setText("");
        
        tablaDocentes.getSelectionModel().clearSelection();
        limpiarSeleccion();
        
        actualizarEstado("Formulario limpiado");
    }
    
    // ========== GESTIÓN DE CARGOS ==========
    private void cargarCargosDocente() {
        if (docenteSeleccionado != null) {
            // Forzar carga de la lista de cargos (puede ser lazy)
            List<CargoDocente> cargos = docenteSeleccionado.getCargos();
            cargosObservable = FXCollections.observableArrayList(cargos);
            tablaCargos.setItems(cargosObservable);
        } else {
            if (cargosObservable != null) {
                cargosObservable.clear();
            }
        }
    }
    
    @FXML
    private void abrirDialogoAsignarCargo() {
        if (docenteSeleccionado == null) {
            mostrarMensaje("❌ Debe seleccionar un docente primero", true);
            return;
        }
        
        try {
            // Aquí puedes implementar el diálogo para asignar cargo
            // Por ahora, simulamos la asignación de un cargo de ejemplo
            // En el futuro, puedes crear un AsignarCargoDialogController similar al que ya tienes
            
            // Ejemplo rápido: crear un cargo dummy (solo para probar)
            // CargoDocente nuevoCargo = new CargoDocente(10, institutoSeleccionado, docenteSeleccionado);
            // docenteService.agregarCargoDocente(docenteSeleccionado, nuevoCargo);
            // cargarCargosDocente();
            
            mostrarMensaje("⏳ Funcionalidad en desarrollo. Próximamente: diálogo para asignar cargos.", false);
            
        } catch (Exception e) {
            mostrarMensaje("Error al abrir diálogo: " + e.getMessage(), true);
            e.printStackTrace();
        }
    }
    
    @FXML
    private void quitarCargoSeleccionado() {
        CargoDocente cargoSeleccionado = tablaCargos.getSelectionModel().getSelectedItem();
        
        if (cargoSeleccionado == null) {
            mostrarMensaje("❌ Seleccione un cargo de la tabla", true);
            return;
        }
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar");
        confirmacion.setHeaderText("¿Quitar cargo?");
        confirmacion.setContentText("Instituto: " + cargoSeleccionado.getInstituto().getDenominacion() +
                                   "\nHoras: " + cargoSeleccionado.getDedicacionHoras());
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    docenteService.eliminarCargoDocente(docenteSeleccionado, cargoSeleccionado);
                    cargarCargosDocente();  // Recargar la lista
                    mostrarMensaje("✅ Cargo quitado correctamente", false);
                } catch (Exception e) {
                    mostrarMensaje("Error al quitar cargo: " + e.getMessage(), true);
                    e.printStackTrace();
                }
            }
        });
    }
    
    // ========== VALIDACIONES ==========
    private boolean validarFormulario() {
        String documento = txtDocumento.getText().trim();
        String nombres = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        
        if (documento.isEmpty()) {
            mostrarMensaje("❌ El documento es obligatorio", true);
            return false;
        }
        if (nombres.isEmpty()) {
            mostrarMensaje("❌ Los nombres son obligatorios", true);
            return false;
        }
        if (apellidos.isEmpty()) {
            mostrarMensaje("❌ Los apellidos son obligatorios", true);
            return false;
        }
        
        if (!documento.matches("\\d+")) {
            mostrarMensaje("❌ El documento debe contener solo números", true);
            return false;
        }
        
        // Validar edad si se ingresó fecha
        if (dateFechaNacimiento.getValue() != null) {
            int edad = Period.between(dateFechaNacimiento.getValue(), LocalDate.now()).getYears();
            if (edad < 21 || edad > 80) {
                mostrarMensaje("❌ El docente debe tener entre 21 y 80 años", true);
                return false;
            }
        }
        
        return true;
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
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
    
    @FXML
    private void volverAlMenu() {
        try {
            MainApp.setRoot("MainView");
        } catch (Exception e) {
            mostrarMensaje("Error al volver: " + e.getMessage(), true);
        }
    }
}