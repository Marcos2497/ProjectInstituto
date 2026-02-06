package com.facultad.repositorio;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;

public class Repositorio {
    private final EntityManager em;
    
    public Repositorio(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }
    
    // ========== TRANSACCIONES ==========
    public void iniciarTransaccion() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }
    
    public void confirmarTransaccion() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }
    
    public void descartarTransaccion() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
    
    // ========== OPERACIONES CRUD ==========
    public void insertar(Object entidad) {
        em.persist(entidad);
    }
    
    public void modificar(Object entidad) {
        em.merge(entidad);
    }
    
    public void eliminar(Object entidad) {
        em.remove(em.contains(entidad) ? entidad : em.merge(entidad));
    }
    
    public void refrescar(Object entidad) {
        em.refresh(entidad);
    }
    
    // ========== CONSULTAS ==========
    public <T> T buscar(Class<T> clase, Object id) {
        return em.find(clase, id);
    }
    
    public <T> List<T> buscarTodos(Class<T> clase) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> consulta = cb.createQuery(clase);
        Root<T> origen = consulta.from(clase);
        consulta.select(origen);
        return em.createQuery(consulta).getResultList();
    }
    
    public <T, P> List<T> buscarTodosOrdenadosPor(Class<T> clase, SingularAttribute<T, P> atributoOrden) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> consulta = cb.createQuery(clase);
        Root<T> origen = consulta.from(clase);
        consulta.select(origen);
        consulta.orderBy(cb.asc(origen.get(atributoOrden)));
        return em.createQuery(consulta).getResultList();
    }
    
    // Cerrar EntityManager
    public void cerrar() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}