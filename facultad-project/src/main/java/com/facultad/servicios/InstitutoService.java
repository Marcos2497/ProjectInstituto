package com.facultad.servicios;

import com.facultad.modelo.Instituto;
import com.facultad.modelo.Asignatura;
import com.facultad.modelo.CargoDocente;
import com.facultad.repositorio.Repositorio;
import java.util.List;

public class InstitutoService {
    
    private final Repositorio repositorio;
    // Referencias a otros servicios (los inicializaremos cuando los necesitemos)
    private AsignaturaService asignaturaService;
    private CargoDocenteService cargoDocenteService;
    
    public InstitutoService(Repositorio repositorio) {
        this.repositorio = repositorio;
        // Inicializar servicios relacionados
        this.asignaturaService = new AsignaturaService(repositorio);
        this.cargoDocenteService = new CargoDocenteService(repositorio);
    }
    
    // ========== OPERACIONES CRUD ==========
    public void agregarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.insertar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al agregar instituto: " + e.getMessage(), e);
        }
    }
    
    public void editarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.modificar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al editar instituto: " + e.getMessage(), e);
        }
    }
    
    public void eliminarInstituto(Instituto instituto) {
        try {
            repositorio.iniciarTransaccion();
            repositorio.eliminar(instituto);
            repositorio.confirmarTransaccion();
        } catch (Exception e) {
            repositorio.descartarTransaccion();
            throw new RuntimeException("Error al eliminar instituto: " + e.getMessage(), e);
        }
    }
    
    // ========== CONSULTAS ==========
    public List<Instituto> obtenerTodos() {
        return repositorio.buscarTodos(Instituto.class);
    }
    
    public boolean existeInstituto(String codigo) {
        try {
            return buscarPorCodigo(codigo) != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public Instituto buscarPorCodigo(String codigo) {
        try {
            return repositorio.buscar(Instituto.class, codigo);
        } catch (Exception e) {
            System.err.println("Error buscando instituto por código: " + e.getMessage());
            return null;
        }
    }
    
    // ========== NUEVOS MÉTODOS PARA VALIDACIÓN DE ELIMINACIÓN ==========
    
    /**
     * Verifica si el instituto tiene asignaturas asociadas
     * @return true si tiene asignaturas, false si no
     */
    public boolean tieneAsignaturas(Instituto instituto) {
        return contarAsignaturas(instituto) > 0;
    }
    
    /**
     * Obtiene la cantidad de asignaturas del instituto
     */
    public int contarAsignaturas(Instituto instituto) {
        if (instituto == null) return 0;
        List<Asignatura> asignaturas = asignaturaService.buscarPorInstituto(instituto);
        return asignaturas.size();
    }
    
    /**
     * Obtiene la lista de asignaturas del instituto
     */
    public List<Asignatura> obtenerAsignaturas(Instituto instituto) {
        if (instituto == null) return List.of();
        return asignaturaService.buscarPorInstituto(instituto);
    }
    
    /**
     * Verifica si el instituto tiene cargos docentes asociados
     * @return true si tiene cargos, false si no
     */
    public boolean tieneCargosDocentes(Instituto instituto) {
        return contarCargosDocentes(instituto) > 0;
    }
    
    /**
     * Obtiene la cantidad de cargos del instituto
     */
    public int contarCargosDocentes(Instituto instituto) {
        if (instituto == null) return 0;
        List<CargoDocente> todosLosCargos = cargoDocenteService.obtenerTodos();
        return (int) todosLosCargos.stream()
            .filter(c -> c.getInstituto() != null && c.getInstituto().equals(instituto))
            .count();
    }
    
    /**
     * Obtiene la lista de cargos del instituto
     */
    public List<CargoDocente> obtenerCargosDocentes(Instituto instituto) {
        if (instituto == null) return List.of();
        List<CargoDocente> todosLosCargos = cargoDocenteService.obtenerTodos();
        return todosLosCargos.stream()
            .filter(c -> c.getInstituto() != null && c.getInstituto().equals(instituto))
            .toList();
    }
    
    /**
     * Verificación completa para eliminación
     * @param instituto Instituto a verificar
     * @param mensaje StringBuilder para construir el mensaje de error
     * @return true si se puede eliminar, false si tiene dependencias
     */
    public boolean sePuedeEliminar(Instituto instituto, StringBuilder mensaje) {
        int cantAsignaturas = contarAsignaturas(instituto);
        int cantCargos = contarCargosDocentes(instituto);
        
        if (cantAsignaturas == 0 && cantCargos == 0) {
            return true;
        }
        
        mensaje.append("❌ No se puede eliminar el instituto '")
               .append(instituto.getDenominacion())
               .append("' porque:\n\n");
        
        if (cantAsignaturas > 0) {
            mensaje.append("📚 Tiene ").append(cantAsignaturas)
                   .append(" asignatura(s) asociada(s):\n");
            // Opcional: listar algunas asignaturas
            List<Asignatura> asignaturas = obtenerAsignaturas(instituto);
            asignaturas.stream().limit(3).forEach(a -> 
                mensaje.append("   • ").append(a.getCodigo())
                       .append(" - ").append(a.getNombre()).append("\n")
            );
            if (asignaturas.size() > 3) {
                mensaje.append("   • ... y ").append(asignaturas.size() - 3)
                       .append(" más\n");
            }
            mensaje.append("\n");
        }
        
        if (cantCargos > 0) {
            mensaje.append("👤 Tiene ").append(cantCargos)
                   .append(" cargo(s) docente(s) asociado(s)\n");
            // Opcional: listar algunos cargos
            List<CargoDocente> cargos = obtenerCargosDocentes(instituto);
            cargos.stream().limit(3).forEach(c -> 
                mensaje.append("   • Docente: ").append(c.getDocente().getNombreCompleto())
                       .append(" - ").append(c.getDedicacionHoras()).append(" horas\n")
            );
            if (cargos.size() > 3) {
                mensaje.append("   • ... y ").append(cargos.size() - 3)
                       .append(" más\n");
            }
        }
        
        mensaje.append("\n💡 Debe reasignar o eliminar estas dependencias primero.");
        return false;
    }
}