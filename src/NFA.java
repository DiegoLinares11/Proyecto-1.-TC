import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class NFA {
    State start;
    State accept;

    NFA(State start, State accept) {
        this.start = start;
        this.accept = accept;
    }

    // Método para obtener todos los estados del AFN
    Set<State> getAllStates() {
        Set<State> allStates = new HashSet<>();
        exploreStates(start, allStates);
        return allStates;
    }

    // Método auxiliar para explorar estados de manera recursiva
    private void exploreStates(State current, Set<State> visited) {
        if (!visited.contains(current)) {
            visited.add(current);
            for (List<State> nextStates : current.transitions.values()) {
                for (State next : nextStates) {
                    exploreStates(next, visited);
                }
            }
        }
    }

    // Método para simular el AFN
    boolean simulate(String input, int visitLimit) {
        return simulate(start, input, 0, new HashMap<>(), visitLimit);
    }

    // Simulación recursiva con límites de visitas
    private boolean simulate(State current, String input, int index, Map<State, Integer> visitCount, int visitLimit) {
        visitCount.put(current, visitCount.getOrDefault(current, 0) + 1);

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
