package view;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana secundaria (JDialog) que muestra el texto del presupuesto ya
 * calculado, en un JTextArea de sólo lectura con fuente monoespaciada
 * para que las columnas queden alineadas.
 */
public class VentanaPresupuesto extends JDialog {

    public VentanaPresupuesto(JFrame padre, String textoPresupuesto) {
        super(padre, "Presupuesto de Cortinas", true);
        setLayout(new BorderLayout(10, 10));

        JTextArea areaTexto = new JTextArea(textoPresupuesto);
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        areaTexto.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        JButton botonCerrar = new JButton("Cerrar");
        botonCerrar.addActionListener(e -> dispose());
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(botonCerrar);
        add(panelBoton, BorderLayout.SOUTH);

        setSize(480, 600);
        setLocationRelativeTo(padre);
    }
}
