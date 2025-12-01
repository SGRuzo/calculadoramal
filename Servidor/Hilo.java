package Servidor;

import java.io.*;
import java.net.Socket;

/**
 * Clase Hilo
 * Cada instancia maneja un cliente individual en un hilo separado.
 */
public class Hilo extends Thread {

    private Socket cliente;
    private BufferedReader entrada;
    private PrintWriter salida;

    public Hilo(Socket cliente) {
        this.cliente = cliente;
        try {
            entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            salida = new PrintWriter(cliente.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error al crear flujos para el cliente: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String mensaje;

        try {
            while ((mensaje = entrada.readLine()) != null) {

                System.out.println("Recibido del cliente: " + mensaje);

                // Calcular resultado
                String resultado = Operaciones.calcular(mensaje);

                // Si el resultado es null, enviamos "0" para la calculadora
                if (resultado == null) resultado = "0";

                salida.println(resultado);
            }
        } catch (IOException e) {
            System.err.println("Cliente desconectado o error de comunicación: " + e.getMessage());
        } finally {
            cerrarCliente();
        }
    }

    /**
     * Cerrar recursos del cliente
     */
    private void cerrarCliente() {
        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (cliente != null) cliente.close();
            System.out.println("Cliente desconectado correctamente.");
        } catch (IOException e) {
            System.err.println("Error al cerrar cliente: " + e.getMessage());
        }
    }
}
