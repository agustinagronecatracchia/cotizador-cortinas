package service;

import model.Cortina;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Contiene toda la lógica de negocio para calcular un presupuesto de
 * cortinas a partir de un objeto Cortina (Traslucida, Opaca o Blackout)
 * ya cargado con los datos de la ventana.
 *
 * Esta clase no conoce nada de la interfaz gráfica: sólo recibe datos,
 * calcula y devuelve resultados / un texto de presupuesto.
 */
public class CalculadoraPresupuesto {

    // ---------- Precios fijos del negocio (dados en la consigna) ----------
    private static final double PRECIO_CM_CINTA = 3;
    private static final double PRECIO_ALFILER = 150;
    private static final double PRECIO_METRO_RIEL = 27000;
    private static final double PRECIO_SOPORTE_7CM = 3000;
    private static final double PRECIO_SOPORTE_12CM = 7000;
    private static final double COSTO_MEDICION = 25000;
    private static final double FACTOR_COLOCACION = 15000; // $ por metro de riel
    private static final double BASE_CONFECCION = 25000;
    private static final double MULTIPLICADOR_CONFECCION = 1.5;

    // ---------- Datos de entrada usados en el último cálculo ----------
    private Cortina cortina;
    private String tipoSoporte;      // "7" o "12"
    private double precioMetroTela;  // ingresado por el usuario (ver nota abajo)

    // ---------- Resultados del cálculo ----------
    private double cantidadTelaMetros;
    private double costoTela;
    private double cantidadCinta;
    private double precioCinta;
    private double cantidadAlfileres;
    private double precioAlfileres;
    private double metrosRiel;
    private double precioRiel;
    private int cantidadSoportes;
    private double precioUnitarioSoporte;
    private double precioSoportes;
    private double costoColocacion;
    private double costoMedicion;
    private double costoConfeccion;
    private double costoTotal;

    /**
     * Calcula todo el presupuesto para la cortina recibida.
     *
     * @param cortina         objeto con los datos de la ventana y la tela
     *                        (ya construido como Traslucida, Opaca o Blackout)
     * @param tipoSoporte     "7" o "12" (cm), tipo de soporte elegido
     * @param precioMetroTela precio por metro de tela ingresado por el
     *                        usuario. La consigna original no incluyó un
     *                        precio de tela (sólo cantidades), y para no
     *                        inventar un valor económico se solicita este
     *                        dato en la interfaz; si se deja en blanco se
     *                        usa 0 y el costo de tela queda en $0.
     */
    public void calcular(Cortina cortina, String tipoSoporte, double precioMetroTela) {
        this.cortina = cortina;
        this.tipoSoporte = tipoSoporte;
        this.precioMetroTela = precioMetroTela;

        // 1) Medidas base de la cortina (comunes a todo tipo de cortina)
        cortina.calcularAnchoCortina();
        cortina.calcularAlturaCortina();
        cortina.calcularCinta();
        cortina.calcularAlfileres();

        double anchoCortina = cortina.getAnchoCortina();

        // 2) Tela: el cálculo específico depende de la subclase (Traslucida,
        // Opaca o Blackout), porque cada una tiene sus propios anchos de tela.
        this.cantidadTelaMetros = cortina.calcularCantidadTela();
        this.costoTela = this.cantidadTelaMetros * precioMetroTela;

        // 3) Cinta plástica: cantidad ya calculada en la cortina (= ancho de
        // cortina, en cm). Precio = cantidad * precio por cm.
        this.cantidadCinta = cortina.getCinta();
        this.precioCinta = this.cantidadCinta * PRECIO_CM_CINTA;

        // 4) Alfileres: cantidad ya calculada en la cortina.
        this.cantidadAlfileres = cortina.getAlfileres();
        this.precioAlfileres = this.cantidadAlfileres * PRECIO_ALFILER;

        // 5) Riel: el precio se da por metro, así que se convierte el ancho
        // de cortina de cm a metros antes de multiplicar.
        this.metrosRiel = anchoCortina / 100.0;
        this.precioRiel = this.metrosRiel * PRECIO_METRO_RIEL;

        // 6) Soportes.
        // La consigna no especifica una fórmula para la cantidad de
        // soportes, así que se adopta la siguiente regla (documentada y
        // fácil de ajustar): se coloca un soporte en cada extremo de la
        // cortina (mínimo 2) y uno adicional cada 100 cm de ancho, para
        // sostener el peso del riel y la tela en cortinas muy anchas.
        this.cantidadSoportes = (int) Math.ceil(anchoCortina / 100.0) + 1;
        if (this.cantidadSoportes < 2) {
            this.cantidadSoportes = 2;
        }
        this.precioUnitarioSoporte = "12".equals(tipoSoporte)
                ? PRECIO_SOPORTE_12CM
                : PRECIO_SOPORTE_7CM;
        this.precioSoportes = this.cantidadSoportes * this.precioUnitarioSoporte;

        // 7) Colocación: se interpreta "riel" (de la fórmula
        // 15000 * riel) como la cantidad de metros de riel calculados,
        // ya que la mano de obra de instalación se cobra en función de la
        // longitud instalada, no de una cantidad fija de rieles.
        this.costoColocacion = FACTOR_COLOCACION * this.metrosRiel;

        // 8) Medición: costo fijo por visitar al cliente y tomar medidas.
        this.costoMedicion = COSTO_MEDICION;

        // 9) Confección.
        // La fórmula original (25000 * 1.5 * ancho) usaría el ancho en
        // centímetros, lo que daría un costo de confección absurdamente
        // alto (cientos de miles de pesos extra). Se interpreta que
        // "ancho" debe expresarse en metros, ya que la confección se cobra
        // por metro lineal de cortina cosida, no por centímetro.
        double anchoCortinaMetros = anchoCortina / 100.0;
        this.costoConfeccion = BASE_CONFECCION * MULTIPLICADOR_CONFECCION * anchoCortinaMetros;

        // 10) Total: suma de todos los conceptos.
        this.costoTotal = this.costoTela + this.precioCinta + this.precioAlfileres
                + this.precioRiel + this.precioSoportes + this.costoColocacion
                + this.costoMedicion + this.costoConfeccion;
    }

