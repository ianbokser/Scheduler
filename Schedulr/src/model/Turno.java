package model;

import java.time.LocalTime;

public class Turno {
    public static final Turno MANIANA = new Turno(LocalTime.of(9, 0), LocalTime.of(15, 0));
    public static final Turno TARDE = new Turno(LocalTime.of(15, 0), LocalTime.of(21, 0));

    private final LocalTime inicio;
    private final LocalTime fin;

    public Turno(LocalTime inicio, LocalTime fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    @Override
    public String toString() {
        return inicio + " - " + fin;
    }
}
