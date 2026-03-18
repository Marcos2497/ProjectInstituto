package com.facultad.servicios;

import com.facultad.modelo.Asignatura;
import com.facultad.modelo.Instituto;
import com.facultad.modelo.Docente;
import com.facultad.modelo.Carrera;
import com.facultad.repositorio.Repositorio;
import java.util.List;

public class AsignaturaService {
    
    private final Repositorio repositorio;
    
    public AsignaturaService(Repositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    // ========== CRUD ==========
    public void agregarAsignatura(Asignatura asignatura) {
        validarAsignatura(asignatura);
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar asignatura: " + e.getMessage());
        }
    }
    
    public void editarAsignatura(Asignatura asignatura) {
        validarAsignatura(asignatura);
        try {
            repositorio.iniciarTransaccion();
            repositorio.modificar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al editar asignatura: " + e.getMessage());
        }
    }
    
    public void eliminarAsignatura(Asignatura asignatura) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.eliminar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar asignatura: " + e.getMessage());
        }
    }
    
    // ========== VALIDACIONES ==========
    private void validarAsignatura(Asignatura asignatura) {
        if (asignatura.getCodigo() == null || asignatura.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (asignatura.getNombre() == null || asignatura.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (asignatura.getInstituto() == null) {
            throw new IllegalArgumentException("Debe seleccionar un instituto");
        }
        
        // Validar código único
        Asignatura existente = repositorio.buscar(Asignatura.class, asignatura.getCodigo());
        if (existente != null && !existente.equals(asignatura)) {
            throw new IllegalArgumentException("Ya existe una asignatura con el código: " + asignatura.getCodigo());
        }
    }
    
    // ========== CONSULTAS ==========
    public List<Asignatura> obtenerTodas() {
        return repositorio.buscarTodos(Asignatura.class);
    }
    
    public Asignatura buscarPorCodigo(String codigo) {
        return repositorio.buscar(Asignatura.class, codigo);
    }
    
    public List<Asignatura> buscarPorNombre(String nombre) {
        List<Asignatura> todas = obtenerTodas();
        return todas.stream()
            .filter(a -> a.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .toList();
    }
    
    public List<Asignatura> buscarPorInstituto(Instituto instituto) {
        List<Asignatura> todas = obtenerTodas();
        return todas.stream()
            .filter(a -> a.getInstituto().equals(instituto))
            .toList();
    }
    
    // ========== GESTIÓN DE CARRERAS (CORREGIDO) ==========
    
    /**
     * Agrega una carrera a una asignatura
     */
    public void agregarCarrera(Asignatura asignatura, Carrera carrera) {
        try {
            repositorio.iniciarTransaccion();
            // Usar el método de conveniencia de la entidad
            asignatura.agregarCarrera(carrera);
            repositorio.modificar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar carrera a asignatura: " + e.getMessage());
        }
    }
    
    /**
     * Quita una carrera de una asignatura
     */
    public void quitarCarrera(Asignatura asignatura, Carrera carrera) {
        try {
            repositorio.iniciarTransaccion();
            // Usar el método de conveniencia de la entidad
            asignatura.quitarCarrera(carrera);
            repositorio.modificar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al quitar carrera de asignatura: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene las carreras que NO están asignadas a una asignatura
     */
    public List<Carrera> obtenerCarrerasNoAsignadas(Asignatura asignatura) {
        List<Carrera> todasLasCarreras = repositorio.buscarTodos(Carrera.class);
        if (asignatura == null) {
            return todasLasCarreras;
        }
        return todasLasCarreras.stream()
            .filter(c -> !asignatura.tieneCarrera(c))
            .toList();
    }
    
    /**
     * Obtiene las carreras asignadas a una asignatura
     */
    public List<Carrera> obtenerCarrerasAsignadas(Asignatura asignatura) {
        if (asignatura == null) return List.of();
        return asignatura.getCarreras(); // Ahora es directo, no hay AsignaturaCarrera
    }

    // ========== BÚSQUEDAS POR DOCENTE RESPONSABLE ==========

    /**
     * Busca todas las asignaturas donde un docente específico (objeto) es responsable
     */
    public List<Asignatura> buscarPorDocenteResponsable(Docente docente) {
        if (docente == null) return List.of();
        
        List<Asignatura> todas = obtenerTodas();
        return todas.stream()
            .filter(a -> a.getDocenteResponsable() != null && 
                         a.getDocenteResponsable().equals(docente))
            .toList();
    }
    
    /**
     * Busca todas las asignaturas donde un docente (por legajo) es responsable
     */
    public List<Asignatura> buscarPorDocenteResponsableLegajo(Integer legajoDocente) {
        if (legajoDocente == null) return List.of();
        
        List<Asignatura> todas = obtenerTodas();
        return todas.stream()
            .filter(a -> a.getDocenteResponsable() != null && 
                         a.getDocenteResponsable().getLegajo().equals(legajoDocente))
            .toList();
    }
    
    /**
     * Verifica si un docente tiene asignaturas como responsable
     */
    public boolean tieneAsignaturasComoResponsable(Docente docente) {
        return !buscarPorDocenteResponsable(docente).isEmpty();
    }
    
    /**
     * Cuenta cuántas asignaturas tiene un docente como responsable
     */
    public int contarAsignaturasPorResponsable(Docente docente) {
        return buscarPorDocenteResponsable(docente).size();
    }
}