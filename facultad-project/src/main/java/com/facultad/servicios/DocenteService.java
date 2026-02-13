package com.facultad.servicios;

import com.facultad.modelo.Docente;
import com.facultad.modelo.CargoDocente;
import com.facultad.modelo.Instituto;
import com.facultad.modelo.Asignatura;
import com.facultad.repositorio.Repositorio;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class DocenteService {
    
    private final Repositorio repositorio;
    
    public DocenteService(Repositorio repositorio) {
        this.repositorio = repositorio;
    }
    
    // ========== OPERACIONES CRUD ==========
    public void agregarDocente(Docente docente) {
        validarDocente(docente);
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(docente);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar docente: " + e.getMessage(), e);
        }
    }
    
    public void editarDocente(Docente docente) {
        validarDocente(docente);
        try {
            repositorio.iniciarTransaccion();
            repositorio.modificar(docente);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al editar docente: " + e.getMessage(), e);
        }
    }
    
    public void eliminarDocente(Docente docente) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.eliminar(docente);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar docente: " + e.getMessage(), e);
        }
    }
    
    // ========== VALIDACIONES ==========
    private void validarDocente(Docente docente) {
        // Validar edad (21-80 años)
        if (docente.getFechaNacimiento() != null) {
            int edad = Period.between(docente.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad < 21 || edad > 80) {
                throw new IllegalArgumentException("El docente debe tener entre 21 y 80 años. Edad: " + edad);
            }
        }
        
        // Validar documento (solo números)
        if (docente.getDocumentoUnico() != null && !docente.getDocumentoUnico().matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener solo números");
        }
        
        // Validar que no exista otro docente con el mismo documento
        if (existeDocenteConDocumento(docente.getDocumentoUnico(), docente.getLegajo())) {
            throw new IllegalArgumentException("Ya existe un docente con el documento: " + docente.getDocumentoUnico());
        }
    }
    
    // ========== CONSULTAS ==========
    public List<Docente> obtenerTodos() {
        return repositorio.buscarTodos(Docente.class);
    }
    
    public Docente buscarPorLegajo(Integer legajo) {
        return repositorio.buscar(Docente.class, legajo);
    }
    
    public boolean existeDocenteConDocumento(String documento, Integer legajoExcluir) {
        List<Docente> docentes = obtenerTodos();
        return docentes.stream()
            .filter(d -> !d.getLegajo().equals(legajoExcluir))
            .anyMatch(d -> d.getDocumentoUnico().equals(documento));
    }
    
    public boolean existeDocenteConDocumento(String documento) {
        return existeDocenteConDocumento(documento, -1);
    }
    
    // ========== BÚSQUEDAS ESPECÍFICAS ==========
    public List<Docente> buscarPorApellido(String apellido) {
        List<Docente> todos = obtenerTodos();
        return todos.stream()
            .filter(d -> d.getApellidos().toLowerCase().contains(apellido.toLowerCase()))
            .toList();
    }
    
    public List<Docente> buscarPorDocumento(String documento) {
        List<Docente> todos = obtenerTodos();
        return todos.stream()
            .filter(d -> d.getDocumentoUnico().contains(documento))
            .toList();
    }
    
    public List<Docente> buscarPorInstituto(Instituto instituto) {
        List<Docente> todos = obtenerTodos();
        return todos.stream()
            .filter(d -> d.getCargos().stream()
                .anyMatch(c -> c.getInstituto().equals(instituto)))
            .toList();
    }
    
    public List<Docente> buscarPorAsignaturaResponsable(Asignatura asignatura) {
        List<Docente> todos = obtenerTodos();
        return todos.stream()
            .filter(d -> d.getAsignaturasResponsables().contains(asignatura))
            .toList();
    }
    
    // ========== OPERACIONES CON CARGOS ==========
    public void agregarCargoDocente(Docente docente, CargoDocente cargo) {
        try {
            repositorio.iniciarTransaccion();
            docente.agregarCargo(cargo);
            repositorio.modificar(docente);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar cargo: " + e.getMessage(), e);
        }
    }
    
    public void eliminarCargoDocente(Docente docente, CargoDocente cargo) {
        try {
            repositorio.iniciarTransaccion();
            docente.getCargos().remove(cargo);
            repositorio.modificar(docente);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar cargo: " + e.getMessage(), e);
        }
    }
    
    // ========== FORMATO DE DOCUMENTO ==========
    public String formatearDocumento(String documento) {
        if (documento == null || documento.isEmpty()) return "";
        
        // Remover cualquier caracter no numérico
        String soloNumeros = documento.replaceAll("\\D", "");
        
        // Formato ##.###.###
        if (soloNumeros.length() >= 8) {
            return soloNumeros.substring(0, 2) + "." + 
                   soloNumeros.substring(2, 5) + "." + 
                   soloNumeros.substring(5, Math.min(8, soloNumeros.length()));
        }
        return soloNumeros; // Si no tiene formato suficiente, devolver como está
    }
}