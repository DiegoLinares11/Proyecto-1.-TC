import java.util.Stack;

public class PostfixToAST {
    public static ASTNode constructAST(String postfix) {
        Stack<ASTNode> stack = new Stack<>();

        for (char c : postfix.toCharArray()) {
            // Si el carácter no es un operador, lo tratamos como un operando
            if (isOperand(c)) {
                stack.push(new OperandNode(c));
            }
            // Si es un operador unario (*, +, ?)
            else if (c == '*' || c == '+' || c == '?') {
                if (stack.size() < 1) {
                    throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
                }
                ASTNode operand = stack.pop();
                stack.push(new UnaryOperatorNode(c, operand));
            }
            // Si es un operador binario (|, .)
            else if (isOperator(c)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
                }
                ASTNode right = stack.pop();
                ASTNode left = stack.pop();
                stack.push(new OperatorNode(c, left, right));
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
        }

        return stack.pop();
    }

    // Método para verificar si un carácter es un operando (no es operador ni
    // símbolo especial)
    private static boolean isOperand(char c) {
        // Aceptamos cualquier carácter que no sea un operador definido (|, ., *, +, ?)
        return !isOperator(c) && c != '*' && c != '+' && c != '?' && c != '(' && c != ')';
    }

    // Método para verificar si un carácter es un operador
    private static boolean isOperator(char c) {
        return c == '|' || c == '.';
    }
}
