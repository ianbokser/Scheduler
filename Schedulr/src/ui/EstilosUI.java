package ui;

import java.awt.*;
import javax.swing.*;

public class EstilosUI {
    public static void aplicarEstilos() {
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("List.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 24);
        UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
        UIManager.put("Panel.background", Color.decode("#f0f2f5"));
        UIManager.put("Button.background", Color.decode("#4a90e2"));
        UIManager.put("Button.foreground", Color.white);
        UIManager.put("List.background", Color.white);
        UIManager.put("List.selectionBackground", Color.decode("#4a90e2"));
    }

    public static JButton crearBotonModerno(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(Color.decode("#4a90e2"));
        boton.setForeground(Color.white);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setPreferredSize(new Dimension(200, 50));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return boton;
    }

    public static JButton crearBotonNav(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(new Color(0, 0, 0, 0));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(255, 255, 255, 30));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(0, 0, 0, 0));
            }
        });
        
        return boton;
    }
}
