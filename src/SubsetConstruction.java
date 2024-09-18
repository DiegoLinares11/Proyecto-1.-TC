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

        while (!unmarkedStates.isEmpty()) {
            DFAState currentDFAState = unmarkedStates.iterator().next();
            unmarkedStates.remove(currentDFAState);

            // Para cada símbolo posible en las transiciones del AFN
            for (char symbol : getSymbolsFromNFA(nfa)) {
                // Calcula los estados alcanzables mediante el símbolo
                Set<State> reachable = new HashSet<>();
                for (State nfaState : currentDFAState.nfaStates) {
                    if (nfaState.transitions.containsKey(symbol)) {
                        for (State target : nfaState.transitions.get(symbol)) {
                            reachable.addAll(epsilonClosure(Collections.singleton(target)));
                        }
                    }
                }

                if (!reachable.isEmpty()) {
                    DFAState nextState = stateMapping.get(reachable);
                    if (nextState == null) {
                        nextState = new DFAState(reachable, containsAcceptState(reachable), IDGenerator.getNextId());
                        stateMapping.put(reachable, nextState);
                        unmarkedStates.add(nextState);
                        dfa.states.add(nextState);
                        if (nextState.isAccept) {
                            dfa.acceptStates.add(nextState);
                        }
                    }
                    dfa.addTransition(currentDFAState, symbol, nextState);
                }
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
        for (State state : states) {
            if (state.isAccept && state == nfa.accept) {// yo lo tenia solo como if(state.isAccept))
                return true;
            }
        }
        return false;
    }

}