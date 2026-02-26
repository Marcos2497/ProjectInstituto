package com.facultad.servicios;

import com.facultad.modelo.CargoDocente;
import com.facultad.repositorio.Repositorio;
import java.util.List;

public class CargoDocenteService {
    
    private final Repositorio repositorio;
    
    public CargoDocenteService(Repositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    public void agregarCargo(CargoDocente cargo) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(cargo);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al crear cargo: " + e.getMessage());
        }
    }
    
    public List<CargoDocente> obtenerTodos() {
        return repositorio.buscarTodos(CargoDocente.class);
    }
    
    public List<CargoDocente> buscarPorDocente(Integer legajo) {
        List<CargoDocente> todos = obtenerTodos();
        return todos.stream()
            .filter(c -> c.getDocente().getLegajo().equals(legajo))
            .toList();
    }
}