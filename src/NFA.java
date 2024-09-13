import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
}
