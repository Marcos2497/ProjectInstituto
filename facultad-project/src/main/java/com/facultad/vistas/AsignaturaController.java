package com.facultad.vistas;

import com.facultad.MainApp;
import com.facultad.modelo.Asignatura;
import com.facultad.modelo.CargoDocente;
import com.facultad.modelo.Carrera;
import com.facultad.modelo.Docente;
import com.facultad.modelo.Instituto;
import com.facultad.servicios.AsignaturaService;
import com.facultad.servicios.CarreraService;
import com.facultad.servicios.DocenteService;
import com.facultad.servicios.InstitutoService;
import com.facultad.servicios.CargoDocenteService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

public class AsignaturaController {
    
    // ========== SERVICIOS ==========
    private AsignaturaService asignaturaService;
    private InstitutoService institutoService;
    private DocenteService docenteService;
    private CarreraService carreraService;
    private CargoDocenteService cargoDocenteService;
    // ========== COMPONENTES DEL FORMULARIO ==========
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private ComboBox<Instituto> cmbInstituto;
    @FXML private ComboBox<Docente> cmbDocenteResponsable;
    
    // ========== LISTA DE CARRERAS ==========
    @FXML private ListView<Carrera> listCarreras;
    @FXML private Button btnQuitarCarrera;
    
    // ========== TABLA PRINCIPAL ==========
    @FXML private TableView<Asignatura> tablaAsignaturas;
    @FXML private TableColumn<Asignatura, String> colCodigo;
    @FXML private TableColumn<Asignatura, String> colNombre;
    @FXML private TableColumn<Asignatura, String> colInstituto;
    @FXML private TableColumn<Asignatura, String> colResponsable;
    @FXML private TableColumn<Asignatura, Integer> colCarreras;
    
    // ========== BÚSQUEDA ==========
    @FXML private TextField txtBuscar;
    
    // ========== BOTONES ==========
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    
    // ========== LABELS ==========
    @FXML private Label lblMensaje;
    @FXML private Label lblEstado;
    
    // ========== VARIABLES ==========
    private ObservableList<Asignatura> asignaturasObservable;
    private ObservableList<Carrera> carrerasObservable;
    private ObservableList<Carrera> todasLasCarreras;
    private Asignatura asignaturaSeleccionada;
    
