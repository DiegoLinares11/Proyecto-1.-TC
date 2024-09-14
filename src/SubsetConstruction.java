import java.util.*;

class SubsetConstruction {
    public static DFA constructDFA(NFA nfa) {
        Map<Set<State>, State> subsetToDFAState = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();
        Set<Character> alphabet = nfa.getAlphabet(); // Asegúrate de usar el alfabeto del NFA

        Set<State> startSubset = epsilonClosure(Collections.singleton(nfa.start));
        State startState = new State(generateId(), containsAcceptState(startSubset));
        subsetToDFAState.put(startSubset, startState);
        queue.add(startSubset);

        DFA dfa = new DFA(startState);
        dfa.alphabet.addAll(alphabet); // Agregar el alfabeto al DFA

        while (!queue.isEmpty()) {
            Set<State> currentSubset = queue.poll();
            State currentDFAState = subsetToDFAState.get(currentSubset);

            for (char symbol : alphabet) { // Usar el alfabeto del DFA
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
