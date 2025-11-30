import javax.swing.*;

public class Model {

    // Guarda los números y la operación actual (opcional, se mantienen por compatibilidad)
    private double numeroAnterior = 0;
    private String operacionPendiente = "";

    // --- MÉTODO PARA GUARDAR EL NÚMERO Y LA OPERACIÓN ---
    public void guardarOperacion(double numero, String operacion) {
        numeroAnterior = numero;       // Guardamos el número
        operacionPendiente = operacion; // Y la operación (+, -, ×, ÷)
    }

    // --- CÁLCULO EN SEGUNDO PLANO (CON HILO) usando el estado interno (firma antigua) ---
    public void calcularEnHilo(double numeroActual, ResultadoCallback callback) {
        calcularEnHilo(numeroAnterior, operacionPendiente, numeroActual, callback);
    }

    // --- NUEVO: cálculo en hilo recibiendo todos los parámetros explícitos ---
    public void calcularEnHilo(double numeroAnteriorParam, String operacion, double numeroActual, ResultadoCallback callback) {

        // Creamos un hilo para el cálculo
        Thread hiloCalculo = new Thread(() -> {

            double resultado = 0;

            try {
                // Simulación de operación pesada
                Thread.sleep(200);  // solo para ver el uso de hilos (puedes quitarlo si quieres)

                switch (operacion) {
                    case "+":
                        resultado = numeroAnteriorParam + numeroActual;
                        break;
                    case "-":
                        resultado = numeroAnteriorParam - numeroActual;
                        break;
                    case "×":
                        resultado = numeroAnteriorParam * numeroActual;
                        break;
                    case "÷":
                        if (numeroActual == 0) {
                            SwingUtilities.invokeLater(() -> callback.onError("Error: División entre 0"));
                            return;
                        }
                        resultado = numeroAnteriorParam / numeroActual;
                        break;
                    default:
                        resultado = numeroActual; // Si no hay operación previa
                }

                double finalResultado = resultado;

                // Swing SOLO puede actualizarse desde el hilo principal
                SwingUtilities.invokeLater(() -> callback.onResultado(finalResultado));

            } catch (InterruptedException e) {
                SwingUtilities.invokeLater(() -> callback.onError("Error en hilo"));
            }
        });

        hiloCalculo.setDaemon(true);
        hiloCalculo.start(); // Se inicia el hilo
    }

    // --- CALLBACK PARA DEVOLVER EL RESULTADO ---
    public interface ResultadoCallback {
        void onResultado(double resultado);
        void onError(String mensajeError);
    }
}
