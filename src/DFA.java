import java.util.*;

class DFA {
    Set<State> states;
    Set<State> acceptStates;
    State startState;
    Set<Character> alphabet;  // This is your alphabet set

    // Constructor of DFA
    public DFA(State startState) {
        this.startState = startState;
        this.states = new HashSet<>();
        this.acceptStates = new HashSet<>();
        this.alphabet = new HashSet<>();  // Ensure alphabet is initialized here
        this.states.add(startState);
    }

    public void addSymbolToAlphabet(char symbol) {
        alphabet.add(symbol);  // Ensure this is not null
    }

    public DFA(State start, Set<Character> alphabet) {
        this(start);  // Llama al constructor anterior
        this.alphabet = new HashSet<>(alphabet);  // Copia el alfabeto
    }
    
    // Agrega un estado al conjunto de estados del AFD
    public void addState(State state) {
        if (!states.contains(state)) {
            System.out.println("Añadiendo estado al DFA: " + state.id);
            states.add(state);  // Añadir el estado al conjunto de estados
            if (state.isAccept) {
                acceptStates.add(state);  // Si es un estado de aceptación, añadirlo al conjunto
            }
        }
    }

    // Devuelve el estado inicial
    public State getStartState() {
        return startState;
    }

    // Devuelve el conjunto de todos los estados del AFD
    public Set<State> getStates() {
        return states;
    }

    // Devuelve el conjunto de estados de aceptación
    public Set<State> getAcceptStates() {
        return acceptStates;
    }

    // Devuelve el conjunto del alfabeto
    public Set<Character> getAlphabet() {
        return alphabet;
    }

    // Verifica si un estado es un estado de aceptación
    public boolean isAcceptingState(State state) {
        return acceptStates.contains(state);
    }

    // Simula la aceptación de una cadena con un límite de visitas
    public boolean simulate(String input) {
        State currentState = startState;
    
        for (char symbol : input.toCharArray()) {
            if (!alphabet.contains(symbol)) {
                throw new IllegalArgumentException("Símbolo no reconocido: " + symbol);
            }
            currentState = currentState.getDFATransition(symbol);
            if (currentState == null) {
                return false;  // Transición no encontrada para el símbolo dado
            }
        }
    
        return currentState.isAccept;
    }    
    

    //Metodo para eliminar los estados no alcanzables
    public void removeUnreachableStates() {
        Set<State> reachable = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        queue.add(startState);
        reachable.add(startState);

        while (!queue.isEmpty()) {
            State current = queue.poll();
            for (State next : current.dfaTransitions.values()) {
                if (!reachable.contains(next)) {
                    reachable.add(next);
                    queue.add(next);
                }
            }
        }

        // Filtramos los estados alcanzables
        states.retainAll(reachable);
        acceptStates.retainAll(reachable);
    }

    public boolean isSymbolValid(char symbol) {
        return alphabet.contains(symbol);  // Verifica si el símbolo está en el alfabeto
    }
}
