abstract class ASTNode {
    abstract void accept(ASTVisitor visitor);

    abstract NFA toNFA();
}

class OperandNode extends ASTNode {
    char value;

    OperandNode(char value) {
        if (value != 'a' && value != 'b') {  // Validación de operandos válidos
            throw new IllegalArgumentException("Símbolo no reconocido: " + value);
        }
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
        System.out.println("Creando NFA para operando: " + this.value); // Debug para operandos
        return new NFA(start, accept);
    }
}

class OperatorNode extends ASTNode {
    char operator;
    ASTNode left, right;

    OperatorNode(char operator, ASTNode left, ASTNode right) {
        if (operator != '|' && operator != '.') {
            throw new IllegalArgumentException("Operador no reconocido: " + operator);
        }
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
        System.out.println("Creando NFA para el operador: " + operator);  // Depuración del operador
        NFA leftNFA = left.toNFA();
        NFA rightNFA = right.toNFA();

        State start = new State(IDGenerator.getNextId(), false);
        State accept = new State(IDGenerator.getNextId(), true);  // Estado de aceptación

        if (operator == '|') {
            System.out.println("Aplicando operador |");
            start.addTransition('e', leftNFA.start);
            start.addTransition('e', rightNFA.start);
            leftNFA.accept.addTransition('e', accept);
            rightNFA.accept.addTransition('e', accept);
        } else if (operator == '.') {
            System.out.println("Aplicando operador . (concatenación)");
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
        if (operator != '*' && operator != '+') {
            throw new IllegalArgumentException("Operador no reconocido: " + operator);
        }
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

        System.out.println("Procesando operador: " + operator);  // Depuración del operador
        switch (operator) {
            case '*':
                System.out.println("Aplicando * al operando");
                start.addTransition('e', operandNFA.start);
                start.addTransition('e', accept);
                operandNFA.accept.addTransition('e', operandNFA.start);
                operandNFA.accept.addTransition('e', accept);
                break;
            case '+':
                System.out.println("Aplicando + al operando");
                start.addTransition('e', operandNFA.start);
                operandNFA.accept.addTransition('e', operandNFA.start);  // Repetición del operando
                operandNFA.accept.addTransition('e', accept);  // Ir al estado de aceptación
                break;
        }

        return new NFA(start, accept);
    }
}