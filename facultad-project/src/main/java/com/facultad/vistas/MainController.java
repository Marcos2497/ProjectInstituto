package com.facultad.vistas;

import com.facultad.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class MainController {
    
    @FXML
    private void abrirInstitutos() {
        try {
            MainApp.setRoot("InstitutoView");
        } catch (Exception e) {
            mostrarError("No se pudo abrir la gestión de Institutos", e);
        }
    }
    
    @FXML
    private void abrirDocentes() {
        mostrarMensaje("Próximamente", "La gestión de Docentes estará disponible en la próxima versión.");
    }
    
    @FXML
    private void abrirAsignaturas() {
        mostrarMensaje("Próximamente", "La gestión de Asignaturas estará disponible en la próxima versión.");
    }
    
    @FXML
    private void salir() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText("¿Está seguro que desea salir?");
        alert.setContentText("Todos los cambios no guardados se perderán.");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage = (Stage) MainApp.getScene().getWindow();
            stage.close();
        }
    }
    
    @FXML
    private void mostrarAcercaDe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("Sistema de Gestión Facultad");
        alert.setContentText(
            "Versión 1.0\n" +
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
    
    // ========== MÉTODOS DE UTILIDAD ==========
    
    private void mostrarError(String mensaje, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(mensaje);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
    
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    // Necesitamos agregar este método a MainApp para que funcione
    // Agregar en MainApp.java:
    // public static Scene getScene() { return scene; }
}