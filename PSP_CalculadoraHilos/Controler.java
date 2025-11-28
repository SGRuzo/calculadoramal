import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controler {

    private View view;
    private Model model;

    public Controler(View view, Model model) {
        this.view = view;
        this.model = model;
        inicializarBotones();
    }

    /**
     * Asocia los botones de la vista con las funciones del modelo
     */
    private void inicializarBotones() {
        agregarListenersRecursivo(view.getContentPane());
    }

    private void agregarListenersRecursivo(Container contenedor) {
        for (Component comp : contenedor.getComponents()) {
            if (comp instanceof JButton boton) {
                boton.addActionListener(e -> manejarBoton(boton.getText()));
            } else if (comp instanceof Container cont) {
                agregarListenersRecursivo(cont); // Llama recursivamente a los hijos
            }
        }
    }

    /**
     * Procesa cada botón pulsado
     */
    private void manejarBoton(String textoBoton) {
        String pantallaActual = view.getPantalla();

        switch (textoBoton) {
            case "C":
                view.setPantalla(model.reiniciar());
                break;
            case "+/-":
                view.setPantalla(model.cambiarSigno(pantallaActual));
                break;
            case "%":
                view.setPantalla(model.calcularPorcentaje(pantallaActual));
                break;

            case "+":
            case "-":
            case "×":
            case "÷":
                // 1. Si hay operación pendiente, ejecuta el cálculo anterior primero
                if (model.getOperacionPendiente() != null) {
                    // La lógica completa aquí llamaría a calcularResultadoRed()
                    // Si se está en modo simple (operaciones encadenadas), esto es necesario.
                }
                // 2. Guarda el operando actual y la nueva operación
                try {
                    model.setOperando1(Double.parseDouble(pantallaActual));
                    model.setOperacionPendiente(textoBoton);
                    model.setInicioDeNumero(true); // Prepara para la entrada del segundo operando
                } catch (NumberFormatException e) {
                    view.setPantalla("Error");
                }
                break;

            case "=":
                if (model.getOperacionPendiente() != null) {
                    calcularResultadoRed(pantallaActual);
                }
                break;

            default: // Números y '.'
                view.setPantalla(model.agregarNumero(pantallaActual, textoBoton));
                break;
        }
    }

    /**
     * Envía la solicitud al servidor y procesa la respuesta.
     * @param operando2Str El segundo operando introducido por el usuario.
     */
    private void calcularResultadoRed(String operando2Str) {
        try {
            double operando2 = Double.parseDouble(operando2Str);
            String operacion = model.getOperacionPendiente();

            // Mapear símbolo a la clave del protocolo
            String opProtocolo = switch (operacion) {
                case "+" -> "SUMA";
                case "-" -> "RESTA";
                case "×" -> "MULTIPLICA";
                case "÷" -> "DIVIDE";
                default -> "ERROR";
            };

            // Formato de protocolo: CALC:OP:OP_KEY:OP1:OP2
            String solicitud = String.format("CALC:OP:%s:%s:%s", opProtocolo, model.getOperando1(), operando2);

            // --- CÓDIGO DE RED (ASUMIMOS MÉTODO en ClientConnection) ---
            // String respuesta = clientConnection.enviarSolicitud(solicitud);
            // procesarRespuestaServidor(respuesta);
            // --- FIN CÓDIGO DE RED ---

            // Placeholder temporal SIN RED (para que compile)
            view.setPantalla("Esperando Servidor...");

            // Resetear estado después del cálculo
            model.setOperacionPendiente(null);
            model.setInicioDeNumero(true);

        } catch (NumberFormatException e) {
            view.setPantalla("Error de Formato");
            model.reiniciar();
        } catch (Exception e) {
            view.setPantalla("Error de Conexión");
            // clientConnection.cerrar();
        }
    }

    // El método procesarRespuestaServidor(String respuesta) iría aquí para actualizar la View
    // y el estado del Model (model.setOperando1(nuevoResultado))
}