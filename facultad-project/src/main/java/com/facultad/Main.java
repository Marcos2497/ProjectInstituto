package com.facultad;

import com.facultad.modelo.*;
import com.facultad.util.HibernateUtil;
import jakarta.persistence.EntityManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("SISTEMA DE GESTIÓN FACULTAD");
        System.out.println("========================================");
        
        // Probar conexión a base de datos
        System.out.println("\n🔍 Probando conexión a PostgreSQL...");
        HibernateUtil.testDatabaseConnection();
        
        // Crear datos de prueba
        System.out.println("\n🧪 Creando datos de prueba...");
        crearDatosDePrueba();
        
        // Cerrar conexiones al finalizar
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔒 Cerrando conexiones...");
            HibernateUtil.shutdown();
        }));
        
        System.out.println("\n✅ ¡TODAS LAS ENTIDADES CREADAS CORRECTAMENTE!");
        System.out.println("✅ ¡PROYECTO LISTO PARA DESARROLLAR!");
    }
    
    private static void crearDatosDePrueba() {
        EntityManager em = HibernateUtil.getEntityManager();
        
        try {
            em.getTransaction().begin();
            
            // 1. Crear Instituto
            Instituto instituto = new Instituto("INS-001", "Instituto de Tecnología");
            em.persist(instituto);
            System.out.println("✓ Instituto creado: " + instituto);
            
            // 2. Crear Docente
            Docente docente = new Docente("12345678", "Juan", "Pérez");
            docente.setFechaNacimiento(java.time.LocalDate.of(1980, 5, 15));
            docente.setDireccionNotificaciones("Calle Falsa 123");
            em.persist(docente);
            System.out.println("✓ Docente creado: " + docente);
            
            // 3. Crear CargoDocente (relaciona Instituto y Docente)
            CargoDocente cargo = new CargoDocente(40, instituto, docente);
            em.persist(cargo);
            System.out.println("✓ CargoDocente creado: " + cargo);
            
            // 4. Crear Asignatura
            Asignatura asignatura = new Asignatura("MAT-101", "Matemática I", instituto);
            asignatura.setDocenteResponsable(docente);
            asignatura.setDescripcion("Curso introductorio de matemática");
            em.persist(asignatura);
            System.out.println("✓ Asignatura creada: " + asignatura);
            
            // 5. Crear Carrera
            Carrera carrera = new Carrera("ING-INF", "Ingeniería en Informática");
            em.persist(carrera);
            System.out.println("✓ Carrera creada: " + carrera);
            
            em.getTransaction().commit();
            System.out.println("✓ Transacción completada exitosamente");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("❌ Error al crear datos de prueba: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}