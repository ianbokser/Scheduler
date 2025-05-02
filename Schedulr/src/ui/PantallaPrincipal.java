package ui;

import data.Database;
import java.awt.*;
import javax.swing.*;

public class PantallaPrincipal extends JFrame {
	public PantallaPrincipal() {
		EstilosUI.aplicarEstilos();
		Image icono = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/images/Logo.jpg"));
		setIconImage(icono);
		setTitle("Asignador de Horarios");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(null);

		BackgroundPanel backgroundPanel = new BackgroundPanel();
		backgroundPanel.setLayout(new GridBagLayout());
		setContentPane(backgroundPanel);

		JButton btnEditarTrabajadores = EstilosUI.crearBotonModerno("Editar trabajadores");
		JButton btnCrearCronograma = EstilosUI.crearBotonModerno("Crear cronograma");
		JButton btnVerSemanas = EstilosUI.crearBotonModerno("Ver semanas guardadas");

		btnVerSemanas.setPreferredSize(new Dimension(200, 50));
		btnVerSemanas.addActionListener(e -> abrirSemanasGuardadas());

		btnEditarTrabajadores.setPreferredSize(new Dimension(200, 50));
		btnCrearCronograma.setPreferredSize(new Dimension(200, 50));

		btnEditarTrabajadores.addActionListener(e -> abrirEditorTrabajadores());
		btnCrearCronograma.addActionListener(e -> abrirCronograma());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(20, 0, 20, 0);
		gbc.gridy = 0;
		backgroundPanel.add(btnEditarTrabajadores, gbc);

		gbc.gridy = 1;
		backgroundPanel.add(btnVerSemanas, gbc);

		gbc.gridy = 2;
		backgroundPanel.add(btnCrearCronograma, gbc);

		setVisible(true);
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try {
					Database.cerrar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void abrirEditorTrabajadores() {
		SwingUtilities.invokeLater(() -> new EditorTrabajadores(this));
	}

	private void abrirCronograma() {
		SwingUtilities.invokeLater(() -> new VentanaCronograma());
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(PantallaPrincipal::new);
	}

	private void abrirSemanasGuardadas() {
		SwingUtilities.invokeLater(VentanaSemanasGuardadas::new);
	}

}
