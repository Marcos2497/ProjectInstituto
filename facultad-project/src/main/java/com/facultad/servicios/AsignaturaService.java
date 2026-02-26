package com.facultad.servicios;

import com.facultad.modelo.Asignatura;
import com.facultad.modelo.Instituto;
import com.facultad.modelo.Docente;
import com.facultad.modelo.Carrera;
import com.facultad.modelo.AsignaturaCarrera;
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
    
    // ========== GESTIÓN DE CARRERAS ==========
    public void agregarCarrera(Asignatura asignatura, Carrera carrera) {
        try {
            repositorio.iniciarTransaccion();
            asignatura.agregarCarrera(carrera);
            repositorio.modificar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar carrera a asignatura: " + e.getMessage());
        }
    }
    
    public void quitarCarrera(Asignatura asignatura, Carrera carrera) {
        try {
            repositorio.iniciarTransaccion();
            asignatura.getCarreras().removeIf(ac -> ac.getCarrera().equals(carrera));
            repositorio.modificar(asignatura);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al quitar carrera de asignatura: " + e.getMessage());
        }
    }
    
    public List<Carrera> obtenerCarrerasNoAsignadas(Asignatura asignatura) {
        List<Carrera> todasLasCarreras = repositorio.buscarTodos(Carrera.class);
        return todasLasCarreras.stream()
            .filter(c -> !asignatura.tieneCarrera(c))
            .toList();
    }
}