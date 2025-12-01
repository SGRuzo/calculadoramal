package Servidor;

public class Operaciones {

    public static String calcular(String operacion) {
        try {
            operacion = operacion.replace(" ", "");

            char operador = 0;
            int index = -1;

            for (int i = 0; i < operacion.length(); i++) {
                char c = operacion.charAt(i);

                // Si es un operador PERO no es el primer caracter, entonces sí es operador real
                if ((c == '+' || c == '-' || c == '×' || c == '÷' || c == '%') && i != 0) {
                    operador = c;
                    index = i;
                    break;
                }
            }

            if (operador == 0) return null;

            String parte1 = operacion.substring(0, index);
            String parte2 = operacion.substring(index + 1);

            double num1 = Double.parseDouble(parte1);
            double num2 = Double.parseDouble(parte2);

            double resultado;

            switch (operador) {
                case '+': resultado = num1 + num2; break;
                case '-': resultado = num1 - num2; break;
                case '×': resultado = num1 * num2; break;
                case '÷':
                    if (num2 == 0) return null;
                    resultado = num1 / num2;
                    break;
                case '%':
                    if (num2 == 0) return null;
                    resultado = num1 % num2;
                    break;
                default: return null;
            }

            if (resultado == (int) resultado) {
                return String.valueOf((int) resultado);
            } else {
                return String.valueOf(resultado);
            }

        } catch (Exception e) {
            return null;
        }
    }

}
