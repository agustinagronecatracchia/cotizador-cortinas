package view;

import model.Blackout;
import model.Cortina;
import model.Opaca;
import model.Traslucida;
import service.CalculadoraPresupuesto;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

/**
 * Ventana principal de la aplicación. Permite cargar los datos de la
 * ventana y de la cortina, validarlos, calcular el presupuesto y mostrar
 * el resultado en una ventana secundaria (VentanaPresupuesto).
 */
public class VentanaPrincipal extends JFrame {

    // ---------- Campos de datos de la ventana ----------
    private final JTextField campoAnchoVentana = new JTextField(8);
    private final JTextField campoAlturaVentana = new JTextField(8);
    private final JTextField campoRuedo = new JTextField(8);
    private final JTextField campoArrastre = new JTextField(8);

    // ---------- Campos de datos de la cortina ----------
    private final JComboBox<String> comboTipoCortina =
            new JComboBox<>(new String[]{"Traslúcida", "Opaca", "Blackout"});
    private final JComboBox<String> comboTipoTela = new JComboBox<>();
    private final JComboBox<String> comboTipoCabezal =
            new JComboBox<>(new String[]{"Ollao", "Bolsillo", "Broche"});
    private final JComboBox<String> comboTipoSoporte =
            new JComboBox<>(new String[]{"7 cm", "12 cm"});
    private final JTextField campoPrecioTela = new JTextField(8);

    // ---------- Botones ----------
    private final JButton botonCalcular = new JButton("CALCULAR PRESUPUESTO");
    private final JButton botonLimpiar = new JButton("LIMPIAR");
    private final JButton botonNuevo = new JButton("NUEVO PRESUPUESTO");
    private final JButton botonSalir = new JButton("SALIR");

    public VentanaPrincipal() {
        super("Presupuesto de Cortinas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(construirPanelFormulario(), BorderLayout.CENTER);
        add(construirPanelBotones(), BorderLayout.SOUTH);

        actualizarComboTelas(); // carga inicial de telas según el tipo de cortina

        comboTipoCortina.addActionListener(e -> actualizarComboTelas());
        botonCalcular.addActionListener(e -> calcularPresupuesto());
        botonLimpiar.addActionListener(e -> limpiarFormulario());
        botonNuevo.addActionListener(e -> limpiarFormulario());
        botonSalir.addActionListener(e -> System.exit(0));

        pack();
        setMinimumSize(new Dimension(480, 420));
        setLocationRelativeTo(null);
    }

    /** Arma el panel central con todos los campos del formulario. */
    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        int fila = 0;
        fila = agregarFila(panel, c, fila, "Ancho de ventana (cm):", campoAnchoVentana);
        fila = agregarFila(panel, c, fila, "Altura de ventana (cm):", campoAlturaVentana);
        fila = agregarFila(panel, c, fila, "Ruedo (cm):", campoRuedo);
        fila = agregarFila(panel, c, fila, "Arrastre (cm):", campoArrastre);
        fila = agregarFila(panel, c, fila, "Tipo de cortina:", comboTipoCortina);
        fila = agregarFila(panel, c, fila, "Tipo de tela:", comboTipoTela);
        fila = agregarFila(panel, c, fila, "Tipo de cabezal:", comboTipoCabezal);
        fila = agregarFila(panel, c, fila, "Tipo de soporte:", comboTipoSoporte);
        fila = agregarFila(panel, c, fila, "Precio por metro de tela ($):", campoPrecioTela);

        JLabel notaPrecio = new JLabel("(Opcional. Si se deja vacío, el costo de tela se muestra como $0)");
        notaPrecio.setFont(notaPrecio.getFont().deriveFont(Font.ITALIC, 11f));
        c.gridx = 1;
        c.gridy = fila;
        panel.add(notaPrecio, c);

        return panel;
    }

