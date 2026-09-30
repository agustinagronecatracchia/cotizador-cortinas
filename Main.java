package main;

import view.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación.
 */
public class Main {
    public static void main(String[] args) {
        // Se ejecuta la interfaz gráfica en el Event Dispatch Thread (EDT),
        // como recomienda Swing para evitar problemas de concurrencia.
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si falla, se usa el look and feel por defecto de Swing.
            }
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
