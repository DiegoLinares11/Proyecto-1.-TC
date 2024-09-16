import java.util.*;

class DFA {
    Set<DFAState> states = new HashSet<>();
    DFAState start;
    Set<DFAState> acceptStates = new HashSet<>();
    Map<DFAState, Map<Character, DFAState>> transitions = new HashMap<>();

    DFA(DFAState start) {
        this.start = start;
    }

    void addTransition(DFAState from, char symbol, DFAState to) {
        transitions.computeIfAbsent(from, k -> new HashMap<>()).put(symbol, to);
    }

    // Simula la aceptación de una cadena en el AFD
    public boolean simulate(String input) {
        DFAState currentState = start; // Comienza en el estado inicial

        for (char symbol : input.toCharArray()) {
            // Si el estado actual no tiene una transición para el símbolo, la cadena no es
            // aceptada
            if (!transitions.containsKey(currentState) || !transitions.get(currentState).containsKey(symbol)) {
                return false;
            }
            // Avanza al siguiente estado según el símbolo
            currentState = transitions.get(currentState).get(symbol);
        }

        // Verifica si el estado final es un estado de aceptación
        return acceptStates.contains(currentState);
    }
}
