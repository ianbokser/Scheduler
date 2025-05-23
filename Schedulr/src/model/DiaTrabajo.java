package model;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class DiaTrabajo {
    private LocalDate fecha;
    private Map<Turno, Set<Trabajador>> asignaciones;


    public DiaTrabajo(LocalDate fecha) {
        this.fecha = fecha;
        this.asignaciones = new HashMap<>();
    }
    
    

    public boolean asignar(Turno turno, Trabajador trabajador) {
        asignaciones.putIfAbsent(turno, new HashSet<>());
        if (asignaciones.get(turno).size() >= 2) return false;
        asignaciones.get(turno).add(trabajador);
        return true;
    }

    public Set<Trabajador> getTrabajadoresEnTurno(Turno turno) {
        return asignaciones.getOrDefault(turno, Collections.emptySet());
    }

    public Set<Trabajador> getTrabajadoresDelDia() {
        return asignaciones.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Map<Turno, Set<Trabajador>> getAsignaciones() {
        return asignaciones;
    }

    public boolean estaAsignado(Turno turno, Trabajador trabajador) {
        return asignaciones.getOrDefault(turno, new HashSet<>()).contains(trabajador);
    }

    public boolean estaAsignadoEnDia(Trabajador trabajador) {
        for (Set<Trabajador> lista : asignaciones.values()) {
            if (lista.contains(trabajador)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return fecha + ": " + asignaciones;
    }
}
