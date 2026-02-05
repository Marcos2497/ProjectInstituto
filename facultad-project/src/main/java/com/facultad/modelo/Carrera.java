package com.facultad.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carrera")
public class Carrera {
    
    @Id
    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    // Una Carrera puede tener muchas Asignaturas
    @OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AsignaturaCarrera> asignaturas = new ArrayList<>();
    
    // Constructores
    public Carrera() {}
    
    public Carrera(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public List<AsignaturaCarrera> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(List<AsignaturaCarrera> asignaturas) { this.asignaturas = asignaturas; }
    
    // Métodos de conveniencia
    public void agregarAsignatura(Asignatura asignatura) {
        AsignaturaCarrera asignaturaCarrera = new AsignaturaCarrera(asignatura, this);
        asignaturas.add(asignaturaCarrera);
    }
    
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}