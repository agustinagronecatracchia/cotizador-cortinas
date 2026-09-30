package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cortina de tipo Opaca. Hereda de Cortina y agrega el comportamiento
 * específico para calcular la cantidad de tela según el tipo de tela
 * opaca elegido.
 */
public class Opaca extends Cortina {

    /** Telas disponibles y su ancho/grosor útil, en cm. */
    public static final Map<String, Double> TELAS = new LinkedHashMap<>();
    static {
        TELAS.put("Tusor", 300.0);
        TELAS.put("Linel Gray", 260.0);
    }

    private final String tipoTela;
    private double cantidadPanos;

    public Opaca(String tipoCabezal, double anchoVentana, double alturaVentana,
                 double ruedo, double arrastre, String tipoTela) {
        super(tipoCabezal, anchoVentana, alturaVentana, ruedo, arrastre);
        this.tipoTela = tipoTela;
    }

    @Override
    public double calcularCantidadTela() {
        double grosorTela = getAnchoTela();

        // Cantidad de paños necesarios para cubrir el ancho de la cortina,
        // redondeado hacia arriba (no existen fracciones de paño).
        this.cantidadPanos = Math.ceil(getAnchoCortina() / grosorTela);

        // Cantidad total de tela en metros: paños * altura de cortina (cm),
        // convertido a metros dividiendo por 100.
        return (this.cantidadPanos * getAlturaCortina()) / 100.0;
    }

    @Override
    public String getNombreTela() { return tipoTela; }

    @Override
    public double getAnchoTela() { return TELAS.get(tipoTela); }

    @Override
    public double getCantidadPanos() { return cantidadPanos; }

    @Override
    public String getTipoCortina() { return "Opaca"; }
}
