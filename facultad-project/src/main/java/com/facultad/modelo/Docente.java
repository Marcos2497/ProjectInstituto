package com.facultad.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "docente")
public class Docente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "legajo")
    private Integer legajo;
    
    @Column(name = "documento_unico", length = 20, nullable = false, unique = true)
    private String documentoUnico;
    
    @Column(name = "nombres", length = 50, nullable = false)
    private String nombres;
    
    @Column(name = "apellidos", length = 50, nullable = false)
    private String apellidos;
    
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;
    
    @Column(name = "direccion_notificaciones", length = 200)
    private String direccionNotificaciones;
    
    // Un Docente ocupa muchos Cargos
    @OneToMany(mappedBy = "docente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CargoDocente> cargos = new ArrayList<>();
    
    // Un Docente es responsable de muchas Asignaturas
    @OneToMany(mappedBy = "docenteResponsable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignatura> asignaturasResponsables = new ArrayList<>();
    
    // Constructores
    public Docente() {}
    
    public Docente(String documentoUnico, String nombres, String apellidos) {
        this.documentoUnico = documentoUnico;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }
    
    // Getters y Setters
    public Integer getLegajo() { return legajo; }
    public void setLegajo(Integer legajo) { this.legajo = legajo; }
    
    public String getDocumentoUnico() { return documentoUnico; }
    public void setDocumentoUnico(String documentoUnico) { this.documentoUnico = documentoUnico; }
    
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getDireccionNotificaciones() { return direccionNotificaciones; }
    public void setDireccionNotificaciones(String direccionNotificaciones) { this.direccionNotificaciones = direccionNotificaciones; }
    
    public List<CargoDocente> getCargos() { return cargos; }
    public void setCargos(List<CargoDocente> cargos) { this.cargos = cargos; }
    
    public List<Asignatura> getAsignaturasResponsables() { return asignaturasResponsables; }
    public void setAsignaturasResponsables(List<Asignatura> asignaturasResponsables) { this.asignaturasResponsables = asignaturasResponsables; }
    
    // Métodos de conveniencia
    public void agregarCargo(CargoDocente cargo) {
        cargos.add(cargo);
        cargo.setDocente(this);
    }
    
    public void agregarAsignaturaResponsable(Asignatura asignatura) {
        asignaturasResponsables.add(asignatura);
        asignatura.setDocenteResponsable(this);
    }
    
    public String getNombreCompleto() {
        return apellidos + ", " + nombres;
    }
    
    @Override
    public String toString() {
        return legajo + " - " + getNombreCompleto();
    }
}