    /**
     * Genera el texto del presupuesto, ya formateado, listo para mostrar
     * en pantalla o copiar para el cliente.
     */
    public String generarReporte() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "AR"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        DecimalFormat moneda = new DecimalFormat("#,##0", simbolos);
        DecimalFormat numero = new DecimalFormat("#,##0.##", simbolos);

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           PRESUPUESTO DE CORTINAS\n");
        sb.append("========================================\n\n");

        sb.append("Tipo de cortina: ").append(cortina.getTipoCortina()).append("\n");
        sb.append("Tela: ").append(cortina.getNombreTela()).append("\n");
        sb.append("Tipo de cabezal: ").append(cortina.getTipoCabezal()).append("\n\n");

        sb.append("MEDIDAS\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Ancho de ventana:      %s cm%n", numero.format(cortina.getAnchoVentana())));
        sb.append(String.format("Altura de ventana:     %s cm%n", numero.format(cortina.getAlturaVentana())));
        sb.append(String.format("Ruedo:                 %s cm%n", numero.format(cortina.getRuedo())));
        sb.append(String.format("Arrastre:              %s cm%n%n", numero.format(cortina.getArrastre())));

        sb.append("MEDIDAS CALCULADAS\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Ancho de cortina:      %s cm%n", numero.format(cortina.getAnchoCortina())));
        sb.append(String.format("Altura de cortina:     %s cm%n", numero.format(cortina.getAlturaCortina())));
        sb.append(String.format("Cantidad de paños:     %s%n", numero.format(cortina.getCantidadPanos())));
        sb.append(String.format("Cantidad de tela:      %s m%n%n", numero.format(cantidadTelaMetros)));

        sb.append("MATERIALES\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Cinta plástica:        %s cm%n", numero.format(cantidadCinta)));
        sb.append(String.format("Precio cinta:          $%s%n%n", moneda.format(precioCinta)));

        sb.append(String.format("Alfileres:             %s unidades%n", numero.format(cantidadAlfileres)));
        sb.append(String.format("Precio alfileres:      $%s%n%n", moneda.format(precioAlfileres)));

        sb.append(String.format("Riel:                  %s m%n", numero.format(metrosRiel)));
        sb.append(String.format("Precio riel:           $%s%n%n", moneda.format(precioRiel)));

        sb.append("Soportes:\n");
        sb.append(String.format("Tipo:                  %s cm%n", tipoSoporte));
        sb.append(String.format("Cantidad:              %d%n", cantidadSoportes));
        sb.append(String.format("Precio:                $%s%n%n", moneda.format(precioSoportes)));

        sb.append("SERVICIOS\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Medición:              $%s%n", moneda.format(costoMedicion)));
        sb.append(String.format("Confección:            $%s%n", moneda.format(costoConfeccion)));
        sb.append(String.format("Colocación:            $%s%n", moneda.format(costoColocacion)));
        if (precioMetroTela > 0) {
            sb.append(String.format("Costo de tela:         $%s%n", moneda.format(costoTela)));
        } else {
            sb.append("Costo de tela:         (no informado)\n");
        }
        sb.append("\n");

        sb.append("========================================\n");
        sb.append(String.format("TOTAL PRESUPUESTO:     $%s%n", moneda.format(costoTotal)));
        sb.append("========================================\n");

        return sb.toString();
    }

    // ---------- Getters de resultados (útiles para tests o reportes propios) ----------
    public double getCantidadTelaMetros() { return cantidadTelaMetros; }
    public double getCostoTela() { return costoTela; }
    public double getCantidadCinta() { return cantidadCinta; }
    public double getPrecioCinta() { return precioCinta; }
    public double getCantidadAlfileres() { return cantidadAlfileres; }
    public double getPrecioAlfileres() { return precioAlfileres; }
    public double getMetrosRiel() { return metrosRiel; }
    public double getPrecioRiel() { return precioRiel; }
    public int getCantidadSoportes() { return cantidadSoportes; }
    public double getPrecioUnitarioSoporte() { return precioUnitarioSoporte; }
    public double getPrecioSoportes() { return precioSoportes; }
    public double getCostoColocacion() { return costoColocacion; }
    public double getCostoMedicion() { return costoMedicion; }
    public double getCostoConfeccion() { return costoConfeccion; }
    public double getCostoTotal() { return costoTotal; }
}