    // ========== INICIALIZACIÓN ==========
    @FXML
    public void initialize() {
        System.out.println("🔄 Inicializando AsignaturaController...");
        
        try {
            // Obtener servicios
            this.asignaturaService = MainApp.getAsignaturaService();
            this.institutoService = MainApp.getInstitutoService();
            this.docenteService = MainApp.getDocenteService();
            this.carreraService = MainApp.getCarreraService();
            this.cargoDocenteService = MainApp.getCargoDocenteService();
            // Configurar tablas y combos
            configurarTabla();
            configurarCombos();
            configurarListView();
            
            // Cargar datos
            cargarAsignaturas();
            cargarCombos();
            cargarTodasLasCarreras();
            
            // Configurar listeners
            configurarListeners();
            
            actualizarEstado("Sistema listo");
            System.out.println("✅ AsignaturaController inicializado");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            mostrarMensaje("Error al inicializar: " + e.getMessage(), true);
        }
    }
    
    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        
        colInstituto.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getInstituto().getDenominacion())
        );
        
        colResponsable.setCellValueFactory(cellData -> {
            Docente responsable = cellData.getValue().getDocenteResponsable();
            return new SimpleStringProperty(responsable != null ? 
                responsable.getApellidos() + ", " + responsable.getNombres() : "No asignado");
        });
        
        colCarreras.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getCarreras().size()
            ).asObject()
        );
        
        tablaAsignaturas.setPlaceholder(new Label("No hay asignaturas registradas"));
    }
    
    private void configurarCombos() {
        // Instituto
        cmbInstituto.setCellFactory(lv -> new ListCell<Instituto>() {
            @Override
            protected void updateItem(Instituto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDenominacion());
            }
        });
        cmbInstituto.setButtonCell(new ListCell<Instituto>() {
            @Override
            protected void updateItem(Instituto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDenominacion());
            }
        });
        
        // Docente responsable
        cmbDocenteResponsable.setCellFactory(lv -> new ListCell<Docente>() {
            @Override
            protected void updateItem(Docente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getApellidos() + ", " + item.getNombres());
            }
        });
        cmbDocenteResponsable.setButtonCell(new ListCell<Docente>() {
            @Override
            protected void updateItem(Docente item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getApellidos() + ", " + item.getNombres());
            }
        });
    }
    
    private void configurarListView() {
        listCarreras.setCellFactory(lv -> new ListCell<Carrera>() {
            @Override
            protected void updateItem(Carrera item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getCodigo() + " - " + item.getNombre());
            }
        });
    }
    
    private void configurarListeners() {
        // Selección en tabla
        tablaAsignaturas.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newSelection) -> {
                if (newSelection != null) {
                    seleccionarAsignatura(newSelection);
                } else {
                    limpiarSeleccion();
                }
            }
        );
        
        // Selección en ListView
        listCarreras.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newSelection) -> {
                btnQuitarCarrera.setDisable(newSelection == null || asignaturaSeleccionada == null);
            }
        );
    }
    
    // ========== CARGA DE DATOS ==========
    private void cargarAsignatura() {
        try {
            List<Asignatura> asignaturas = asignaturaService.obtenerTodas();
            asignaturasObservable = FXCollections.observableArrayList(asignaturas);
            tablaAsignaturas.setItems(asignaturasObservable);
            actualizarEstado(asignaturas.size() + " asignaturas cargadas");
        } catch (Exception e) {
            mostrarMensaje("Error al cargar asignaturas: " + e.getMessage(), true);
        }
    }
    
    private void cargarCombos() {
        try {
            cmbInstituto.setItems(FXCollections.observableArrayList(institutoService.obtenerTodos()));
            
            List<Docente> docentes = docenteService.obtenerTodos();
            ObservableList<Docente> items = FXCollections.observableArrayList(docentes);
            items.add(0, null); // Opción "Sin responsable"
            cmbDocenteResponsable.setItems(items);
        } catch (Exception e) {
            mostrarMensaje("Error al cargar combos: " + e.getMessage(), true);
        }
    }
    
    private void cargarTodasLasCarreras() {
        try {
            todasLasCarreras = FXCollections.observableArrayList(carreraService.obtenerTodos());
        } catch (Exception e) {
            mostrarMensaje("Error al cargar carreras: " + e.getMessage(), true);
        }
    }
    
    private void cargarCarrerasAsignadas() {
        if (asignaturaSeleccionada != null) {
            List<Carrera> carreras = asignaturaSeleccionada.getCarreras().stream()
                .map(ac -> ac.getCarrera())
                .toList();
            carrerasObservable = FXCollections.observableArrayList(carreras);
            listCarreras.setItems(carrerasObservable);
        }
    }
    
    // ========== SELECCIÓN ==========
    private void seleccionarAsignatura(Asignatura asignatura) {
        this.asignaturaSeleccionada = asignatura;
        
        txtCodigo.setText(asignatura.getCodigo());
        txtNombre.setText(asignatura.getNombre());
        txtDescripcion.setText(asignatura.getDescripcion());
        cmbInstituto.setValue(asignatura.getInstituto());
        cmbDocenteResponsable.setValue(asignatura.getDocenteResponsable());
        
        txtCodigo.setDisable(true);
        btnEditar.setDisable(false);
        btnEliminar.setDisable(false);
        
        cargarCarrerasAsignadas();
    }
    
    private void limpiarSeleccion() {
        this.asignaturaSeleccionada = null;
        txtCodigo.setDisable(false);
        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);
        btnQuitarCarrera.setDisable(true);
        
        if (carrerasObservable != null) {
            carrerasObservable.clear();
        }
    }
    
    @FXML
    private void limpiarFormulario() {
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        cmbInstituto.setValue(null);
        cmbDocenteResponsable.setValue(null);
        
        if (carrerasObservable != null) {
            carrerasObservable.clear();
        }
        
        tablaAsignaturas.getSelectionModel().clearSelection();
        limpiarSeleccion();
        actualizarEstado("Formulario limpiado");
    }
    
    // ========== CRUD ==========
