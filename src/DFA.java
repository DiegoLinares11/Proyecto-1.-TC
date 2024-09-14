import java.util.*;

class DFA {
    State start;
    Set<State> states;
    private Set<State> acceptStates; // Conjunto de estados de aceptación
    public Set<Character> alphabet; // Conjunto de símbolos del alfabeto

    public DFA(State start) {
        this.start = start;
        this.states = new HashSet<>();
        this.states.add(start);
        this.acceptStates = new HashSet<>();
        this.alphabet = new HashSet<>();
    }

    // Agrega un estado al conjunto de estados del AFD
    public void addState(State state) {
        this.states.add(state);
        if (state.isAccept) {
            this.acceptStates.add(state);
        }
    }

    // Devuelve el estado inicial
    public State getStartState() {
        return start;
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
    public boolean simulate(String input, int visitLimit) {
        State currentState = start;
        int visitCount = 0;

        for (char symbol : input.toCharArray()) {
            if (!alphabet.contains(symbol)) {
                throw new IllegalArgumentException("Símbolo no reconocido: " + symbol);
            }

            if (visitCount >= visitLimit) {
                throw new IllegalStateException("Límite de visitas alcanzado");
            }

            currentState = currentState.dfaTransitions.get(symbol);
            if (currentState == null) {
                return false; // No hay transición para el símbolo
            }

            visitCount++;
        }

        return isAcceptingState(currentState);
    }

    // Método para agregar un símbolo al alfabeto
    public void addSymbolToAlphabet(char symbol) {
        this.alphabet.add(symbol);
    }
}
