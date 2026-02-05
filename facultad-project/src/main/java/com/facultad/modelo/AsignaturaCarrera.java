package com.facultad.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "asignatura_carrera")
public class AsignaturaCarrera {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    // Una AsignaturaCarrera referencia a una Asignatura
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_codigo", nullable = false)
    private Asignatura asignatura;
    
    // Una AsignaturaCarrera referencia a una Carrera
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrera_codigo", nullable = false)
    private Carrera carrera;
    
    // Constructores
    public AsignaturaCarrera() {}
    
    public AsignaturaCarrera(Asignatura asignatura, Carrera carrera) {
        this.asignatura = asignatura;
        this.carrera = carrera;
    }
    
    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
    
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    
    @Override
    public String toString() {
        return asignatura.getCodigo() + " - " + carrera.getCodigo();
    }
}