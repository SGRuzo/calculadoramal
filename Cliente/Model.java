package Cliente;

import java.io.*;
import java.net.Socket;

/**
 * Clase Model
 * Se encarga de:
 * 1. Mantener la conexión con el servidor.
 * 2. Enviar operaciones al servidor.
 * 3. Recibir resultados.
 */
public class Model {

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;

    /**
     * Constructor del modelo.
     * Se conecta al servidor en la IP y puerto especificados.
     */
    public Model() {
        try {
            // Conectar al servidor (localhost, puerto 5000 por ejemplo)
            socket = new Socket("localhost", 5000);

            // Flujos de entrada y salida de texto plano
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    /**
     * Enviar una operación al servidor y recibir el resultado.
     * @param operacion Texto que representa la operación (ej: "5+3")
     * @return Resultado devuelto por el servidor como String
     */
    public String enviarOperacionAlServidor(String operacion) {
        if (socket == null || socket.isClosed()) {
            return "Error: sin conexión al servidor";
        }

        try {
            // Enviar la operación al servidor
            salida.println(operacion);
            salida.flush();

            // Leer la respuesta del servidor
            String respuesta = entrada.readLine();

            // Comprobar respuesta nula (servidor pudo cerrar conexión inesperadamente)
            if (respuesta == null) {
                return "Error: servidor desconectado";
            }

            return respuesta;
        } catch (IOException e) {
            return "Error de comunicación: " + e.getMessage();
        }
    }

    /**
     * Cerrar la conexión con el servidor al salir de la aplicación.
     */
    public void cerrarConexion() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
