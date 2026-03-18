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
    
    // 🔥 CAMBIADO: De OneToMany a ManyToMany (lado inverso)
    @ManyToMany(mappedBy = "carreras")
    private List<Asignatura> asignaturas = new ArrayList<>();
    
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
    
    public List<Asignatura> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(List<Asignatura> asignaturas) { this.asignaturas = asignaturas; }
    
    // Método de conveniencia
    public void agregarAsignatura(Asignatura asignatura) {
        if (!asignaturas.contains(asignatura)) {
            asignaturas.add(asignatura);
            asignatura.getCarreras().add(this);
        }
    }
    
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}