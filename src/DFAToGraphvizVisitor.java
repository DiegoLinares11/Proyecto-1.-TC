import java.util.List;
import java.util.Map;

class DFAToGraphvizVisitor {
    private StringBuilder graphBuilder = new StringBuilder();

    // Método para convertir el DFA a formato Graphviz
    public void visit(DFA dfa) {
        if (dfa == null) {
            System.out.println("Error: No se puede visitar un DFA null.");
            return;
        }
    
        graphBuilder.append("digraph DFA {\n");
        graphBuilder.append("  rankdir=LR;\n");
        graphBuilder.append("  node [shape=circle];\n");
    
        // Lógica de visualización
        for (State state : dfa.getStates()) {
            String shape = dfa.isAcceptingState(state) ? "doublecircle" : "circle";
            graphBuilder.append("  ").append(state.id).append(" [shape=").append(shape).append("];\n");
        }
    
        // Agregar transiciones
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
