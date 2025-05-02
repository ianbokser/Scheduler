package ui;

import model.Trabajador;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private static final List<Trabajador> trabajadores = new ArrayList<>();

    static {
        trabajadores.add(new Trabajador("Alice"));
        trabajadores.add(new Trabajador("Bob"));
        trabajadores.add(new Trabajador("Carlos"));
        trabajadores.add(new Trabajador("Diana"));
        trabajadores.add(new Trabajador("Eva"));
        trabajadores.add(new Trabajador("Fede"));
    }

    public static List<Trabajador> getTrabajadores() {
        return trabajadores;
    }
}
