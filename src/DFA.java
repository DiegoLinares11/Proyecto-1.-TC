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
        // Paso 1: Eliminar estados inalcanzables
        removeUnreachableStates();

        // Paso 2: Inicializar particiones de estados aceptadores y no aceptadores
        Set<DFAState> nonAccepting = new HashSet<>(states);
        nonAccepting.removeAll(acceptStates); // Conjunto de estados no aceptadores

        List<Set<DFAState>> partitions = new ArrayList<>();
        partitions.add(nonAccepting); // Partición de no aceptadores
        partitions.add(new HashSet<>(acceptStates)); // Partición de aceptadores

        boolean updated = true;

        // Paso 3: Refinar las particiones
        while (updated) {
            updated = false;
            List<Set<DFAState>> newPartitions = new ArrayList<>();

            for (Set<DFAState> partition : partitions) {
                Map<Map<DFAState, DFAState>, Set<DFAState>> transitionGroups = new HashMap<>();

                for (DFAState state : partition) {
                    Map<DFAState, DFAState> transitionGroupKey = new HashMap<>();

                    // Iterar sobre cada símbolo y registrar hacia dónde va cada estado en función
                    // de las transiciones
                    for (char symbol : getAlphabet()) {
                        DFAState nextState = transitions.getOrDefault(state, new HashMap<>()).get(symbol);

                        // Agrupar los estados según las transiciones a otras particiones
                        DFAState nextPartition = getPartition(partitions, nextState);
                        transitionGroupKey.put(nextPartition, nextState);
                    }

                    // Agrupar los estados que comparten el mismo patrón de transiciones
                    transitionGroups.computeIfAbsent(transitionGroupKey, k -> new HashSet<>()).add(state);
                }

                // Refinar particiones en función de las transiciones
                newPartitions.addAll(transitionGroups.values());

                if (transitionGroups.size() > 1) {
                    updated = true; // Las particiones han cambiado
                }
            }

            partitions = newPartitions; // Actualizar particiones refinadas
        }

        // Paso 4: Construir un nuevo DFA minimizado con las particiones refinadas
        Set<DFAState> newStates = new HashSet<>();
        Map<DFAState, DFAState> stateMapping = new HashMap<>();
        DFAState newStart = null;

        // Crear las transiciones correctas entre particiones
        for (Set<DFAState> partition : partitions) {
            if (partition.isEmpty())
                continue;

            DFAState representative = partition.iterator().next(); // Elegir un representante para la partición
            DFAState newState = new DFAState(representative.nfaStates, acceptStates.contains(representative),
                    representative.id);
            newStates.add(newState);
            stateMapping.put(representative, newState);

            if (representative.equals(start)) {
                newStart = newState;
            }
        }

        // Paso 5: Actualizar las transiciones del nuevo DFA
        Map<DFAState, Map<Character, DFAState>> newTransitions = new HashMap<>();
        for (Map.Entry<DFAState, Map<Character, DFAState>> entry : transitions.entrySet()) {
            DFAState fromState = stateMapping.get(entry.getKey()); // Estado original mapeado al nuevo estado minimizado
            if (fromState == null)
                continue; // Si no existe, saltar

            Map<Character, DFAState> newMap = new HashMap<>();
            for (Map.Entry<Character, DFAState> transition : entry.getValue().entrySet()) {
                DFAState toState = stateMapping.get(transition.getValue()); // Mapeamos el estado destino al nuevo DFA
                if (toState != null) {
                    newMap.put(transition.getKey(), toState);
                }
            }
            newTransitions.put(fromState, newMap); // Asignamos las nuevas transiciones al nuevo DFA
        }

        // Actualizamos el DFA con los nuevos estados y transiciones minimizados
        this.states = newStates;
        this.start = newStart;
        this.transitions = newTransitions;
    }

    // Método para obtener la partición de un estado
    private DFAState getPartition(List<Set<DFAState>> partitions, DFAState state) {
        if (state == null) {
            return null; // Estado de trampa
        }
        for (Set<DFAState> partition : partitions) {
            if (partition.contains(state)) {
                return partition.iterator().next(); // Devuelve un representante de la partición
            }
        }
        return null;
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