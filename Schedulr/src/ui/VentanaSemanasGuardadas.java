package ui;

import data.Database;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import model.Semana;

public class VentanaSemanasGuardadas extends JFrame {
    private JList<String> listaSemanas;
    private DefaultListModel<String> modeloLista;
    private List<Semana> semanas;

    public VentanaSemanasGuardadas() {
        EstilosUI.aplicarEstilos();
        Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/Logo.jpg"));
        setIconImage(icono);
        setTitle("Semanas Guardadas");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        modeloLista = new DefaultListModel<>();
        listaSemanas = new JList<>(modeloLista);
        JScrollPane scrollPane = new JScrollPane(listaSemanas);
        add(scrollPane, BorderLayout.CENTER);

        JButton btnVer = EstilosUI.crearBotonModerno("Ver Cronograma");
        JButton btnEliminar = EstilosUI.crearBotonModerno("Eliminar Semana");

        btnVer.addActionListener(e -> verCronograma());
        btnEliminar.addActionListener(e -> eliminarSemana());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnVer);
        panelBotones.add(btnEliminar);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(panelBotones, BorderLayout.SOUTH);

        cargarSemanas();

        setVisible(true);
    }

    private void cargarSemanas() {
        try {
            Database.conectar();
            semanas = Database.obtenerSemanasGuardadas();
            modeloLista.clear();
            for (Semana s : semanas) {
                LocalDate inicio = s.getFechaInicio();
                modeloLista.addElement("Semana del " + inicio);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar semanas: " + e.getMessage());
        }
    }

    private void verCronograma() {
        int index = listaSemanas.getSelectedIndex();
        if (index >= 0) {
            Semana seleccionada = semanas.get(index);
            new VentanaCronogramaGuardado(seleccionada);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una semana.");
        }
    }
    private void eliminarSemana() {
        int index = listaSemanas.getSelectedIndex();
        if (index >= 0) {
            Semana seleccionada = semanas.get(index);
            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "�Est� seguro que desea eliminar la semana del " + seleccionada.getFechaInicio() + "?",
                    "Confirmar eliminaci�n",
                    JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                try {
                    Database.eliminarSemana(seleccionada.getFechaInicio());
                    cargarSemanas();
                    JOptionPane.showMessageDialog(this, "Semana eliminada exitosamente.");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar semana: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione una semana para eliminar.");
        }
    }

}
