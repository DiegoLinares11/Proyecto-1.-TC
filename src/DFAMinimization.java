import java.util.*;

class DFAMinimization {
    public static DFA minimizeDFA(DFA dfa) {
        if (dfa == null) {
            System.out.println("Error: DFA es null al intentar minimizarlo.");
            return null;
        }
    
        System.out.println("Minimizando DFA...");
        
        // Crear particiones de estados de aceptación y no aceptación
        Set<State> acceptingStates = new HashSet<>();
        Set<State> nonAcceptingStates = new HashSet<>();
    
        for (State state : dfa.getStates()) {
            if (state.isAccept) {
                acceptingStates.add(state);
            } else {
                nonAcceptingStates.add(state);
            }
        }
    
        // Si no hay estados de aceptación o no aceptación, imprime un error
        if (acceptingStates.isEmpty() || nonAcceptingStates.isEmpty()) {
            System.out.println("Error: No hay suficientes estados en el DFA para minimizar.");
            return null;
        }
    
        // Particionar los estados
        Set<Set<State>> partitions = new HashSet<>();
        partitions.add(acceptingStates);
        partitions.add(nonAcceptingStates);
    
        boolean refined;
        do {
            refined = false;
            Set<Set<State>> newPartitions = new HashSet<>();
    
            for (Set<State> partition : partitions) {
                Map<Map<State, Character>, Set<State>> transitionGroups = new HashMap<>();
    
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
    
        // Si no hay particiones, imprime un error
        if (partitions.isEmpty()) {
            System.out.println("Error: No se pudieron generar particiones durante la minimización.");
            return null;
        }
    
        // Ahora construimos el DFA minimizado
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
    
        if (minimizedDFA == null) {
            System.out.println("Error: DFA minimizado es null.");
            return null;
        }
    
        // Copiar las transiciones
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
