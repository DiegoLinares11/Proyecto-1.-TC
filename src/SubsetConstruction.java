import java.util.*;

class SubsetConstruction {
    private NFA nfa;

    SubsetConstruction(NFA nfa) {
        this.nfa = nfa;
    }

    // Construir AFD desde AFN
    public DFA toDFA() {
        Set<DFAState> unmarkedStates = new HashSet<>();
        Map<Set<State>, DFAState> stateMapping = new HashMap<>();

        // Calcula el epsilon-cierre del estado inicial del AFN
        Set<State> initialClosure = epsilonClosure(Collections.singleton(nfa.start));
        DFAState startState = new DFAState(initialClosure, containsAcceptState(initialClosure),
                IDGenerator.getNextId());

        DFA dfa = new DFA(startState);
        unmarkedStates.add(startState);
        stateMapping.put(initialClosure, startState);

        // Crear el estado de escape (dead state)
        DFAState deadState = new DFAState(new HashSet<>(), false, IDGenerator.getNextId());
        dfa.states.add(deadState);
        // Agregar transiciones del estado de trampa a sí mismo para todos los símbolos
        for (char symbol : getSymbolsFromNFA(nfa)) {
            dfa.addTransition(deadState, symbol, deadState);
        }

        while (!unmarkedStates.isEmpty()) {
            DFAState currentDFAState = unmarkedStates.iterator().next();
            unmarkedStates.remove(currentDFAState);

            // Procesar cada símbolo en el alfabeto
            for (char symbol : getSymbolsFromNFA(nfa)) {
                Set<State> reachable = new HashSet<>();

                // Buscar estados alcanzables a través del símbolo
                for (State nfaState : currentDFAState.nfaStates) {
                    if (nfaState.transitions.containsKey(symbol)) {
                        for (State target : nfaState.transitions.get(symbol)) {
                            reachable.addAll(epsilonClosure(Collections.singleton(target)));
                        }
                    }
                }

                DFAState nextState;
                if (!reachable.isEmpty()) {
                    // Si hay estados alcanzables, buscamos si ya existe ese subconjunto en el DFA
                    nextState = stateMapping.get(reachable);

                    if (nextState == null) {
                        // Si no existe, creamos un nuevo estado
                        nextState = new DFAState(reachable, containsAcceptState(reachable), IDGenerator.getNextId());
                        stateMapping.put(reachable, nextState);
                        unmarkedStates.add(nextState);
                        dfa.states.add(nextState);
                        if (nextState.isAccept) {
                            dfa.acceptStates.add(nextState);
                        }
                    }
                } else {
                    // Si no hay estados alcanzables, usamos el estado de trampa
                    nextState = deadState;
                }
                // Agregamos la transición desde el estado actual con el símbolo al siguiente
                // estado
                dfa.addTransition(currentDFAState, symbol, nextState);
            }
        }

        return dfa;
    }

    private Set<Character> getSymbolsFromNFA(NFA nfa) {
        Set<Character> symbols = new HashSet<>();
        for (State state : nfa.getAllStates()) {
            symbols.addAll(state.transitions.keySet());
        }
        symbols.remove('e'); // Excluir transiciones epsilon
        return symbols;
    }

    // Calcula el epsilon-cierre de un conjunto de estados
    private Set<State> epsilonClosure(Set<State> states) {
        Stack<State> stack = new Stack<>();
        stack.addAll(states); // Añadir todos los estados al stack
        Set<State> closure = new HashSet<>(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            if (state.transitions.containsKey('e')) { // Si hay transiciones epsilon
                for (State next : state.transitions.get('e')) {
                    if (!closure.contains(next)) { // Si aún no está en el cierre
                        closure.add(next);
                        stack.push(next); // Agrega el siguiente estado al stack
                    }
                }
            }
        }

        return closure;
    }

    private boolean containsAcceptState(Set<State> states) {
        for (State state : states) { // yo lo tenia solo como if (state.isaccept)
            if (state.isAccept && state == nfa.accept) {
                return true;
            }
        }
        return false;
    }
}