@FXML
private void guardarAsignatura() {
    if (!validarFormulario()) return;
    
    try {
        Asignatura asignatura = new Asignatura();
        asignatura.setCodigo(txtCodigo.getText().trim().toUpperCase());
        asignatura.setNombre(txtNombre.getText().trim());
        asignatura.setDescripcion(txtDescripcion.getText().trim());
        asignatura.setInstituto(cmbInstituto.getValue());
        
        Docente docenteSeleccionado = cmbDocenteResponsable.getValue();
        asignatura.setDocenteResponsable(docenteSeleccionado);
        
        // 1. Guardar la asignatura primero
        asignaturaService.agregarAsignatura(asignatura);
        
        // 2. Si hay docente responsable, crear el cargo automáticamente
        if (docenteSeleccionado != null) {
            CargoDocente cargo = new CargoDocente();
            cargo.setDocente(docenteSeleccionado);
            cargo.setInstituto(cmbInstituto.getValue());
            cargo.setDedicacionHoras(10); // Valor fijo: 10 horas (podés cambiarlo)
            
            cargoDocenteService.agregarCargo(cargo);
            
            System.out.println("✅ Cargo creado automáticamente para: " + 
                             docenteSeleccionado.getNombreCompleto());
        }
        
        cargarAsignatura();
        limpiarFormulario();
        mostrarMensaje("✅ Asignatura guardada correctamente con su cargo", false);
        
    } catch (Exception e) {
        mostrarMensaje("❌ Error: " + e.getMessage(), true);
    }
}
@FXML
private void editarAsignatura() {
    if (asignaturaSeleccionada == null) {
        mostrarMensaje("❌ Seleccione una asignatura", true);
        return;
    }
    
    if (!validarNombre()) return;
    
    try {
        Docente docenteAnterior = asignaturaSeleccionada.getDocenteResponsable();
        Docente docenteNuevo = cmbDocenteResponsable.getValue();
        
        // Actualizar datos básicos
        asignaturaSeleccionada.setNombre(txtNombre.getText().trim());
        asignaturaSeleccionada.setDescripcion(txtDescripcion.getText().trim());
        asignaturaSeleccionada.setDocenteResponsable(docenteNuevo);
        
        asignaturaService.editarAsignatura(asignaturaSeleccionada);
        
        // Si cambió el docente responsable, crear nuevo cargo
        if (docenteNuevo != null && !docenteNuevo.equals(docenteAnterior)) {
            CargoDocente cargo = new CargoDocente();
            cargo.setDocente(docenteNuevo);
            cargo.setInstituto(asignaturaSeleccionada.getInstituto());
            cargo.setDedicacionHoras(10); // Mismo valor fijo
            
            cargoDocenteService.agregarCargo(cargo);
            
            System.out.println("✅ Nuevo cargo creado por cambio de responsable");
        }
        
        tablaAsignaturas.refresh();
        mostrarMensaje("✅ Asignatura actualizada", false);
        
    } catch (Exception e) {
        mostrarMensaje("❌ Error: " + e.getMessage(), true);
    }
}
    @FXML
    private void eliminarAsignatura() {
        if (asignaturaSeleccionada == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("¿Eliminar asignatura " + asignaturaSeleccionada.getNombre() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    asignaturaService.eliminarAsignatura(asignaturaSeleccionada);
                    cargarAsignaturas();
                    limpiarFormulario();
                    mostrarMensaje("✅ Asignatura eliminada", false);
                } catch (Exception e) {
                    mostrarMensaje("❌ Error: " + e.getMessage(), true);
                }
            }
        });
    }
    
    // ========== GESTIÓN DE CARRERAS ==========
    @FXML
    private void agregarCarrera() {
        if (asignaturaSeleccionada == null) {
            mostrarMensaje("❌ Primero seleccione una asignatura", true);
            return;
        }
        
        // Crear diálogo para seleccionar carrera
        ChoiceDialog<Carrera> dialog = new ChoiceDialog<>();
        dialog.setTitle("Agregar Carrera");
        dialog.setHeaderText("Seleccione una carrera para agregar a " + asignaturaSeleccionada.getNombre());
        
        List<Carrera> noAsignadas = asignaturaService.obtenerCarrerasNoAsignadas(asignaturaSeleccionada);
        if (noAsignadas.isEmpty()) {
            mostrarMensaje("Todas las carreras ya están asignadas", false);
            return;
        }
        
        dialog.getItems().addAll(noAsignadas);
        dialog.setSelectedItem(noAsignadas.get(0));
        
        dialog.showAndWait().ifPresent(carrera -> {
            try {
                asignaturaService.agregarCarrera(asignaturaSeleccionada, carrera);
                cargarCarrerasAsignadas();
                tablaAsignaturas.refresh();
                mostrarMensaje("✅ Carrera agregada", false);
            } catch (Exception e) {
                mostrarMensaje("❌ Error: " + e.getMessage(), true);
            }
        });
    }
    
    @FXML
    private void quitarCarrera() {
        Carrera carrera = listCarreras.getSelectionModel().getSelectedItem();
        if (carrera == null || asignaturaSeleccionada == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("¿Quitar la carrera " + carrera.getNombre() + " de esta asignatura?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    asignaturaService.quitarCarrera(asignaturaSeleccionada, carrera);
                    cargarCarrerasAsignadas();
                    tablaAsignaturas.refresh();
                    mostrarMensaje("✅ Carrera quitada", false);
                } catch (Exception e) {
                    mostrarMensaje("❌ Error: " + e.getMessage(), true);
                }
            }
        });
    }
    
    // ========== BÚSQUEDA ==========
    @FXML
    private void buscarAsignaturas() {
        String criterio = txtBuscar.getText().trim();
        if (criterio.isEmpty()) {
            cargarAsignaturas();
            return;
        }
        
        try {
            List<Asignatura> resultados = asignaturaService.buscarPorNombre(criterio);
            asignaturasObservable = FXCollections.observableArrayList(resultados);
            tablaAsignaturas.setItems(asignaturasObservable);
            actualizarEstado(resultados.size() + " resultados para '" + criterio + "'");
        } catch (Exception e) {
            mostrarMensaje("Error en búsqueda: " + e.getMessage(), true);
        }
    }
    
    @FXML
    private void cargarAsignaturas() {
        cargarAsignatura();
    }
    
    // ========== VALIDACIONES ==========
    private boolean validarFormulario() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarMensaje("❌ El código es obligatorio", true);
            return false;
        }
        if (txtNombre.getText().trim().isEmpty()) {
            mostrarMensaje("❌ El nombre es obligatorio", true);
            return false;
        }
        if (cmbInstituto.getValue() == null) {
            mostrarMensaje("❌ Debe seleccionar un instituto", true);
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
    
    // ========== UTILIDADES ==========
    private void mostrarMensaje(String mensaje, boolean error) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(error ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #27ae60;");
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