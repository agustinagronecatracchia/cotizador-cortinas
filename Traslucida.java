package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cortina de tipo Traslúcida. Hereda de Cortina y agrega el
 * comportamiento específico para calcular la cantidad de tela según el
 * tipo de tela traslúcida elegido.
 */
public class Traslucida extends Cortina {

    /** Telas disponibles y su ancho/grosor útil, en cm. */
    public static final Map<String, Double> TELAS = new LinkedHashMap<>();
    static {
        TELAS.put("Algodón", 280.0);
        TELAS.put("Bambú", 260.0);
        TELAS.put("Organza de lino", 270.0);
    }

    private final String tipoTela;
    private double cantidadPanos; // cantidad de paños necesarios (calculado)

    public Traslucida(String tipoCabezal, double anchoVentana, double alturaVentana,
                       double ruedo, double arrastre, String tipoTela) {
        super(tipoCabezal, anchoVentana, alturaVentana, ruedo, arrastre);
        this.tipoTela = tipoTela;
    }

    @Override
    public double calcularCantidadTela() {
        double grosorTela = getAnchoTela();

        // Alto = cantidad de paños necesarios para cubrir el ancho de la
        // cortina. Se redondea hacia arriba porque no se puede comprar/usar
        // una fracción de paño.
        this.cantidadPanos = Math.ceil(getAnchoCortina() / grosorTela);

        // Cantidad de tela = paños * altura de cada paño (altura de la
        // cortina, en cm). El resultado se expresa en metros (dividiendo
        // por 100) porque la tela se compra por metro lineal.
        return (this.cantidadPanos * getAlturaCortina()) / 100.0;
    }

    @Override
    public String getNombreTela() { return tipoTela; }

    @Override
    public double getAnchoTela() { return TELAS.get(tipoTela); }

    @Override
    public double getCantidadPanos() { return cantidadPanos; }

    @Override
    public String getTipoCortina() { return "Traslúcida"; }
}
