import java.util.*;

class DFA {
    State start;
    Set<State> states;
    private Set<State> acceptStates; // Conjunto de estados de aceptación
    public Set<Character> alphabet; // Conjunto de símbolos del alfabeto

    public DFA(State start) {
        this.start = start;
        this.states = new HashSet<>();
        this.acceptStates = new HashSet<>();
        this.alphabet = new HashSet<>();  // Inicializar un alfabeto vacío
    }

    public DFA(State start, Set<Character> alphabet) {
        this(start);  // Llama al constructor anterior
        this.alphabet = new HashSet<>(alphabet);  // Copia el alfabeto
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
    
        System.out.println("Simulando entrada: " + input);  // Debug
    
        for (char symbol : input.toCharArray()) {
            // Verifica que el currentState no sea nulo antes de acceder a él
            if (currentState == null) {
                System.out.println("El estado actual es nulo. No hay transición para el símbolo: " + symbol);
                return false;
            }
            
            System.out.println("Procesando símbolo: " + symbol + " en estado: " + currentState.id);
    
            if (!alphabet.contains(symbol)) {
                System.out.println("Alfabeto del DFA: " + alphabet);  // Asegúrate de imprimir el alfabeto correcto
                throw new IllegalArgumentException("Símbolo no reconocido: " + symbol);
            }
    
            // Obtener el siguiente estado basado en el símbolo actual
            currentState = currentState.dfaTransitions.get(symbol);
            
            // Verificación adicional si la transición es nula
            if (currentState == null) {
                System.out.println("No hay transición para el símbolo: " + symbol + " desde el estado actual.");
                return false;
            }
    
            visitCount++;
            if (visitCount >= visitLimit) {
                throw new IllegalStateException("Límite de visitas alcanzado");
            }
        }
    
        System.out.println("La cadena fue aceptada: " + isAcceptingState(currentState));
        return isAcceptingState(currentState);
    }
    
      

    //Metodo para eliminar los estados no alcanzables
    public void removeUnreachableStates() {
        Set<State> reachable = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        queue.add(start);
        reachable.add(start);

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

    // Método para agregar un símbolo al alfabeto
    public void addSymbolToAlphabet(char symbol) {
        this.alphabet.add(symbol);
    }
}
