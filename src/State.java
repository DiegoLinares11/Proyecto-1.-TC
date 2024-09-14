import java.util.*;

class State {
    int id;
    boolean isAccept;
    Map<Character, List<State>> transitions = new HashMap<>(); // AFN transitions
    Map<Character, State> dfaTransitions = new HashMap<>(); // AFD transitions (sin listas)

    State(int id, boolean isAccept) {
        this.id = id;
        this.isAccept = isAccept;
    }

    // Método para agregar una transición de AFN (puede ir a múltiples estados)
    void addTransition(char symbol, State toState) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(toState);
    }

    // Método para agregar una transición de AFD (debe ir a un solo estado)
    void addDFATransition(char symbol, State toState) {
        dfaTransitions.put(symbol, toState);
    }

    // Obtener las transiciones para el AFN
    List<State> getNFATransitions(char symbol) {
        return transitions.getOrDefault(symbol, new ArrayList<>());
    }

    // Obtener las transiciones para el AFD
    State getDFATransition(char symbol) {
        return dfaTransitions.get(symbol);
    }

    // Sobrescribimos el método toString para ayudar en la depuración
    @Override
    public String toString() {
        return "State " + id + (isAccept ? " (accept)" : "");
    }
}
