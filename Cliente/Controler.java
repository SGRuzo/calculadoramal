package Cliente;

import Cliente.View;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Clase Controler
 * Se encarga de:
 * 1. Recibir eventos de la vista (botones pulsados).
 * 2. Decidir qué hacer según el botón.
 * 3. Actualizar la vista con resultados o mensajes de error.
 */
public class Controler {

    private View view;
    private Model model;
    private String operacionActual = ""; // Guardar la operación que se está escribiendo

    public Controler(View view, Model model) {
        this.view = view;
        this.model = model;

        // Conectar listener de botones
        view.setBotonListener(this::procesarBoton);

        // Manejar cierre de ventana para cerrar conexión
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                model.cerrarConexion();
                System.exit(0);
            }
        });
    }

    /**
     * Procesa cada botón pulsado.
     * @param texto Texto del botón pulsado
     */
    private void procesarBoton(String texto) {

        switch (texto) {

            case "C":
                operacionActual = "";
                view.setPantalla("0");
                break;

            case "=":
                if (!operacionActual.isEmpty()) {
                    String resultado = model.enviarOperacionAlServidor(operacionActual);
                    view.setPantalla(resultado);
                    operacionActual = resultado;
                }
                break;

            case "+/-":
                if (operacionActual.startsWith("-"))
                    operacionActual = operacionActual.substring(1);
                else
                    operacionActual = "-" + operacionActual;

                view.setPantalla(operacionActual);
                break;

            default:
                // Agregar directamente cualquier dígito, punto u operador
                operacionActual += texto;
                view.setPantalla(operacionActual);
                break;
        }
    }



}
