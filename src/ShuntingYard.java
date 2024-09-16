import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class ShuntingYard {
    // Precedencia de los operadores
    private static int precedence(char op) {
        switch (op) {
            case '|':
            case '.':
                return 1;
            case '*':
            case '+':
                return 3;
            default:
                return -1;
        }
    }

    // Asociación de los operadores (izquierda o derecha)
    private static boolean isRightAssociative(char op) {
        return op == '*' || op == '+' || op == '?';
    }

    // Verificar si el carácter es un operador
    private static boolean isOperator(char c) {
        return c == '+' || c == '*' || c == '|' || c == '.';
    }

    // Verificar si el carácter es un símbolo especial (no es un operador pero no es
    // un operando)
    private static boolean isSpecialSymbol(char c) {
        return c == '(' || c == ')';
    }

    // Verificar si el carácter es un operando (no es un operador ni un símbolo
    // especial)
    private static boolean isOperand(char c) {
        return !isOperator(c) && !isSpecialSymbol(c);
    }

    // Método para reemplazar `?` por `|e`
    private static String replaceQuestionMark(String regex) {
        return regex.replace("?", "|e");
    }

    // Reconocer que si estan juntos hay que ingresar un operador de concatenación
    // explícito '.'
    private static String insertExplicitConcatOperator(String regex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < regex.length(); i++) {
            char c1 = regex.charAt(i);
            result.append(c1);
            if (i + 1 < regex.length()) {
                char c2 = regex.charAt(i + 1);
                if ((isOperand(c1) || c1 == '*' || c1 == '+' || c1 == ')' || c1 == 'e') &&
                        (isOperand(c2) || c2 == '(' || c2 == 'e')) {
                    result.append('.');
                }
            }
        }
        return result.toString();
    }

    // De notacion infija a postfija
    public static String infixToPostfix(String infix) {
        infix = replaceQuestionMark(infix); // Primero reemplazamos `?`
        infix = insertExplicitConcatOperator(infix); // Luego insertamos los operadores de concatenación
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (int i = 0; i < infix.length(); i++) {
            char c = infix.charAt(i);

            // Si el caracter es un operando (no operador ni símbolo especial), añadirlo a
            // la salida
            if (isOperand(c) || c == 'e') {
                output.append(c);
            }
            // Si el carácter es un paréntesis de apertura, añadirlo a la pila
            else if (c == '(') {
                stack.push(c);
            }
            // Si el carácter es un paréntesis de cierre
            else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                stack.pop();
            }
            // Si el carácter es un operador
            else if (isOperator(c)) {
                while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
                    if (isRightAssociative(c) && precedence(c) == precedence(stack.peek())) {
                        break;
                    } else {
                        output.append(stack.pop());
                    }
                }
                stack.push(c);
            }
        }

        // Sacar todos los operadores restantes de la pila
        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }

        return output.toString();
    }
}
