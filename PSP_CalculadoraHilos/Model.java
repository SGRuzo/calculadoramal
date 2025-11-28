
/**
 * Gestiona la lógica de entrada de números y operaciones locales (C, +/-, %).
 * La lógica de las 4 operaciones básicas (+, -, x, /) se asume que se
 * delega al servidor a través del Controller.
 */
public class Model {

    // Estado de la calculadora (solo si se implementa lógica de estado)
    private double operando1 = 0;
    private String operacionPendiente = null;
    private boolean inicioDeNumero = true;

    // --- Métodos de Gestión de Estado (para uso del Controller) ---

    public void setInicioDeNumero(boolean inicioDeNumero) {
        this.inicioDeNumero = inicioDeNumero;
    }

    public boolean isInicioDeNumero() {
        return inicioDeNumero;
    }

    public void setOperando1(double operando1) {
        this.operando1 = operando1;
    }

    public double getOperando1() {
        return operando1;
    }

    public void setOperacionPendiente(String op) {
        this.operacionPendiente = op;
    }

    public String getOperacionPendiente() {
        return operacionPendiente;
    }

    // --- Lógica de Entrada de Usuario (Números) ---

    public String agregarNumero(String pantallaActual, String textoBoton) {
        if (isInicioDeNumero()) {
            // Si es el inicio de un nuevo número
            if (textoBoton.equals(".")) {
                setInicioDeNumero(false);
                return "0.";
            } else {
                setInicioDeNumero(false);
                return textoBoton;
            }
        } else {
            // Continuación del número actual
            if (textoBoton.equals(".")) {
                if (!pantallaActual.contains(".")) {
                    return pantallaActual + textoBoton;
                }
                return pantallaActual; // No añade si ya tiene punto
            } else {
                if (pantallaActual.equals("0")) {
                    return textoBoton; // Reemplaza el cero inicial
                }
                return pantallaActual + textoBoton; // Concatenar número
            }
        }
    }


    // --- Lógica de Operaciones Locales (No de Red) ---

    public String reiniciar() {
        setOperando1(0);
        setOperacionPendiente(null);
        setInicioDeNumero(true);
        return "0";
    }

    public String cambiarSigno(String valorStr) {
        try {
            double valor = Double.parseDouble(valorStr);
            double resultado = -valor;
            // Formatear el resultado (evitar .0 si es entero)
            if (resultado == (long) resultado) {
                return String.format("%d", (long) resultado);
            }
            return String.valueOf(resultado);
        } catch (NumberFormatException e) {
            return "Error"; // No debería pasar si la pantalla es controlada
        }
    }

    public String calcularPorcentaje(String valorStr) {
        try {
            double valor = Double.parseDouble(valorStr);
            double resultado = valor / 100.0;
            setInicioDeNumero(true); // El resultado se puede usar como Operando1
            return String.valueOf(resultado);
        } catch (NumberFormatException e) {
            return "Error";
        }
    }
}