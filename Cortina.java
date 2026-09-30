package model;

/**
 * Clase padre (abstracta) que representa una cortina genérica.
 * Contiene los datos de la ventana y del cabezal, que son comunes a
 * todos los tipos de cortina (Traslucida, Opaca, Blackout), y los
 * cálculos que no dependen del tipo de tela.
 *
 * Las subclases sólo deben resolver aquello que sí depende del tipo de
 * tela: cuántos paños entran y cuánta tela se necesita en total.
 */
public abstract class Cortina {

    // ---------- Atributos de la ventana / pedido ----------
    private String tipoCabezal;   // Ej: "Ollao", "Bolsillo", "Broche"
    private double anchoVentana;  // Ancho de la ventana, en cm
    private double alturaVentana; // Altura de la ventana, en cm (referencia del cliente)
    private double ruedo;         // Ruedo (dobladillo inferior de la tela), en cm
    private double arrastre;      // Arrastre (holgura para que la cortina roce el piso), en cm

    // ---------- Atributos calculados (comunes a toda cortina) ----------
    private double cinta;         // Cantidad de cinta plástica necesaria, en cm
    private double alfileres;     // Cantidad de alfileres necesarios, en unidades
    private double anchoCortina;  // Ancho final de la cortina terminada, en cm
    private double alturaCortina; // Altura final de la cortina terminada, en cm

    public Cortina(String tipoCabezal, double anchoVentana, double alturaVentana,
                    double ruedo, double arrastre) {
        this.tipoCabezal = tipoCabezal;
        this.anchoVentana = anchoVentana;
        this.alturaVentana = alturaVentana;
        this.ruedo = ruedo;
        this.arrastre = arrastre;
    }

    // ---------- Cálculos comunes a toda cortina ----------

    /**
     * Ancho de la cortina terminada.
     * Se multiplica el ancho de la ventana por 2.5 porque la tela debe
     * quedar fruncida (plegada) para lograr volumen y una buena caída;
     * ese es el factor de fruncido habitual en cortinería.
     */
    public void calcularAnchoCortina() {
        this.anchoCortina = this.anchoVentana * 2.5;
    }

    /**
     * Altura de la cortina terminada.
     * Se parte de 320 cm (altura estándar de instalación, del riel al piso)
     * más 8 cm de dobladillo superior (donde se forma el bolsillo/cabezal
     * que sostiene el riel), más el ruedo (dobladillo inferior) y el
     * arrastre (holgura para que la tela roce levemente el piso).
     */
    public void calcularAlturaCortina() {
        final double ALTURA_BASE = 320;
        final double DOBLADILLO_SUPERIOR = 8;
        this.alturaCortina = ALTURA_BASE + DOBLADILLO_SUPERIOR + this.ruedo + this.arrastre;
    }

    /**
     * Cantidad de cinta plástica: recorre todo el ancho superior de la
     * cortina, por lo tanto es igual al ancho de la cortina (en cm).
     */
    public void calcularCinta() {
        this.cinta = this.anchoCortina;
    }

    /**
     * Cantidad de alfileres: se coloca uno cada 10 cm de ancho de cortina.
     * Se redondea hacia arriba porque no existen fracciones de alfiler.
     */
    public void calcularAlfileres() {
        this.alfileres = Math.ceil(this.anchoCortina / 10.0);
    }

    /**
     * Cada subclase define cómo se calcula la cantidad de tela (en metros),
     * ya que depende del ancho/grosor disponible del tipo de tela elegido.
     */
    public abstract double calcularCantidadTela();

    /** Nombre de la tela seleccionada (para mostrar en el presupuesto). */
    public abstract String getNombreTela();

    /** Ancho/grosor disponible (en cm) de la tela seleccionada. */
    public abstract double getAnchoTela();

    /** Cantidad de paños de tela necesarios (calculado por la subclase). */
    public abstract double getCantidadPanos();

    /** Nombre del tipo de cortina, para mostrar en el presupuesto. */
    public abstract String getTipoCortina();

    // ---------- Getters y setters ----------
    public String getTipoCabezal() { return tipoCabezal; }
    public void setTipoCabezal(String tipoCabezal) { this.tipoCabezal = tipoCabezal; }

    public double getAnchoVentana() { return anchoVentana; }
    public void setAnchoVentana(double anchoVentana) { this.anchoVentana = anchoVentana; }

    public double getAlturaVentana() { return alturaVentana; }
    public void setAlturaVentana(double alturaVentana) { this.alturaVentana = alturaVentana; }

    public double getRuedo() { return ruedo; }
    public void setRuedo(double ruedo) { this.ruedo = ruedo; }

    public double getArrastre() { return arrastre; }
    public void setArrastre(double arrastre) { this.arrastre = arrastre; }

    public double getCinta() { return cinta; }
    public double getAlfileres() { return alfileres; }
    public double getAnchoCortina() { return anchoCortina; }
    public double getAlturaCortina() { return alturaCortina; }
}
