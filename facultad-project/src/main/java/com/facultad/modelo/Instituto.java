package com.facultad.modelo;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instituto")
public class Instituto {
    
    @Id
    @Column(name = "codigo", length = 20, nullable = false)
    private String codigo;
    
    @Column(name = "denominacion", length = 100, nullable = false)
    private String denominacion;
    
    // Un Instituto tiene muchos CargosDocentes
    @OneToMany(mappedBy = "instituto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoDocente> cargos = new ArrayList<>();
    
    // Un Instituto dicta muchas Asignaturas
    @OneToMany(mappedBy = "instituto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignatura> asignaturas = new ArrayList<>();
    
    // Constructores
    public Instituto() {}
    
    public Instituto(String codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }
    
    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getDenominacion() { return denominacion; }
    public void setDenominacion(String denominacion) { this.denominacion = denominacion; }
    
    public List<CargoDocente> getCargos() { return cargos; }
    public void setCargos(List<CargoDocente> cargos) { this.cargos = cargos; }
    
    public List<Asignatura> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(List<Asignatura> asignaturas) { this.asignaturas = asignaturas; }
    
    // Métodos de conveniencia
    public void agregarCargo(CargoDocente cargo) {
        cargos.add(cargo);
        cargo.setInstituto(this);
    }
    
    public void agregarAsignatura(Asignatura asignatura) {
        asignaturas.add(asignatura);
        asignatura.setInstituto(this);
    }
    
    @Override
    public String toString() {
        return codigo + " - " + denominacion;
    }
}