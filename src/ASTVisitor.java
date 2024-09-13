public interface ASTVisitor {
    void visit(OperandNode node);

    void visit(OperatorNode node);

    void visit(UnaryOperatorNode node);
}
