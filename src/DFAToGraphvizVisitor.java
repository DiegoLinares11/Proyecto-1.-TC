import java.util.Set;
import java.util.List;
import java.util.Map;

class DFAToGraphvizVisitor {
    private StringBuilder graphBuilder = new StringBuilder();

    // Método para convertir el DFA a formato Graphviz
    public void visit(DFA dfa) {
        graphBuilder.append("digraph DFA {\n");
        graphBuilder.append("  rankdir=LR;\n");
        graphBuilder.append("  node [shape=circle];\n");

        // Itera sobre todos los estados del DFA
        for (State state : dfa.getStates()) {
            String shape = dfa.isAcceptingState(state) ? "doublecircle" : "circle";
            graphBuilder.append("  ").append(state.id).append(" [shape=").append(shape).append("];\n");
        }

        // Agrega las transiciones
        for (State state : dfa.getStates()) {
            for (Map.Entry<Character, List<State>> entry : state.transitions.entrySet()) {
                char symbol = entry.getKey();
                for (State nextState : entry.getValue()) {
                    graphBuilder.append("  ").append(state.id).append(" -> ").append(nextState.id)
                            .append(" [label=\"").append(symbol).append("\"];\n");
                }
            }
        }

        graphBuilder.append("}\n");
    }

    public String getGraph() {
        return graphBuilder.toString();
    }
}
