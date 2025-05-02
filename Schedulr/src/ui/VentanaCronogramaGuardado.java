package ui;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.*;
import model.*;

public class VentanaCronogramaGuardado extends JFrame {
    private static final Color COLOR_HEADER = new Color(52, 152, 219);
    private static final Color COLOR_FILA_PAR = new Color(241, 245, 249);
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;
    
    public VentanaCronogramaGuardado(Semana semana) {
        EstilosUI.aplicarEstilos();
        
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/Logo.jpg"));
        setIconImage(icono);
        setTitle("Cronograma guardado");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_HEADER);
        panelTitulo.setPreferredSize(new Dimension(getWidth(), 60));
        panelTitulo.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy");
        JLabel lblTitulo = new JLabel("Cronograma: Semana del " + semana.getFechaInicio().format(formatter));
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);
        
        add(panelTitulo, BorderLayout.NORTH);

        // Tabla con estilo moderno
        String[] columnas = {"Día", "Mañana", "Tarde"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (DiaTrabajo dia : semana.getDias()) {
            String diaStr = dia.getFecha().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) +
                           " " + dia.getFecha().format(DateTimeFormatter.ofPattern("d/M"));
            
            String manana = formatearTrabajadores(dia.getTrabajadoresEnTurno(Turno.MANIANA));
            String tarde = formatearTrabajadores(dia.getTrabajadoresEnTurno(Turno.TARDE));

            modelo.addRow(new Object[]{diaStr, manana, tarde});
        }

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.setRowHeight(35);
        tabla.setIntercellSpacing(new Dimension(10, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(COLOR_HEADER);
        tabla.getTableHeader().setForeground(Color.WHITE);
        ((DefaultTableCellRenderer)tabla.getTableHeader().getDefaultRenderer())
            .setHorizontalAlignment(JLabel.CENTER);

        // Renderer personalizado para las celdas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                ((JLabel)c).setHorizontalAlignment(column == 0 ? JLabel.LEFT : JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Configurar anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private String formatearTrabajadores(Set<Trabajador> trabajadores) {
        return trabajadores.stream()
                .map(Trabajador::getNombre)
                .collect(Collectors.joining(", "));
    }
}
