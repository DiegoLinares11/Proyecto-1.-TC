abstract class ASTNode {
    abstract void accept(ASTVisitor visitor);

    abstract NFA toNFA();
}

class OperandNode extends ASTNode {
    char value;

    OperandNode(char value) {
        this.value = value;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    NFA toNFA() {
        State start = new State(IDGenerator.getNextId(), false);
        State accept = new State(IDGenerator.getNextId(), true);
        start.addTransition(this.value, accept);
        System.out.println("Creando NFA para operando: " + this.value); // Debug
        return new NFA(start, accept);
    }
}

class OperatorNode extends ASTNode {
    char operator;
    ASTNode left, right;

    OperatorNode(char operator, ASTNode left, ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    NFA toNFA() {
        NFA leftNFA = left.toNFA();
        NFA rightNFA = right.toNFA();
        System.out.println("Construyendo NFA para operador: " + this.operator);

        State start = new State(IDGenerator.getNextId(), false);
        State accept = new State(IDGenerator.getNextId(), true);

        if (operator == '|') {
            start.addTransition('e', leftNFA.start);
            start.addTransition('e', rightNFA.start);
            leftNFA.accept.addTransition('e', accept);
            rightNFA.accept.addTransition('e', accept);
        } else if (operator == '.') {
            leftNFA.accept.addTransition('e', rightNFA.start);
            return new NFA(leftNFA.start, rightNFA.accept);
        }

        return new NFA(start, accept);
    }
}

class UnaryOperatorNode extends ASTNode {
    char operator;
    ASTNode operand;

    UnaryOperatorNode(char operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    NFA toNFA() {
        NFA operandNFA = operand.toNFA();

        State start = new State(IDGenerator.getNextId(), false);
        State accept = new State(IDGenerator.getNextId(), true);

        switch (operator) {
            case '*':
                start.addTransition('e', operandNFA.start);
                start.addTransition('e', accept);
                operandNFA.accept.addTransition('e', operandNFA.start);
                operandNFA.accept.addTransition('e', accept);
                break;
            case '+':
                start.addTransition('e', operandNFA.start);
                operandNFA.accept.addTransition('e', operandNFA.start);
                operandNFA.accept.addTransition('e', accept);
                break;
        }

        return new NFA(start, accept);
    }
}
