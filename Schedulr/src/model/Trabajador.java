package model;

import java.time.LocalDate;
import java.util.*;

public class Trabajador {
    private String nombre;
    private Set<LocalDate> pedidosDeFranco;
    private Deque<Semana> historialSemanas;

    public Trabajador(String nombre) {
        this.nombre = nombre;
        this.pedidosDeFranco = new HashSet<>();
        this.historialSemanas = new ArrayDeque<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void pedirFranco(LocalDate dia) {
        pedidosDeFranco.add(dia);
    }

    public boolean pidioFranco(LocalDate dia) {
        return pedidosDeFranco.contains(dia);
    }

    public void agregarSemana(Semana semana) {
        historialSemanas.addFirst(semana);
        if (historialSemanas.size() > 2) {
            historialSemanas.removeLast();
        }
    }

    public Deque<Semana> getHistorialSemanas() {
        return historialSemanas;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
