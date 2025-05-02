package ui;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.*;

public class VentanaPrincipal extends JFrame {
    private static final Color COLOR_HEADER = new Color(52, 152, 219);
    private static final Color COLOR_FILA_PAR = new Color(241, 245, 249);
    private static final Color COLOR_FILA_IMPAR = Color.WHITE;

    private JTable tabla;
    private LocalDate fechaInicio;

    public VentanaPrincipal() {
        setTitle("Ventana Principal");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 10));

        // Panel superior con título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(COLOR_HEADER);
        panelTitulo.setPreferredSize(new Dimension(getWidth(), 60));
        panelTitulo.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel lblTitulo = new JLabel("Cronograma Semanal");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo);

        add(panelTitulo, BorderLayout.NORTH);

        // Crear tabla
        tabla = crearTabla();
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Inicializar fecha de inicio
        fechaInicio = LocalDate.now();
        actualizarFechasTabla();

        setVisible(true);
    }

    private JTable crearTabla() {
        String[] columnas = {"Día", "Mañana", "Tarde"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 7) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Solo las columnas de turnos son editables
            }
        };

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabla.setRowHeight(35);
        tabla.setIntercellSpacing(new Dimension(10, 0));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabla.getTableHeader().setBackground(COLOR_HEADER);
        tabla.getTableHeader().setForeground(Color.WHITE);
        ((DefaultTableCellRenderer) tabla.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        // Renderer personalizado para las celdas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? COLOR_FILA_PAR : COLOR_FILA_IMPAR);
                ((JLabel) c).setHorizontalAlignment(column == 0 ? JLabel.LEFT : JLabel.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        });

        // Configurar anchos de columna
        tabla.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(250);

        return tabla;
    }

    private void actualizarFechasTabla() {
        for (int i = 0; i < 7; i++) {
            LocalDate fecha = fechaInicio.plusDays(i);
            String diaStr = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES")) +
                    " " + fecha.format(DateTimeFormatter.ofPattern("d/M"));
            tabla.setValueAt(diaStr, i, 0);
        }
    }
}