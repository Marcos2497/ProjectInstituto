package com.facultad.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateUtil {
    private static EntityManagerFactory entityManagerFactory;
    
    static {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("facultadPU");
            System.out.println("✅ EntityManagerFactory creado exitosamente");
        } catch (Exception e) {
            System.err.println("❌ Error al crear EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }
    
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("🔒 EntityManagerFactory cerrado");
        }
    }
    
    public static void testDatabaseConnection() {
        EntityManager em = null;
        try {
            em = getEntityManager();
            // Ejecutar una consulta simple para probar conexión
            Object result = em.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("✅ Conexión a PostgreSQL exitosa");
            
            // Verificar que las tablas se crearon
            System.out.println("📊 Tablas creadas automáticamente por Hibernate");
        } catch (Exception e) {
            System.err.println("❌ Error de conexión a PostgreSQL: " + e.getMessage());
            System.err.println("   Verifica que:");
            System.err.println("   1. PostgreSQL esté corriendo");
            System.err.println("   2. La base 'facultad_db' exista");
            System.err.println("   3. El usuario/contraseña sean correctos en persistence.xml");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}