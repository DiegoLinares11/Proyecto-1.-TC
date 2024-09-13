import java.util.Stack;

public class PostfixToAST {
    public static ASTNode constructAST(String postfix) {
        Stack<ASTNode> stack = new Stack<>();

        for (char c : postfix.toCharArray()) {
            if (Character.isLetterOrDigit(c) || c == 'e') {
                stack.push(new OperandNode(c));
            } else if (c == '*' || c == '+' || c == '?') {
                if (stack.size() < 1) {
                    throw new IllegalArgumentException("Invalid postfix expression: " + postfix);
                }
                ASTNode operand = stack.pop();
                stack.push(new UnaryOperatorNode(c, operand));
            } else {
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
}
