package service;

import data.Database;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;
import model.*;

public class AsignadorHorarios {

    public static void asignarSemana(Semana semana, List<Trabajador> trabajadores) {
        Map<Trabajador, Integer> contadorDias = new HashMap<>();
        Map<Set<Trabajador>, Integer> contadorParejas = new HashMap<>();
        Set<Trabajador> trabajaronDomingoAnterior = new HashSet<>();

        // Revisar la semana anterior para identificar quiénes trabajaron el domingo
        Semana semanaAnterior = obtenerSemanaAnterior(semana, trabajadores);
        if (semanaAnterior != null) {
            semanaAnterior.getDias().stream()
                .filter(d -> d.getFecha().getDayOfWeek() == DayOfWeek.SUNDAY)
                .flatMap(d -> d.getTrabajadoresDelDia().stream())
                .forEach(trabajaronDomingoAnterior::add);
        }

        // Inicializar contadores
        for (Trabajador t : trabajadores) {
            contadorDias.put(t, 0);
        }

        Set<Trabajador> trabajanDomingoEstaSemana = new HashSet<>();

        // Procesar los días en orden
        List<DiaTrabajo> diasOrdenados = semana.getDias().stream()
            .sorted(Comparator.comparing(d -> d.getFecha().getDayOfWeek()))
            .collect(Collectors.toList());

        for (DiaTrabajo dia : diasOrdenados) {
            procesarDia(dia, trabajadores, contadorDias, contadorParejas,
                    trabajaronDomingoAnterior, trabajanDomingoEstaSemana);
        }

        // Guardar la semana en el historial
        for (Trabajador t : trabajadores) {
            t.agregarSemana(semana);
        }
        try {
            Database.guardarSemana(semana);
        } catch (SQLException e) {
            e.printStackTrace(); // Manejar según la aplicación
        }
    }

    private static Semana obtenerSemanaAnterior(Semana semana, List<Trabajador> trabajadores) {
        return trabajadores.stream()
            .flatMap(t -> t.getHistorialSemanas().stream())
            .filter(s -> s.getLunes().isBefore(semana.getLunes()))
            .max(Comparator.comparing(Semana::getLunes))
            .orElse(null);
    }

    private static void procesarDia(DiaTrabajo dia, List<Trabajador> trabajadores,
            Map<Trabajador, Integer> contadorDias, Map<Set<Trabajador>, Integer> contadorParejas,
            Set<Trabajador> trabajaronDomingoAnterior, Set<Trabajador> trabajanDomingoEstaSemana) {
        DayOfWeek diaSemana = dia.getFecha().getDayOfWeek();

        List<Trabajador> trabajadoresDisponibles = trabajadores.stream()
            .filter(t -> !t.pidioFranco(dia.getFecha()))
            .filter(t -> contadorDias.get(t) < 6)
            .filter(t -> {
                if (diaSemana == DayOfWeek.MONDAY) {
                    // Excluir trabajadores que trabajaron el domingo anterior
                    return !trabajaronDomingoAnterior.contains(t);
                }
                if (diaSemana == DayOfWeek.SUNDAY) {
                    // Excluir trabajadores que trabajaron el domingo actual
                    return !trabajanDomingoEstaSemana.contains(t);
                }
                return true;
            })
            .collect(Collectors.toList());

        List<Turno> turnosDelDia = diaSemana == DayOfWeek.SUNDAY ? 
            Collections.singletonList(Turno.MANIANA) : 
            Arrays.asList(Turno.MANIANA, Turno.TARDE);

        for (Turno turno : turnosDelDia) {
            // Filtrar trabajadores ya asignados en este día/turno
            List<Trabajador> disponibles = trabajadoresDisponibles.stream()
                .filter(t -> !dia.estaAsignado(turno, t))
                .filter(t -> !dia.estaAsignadoEnDia(t))
                .collect(Collectors.toList());

            // Generar todas las combinaciones posibles de 2 trabajadores
            List<Set<Trabajador>> posiblesParejas = new ArrayList<>();
            for (int i = 0; i < disponibles.size(); i++) {
                for (int j = i + 1; j < disponibles.size(); j++) {
                    Set<Trabajador> pareja = new HashSet<>(Arrays.asList(disponibles.get(i), disponibles.get(j)));
                    posiblesParejas.add(pareja);
                }
            }

            // Elegir la mejor pareja: la menos repetida
            posiblesParejas.sort(Comparator.comparingInt(p -> contadorParejas.getOrDefault(p, 0)));

            // Selección aleatoria entre las mejores (menos repetidas)
            int minRepeticiones = posiblesParejas.isEmpty() ? 0 :
                    contadorParejas.getOrDefault(posiblesParejas.get(0), 0);

            List<Set<Trabajador>> mejores = posiblesParejas.stream()
                    .filter(p -> contadorParejas.getOrDefault(p, 0) == minRepeticiones)
                    .collect(Collectors.toList());

            if (!mejores.isEmpty()) {
                Set<Trabajador> elegida = mejores.get(new Random().nextInt(mejores.size()));
                Iterator<Trabajador> it = elegida.iterator();
                Trabajador t1 = it.next();
                Trabajador t2 = it.next();

                dia.asignar(turno, t1);
                dia.asignar(turno, t2);

                contadorDias.put(t1, contadorDias.get(t1) + 1);
                contadorDias.put(t2, contadorDias.get(t2) + 1);

                contadorParejas.put(elegida, contadorParejas.getOrDefault(elegida, 0) + 1);

                if (diaSemana == DayOfWeek.SUNDAY) {
                    trabajanDomingoEstaSemana.add(t1);
                    trabajanDomingoEstaSemana.add(t2);
                }
            }
        }

        if (diaSemana == DayOfWeek.SUNDAY) {
            // Actualizar la lista de trabajadores que trabajaron el domingo
            dia.getTrabajadoresDelDia().forEach(trabajanDomingoEstaSemana::add);
        }
    }
}
