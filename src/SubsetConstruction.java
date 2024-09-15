import java.util.*;

class SubsetConstruction {

    public static DFA constructDFA(NFA nfa) {
        Map<Set<State>, State> subsetToDFAState = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();
        Set<Character> alphabet = nfa.getAlphabet(); // Obtener el alfabeto del NFA
        System.out.println("Alfabeto extraído del NFA: " + alphabet);

        Set<State> startSubset = epsilonClosure(Collections.singleton(nfa.start));
        System.out.println("Estado inicial del DFA: " + startSubset);
        State startState = new State(generateId(), containsAcceptState(startSubset));
        subsetToDFAState.put(startSubset, startState);
        queue.add(startSubset);

        // Usa el constructor que toma el alfabeto
        DFA dfa = new DFA(startState, alphabet);  // Pasar el alfabeto aquí
        System.out.println("Alfabeto en el DFA: " + dfa.getAlphabet());

        
        while (!queue.isEmpty()) {
            Set<State> currentSubset = queue.poll();
            State currentDFAState = subsetToDFAState.get(currentSubset);
    
            for (char symbol : alphabet) {  // Usar el alfabeto del DFA
                Set<State> nextSubset = move(currentSubset, symbol);
                nextSubset = epsilonClosure(nextSubset);
    
                if (!nextSubset.isEmpty()) {
                    State nextDFAState = subsetToDFAState.get(nextSubset);
                    if (nextDFAState == null) {
                        nextDFAState = new State(generateId(), containsAcceptState(nextSubset));
                        subsetToDFAState.put(nextSubset, nextDFAState);
                        queue.add(nextSubset);
                    }
                    currentDFAState.addDFATransition(symbol, nextDFAState);
                    System.out.println("Agregando transición DFA para símbolo: " + symbol);
                }
            }
        }
    
        return dfa;
    }    
    

    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            for (State nextState : state.getNFATransitions('\0')) {
                if (!closure.contains(nextState)) {
                    closure.add(nextState);
                    stack.add(nextState);
                }
            }
        }

        return closure;
    }

    private static Set<State> move(Set<State> states, char symbol) {
        Set<State> result = new HashSet<>();
        for (State state : states) {
            result.addAll(state.getNFATransitions(symbol));
        }
        return result;
    }

    private static boolean containsAcceptState(Set<State> states) {
        for (State state : states) {
            if (state.isAccept) {
                return true;
            }
        }
        return false;
    }

    private static int idCounter = 0;

    private static int generateId() {
        return idCounter++;
    }
}
