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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituto_codigo", nullable = false)
    private Instituto instituto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_responsable_legajo")
    private Docente docenteResponsable;
    
    // 🔥 CAMBIADO: De OneToMany a ManyToMany
    @ManyToMany
    @JoinTable(
        name = "asignatura_carrera",
        joinColumns = @JoinColumn(name = "asignatura_codigo"),
        inverseJoinColumns = @JoinColumn(name = "carrera_codigo")
    )
    private List<Carrera> carreras = new ArrayList<>();
    
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
    
    public List<Carrera> getCarreras() { return carreras; }
    public void setCarreras(List<Carrera> carreras) { this.carreras = carreras; }
    
    // 🔥 MÉTODOS ACTUALIZADOS (sin AsignaturaCarrera)
    public void agregarCarrera(Carrera carrera) {
        if (!carreras.contains(carrera)) {
            carreras.add(carrera);
            carrera.getAsignaturas().add(this); // Mantener consistencia
        }
    }
    
    public void quitarCarrera(Carrera carrera) {
        carreras.remove(carrera);
        carrera.getAsignaturas().remove(this); // Mantener consistencia
    }
    
    public boolean tieneCarrera(Carrera carrera) {
        return carreras.contains(carrera);
    }
    
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}