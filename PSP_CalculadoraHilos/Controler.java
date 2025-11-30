import java.text.DecimalFormat;

public class Controler {

    private View vista;
    private Model modelo;

    // Número actual en pantalla
    private double numeroEnPantalla = 0;

    // Para limpiar la pantalla en la siguiente entrada
    private boolean limpiarPantalla = false;

    // Para manejar repetición de "="
    private String ultimaOperacionRepeat = "";
    private double ultimoOperandoRepeat = 0;

    // Flag de error
    private boolean estadoError = false;

    // Para almacenar la operación pendiente actual
    private String operacionPendiente = "";

    public Controler(View vista, Model modelo) {
        this.vista = vista;
        this.modelo = modelo;
        asignarEventos();
    }

    private void asignarEventos() {
        vista.setBotonListener(this::gestionarBoton);
    }

    private void gestionarBoton(String texto) {
        // Si estamos en estado de error, solo números, C o +/- reinician
        if (estadoError) {
            if ("C".equals(texto)) {
                resetTotal();
                vista.setPantalla("0");
                return;
            } else if (texto.matches("[0-9.]") || "+/-".equals(texto)) {
                resetTotal();
            } else {
                return; // Ignorar otras pulsaciones
            }
        }

        // Números y punto
        if (texto.matches("[0-9.]")) {
            escribirNumero(texto);
            return;
        }

        switch (texto) {
            case "C":
                resetTotal();
                vista.setPantalla("0");
                break;
            case "+": case "-": case "×": case "÷":
                manejarOperacion(texto);
                break;
            case "=":
                manejarIgual();
                break;
            case "+/-":
                toggleSign();
                break;
            case "%":
                manejarPorcentaje();
                break;
        }
    }

    private void resetTotal() {
        numeroEnPantalla = 0;
        limpiarPantalla = false;
        ultimaOperacionRepeat = "";
        ultimoOperandoRepeat = 0;
        estadoError = false;
        operacionPendiente = "";
        modelo.guardarOperacion(0, "");
    }

    private void escribirNumero(String digito) {
        if (limpiarPantalla) {
            vista.setPantalla(digito.equals(".") ? "0." : digito);
            limpiarPantalla = false;
            return;
        }

        String textoActual = vista.getPantalla();
        if (textoActual.equals("0") && !digito.equals(".")) {
            vista.setPantalla(digito);
        } else if (digito.equals(".") && textoActual.contains(".")) {
            return; // no permitir dos puntos
        } else {
            vista.setPantalla(textoActual + digito);
        }
    }

    private void manejarOperacion(String nuevaOperacion) {
        double actual;
        try {
            actual = Double.parseDouble(vista.getPantalla());
        } catch (NumberFormatException e) {
            return;
        }

        if (!operacionPendiente.isEmpty() && !limpiarPantalla) {
            // Encadenar operaciones: calcular primero
            double anterior = numeroEnPantalla;
            modelo.calcularEnHilo(anterior, operacionPendiente, actual, new Model.ResultadoCallback() {
                @Override
                public void onResultado(double resultado) {
                    numeroEnPantalla = resultado;
                    vista.setPantalla(formatear(resultado));
                    operacionPendiente = nuevaOperacion;
                    limpiarPantalla = true;
                }

                @Override
                public void onError(String mensajeError) {
                    estadoError = true;
                    vista.setPantalla(mensajeError);
                    operacionPendiente = "";
                }
            });
        } else {
            numeroEnPantalla = actual;
            operacionPendiente = nuevaOperacion;
            limpiarPantalla = true;
        }

        ultimaOperacionRepeat = "";
        ultimoOperandoRepeat = 0;
    }

    private void manejarIgual() {
        double actual;
        try {
            actual = Double.parseDouble(vista.getPantalla());
        } catch (NumberFormatException e) {
            return;
        }

        // Si hay operación pendiente, calculamos
        if (!operacionPendiente.isEmpty()) {
            double anterior = numeroEnPantalla;
            double operando = actual;
            String op = operacionPendiente;

            modelo.calcularEnHilo(anterior, op, operando, new Model.ResultadoCallback() {
                @Override
                public void onResultado(double resultado) {
                    numeroEnPantalla = resultado;
                    vista.setPantalla(formatear(resultado));
                    ultimaOperacionRepeat = op;
                    ultimoOperandoRepeat = operando;
                    limpiarPantalla = true;
                    operacionPendiente = ""; // operación ejecutada
                }

                @Override
                public void onError(String mensajeError) {
                    estadoError = true;
                    vista.setPantalla(mensajeError);
                    operacionPendiente = "";
                }
            });
        } else if (!ultimaOperacionRepeat.isEmpty()) {
            // Repetición de "="
            double anterior = numeroEnPantalla;
            double operando = ultimoOperandoRepeat;
            String op = ultimaOperacionRepeat;

            modelo.calcularEnHilo(anterior, op, operando, new Model.ResultadoCallback() {
                @Override
                public void onResultado(double resultado) {
                    numeroEnPantalla = resultado;
                    vista.setPantalla(formatear(resultado));
                    limpiarPantalla = true;
                }

                @Override
                public void onError(String mensajeError) {
                    estadoError = true;
                    vista.setPantalla(mensajeError);
                }
            });
        }
    }

    private void toggleSign() {
        String texto = vista.getPantalla();
        if (texto.equals("0") || texto.startsWith("Error")) return;

        if (texto.startsWith("-")) {
            vista.setPantalla(texto.substring(1));
        } else {
            vista.setPantalla("-" + texto);
        }
    }

    private void manejarPorcentaje() {
        double actual;
        try {
            actual = Double.parseDouble(vista.getPantalla());
        } catch (NumberFormatException e) {
            return;
        }

        if (!operacionPendiente.isEmpty()) {
            // iPhone style: porcentaje respecto al numeroEnPantalla
            actual = numeroEnPantalla * (actual / 100);
        } else {
            // simple porcentaje
            actual = actual / 100;
        }

        vista.setPantalla(formatear(actual));
    }

    private String formatear(double numero) {
        DecimalFormat df = new DecimalFormat("#.##########");
        return df.format(numero);
    }
}
