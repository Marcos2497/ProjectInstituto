package com.facultad.servicios;

import com.facultad.modelo.Instituto;
import com.facultad.repositorio.Repositorio;
import java.util.List;

public class InstitutoService {
    
    private final Repositorio repositorio;
    
    public InstitutoService(Repositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    // ========== OPERACIONES CRUD ==========
    public void agregarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar instituto: " + e.getMessage(), e);
        }
    }
    
    public void editarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.modificar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al editar instituto: " + e.getMessage(), e);
        }
    }
    
    public void eliminarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.eliminar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar instituto: " + e.getMessage(), e);
        }
    }
    
    // ========== CONSULTAS ==========
    public List<Instituto> obtenerTodos() {
        return repositorio.buscarTodos(Instituto.class);
    }
    
public boolean existeInstituto(String codigo) {
    try {
        return buscarPorCodigo(codigo) != null;
    } catch (Exception e) {
        return false;
    }
}

// Y mejora el método buscarPorCodigo:
public Instituto buscarPorCodigo(String codigo) {
    try {
        return repositorio.buscar(Instituto.class, codigo);
    } catch (Exception e) {
        System.err.println("Error buscando instituto por código: " + e.getMessage());
        return null;
    }
}
}