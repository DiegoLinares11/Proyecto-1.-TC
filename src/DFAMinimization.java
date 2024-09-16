import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class DFAMinimization {

    // Aquí implementamos la minimización real
    public static DFA minimizeDFA(DFA dfa) {
        if (dfa == null) {
            System.out.println("Error: El DFA es null. No se puede continuar.");
            return null;
        }
    
        // Refinar particiones usando el algoritmo de partición completo
        Set<Set<State>> partitions = partitionDFA(dfa);
        DFA minimizedDFA = buildMinimizedDFA(partitions, dfa);
    
        if (minimizedDFA == null) {
            System.out.println("Error: El DFA minimizado es null.");
            return null;
        }
    
        // Visualizar el DFA minimizado en un archivo .dot
        DFAToGraphvizVisitor dfaVisitor = new DFAToGraphvizVisitor();
        dfaVisitor.visit(minimizedDFA);
        String dfaGraph = dfaVisitor.getGraph();
        String dfaFilename = "dfa_minimized.dot";
        try (FileWriter writer = new FileWriter(dfaFilename)) {
            writer.write(dfaGraph);
        } catch (IOException e) {
            System.err.println("Error escribiendo archivo Graphviz: " + e.getMessage());
        }
        try {
            Runtime.getRuntime().exec(new String[]{"dot", "-Tpng", dfaFilename, "-o", dfaFilename + ".png"});
        } catch (IOException e) {
            System.err.println("Error ejecutando Graphviz: " + e.getMessage());
        }
    
        // Simulación del DFA minimizado con una cadena de prueba
        String cadena = "aab";  // Cambia esta cadena según el test que quieras
        boolean result = minimizedDFA.simulate(cadena);
        System.out.println("La cadena: " + cadena + " es " + (result ? "aceptada" : "rechazada") + " por el DFA minimizado.");
    
        return minimizedDFA;
    }    

    // Método para particionar el DFA (refinamiento de particiones)
    private static Set<Set<State>> partitionDFA(DFA dfa) {
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

        Set<Set<State>> partitions = new HashSet<>();
        if (!acceptingStates.isEmpty()) partitions.add(acceptingStates);
        if (!nonAcceptingStates.isEmpty()) partitions.add(nonAcceptingStates);

        // Refinar las particiones
        boolean refined;
        do {
            refined = false;
            Set<Set<State>> newPartitions = new HashSet<>();

            // Refinar particiones basadas en las transiciones
            for (Set<State> partition : partitions) {
                Map<Map<State, Character>, Set<State>> transitionGroups = new HashMap<>();

                // Agrupar estados por transiciones
                for (State state : partition) {
                    Map<State, Character> transitions = new HashMap<>();
                    for (Map.Entry<Character, State> entry : state.dfaTransitions.entrySet()) {
                        transitions.put(entry.getValue(), entry.getKey());
                    }
                    transitionGroups.computeIfAbsent(transitions, k -> new HashSet<>()).add(state);
                }

                // Añadir particiones refinadas
                newPartitions.addAll(transitionGroups.values());

                if (transitionGroups.size() > 1) {
                    refined = true;
                }
            }

            partitions = newPartitions;  // Actualizar particiones refinadas

        } while (refined);  // Repetir mientras haya refinamientos

        return partitions;
    }


    // Método para construir el DFA minimizado
    private static DFA buildMinimizedDFA(Set<Set<State>> partitions, DFA dfa) {
        Map<State, State> stateMapping = new HashMap<>(); // Mapeo de estados originales a estados minimizados
        DFA minimizedDFA = null;
    
        // Iterar sobre cada partición y crear un estado representativo para cada partición
        for (Set<State> partition : partitions) {
            // Asegurarse de que la partición no esté vacía
            if (partition.isEmpty()) {
                continue;  // Saltar partición vacía
            }
    
            // Seleccionar un estado representativo para la partición
            State representative = partition.iterator().next();
    
            // Crear un nuevo estado en el DFA minimizado
            State newState = new State(representative.id, representative.isAccept); // Se crea el estado combinando los originales
            for (State state : partition) {
                stateMapping.put(state, newState);  // Mapear todos los estados de la partición al nuevo estado
            }
    
            // Agregar el nuevo estado al DFA minimizado
            if (minimizedDFA == null) {
                minimizedDFA = new DFA(newState);  // Definir el estado inicial del DFA
            } else {
                minimizedDFA.addState(newState);   // Agregar el nuevo estado al DFA
            }
        }
    
        // Ahora copiar las transiciones desde el DFA original al DFA minimizado
        for (State oldState : stateMapping.keySet()) {
            State newState = stateMapping.get(oldState);  // Obtener el estado combinado del minimizado
            for (Map.Entry<Character, State> entry : oldState.dfaTransitions.entrySet()) {
                char symbol = entry.getKey();
                State oldTargetState = entry.getValue();
                if (oldTargetState != null && stateMapping.get(oldTargetState) != null) {
                    // Añadir la transición al nuevo estado en el DFA minimizado
                    newState.addDFATransition(symbol, stateMapping.get(oldTargetState));
                }
            }
        }
    
        System.out.println("DFA minimizado con éxito.");
        return minimizedDFA;
    }

}