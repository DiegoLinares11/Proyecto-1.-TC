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

    public void removeUnreachableStates() {
        Set<DFAState> reachableStates = new HashSet<>();
        Queue<DFAState> queue = new LinkedList<>();
        queue.add(start);
        reachableStates.add(start);

        while (!queue.isEmpty()) {
            DFAState current = queue.poll();
            if (transitions.containsKey(current)) {
                for (DFAState next : transitions.get(current).values()) {
                    if (!reachableStates.contains(next)) {
                        reachableStates.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        // Elimina los estados no alcanzables
        states.retainAll(reachableStates);
        acceptStates.retainAll(reachableStates);
    }

    public void minimize() {
        // Paso 1: Separar estados en dos grupos: estados de aceptación y no aceptación
        List<Set<DFAState>> P = new ArrayList<>();
        Set<DFAState> acceptingStates = new HashSet<>(acceptStates); // Conjunto de estados de aceptación
        Set<DFAState> nonAcceptingStates = new HashSet<>(states);
        nonAcceptingStates.removeAll(acceptStates); // Conjunto de estados no aceptadores
    
        P.add(acceptingStates);   // Añadir estados de aceptación como una partición
        P.add(nonAcceptingStates); // Añadir estados no aceptadores como otra partición
    
        List<Set<DFAState>> W = new ArrayList<>(P);  // W empieza igual que P
    
        // Depuración: imprimir particiones iniciales
        System.out.println("Particiones iniciales:");
        for (Set<DFAState> partition : P) {
            System.out.println("Partición: " + partition);
        }
    
        // Paso 2: Refinar particiones
        while (!W.isEmpty()) {
            Set<DFAState> A = W.remove(W.size() - 1);  // Tomamos una partición del conjunto W
    
            for (char s : getAlphabet()) {
                Set<DFAState> X = new HashSet<>();
    
                // Buscar todos los estados que tienen una transición con el símbolo 's' hacia los estados en A
                for (DFAState q : states) {
                    DFAState targetState = transitions.getOrDefault(q, new HashMap<>()).get(s);
                    if (targetState != null && A.contains(targetState)) {
                        X.add(q);
                    }
                }
    
                // Depuración: imprimir las transiciones hacia la partición A para el símbolo actual
                System.out.println("Transiciones con símbolo '" + s + "' hacia partición A: " + A);
                System.out.println("Estados que se pueden mover a la partición A: " + X);
    
                // Crear una lista temporal para almacenar las nuevas particiones
                List<Set<DFAState>> newPartitions = new ArrayList<>();
    
                // Para cada partición Y en P, dividir en Y1 y Y2 según las transiciones hacia A
                for (Set<DFAState> Y : new ArrayList<>(P)) {  // Iteramos sobre una copia de P
                    Set<DFAState> Y1 = new HashSet<>(Y);
                    Y1.retainAll(X);  // Y1 es la intersección de Y y X
                    Set<DFAState> Y2 = new HashSet<>(Y);
                    Y2.removeAll(X);  // Y2 es Y - X
    
                    if (!Y1.isEmpty() && !Y2.isEmpty()) {
                        // Si Y puede dividirse en Y1 y Y2, reemplazamos Y por Y1 y Y2
                        newPartitions.add(Y1);
                        newPartitions.add(Y2);
    
                        // Eliminar Y de P y W, ya que se ha dividido
                        P.remove(Y);
    
                        if (W.contains(Y)) {
                            W.remove(Y);
                            W.add(Y1);
                            W.add(Y2);
                        } else {
                            W.add(Y1.size() <= Y2.size() ? Y1 : Y2);
                        }
    
                        // Depuración: imprimir las nuevas particiones
                        System.out.println("Partición Y dividida en Y1: " + Y1 + " y Y2: " + Y2);
                    } else {
                        // Si Y no se divide, lo mantenemos como está
                        newPartitions.add(Y);
                    }
                }
    
                // Actualizar P con las nuevas particiones refinadas
                P.clear();
                P.addAll(newPartitions);
            }
        }
    
        // Paso 3: Refinar más estrictamente los estados de aceptación según las transiciones salientes
        // Utilizamos una lista temporal para evitar modificar P mientras iteramos
        List<Set<DFAState>> tempPartitions = new ArrayList<>(P);
    
        for (Set<DFAState> partition : tempPartitions) {
            if (partition.stream().anyMatch(acceptStates::contains)) {
                // Refinar la partición basada en las transiciones salientes
                Set<Character> alphabet = getAlphabet();
                List<Set<DFAState>> refinedPartition = new ArrayList<>();
                refinedPartition.add(new HashSet<>(partition));
    
                for (char symbol : alphabet) {
                    List<Set<DFAState>> newRefinedPartition = new ArrayList<>();
    
                    for (Set<DFAState> subPartition : refinedPartition) {
                        Map<DFAState, Set<DFAState>> transitionGroups = new HashMap<>();
    
                        for (DFAState state : subPartition) {
                            DFAState nextState = transitions.getOrDefault(state, new HashMap<>()).get(symbol);
                            transitionGroups.computeIfAbsent(nextState, k -> new HashSet<>()).add(state);
                        }
    
                        newRefinedPartition.addAll(transitionGroups.values());
                    }
                    refinedPartition = newRefinedPartition;
                }
    
                // Actualizar P con las nuevas particiones refinadas
                P.remove(partition);  // Eliminar la partición original
                P.addAll(refinedPartition);  // Añadir las nuevas particiones refinadas
    
                // Depuración: imprimir las particiones refinadas para los estados de aceptación
                System.out.println("Particiones refinadas de estados de aceptación: " + refinedPartition);
            }
        }
    
        // Paso 4: Reconstruir el DFA minimizado
        rebuildDFA(P);
    }        
    
    // Método para reconstruir el DFA minimizado a partir de las particiones
    private void rebuildDFA(List<Set<DFAState>> partitions) {
        Set<DFAState> newStates = new HashSet<>();
        Map<DFAState, DFAState> representativeMapping = new HashMap<>();
    
        // Depuración: imprimir las particiones finales antes de reconstruir el DFA
        System.out.println("Particiones finales después del refinamiento:");
        for (Set<DFAState> partition : partitions) {
            System.out.println("Partición: " + partition);
        }
    
        // Crear nuevos estados, uno por cada partición
        for (Set<DFAState> partition : partitions) {
            DFAState representative = partition.iterator().next();  // Elegimos un representante de la partición
            boolean isAccepting = partition.stream().anyMatch(acceptStates::contains);  // Verificar si hay un estado aceptador en la partición
            DFAState newState = new DFAState(representative.nfaStates, isAccepting, representative.id);  // Crear nuevo estado
            newStates.add(newState);
    
            // Depuración: imprimir el estado creado
            System.out.println("Nuevo estado creado: " + newState + ", ¿Es estado de aceptación? " + isAccepting);
    
            // Mapear los estados originales al nuevo representante
            for (DFAState state : partition) {
                representativeMapping.put(state, newState);
            }
        }
    
        // Crear nuevas transiciones en el DFA minimizado
        Map<DFAState, Map<Character, DFAState>> newTransitions = new HashMap<>();
        for (DFAState oldState : transitions.keySet()) {
            DFAState newFromState = representativeMapping.get(oldState);  // Obtener el nuevo estado representativo
    
            Map<Character, DFAState> originalTransitions = transitions.get(oldState);
            for (Map.Entry<Character, DFAState> entry : originalTransitions.entrySet()) {
                char symbol = entry.getKey();
                DFAState oldToState = entry.getValue();
                DFAState newToState = representativeMapping.get(oldToState);  // Obtener el nuevo estado representativo de destino
    
                newTransitions.computeIfAbsent(newFromState, k -> new HashMap<>()).put(symbol, newToState);
            }
        }
    
        // Actualizar el DFA con los nuevos estados y transiciones
        this.states = newStates;
        this.start = representativeMapping.get(start);  // Actualizar el estado inicial
        this.transitions = newTransitions;
    
        // Actualizar los estados de aceptación
        this.acceptStates.clear();
        for (DFAState state : newStates) {
            if (state.isAccept) {
                this.acceptStates.add(state);
            }
        }
    
        // Depuración: imprimir los estados de aceptación finales
        System.out.println("Estados de aceptación finales en el DFA minimizado: " + this.acceptStates);
    }        

    // Método auxiliar para obtener el alfabeto
    private Set<Character> getAlphabet() {
        Set<Character> alphabet = new HashSet<>();
        for (Map<Character, DFAState> transitionMap : transitions.values()) {
            alphabet.addAll(transitionMap.keySet());
        }
        return alphabet;
    }
}