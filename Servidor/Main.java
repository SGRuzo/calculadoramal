package Servidor;

/**
 * Clase Main para iniciar el servidor.
 */
public class Main {
    public static void main(String[] args) {
        int puerto = 5000; // Puerto donde escuchará el servidor
        Calculadora servidor = new Calculadora(puerto);
        servidor.iniciar(); // Arrancar servidor
    }
}
