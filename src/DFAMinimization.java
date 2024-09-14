import java.util.*;

class DFAMinimization {
    public static DFA minimizeDFA(DFA dfa) {
        Set<State> acceptingStates = new HashSet<>();
        Set<State> nonAcceptingStates = new HashSet<>();

        // Clasificar los estados en conjuntos de aceptación y no aceptación
        for (State state : dfa.getStates()) {
            if (state.isAccept) {
                acceptingStates.add(state);
            } else {
                nonAcceptingStates.add(state);
            }
        }

        // Particionar los estados en dos grupos iniciales: aceptación y no aceptación
        Set<Set<State>> partitions = new HashSet<>();
        partitions.add(acceptingStates);
        partitions.add(nonAcceptingStates);

        // Refinar las particiones
        boolean refined;
        do {
            refined = false;
            Set<Set<State>> newPartitions = new HashSet<>();

            for (Set<State> partition : partitions) {
                Map<Map<State, Character>, Set<State>> transitionGroups = new HashMap<>();

                // Agrupar estados con transiciones equivalentes
                for (State state : partition) {
                    Map<State, Character> transitions = new HashMap<>();
                    for (Map.Entry<Character, State> entry : state.dfaTransitions.entrySet()) {
                        transitions.put(entry.getValue(), entry.getKey());
                    }

                    transitionGroups.computeIfAbsent(transitions, k -> new HashSet<>()).add(state);
                }

                newPartitions.addAll(transitionGroups.values());

                if (transitionGroups.size() > 1) {
                    refined = true;
                }
            }

            partitions = newPartitions;
        } while (refined);

        // Construir el nuevo DFA
        Map<State, State> stateMapping = new HashMap<>();
        DFA minimizedDFA = null;

        for (Set<State> partition : partitions) {
            State representative = partition.iterator().next();
            State newState = new State(representative.id, representative.isAccept);
            stateMapping.put(representative, newState);

            if (minimizedDFA == null) {
                minimizedDFA = new DFA(newState); // Estado inicial
            } else {
                minimizedDFA.addState(newState);
            }
        }

        // Copiar las transiciones al nuevo DFA
        for (State oldState : stateMapping.keySet()) {
            State newState = stateMapping.get(oldState);
            for (Map.Entry<Character, State> entry : oldState.dfaTransitions.entrySet()) {
                char symbol = entry.getKey();
                State oldTargetState = entry.getValue();
                newState.addDFATransition(symbol, stateMapping.get(oldTargetState));
            }
        }

        return minimizedDFA;
    }
}
