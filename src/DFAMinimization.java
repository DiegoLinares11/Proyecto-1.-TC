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
    
        // Clasificar los estados correctamente como aceptantes y no aceptantes
        for (State state : dfa.getStates()) {
            if (state.isAccept) {
                acceptingStates.add(state);
            } else {
                nonAcceptingStates.add(state);
            }
        }
    
        // Debug: Imprimir la cantidad de estados de aceptación y no aceptación
        System.out.println("Cantidad de estados de aceptación: " + acceptingStates.size());
        System.out.println("Cantidad de estados de no aceptación: " + nonAcceptingStates.size());
    
        // Si no hay estados de aceptación o no aceptación, imprime una advertencia
        if (acceptingStates.isEmpty() || nonAcceptingStates.isEmpty()) {
            System.out.println("Advertencia: No hay suficientes estados de aceptación o no aceptación en el DFA para minimizar.");
            
            // Forcefully separate states into accepting and non-accepting by selecting at least one state for each partition
            if (acceptingStates.isEmpty()) {
                System.out.println("Forcing partition - treating first state as non-accepting.");
                State forcedNonAccepting = dfa.getStates().iterator().next();  // Take any state and force it to be non-accepting
                nonAcceptingStates.add(forcedNonAccepting);
            } else if (nonAcceptingStates.isEmpty()) {
                System.out.println("Forcing partition - treating first state as accepting.");
                State forcedAccepting = dfa.getStates().iterator().next();  // Take any state and force it to be accepting
                acceptingStates.add(forcedAccepting);
            }
        
            // If there is only one partition, print a warning and return the original DFA
            return dfa;  // Devuelve el DFA original si no es posible minimizar
        }
               
        // Particionar los estados iniciales en aceptación y no aceptación
        Set<Set<State>> partitions = new HashSet<>();
        partitions.add(acceptingStates);
        partitions.add(nonAcceptingStates);

        // Debug: Imprimir las particiones iniciales
        System.out.println("Particiones iniciales:");
        printPartitions(partitions);
    
        boolean refined;
        do {
            refined = false;
            Set<Set<State>> newPartitions = new HashSet<>();
    
            for (Set<State> partition : partitions) {
                Map<Map<State, Character>, Set<State>> transitionGroups = new HashMap<>();

                // Crear transiciones para cada estado
                for (State state : partition) {
                    Map<State, Character> transitions = new HashMap<>();
                    for (Map.Entry<Character, State> entry : state.dfaTransitions.entrySet()) {
                        transitions.put(entry.getValue(), entry.getKey());
                    }

                    transitionGroups.computeIfAbsent(transitions, k -> new HashSet<>()).add(state);
                }

                newPartitions.addAll(transitionGroups.values());

                // Si las transiciones crean nuevas particiones, marca el refinamiento
                if (transitionGroups.size() > 1) {
                    refined = true;
                }
            }
    
            partitions = newPartitions;

            // Debug: Imprimir las particiones después de cada refinamiento
            System.out.println("Particiones después del refinamiento:");
            printPartitions(partitions);
    
        } while (refined);
    
        // Si no hay particiones válidas, imprime un error
        if (partitions.isEmpty()) {
            System.out.println("Error: No se pudieron generar particiones durante la minimización.");
            return null;
        }
    
        // Ahora construimos el DFA minimizado
        Map<State, State> stateMapping = new HashMap<>();
        DFA minimizedDFA = null;
    
        for (Set<State> partition : partitions) {
            State representative = partition.iterator().next();  // Seleccionar el estado representativo de la partición
            State newState = new State(representative.id, representative.isAccept);  // Crear nuevo estado
            stateMapping.put(representative, newState);
    
            if (minimizedDFA == null) {
                minimizedDFA = new DFA(newState);  // Definir el estado inicial
            } else {
                minimizedDFA.addState(newState);
            }
        }
    
        if (minimizedDFA == null) {
            System.out.println("Error: DFA minimizado es null.");
            return null;
        }
    
        // Copiar las transiciones del DFA original al minimizado
        for (State oldState : stateMapping.keySet()) {
            State newState = stateMapping.get(oldState);
            for (Map.Entry<Character, State> entry : oldState.dfaTransitions.entrySet()) {
                char symbol = entry.getKey();
                State oldTargetState = entry.getValue();
                newState.addDFATransition(symbol, stateMapping.get(oldTargetState));
            }
        }

        System.out.println("DFA minimizado con éxito.");
        return minimizedDFA;
    }

    // Método auxiliar para imprimir particiones
    private static void printPartitions(Set<Set<State>> partitions) {
        System.out.println("Particiones iniciales:");
        printPartitions(partitions);

        for (Set<State> partition : partitions) {
            System.out.print("Partición: ");
            for (State state : partition) {
                System.out.print(state.id + " ");
            }
            System.out.println();
        }
    }
}