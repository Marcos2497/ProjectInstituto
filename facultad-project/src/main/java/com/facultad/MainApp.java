package com.facultad;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.facultad.util.HibernateUtil;

public class MainApp extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Sistema de Gestión Facultad");
        
        initRootLayout();
        showMainMenu();
    }
    
    /**
     * Inicializa el layout raíz
     */
    private void initRootLayout() {
        rootLayout = new BorderPane();
        
        // Crear barra de menú
        MenuBar menuBar = createMenuBar();
        rootLayout.setTop(menuBar);
        
        // Crear barra de estado
        Label statusBar = new Label("Sistema de Gestión Facultad - Listo");
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #e0e0e0;");
        rootLayout.setBottom(statusBar);
        
        Scene scene = new Scene(rootLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Configurar cierre de aplicación
        primaryStage.setOnCloseRequest(event -> {
            HibernateUtil.shutdown();
        });
    }
    
    /**
     * Crea la barra de menú principal
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // Menú Archivo
        Menu archivoMenu = new Menu("Archivo");
        MenuItem salirItem = new MenuItem("Salir");
        salirItem.setOnAction(e -> primaryStage.close());
        archivoMenu.getItems().addAll(salirItem);
        
        // Menú Institutos
        Menu institutosMenu = new Menu("Institutos");
        MenuItem gestionInstitutos = new MenuItem("Gestión de Institutos");
        gestionInstitutos.setOnAction(e -> showInstitutoManager());
        institutosMenu.getItems().addAll(gestionInstitutos);
        
        // Menú Docentes
        Menu docentesMenu = new Menu("Docentes");
        MenuItem gestionDocentes = new MenuItem("Gestión de Docentes");
        gestionDocentes.setOnAction(e -> showDocenteManager());
        docentesMenu.getItems().addAll(gestionDocentes);
        
        // Menú Asignaturas
        Menu asignaturasMenu = new Menu("Asignaturas");
        MenuItem gestionAsignaturas = new MenuItem("Gestión de Asignaturas");
        gestionAsignaturas.setOnAction(e -> showAsignaturaManager());
        asignaturasMenu.getItems().addAll(gestionAsignaturas);
        
        // Menú Ayuda
        Menu ayudaMenu = new Menu("Ayuda");
        MenuItem acercaItem = new MenuItem("Acerca de");
        acercaItem.setOnAction(e -> showAboutDialog());
        ayudaMenu.getItems().addAll(acercaItem);
        
        menuBar.getMenus().addAll(archivoMenu, institutosMenu, docentesMenu, asignaturasMenu, ayudaMenu);
        return menuBar;
    }
    
    /**
     * Muestra el menú principal en el centro
     */
    private void showMainMenu() {
        VBox centerPane = new VBox(20);
        centerPane.setPadding(new Insets(40));
        centerPane.setAlignment(Pos.CENTER);
        
        Label titulo = new Label("SISTEMA DE GESTIÓN FACULTAD");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label subtitulo = new Label("Universidad Nacional - Trabajo Práctico Integrador");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        // Tarjetas de acceso rápido
        GridPane cardsGrid = new GridPane();
        cardsGrid.setHgap(20);
        cardsGrid.setVgap(20);
        cardsGrid.setAlignment(Pos.CENTER);
        
        // Card Institutos
        VBox institutoCard = createCard("Institutos", "#3498db", "Gestionar institutos académicos");
        institutoCard.setOnMouseClicked(e -> showInstitutoManager());
        
        // Card Docentes
        VBox docenteCard = createCard("Docentes", "#2ecc71", "Gestionar plantel docente");
        docenteCard.setOnMouseClicked(e -> showDocenteManager());
        
        // Card Asignaturas
        VBox asignaturaCard = createCard("Asignaturas", "#e74c3c", "Gestionar asignaturas");
        asignaturaCard.setOnMouseClicked(e -> showAsignaturaManager());
        
        // Card Carreras
        VBox carreraCard = createCard("Carreras", "#9b59b6", "Gestionar carreras");
        
        cardsGrid.add(institutoCard, 0, 0);
        cardsGrid.add(docenteCard, 1, 0);
        cardsGrid.add(asignaturaCard, 0, 1);
        cardsGrid.add(carreraCard, 1, 1);
        
        // Estadísticas
        HBox statsBox = new HBox(30);
        statsBox.setAlignment(Pos.CENTER);
        
        VBox stat1 = createStatCard("Institutos", "3", "#3498db");
        VBox stat2 = createStatCard("Docentes", "15", "#2ecc71");
        VBox stat3 = createStatCard("Asignaturas", "42", "#e74c3c");
        VBox stat4 = createStatCard("Carreras", "8", "#9b59b6");
        
        statsBox.getChildren().addAll(stat1, stat2, stat3, stat4);
        
        centerPane.getChildren().addAll(titulo, subtitulo, cardsGrid, new Separator(), statsBox);
        rootLayout.setCenter(centerPane);
    }
    
    /**
     * Crea una tarjeta de acceso rápido
     */
    private VBox createCard(String titulo, String color, String descripcion) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        card.setPrefSize(200, 150);
        
        Label titleLabel = new Label(titulo);
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label descLabel = new Label(descripcion);
        descLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.9); -fx-wrap-text: true;");
        descLabel.setMaxWidth(180);
        
        card.getChildren().addAll(titleLabel, descLabel);
        
        // Efecto hover
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"));
        
        return card;
    }
    
    /**
     * Crea una tarjeta de estadística
     */
    private VBox createStatCard(String titulo, String valor, String color) {
        VBox stat = new VBox(5);
        stat.setAlignment(Pos.CENTER);
        stat.setPadding(new Insets(15));
        stat.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: " + color + "; -fx-border-width: 2; -fx-border-radius: 5;");
        
        Label valueLabel = new Label(valor);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        Label titleLabel = new Label(titulo);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        stat.getChildren().addAll(valueLabel, titleLabel);
        return stat;
    }
    
    /**
     * Oscurece un color hexadecimal
     */
    private String darkenColor(String color) {
        // Simplificación - en una app real usarías Color.darker()
        return color;
    }
    
    /**
     * Muestra la ventana de gestión de institutos
     */
    private void showInstitutoManager() {
        VBox institutoPane = new VBox(15);
        institutoPane.setPadding(new Insets(20));
        
        Label titulo = new Label("Gestión de Institutos");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Formulario
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        Label codigoLabel = new Label("Código:");
        TextField codigoField = new TextField();
        codigoField.setPromptText("Ej: INS-001");
        
        Label nombreLabel = new Label("Denominación:");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Ej: Instituto de Tecnología");
        
        formGrid.add(codigoLabel, 0, 0);
        formGrid.add(codigoField, 1, 0);
        formGrid.add(nombreLabel, 0, 1);
        formGrid.add(nombreField, 1, 1);
        
        // Botones
        HBox buttonBox = new HBox(10);
        Button guardarBtn = new Button("Guardar");
        guardarBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        guardarBtn.setOnAction(e -> {
            // TODO: Implementar guardado
            mostrarAlerta("Información", "Instituto guardado (simulación)");
        });
        
        Button limpiarBtn = new Button("Limpiar");
        limpiarBtn.setOnAction(e -> {
            codigoField.clear();
            nombreField.clear();
        });
        
        Button volverBtn = new Button("Volver al Menú");
        volverBtn.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(guardarBtn, limpiarBtn, volverBtn);
        
        // Lista de institutos
        Label listaLabel = new Label("Institutos Registrados:");
        ListView<String> listaInstitutos = new ListView<>();
        listaInstitutos.getItems().addAll(
            "INS-001 - Instituto de Tecnología",
            "INS-002 - Instituto de Humanidades",
            "INS-003 - Instituto de Ciencias Exactas"
        );
        
        institutoPane.getChildren().addAll(titulo, formGrid, buttonBox, listaLabel, listaInstitutos);
        rootLayout.setCenter(institutoPane);
    }
    
    /**
     * Muestra la ventana de gestión de docentes
     */
    private void showDocenteManager() {
        VBox docentePane = new VBox(15);
        docentePane.setPadding(new Insets(20));
        
        Label titulo = new Label("Gestión de Docentes");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Tabla de docentes
        TableView<String> tablaDocentes = new TableView<>();
        
        TableColumn<String, String> colLegajo = new TableColumn<>("Legajo");
        TableColumn<String, String> colNombre = new TableColumn<>("Nombre");
        TableColumn<String, String> colDocumento = new TableColumn<>("Documento");
        TableColumn<String, String> colInstituto = new TableColumn<>("Instituto");
        
        tablaDocentes.getColumns().addAll(colLegajo, colNombre, colDocumento, colInstituto);
        
        // Datos de ejemplo
        for (int i = 1; i <= 5; i++) {
            // tablaDocentes.getItems().add(...); // TODO: Implementar con datos reales
        }
        
        // Botones
        HBox buttonBox = new HBox(10);
        Button nuevoBtn = new Button("Nuevo Docente");
        nuevoBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        
        Button editarBtn = new Button("Editar");
        Button eliminarBtn = new Button("Eliminar");
        eliminarBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        Button volverBtn = new Button("Volver");
        volverBtn.setOnAction(e -> showMainMenu());
        
        buttonBox.getChildren().addAll(nuevoBtn, editarBtn, eliminarBtn, volverBtn);
        
        docentePane.getChildren().addAll(titulo, tablaDocentes, buttonBox);
        rootLayout.setCenter(docentePane);
    }
    
    /**
     * Muestra la ventana de gestión de asignaturas
     */
    private void showAsignaturaManager() {
        VBox asignaturaPane = new VBox(15);
        asignaturaPane.setPadding(new Insets(20));
        
        Label titulo = new Label("Gestión de Asignaturas");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Contenido simple por ahora
        Label infoLabel = new Label("Esta funcionalidad estará disponible pronto");
        infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        Button volverBtn = new Button("Volver al Menú");
        volverBtn.setOnAction(e -> showMainMenu());
        
        asignaturaPane.getChildren().addAll(titulo, infoLabel, volverBtn);
        rootLayout.setCenter(asignaturaPane);
    }
    
    /**
     * Muestra el diálogo "Acerca de"
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Sistema de Gestión Facultad");
        alert.setContentText(
            "Trabajo Práctico Integrador\n" +
            "Universidad Nacional\n\n" +
            "Desarrollado con:\n" +
            "• Java 17\n" +
            "• JavaFX 21\n" +
            "• Hibernate 6.4\n" +
            "• PostgreSQL\n\n" +
            "© 2026 - Claudio Omar Biale"
        );
        alert.showAndWait();
    }
    
    /**
     * Muestra una alerta de información
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        // Inicializar Hibernate
        HibernateUtil.testDatabaseConnection();
        
        // Lanzar aplicación JavaFX
        launch(args);
    }
}