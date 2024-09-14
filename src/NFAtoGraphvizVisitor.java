import java.util.*;

class NFAtoGraphvizVisitor {
    private StringBuilder sb = new StringBuilder();
    private Set<State> visited = new HashSet<>();

    NFAtoGraphvizVisitor() {
        sb.append("digraph NFA {\n");
    }

    // Recorre los estados del AFN, generando las conexiones en el archivo .dot.
    void visit(State state) {
        if (visited.contains(state))
            return;
        visited.add(state);

        for (Map.Entry<Character, List<State>> entry : state.transitions.entrySet()) {
            for (State target : entry.getValue()) {
                sb.append(String.format("node%d -> node%d [label=\"%c\"];\n", state.id, target.id, entry.getKey()));
                visit(target);
            }
        }
    }

    public String getGraph() {
        sb.append("}\n");
        return sb.toString();
    }
}