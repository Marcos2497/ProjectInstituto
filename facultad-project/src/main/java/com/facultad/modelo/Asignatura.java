package com.facultad.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asignatura")
public class Asignatura {
    
    @Id
    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;
    
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    // Una Asignatura pertenece a un Instituto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituto_codigo", nullable = false)
    private Instituto instituto;
    
    // Una Asignatura tiene un Docente responsable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_responsable_legajo")
    private Docente docenteResponsable;
    
    // Una Asignatura puede ser tomada por muchas Carreras
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AsignaturaCarrera> carreras = new ArrayList<>();
    
    // Constructores
    public Asignatura() {}
    
    public Asignatura(String codigo, String nombre, Instituto instituto) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.instituto = instituto;
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
    
    public Docente getDocenteResponsable() { return docenteResponsable; }
    public void setDocenteResponsable(Docente docenteResponsable) { this.docenteResponsable = docenteResponsable; }
    
    public List<AsignaturaCarrera> getCarreras() { return carreras; }
    public void setCarreras(List<AsignaturaCarrera> carreras) { this.carreras = carreras; }
    
    // Métodos de conveniencia
    public void agregarCarrera(Carrera carrera) {
        AsignaturaCarrera asignaturaCarrera = new AsignaturaCarrera(this, carrera);
        carreras.add(asignaturaCarrera);
    }
    
    public boolean tieneCarrera(Carrera carrera) {
        return carreras.stream()
            .anyMatch(ac -> ac.getCarrera().equals(carrera));
    }
    
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}