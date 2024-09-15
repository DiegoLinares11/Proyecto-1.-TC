import java.util.Stack;

public class PostfixToAST {
    public static ASTNode constructAST(String postfix) {
        Stack<ASTNode> stack = new Stack<>();
    
        for (char c : postfix.toCharArray()) {
            System.out.println("Procesando carácter en postfix: " + c); // Debug
    
            if (Character.isLetterOrDigit(c) || c == 'e') {
                stack.push(new OperandNode(c));
                System.out.println("Apilando operand node con valor: " + c); // Debug
            } else if (c == '*' || c == '+' || c == '?') {
                if (stack.size() < 1) {
                    throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
                }
                ASTNode operand = stack.pop();
                stack.push(new UnaryOperatorNode(c, operand));
                System.out.println("Creando UnaryOperatorNode: " + c); // Debug
            } else {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
                }
                ASTNode right = stack.pop();
                ASTNode left = stack.pop();
                stack.push(new OperatorNode(c, left, right));
                System.out.println("Creando OperatorNode con operador: " + c); // Debug
            }
        }
    
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
        }
    
        ASTNode root = stack.pop();
        System.out.println("AST root creado: " + root); // Debug: Mostrar nodo raíz del AST
        return root;
    }
}    
