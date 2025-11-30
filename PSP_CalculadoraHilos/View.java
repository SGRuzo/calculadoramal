import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class View extends JFrame {

    private JTextField pantalla; // Campo donde se muestran los números y resultados

    // Diámetro fijo para los botones redondos
    private static final int DIAMETRO_BOTON = 70;

    // Listener para que el Controller capture el texto del botón pulsado
    private Consumer<String> listenerBotones;

    public View() {
        setTitle("Calculadora");
        // Ajustar el tamaño para el nuevo diámetro de los botones
        setSize(320, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con fondo negro
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setBackground(Color.BLACK);
        // Espacio entre componentes
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        setContentPane(panelPrincipal);

        // Configuración de la pantalla
        pantalla = new JTextField("0");
        pantalla.setFont(new Font("Helvetica Neue", Font.PLAIN, 48));
        pantalla.setForeground(Color.WHITE);
        pantalla.setBackground(Color.BLACK);
        pantalla.setHorizontalAlignment(SwingConstants.RIGHT);
        pantalla.setEditable(false);
        pantalla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(pantalla, BorderLayout.NORTH);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridBagLayout());
        panelBotones.setBackground(Color.BLACK);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        // Matriz con los textos de los botones
        // La última fila está diseñada para que '0' ocupe 2 columnas, '.' 1 columna y '=' 1 columna.
        String[][] textosBotones = {
                {"C", "+/-", "%", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "-"},
                {"1", "2", "3", "+"},
                {"0", ".", "="}
        };

        // Colores para diferentes tipos de botones
        Color colorNumeros = new Color(0x333333);
        Color colorFunciones = new Color(0xA6A6A6);
        Color colorOperaciones = new Color(0xFF9F0A);

        GridBagConstraints restricciones = new GridBagConstraints();
        restricciones.fill = GridBagConstraints.BOTH;
        restricciones.insets = new Insets(5, 5, 5, 5);
        restricciones.weightx = 1;
        restricciones.weighty = 1;

        // La posición inicial de la columna para la fila de botones
        int currentColumn;

        // Crear y agregar botones al panel
        for (int fila = 0; fila < textosBotones.length; fila++) {
            currentColumn = 0; // Reiniciar la columna para cada fila
            for (int j = 0; j < textosBotones[fila].length; j++) {
                String textoBoton = textosBotones[fila][j];
                JButton boton;

                restricciones.gridwidth = 1; // Por defecto ocupa 1 columna

                // Manejo especial para el botón "0" que es más ancho (pastilla)
                if (textoBoton.equals("0")) {
                    boton = new BotonPastilla("0", DIAMETRO_BOTON);
                    restricciones.gridwidth = 2; // Ocupa 2 columnas
                } else {
                    // Botones redondos normales
                    boton = new BotonRedondo(textoBoton, DIAMETRO_BOTON);
                    // Los botones '.' y '=' mantendrán gridwidth=1
                }

                boton.setFont(new Font("Helvetica Neue", Font.BOLD, 24));
                boton.setFocusPainted(false);
                boton.setForeground(Color.WHITE);
                boton.setBorder(BorderFactory.createEmptyBorder());
                boton.setOpaque(false); // Importante para que se vea la forma personalizada

                // Asignar color según tipo de botón
                if ("÷×-+=".contains(textoBoton)) {
                    boton.setBackground(colorOperaciones);
                } else if ("C+/- %".contains(textoBoton)) {
                    boton.setBackground(colorFunciones);
                    boton.setForeground(Color.BLACK);
                } else {
                    boton.setBackground(colorNumeros);
                }

                restricciones.gridx = currentColumn;
                restricciones.gridy = fila;
                panelBotones.add(boton, restricciones);

                // Actualizar la posición de la columna para el siguiente botón.
                currentColumn += restricciones.gridwidth;

                // Acción del botón: notificar al listener externo (Controller)
                boton.addActionListener(e -> {
                    if (listenerBotones != null) {
                        listenerBotones.accept(textoBoton);
                    }
                });
            }
        }
    }

    public void setBotonListener(Consumer<String> listener) {
        this.listenerBotones = listener;
    }

    public String getPantalla() {
        return pantalla.getText();
    }

    public void setPantalla(String valor) {
        pantalla.setText(valor);
    }

    // --- CLASE PARA BOTONES REDONDOS UNIFORMES ---
    public class BotonRedondo extends JButton {
        private final int diametro;

        public BotonRedondo(String texto, int diametro) {
            super(texto);
            this.diametro = diametro;
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar círculo con el color de fondo usando el tamaño del componente
            g2.setColor(getBackground());
            // Usamos Math.min(getWidth(), getHeight()) para asegurar que el círculo se dibuja centrado
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g2.fillOval(x, y, size, size);

            // Dibujar texto centrado
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            // Devolver un tamaño fijo para asegurar la uniformidad
            return new Dimension(diametro, diametro);
        }
    }

    // --- CLASE PARA EL BOTÓN "0" (PASTILLA O RECTÁNGULO REDONDEADO) ---
    public class BotonPastilla extends JButton {
        private final int diametroBase; // Usamos el diámetro como la altura y radio

        public BotonPastilla(String texto, int diametroBase) {
            super(texto);
            this.diametroBase = diametroBase;
            setOpaque(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Dibujar el rectángulo redondeado (pastilla)
            g2.setColor(getBackground());
            // Usamos getHeight() para el arco de la esquina, haciendo la forma de pastilla
            int arc = getHeight();
            // Usamos RoundRectangle2D.Double para la forma de pastilla
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arc, arc));

            // Dibujar texto centrado
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();

            // Centrar el texto '0' horizontalmente
            int x = (getWidth() - stringWidth) / 2;
            // Se mantiene la alineación vertical
            int y = (getHeight() + stringHeight) / 2 - 2;

            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            // La altura es el diámetro, y el ancho es aproximadamente el doble
            int anchoPastilla = (int) (diametroBase * 2.1);
            return new Dimension(anchoPastilla, diametroBase);
        }
    }

    // Método main para lanzar la UI (puedes moverlo a otra clase si quieres)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View view = new View();
            Model model = new Model();
            new Controler(view, model); // Conecta la vista con el modelo
            view.setVisible(true);
        });
    }
}
