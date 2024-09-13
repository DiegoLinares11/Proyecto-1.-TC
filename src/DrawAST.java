import java.io.FileWriter;
import java.io.IOException;

class GraphvizVisitor implements ASTVisitor {
    private StringBuilder sb = new StringBuilder();
    private int nodeCounter = 0;

    GraphvizVisitor() {
        sb.append("digraph G {\n");
    }

    public void visit(OperandNode node) {
        sb.append(String.format("node%d [label=\"%c\"];\n", nodeCounter++, node.value));
    }

    public void visit(OperatorNode node) {
        int current = nodeCounter++;
        sb.append(String.format("node%d [label=\"%c\"];\n", current, node.operator));

        int left = nodeCounter;
        node.left.accept(this);
        sb.append(String.format("node%d -> node%d;\n", current, left));

        int right = nodeCounter;
        node.right.accept(this);
        sb.append(String.format("node%d -> node%d;\n", current, right));
    }

    public void visit(UnaryOperatorNode node) {
        int current = nodeCounter++;
        sb.append(String.format("node%d [label=\"%c\"];\n", current, node.operator));

        int operand = nodeCounter;
        node.operand.accept(this);
        sb.append(String.format("node%d -> node%d;\n", current, operand));
    }

    public String getGraph() {
        sb.append("}\n");
        return sb.toString();
    }
}

public class DrawAST {
    public static void draw(ASTNode root, String filename) throws IOException {
        GraphvizVisitor visitor = new GraphvizVisitor();
        root.accept(visitor);
        String graph = visitor.getGraph();

        FileWriter writer = new FileWriter(filename);
        writer.write(graph);
        writer.close();

        Runtime.getRuntime().exec(new String[] { "dot", "-Tpng", filename, "-o", filename + ".png" });
    }
}
