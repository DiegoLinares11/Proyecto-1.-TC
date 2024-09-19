import java.util.*;

public class DFAToGraphvizVisitor {
    private StringBuilder sb = new StringBuilder();
    private Set<DFAState> visited = new HashSet<>();

    DFAToGraphvizVisitor() {
        sb.append("digraph DFA {\n");
    }

    // Recorre los estados del AFD, generando las conexiones en el archivo .dot
    void visit(DFA dfa) {
        for (DFAState state : dfa.states) {
            if (!visited.contains(state)) {
                visitState(state, dfa);
            }
        }
    }

    private void visitState(DFAState state, DFA dfa) {
        if (visited.contains(state))
            return;
        visited.add(state);

        // Agrega el nodo al gráfico
        sb.append(String.format("node%d [shape=%s];\n",
                state.id, state.isAccept ? "doublecircle" : "circle"));

        // Verifica si hay transiciones para el estado actual
        Map<Character, DFAState> stateTransitions = dfa.transitions.get(state);
        if (stateTransitions == null) {
            return; // No hay transiciones, retornar
        }

        // Recorre las transiciones utilizando stateTransitions
        for (Map.Entry<Character, DFAState> entry : stateTransitions.entrySet()) {
            DFAState target = entry.getValue();
            if (target == null) {
                System.err.println("Error: Estado de destino null en la transición desde el estado: " + state.id);
                continue; // Saltar a la siguiente iteración
            }
            sb.append(String.format("node%d -> node%d [label=\"%c\"];\n", state.id, target.id, entry.getKey()));
            visitState(target, dfa);
        }
    }

    public String getGraph() {
        sb.append("}\n");
        return sb.toString();
    }
}
