package service;

import data.Database;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import model.*;

public class AsignadorHorarios {

    public static void asignarSemana(Semana semana, List<Trabajador> trabajadores) {
        Map<Trabajador, Integer> diasTrabajados = new HashMap<>();
        Map<Set<Trabajador>, Integer> parejasFormadas = new HashMap<>();
        Map<Trabajador, List<LocalDate>> historialTrabajo = new HashMap<>();
        Set<Trabajador> trabajadoresDomingoAnterior = new HashSet<>();
        Set<Trabajador> trabajadoresDomingoActual = new HashSet<>();
        Set<Trabajador> trabajadoresConFrancoLunes = new HashSet<>();

        // Obtener trabajadores del domingo anterior
        Semana semanaAnterior = obtenerSemanaAnterior(semana, trabajadores);
        if (semanaAnterior != null) {
            DiaTrabajo domingoAnterior = semanaAnterior.getDias().stream()
                .filter(d -> d.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY)
                .findFirst().orElse(null);
            if (domingoAnterior != null) {
                trabajadoresDomingoAnterior.addAll(domingoAnterior.getTrabajadoresDelDia());
            }
        }

        trabajadores.forEach(t -> {
            diasTrabajados.put(t, 0);
            historialTrabajo.put(t, new ArrayList<>());
        });

        // Ordenar días de lunes a domingo
        List<DiaTrabajo> diasOrdenados = semana.getDias().stream()
            .sorted(Comparator.comparing(d -> d.getFecha().getDayOfWeek()))
            .collect(Collectors.toList());

        // Detectar trabajadores que trabajan el domingo de esta misma semana
        DiaTrabajo domingoActual = diasOrdenados.stream()
            .filter(d -> d.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY)
            .findFirst().orElse(null);

        if (domingoActual != null) {
            trabajadoresDomingoActual.addAll(domingoActual.getTrabajadoresDelDia());
        }

        // Franco el lunes para los que trabajaron el domingo anterior
        trabajadoresConFrancoLunes.addAll(trabajadoresDomingoAnterior);

        Set<Trabajador> trabajadoresDelUltimoDomingo = new HashSet<>();

        for (DiaTrabajo dia : diasOrdenados) {
            DayOfWeek diaSemana = dia.getFecha().getDayOfWeek();

            // Determinar trabajadores que necesitan franco por trabajar 5 días seguidos
            Set<Trabajador> trabajadoresConFranco = new HashSet<>();
            for (Trabajador t : trabajadores) {
                if (trabajoCincoDiasSeguidos(t, historialTrabajo, dia.getFecha())) {
                    trabajadoresConFranco.add(t);
                }
            }

            // También se agregan los del domingo anterior si es lunes
            if (diaSemana == DayOfWeek.MONDAY) {
                trabajadoresConFranco.addAll(trabajadoresDelUltimoDomingo);
            }

            System.out.println("dia: " + dia + " trabajadores: " + trabajadores + " diasTrabajados: " + diasTrabajados + " parejasFormadas: " + parejasFormadas + " trabajadoresConFranco: " + trabajadoresConFranco);

            asignarTrabajadoresDia(dia, trabajadores, diasTrabajados,
                parejasFormadas, trabajadoresConFranco, historialTrabajo);

            // Si es domingo, actualizar los trabajadores del domingo actual *después* de asignar
            if (diaSemana == DayOfWeek.SUNDAY) {
                trabajadoresDelUltimoDomingo.clear();
                trabajadoresDelUltimoDomingo.addAll(dia.getTrabajadoresDelDia());
            }
        }

        trabajadores.forEach(t -> t.agregarSemana(semana));

        try {
            Database.guardarSemana(semana);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void asignarTrabajadoresDia(DiaTrabajo dia, List<Trabajador> trabajadores,
            Map<Trabajador, Integer> diasTrabajados, Map<Set<Trabajador>, Integer> parejasFormadas,
            Set<Trabajador> trabajadoresConFranco, Map<Trabajador, List<LocalDate>> historialTrabajo) {

        DayOfWeek diaSemana = dia.getFecha().getDayOfWeek();
        boolean esLunes = diaSemana == DayOfWeek.MONDAY;
        boolean esDomingo = diaSemana == DayOfWeek.SUNDAY;

        // Filtrar trabajadores disponibles
        List<Trabajador> disponibles = trabajadores.stream()
            .filter(t -> !t.pidioFranco(dia.getFecha()))
            .filter(t -> diasTrabajados.get(t) < 6)
            .filter(t -> !trabajadoresConFranco.contains(t))
            .collect(Collectors.toList());

        // Turnos a asignar
        List<Turno> turnos = esDomingo ?
            Collections.singletonList(Turno.MANIANA) :
            Arrays.asList(Turno.MANIANA, Turno.TARDE);

        for (Turno turno : turnos) {
            asignarTurno(dia, turno, disponibles, diasTrabajados, parejasFormadas, historialTrabajo);
        }
    }

    private static void asignarTurno(DiaTrabajo dia, Turno turno, List<Trabajador> disponibles,
            Map<Trabajador, Integer> diasTrabajados, Map<Set<Trabajador>, Integer> parejasFormadas,
            Map<Trabajador, List<LocalDate>> historialTrabajo) {

        List<Trabajador> trabajadoresParaTurno = disponibles.stream()
            .filter(t -> !dia.estaAsignadoEnDia(t))
            .collect(Collectors.toList());

        if (trabajadoresParaTurno.size() < 2) return;

        List<Set<Trabajador>> parejasPosibles = new ArrayList<>();
        for (int i = 0; i < trabajadoresParaTurno.size() - 1; i++) {
            for (int j = i + 1; j < trabajadoresParaTurno.size(); j++) {
                Set<Trabajador> pareja = new HashSet<>(Arrays.asList(
                    trabajadoresParaTurno.get(i),
                    trabajadoresParaTurno.get(j)
                ));
                parejasPosibles.add(pareja);
            }
        }

        parejasPosibles.sort(Comparator.comparingInt(p -> parejasFormadas.getOrDefault(p, 0)));

        if (!parejasPosibles.isEmpty()) {
            Set<Trabajador> pareja = parejasPosibles.get(0);
            for (Trabajador t : pareja) {
                dia.asignar(turno, t);
                diasTrabajados.put(t, diasTrabajados.get(t) + 1);
                historialTrabajo.get(t).add(dia.getFecha());
            }
            parejasFormadas.merge(pareja, 1, Integer::sum);
        }
    }

    private static boolean trabajoCincoDiasSeguidos(Trabajador t, Map<Trabajador, List<LocalDate>> historial, LocalDate fechaActual) {
        List<LocalDate> dias = historial.getOrDefault(t, new ArrayList<>());

        dias = dias.stream()
            .filter(d -> d.isBefore(fechaActual))
            .sorted(Comparator.reverseOrder())
            .limit(5)
            .collect(Collectors.toList());

        if (dias.size() < 5) return false;

        for (int i = 0; i < 5; i++) {
            if (!dias.get(i).equals(fechaActual.minusDays(i + 1))) {
                return false;
            }
        }

        return true;
    }

    private static Semana obtenerSemanaAnterior(Semana semana, List<Trabajador> trabajadores) {
        return trabajadores.stream()
            .flatMap(t -> t.getHistorialSemanas().stream())
            .filter(s -> s.getLunes().isBefore(semana.getLunes()))
            .max(Comparator.comparing(Semana::getLunes))
            .orElse(null);
    }
}
