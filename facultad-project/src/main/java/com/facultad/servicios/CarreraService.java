package com.facultad.servicios;

import com.facultad.modelo.Carrera;
import com.facultad.repositorio.Repositorio;
import java.util.List;

public class CarreraService {
    
    private final Repositorio repositorio;
    
    public CarreraService(Repositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    public void agregarCarrera(Carrera carrera) {
        validarCarrera(carrera);
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(carrera);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar carrera: " + e.getMessage());
        }
    }
    
    public void editarCarrera(Carrera carrera) {
        validarCarrera(carrera);
        try {
            repositorio.iniciarTransaccion();
            repositorio.modificar(carrera);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al editar carrera: " + e.getMessage());
        }
    }
    
    public void eliminarCarrera(Carrera carrera) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.eliminar(carrera);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar carrera: " + e.getMessage());
        }
    }
    
    private void validarCarrera(Carrera carrera) {
        if (carrera.getCodigo() == null || carrera.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (carrera.getNombre() == null || carrera.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        
        // Validar código único
        Carrera existente = repositorio.buscar(Carrera.class, carrera.getCodigo());
        if (existente != null && !existente.equals(carrera)) {
            throw new IllegalArgumentException("Ya existe una carrera con el código: " + carrera.getCodigo());
        }
    }
    
    public List<Carrera> obtenerTodos() {
        return repositorio.buscarTodos(Carrera.class);
    }
    
    public Carrera buscarPorCodigo(String codigo) {
        return repositorio.buscar(Carrera.class, codigo);
    }
    
    public List<Carrera> buscarPorNombre(String nombre) {
        List<Carrera> todos = obtenerTodos();
        return todos.stream()
            .filter(c -> c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
            .toList();
    }
}