package ui;

import data.Database;
import java.awt.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.*;
import service.AsignadorHorarios;

public class VentanaCronograma extends JFrame {
    private static final Color COLOR_HEADER = new Color(52, 152, 219);
    private static final Color COLOR_FILA_PAR = new Color(241, 245, 249);
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;
    private JTable tabla;

    public VentanaCronograma() {
        EstilosUI.aplicarEstilos();
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/Logo.jpg"));
        setIconImage(icono);
        setTitle("Nuevo Cronograma");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        List<Trabajador> trabajadores;
        try {
            Database.conectar();
            trabajadores = Database.obtenerTrabajadores();

            LocalDate hoy = LocalDate.now();
            LocalDate baseLunes = hoy.with(DayOfWeek.MONDAY);
            LocalDate proximoLunes = hoy.getDayOfWeek().getValue() > DayOfWeek.MONDAY.getValue()
                    ? baseLunes.plusWeeks(1)
                    : baseLunes;

            final LocalDate finalProximoLunes = proximoLunes;

            // Verificar si ya existe un cronograma para esta semana
            if (Database.semanaYaExiste(finalProximoLunes)) {
                // Si existe, mostrar el cronograma guardado
                List<Semana> semanas = Database.obtenerSemanasGuardadas();
                Semana semanaExistente = semanas.stream()
                        .filter(s -> s.getFechaInicio().equals(finalProximoLunes))
                        .findFirst()
                        .orElse(null);

                if (semanaExistente != null) {
                    JOptionPane.showMessageDialog(this,
                            "Ya existe un cronograma para esta semana. Se mostrará el cronograma guardado.");
                    dispose();
                    new VentanaCronogramaGuardado(semanaExistente);
                    return;
                }
            }

            // Si no existe, crear nuevo cronograma
            Semana semana = new Semana(proximoLunes);
            AsignadorHorarios.asignarSemana(semana, trabajadores);

            // Panel superior con título
            JPanel panelTitulo = new JPanel();
            panelTitulo.setBackground(COLOR_HEADER);
            panelTitulo.setPreferredSize(new Dimension(getWidth(), 60));
            panelTitulo.setLayout(new FlowLayout(FlowLayout.CENTER));
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy");
            JLabel lblTitulo = new JLabel("Nuevo Cronograma: Semana del " + proximoLunes.format(formatter));
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
            lblTitulo.setForeground(Color.WHITE);
            panelTitulo.add(lblTitulo);
            
            add(panelTitulo, BorderLayout.NORTH);

            String[] columnas = {"Día", "Mañana", "Tarde"};
            DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

            for (DiaTrabajo dia : semana.getDias()) {
                String diaStr = dia.getFecha().getDayOfWeek() + " " + dia.getFecha();

                String manana = formatearTrabajadores(dia.getTrabajadoresEnTurno(Turno.MANIANA));
                String tarde = formatearTrabajadores(dia.getTrabajadoresEnTurno(Turno.TARDE));

                modelo.addRow(new Object[]{diaStr, manana, tarde});
            }

            tabla = new JTable(modelo);
            JScrollPane scrollPane = new JScrollPane(tabla);
            add(scrollPane, BorderLayout.CENTER);
            tabla.setFillsViewportHeight(true);
            tabla.setShowHorizontalLines(false);
            tabla.setShowVerticalLines(false);
            tabla.setIntercellSpacing(new Dimension(0, 0));
            tabla.setRowHeight(26);
            setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al obtener trabajadores: " + e.getMessage());
            dispose();
            return;
        }
    }

    private String formatearTrabajadores(Set<Trabajador> trabajadores) {
        return trabajadores.stream()
                .map(Trabajador::getNombre)
                .collect(Collectors.joining(", "));
    }
}
