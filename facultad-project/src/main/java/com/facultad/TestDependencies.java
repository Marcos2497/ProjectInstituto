package com.facultad;

// Importa clases de las dependencias para forzar a Maven a descargarlas
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class TestDependencies {

    // Clase de prueba que usa Hibernate/JPA
    @Entity
    static class TestEntity {
        @Id
        private Long id;
        private String nombre;
    }

    // Método que usa JavaFX
    public static void testJavaFX() {
        System.out.println("JavaFX disponible");
    }

    // Método que usa Hibernate
    public static void testHibernate() {
        System.out.println("Hibernate disponible");
    }
}