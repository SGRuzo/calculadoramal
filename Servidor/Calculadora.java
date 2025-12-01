package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase ServidorCalculadora
 * Servidor principal que acepta conexiones y lanza un hilo por cada cliente.
 */
public class Calculadora {

    private ServerSocket serverSocket;
    private ArrayList<Hilo> clientes;

    public Calculadora(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            clientes = new ArrayList<>();
            System.out.println("Servidor iniciado en puerto " + puerto);
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }

    /**
     * Método para aceptar clientes indefinidamente
     */
    public void iniciar() {
        while (true) {
            try {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + clienteSocket.getInetAddress());

                // Crear un hilo para manejar este cliente
                Hilo hilo = new Hilo(clienteSocket);
                hilo.start();

                // Guardar hilo en la lista (opcional, para control futuro)
                clientes.add(hilo);
            } catch (IOException e) {
                System.err.println("Error al aceptar cliente: " + e.getMessage());
            }
        }
    }
}
