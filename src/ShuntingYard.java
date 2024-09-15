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
                if ((Character.isLetterOrDigit(c1) || c1 == '*' || c1 == '+' || c1 == ')' || c1 == 'e') &&
                        (Character.isLetterOrDigit(c2) || c2 == '(' || c2 == 'e')) {
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
            System.out.println("Procesando carácter: " + c);  // Debug: Ver qué carácter se procesa
    
            if (Character.isLetterOrDigit(c) || c == 'e') {
                output.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                stack.pop();
            } else if (isOperator(c)) {
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
    
        System.out.println("Postfix resultante: " + output.toString()); // Debug: Mostrar postfix
        return output.toString();
    }
    
}