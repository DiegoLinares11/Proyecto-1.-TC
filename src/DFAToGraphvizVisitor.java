import java.util.Map;

class DFAToGraphvizVisitor {
    private StringBuilder graphBuilder = new StringBuilder();

    // Método para convertir el DFA a formato Graphviz
    public void visit(DFA dfa) {
        graphBuilder.append("digraph DFA {\n");
        graphBuilder.append("  rankdir=LR;\n");
        graphBuilder.append("  node [shape=circle];\n");
    
        for (State state : dfa.getStates()) {
            String shape = state.isAccept ? "doublecircle" : "circle";
            graphBuilder.append("  ").append(state.id).append(" [shape=").append(shape).append("];\n");
    
            // Draw the transitions
            for (Map.Entry<Character, State> entry : state.dfaTransitions.entrySet()) {
                char symbol = entry.getKey();
                State nextState = entry.getValue();
                graphBuilder.append("  ").append(state.id).append(" -> ").append(nextState.id)
                            .append(" [label=\"").append(symbol).append("\"];\n");
                            System.out.println("Transition: " + state.id + " -> " + nextState.id + " with symbol: " + symbol);
            }
        }
    
        graphBuilder.append("}\n");
    }
    
    

    public String getGraph() {
        return graphBuilder.toString();
    }
}