    private int agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, JComponent campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        panel.add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(campo, c);
        return fila + 1;
    }

    private JPanel construirPanelBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.add(botonCalcular);
        panel.add(botonLimpiar);
        panel.add(botonNuevo);
        panel.add(botonSalir);
        return panel;
    }

    /** Recarga el combo de telas según el tipo de cortina seleccionado. */
    private void actualizarComboTelas() {
        comboTipoTela.removeAllItems();
        Set<String> telas;
        switch ((String) comboTipoCortina.getSelectedItem()) {
            case "Opaca":
                telas = Opaca.TELAS.keySet();
                break;
            case "Blackout":
                telas = Blackout.TELAS.keySet();
                break;
            default:
                telas = Traslucida.TELAS.keySet();
        }
        for (String tela : telas) {
            comboTipoTela.addItem(tela);
        }
    }

    /** Valida los campos numéricos y ejecuta el cálculo del presupuesto. */
    private void calcularPresupuesto() {
        try {
            double anchoVentana = leerNumeroPositivo(campoAnchoVentana, "Ancho de ventana");
            double alturaVentana = leerNumeroPositivo(campoAlturaVentana, "Altura de ventana");
            double ruedo = leerNumeroNoNegativo(campoRuedo, "Ruedo");
            double arrastre = leerNumeroNoNegativo(campoArrastre, "Arrastre");
            double precioTela = leerPrecioTelaOpcional();

            String tipoCabezal = (String) comboTipoCabezal.getSelectedItem();
            String tipoTela = (String) comboTipoTela.getSelectedItem();
            String tipoCortinaSeleccionado = (String) comboTipoCortina.getSelectedItem();
            String tipoSoporte = comboTipoSoporte.getSelectedItem().toString().startsWith("12") ? "12" : "7";

            Cortina cortina;
            switch (tipoCortinaSeleccionado) {
                case "Opaca":
                    cortina = new Opaca(tipoCabezal, anchoVentana, alturaVentana, ruedo, arrastre, tipoTela);
                    break;
                case "Blackout":
                    cortina = new Blackout(tipoCabezal, anchoVentana, alturaVentana, ruedo, arrastre, tipoTela);
                    break;
                default:
                    cortina = new Traslucida(tipoCabezal, anchoVentana, alturaVentana, ruedo, arrastre, tipoTela);
            }

            CalculadoraPresupuesto calculadora = new CalculadoraPresupuesto();
            calculadora.calcular(cortina, tipoSoporte, precioTela);

            String reporte = calculadora.generarReporte();
            new VentanaPresupuesto(this, reporte).setVisible(true);

        } catch (ValidacionException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Lee un campo numérico y valida que sea mayor a 0. */
    private double leerNumeroPositivo(JTextField campo, String nombreCampo) throws ValidacionException {
        double valor = leerNumero(campo, nombreCampo);
        if (valor <= 0) {
            throw new ValidacionException(nombreCampo + " debe ser mayor que 0.");
        }
        return valor;
    }

    /** Lee un campo numérico y valida que no sea negativo (permite 0). */
    private double leerNumeroNoNegativo(JTextField campo, String nombreCampo) throws ValidacionException {
        double valor = leerNumero(campo, nombreCampo);
        if (valor < 0) {
            throw new ValidacionException(nombreCampo + " no puede ser negativo.");
        }
        return valor;
    }

    /** Lee y convierte el texto de un campo a double, validando formato y campo vacío. */
    private double leerNumero(JTextField campo, String nombreCampo) throws ValidacionException {
        String texto = campo.getText().trim();
        if (texto.isEmpty()) {
            throw new ValidacionException("El campo \"" + nombreCampo + "\" no puede estar vacío.");
        }
        try {
            return Double.parseDouble(texto.replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new ValidacionException("El campo \"" + nombreCampo + "\" debe ser un número válido.");
        }
    }

    /** El precio de tela es opcional: si está vacío, se usa 0. */
    private double leerPrecioTelaOpcional() throws ValidacionException {
        String texto = campoPrecioTela.getText().trim();
        if (texto.isEmpty()) {
            return 0;
        }
        try {
            double valor = Double.parseDouble(texto.replace(",", "."));
            if (valor < 0) {
                throw new ValidacionException("El precio de tela no puede ser negativo.");
            }
            return valor;
        } catch (NumberFormatException ex) {
            throw new ValidacionException("El precio de tela debe ser un número válido.");
        }
    }

    private void limpiarFormulario() {
        campoAnchoVentana.setText("");
        campoAlturaVentana.setText("");
        campoRuedo.setText("");
        campoArrastre.setText("");
        campoPrecioTela.setText("");
        comboTipoCortina.setSelectedIndex(0);
        comboTipoCabezal.setSelectedIndex(0);
        comboTipoSoporte.setSelectedIndex(0);
        actualizarComboTelas();
    }

    /** Excepción simple para reportar errores de validación del formulario. */
    private static class ValidacionException extends Exception {
        ValidacionException(String mensaje) {
            super(mensaje);
        }
    }
}
