import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

class NFA {
    State start;
    State accept;

    NFA(State start, State accept) {
        this.start = start;
        this.accept = accept;
    }

    // Este método toma una cadena de entrada y simula el AFN para determinar si la
    // cadena es aceptada
    boolean simulate(String input, int visitLimit) {
        return simulate(start, input, 0, new HashMap<>(), visitLimit);
    }

    private boolean simulate(State current, String input, int index, Map<State, Integer> visitCount, int visitLimit) {
        // Incrementa el contador de visitas para el estado actual
        visitCount.put(current, visitCount.getOrDefault(current, 0) + 1);

        // Si se ha alcanzado el límite de visitas, detenerse
        if (visitCount.get(current) > visitLimit) {
            return false;
        }

        if (index == input.length()) {
            return current.isAccept;
        }

        char symbol = input.charAt(index);
        if (current.transitions.containsKey(symbol)) {
            for (State next : current.transitions.get(symbol)) {
                if (simulate(next, input, index + 1, new HashMap<>(visitCount), visitLimit)) {
                    return true;
                }
            }
        }

        // Considera transiciones epsilon
        if (current.transitions.containsKey('e')) {
            for (State next : current.transitions.get('e')) {
                if (simulate(next, input, index, new HashMap<>(visitCount), visitLimit)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Método para obtener el conjunto de símbolos del alfabeto del NFA
    public Set<Character> getAlphabet() {
        Set<Character> alphabet = new HashSet<>();
        Set<State> visited = new HashSet<>();
        collectAlphabet(start, alphabet, visited);
        return alphabet;
    }

    // Método recursivo que recolecta el alfabeto a partir de las transiciones
    private void collectAlphabet(State state, Set<Character> alphabet, Set<State> visited) {
        if (visited.contains(state)) {
            return;
        }
        visited.add(state);

        for (Map.Entry<Character, List<State>> entry : state.transitions.entrySet()) {
            char symbol = entry.getKey();
            if (symbol != 'e') { // Ignorar transiciones epsilon
                alphabet.add(symbol);
            }
            for (State nextState : entry.getValue()) {
                collectAlphabet(nextState, alphabet, visited); // Recorrer los estados adyacentes
            }
        }
    }
}
