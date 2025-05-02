package model;

import java.time.*;
import java.util.*;

public class Semana {
    private List<DiaTrabajo> dias;
    private LocalDate lunes;

    public Semana(LocalDate lunes) {
        this.lunes = lunes;
        this.dias = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dias.add(new DiaTrabajo(lunes.plusDays(i)));
        }
    }

    public LocalDate getLunes() {
        return this.lunes;
    }

    public List<DiaTrabajo> getDias() {
        return dias;
    }
    
    public DiaTrabajo getDia(LocalDate fecha) {
        return dias.stream().filter(d -> d.getFecha().equals(fecha)).findFirst().orElse(null);
    }
    
    public LocalDate getFechaInicio() {
        return dias.get(0).getFecha();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Semana:\n");
        for (DiaTrabajo d : dias) sb.append(d).append("\n");
        return sb.toString();
    }
}
