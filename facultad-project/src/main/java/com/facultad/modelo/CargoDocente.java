package com.facultad.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "cargo_docente")
public class CargoDocente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_cargo")
    private Integer numeroCargo;
    
    @Column(name = "dedicacion_horas", nullable = false)
    private Integer dedicacionHoras;
    
    // Un Cargo pertenece a un Instituto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instituto_codigo", nullable = false)
    private Instituto instituto;
    
    // Un Cargo es ocupado por un Docente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_legajo", nullable = false)
    private Docente docente;
    
    // Constructores
    public CargoDocente() {}
    
    public CargoDocente(Integer dedicacionHoras, Instituto instituto, Docente docente) {
        this.dedicacionHoras = dedicacionHoras;
        this.instituto = instituto;
        this.docente = docente;
    }
    
    // Getters y Setters
    public Integer getNumeroCargo() { return numeroCargo; }
    public void setNumeroCargo(Integer numeroCargo) { this.numeroCargo = numeroCargo; }
    
    public Integer getDedicacionHoras() { return dedicacionHoras; }
    public void setDedicacionHoras(Integer dedicacionHoras) { this.dedicacionHoras = dedicacionHoras; }
    
    public Instituto getInstituto() { return instituto; }
    public void setInstituto(Instituto instituto) { this.instituto = instituto; }
    
    public Docente getDocente() { return docente; }
    public void setDocente(Docente docente) { this.docente = docente; }
    
    @Override
    public String toString() {
        return "Cargo #" + numeroCargo + " - " + dedicacionHoras + " horas";
    }
}