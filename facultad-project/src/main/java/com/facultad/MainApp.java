package com.facultad;

import com.facultad.modelo.Docente;
import com.facultad.repositorio.Repositorio;
import com.facultad.servicios.InstitutoService;
import com.facultad.servicios.DocenteService;
import com.facultad.util.HibernateUtil;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    private static Scene scene;
    private static EntityManagerFactory emf;
    private static Repositorio repositorio;
    private static InstitutoService institutoService;
    private static DocenteService docenteService;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1. Inicializar Hibernate/EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("facultadPU");
        
        // 2. Crear Repositorio y Servicios
        repositorio = new Repositorio(emf);
        institutoService = new InstitutoService(repositorio);
        docenteService = new DocenteService(repositorio);


        // 3. Cargar vista principal
        scene = new Scene(loadFXML("MainView"), 1024, 768);
        
        // 4. Configurar stage
        primaryStage.setTitle("Sistema de Gestión Facultad");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
        
        // 5. Configurar cierre de aplicación
        primaryStage.setOnCloseRequest(event -> {
            cerrarRecursos();
        });
    }
    
    // ========== MÉTODOS ESTÁTICOS PARA ACCESO GLOBAL ==========
    
    public static Repositorio getRepositorio() {
        return repositorio;
    }
    
    public static InstitutoService getInstitutoService() {
        return institutoService;
    }
    
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static DocenteService getDocenteService() {
        return docenteService;
    }
    
    // ========== MÉTODOS PARA CAMBIAR VISTAS ==========
    
    public static void setRoot(String fxml) throws Exception {
        scene.setRoot(loadFXML(fxml));
    }
    
    public static void setRoot(Parent root) {
        scene.setRoot(root);
    }
    
    private static Parent loadFXML(String fxml) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/facultad/vistas/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    private void cerrarRecursos() {
        if (repositorio != null) {
            repositorio.cerrar();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        HibernateUtil.shutdown();
        System.out.println("🔒 Recursos cerrados correctamente");
    }

    // ========== GETTERS ==========
    public static Scene getScene() {
    return scene;
}
    
    public static void main(String[] args) {
        System.out.println("🚀 Iniciando Sistema de Gestión Facultad...");
        launch(args);
    }
}