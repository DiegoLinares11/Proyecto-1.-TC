import java.util.*;

class SubsetConstruction {

    public static DFA constructDFA(NFA nfa) {
        Map<Set<State>, State> subsetToDFAState = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();
        Set<Character> alphabet = nfa.getAlphabet();
    
        // Get the epsilon closure of the initial NFA state
        Set<State> startSubset = epsilonClosure(Collections.singleton(nfa.start));
        
        // Create the initial DFA state
        boolean isStartAccept = containsAcceptState(startSubset);  // Check if the start state is accepting
        State startState = new State(generateId(), isStartAccept);
        subsetToDFAState.put(startSubset, startState);
        queue.add(startSubset);
    
        // Debugging: Log the acceptance status of the starting state
        System.out.println("DFA Start state created with id " + startState.id + ", isAccept: " + startState.isAccept);
    
        // Create the DFA
        DFA dfa = new DFA(startState);
    
        while (!queue.isEmpty()) {
            Set<State> currentSubset = queue.poll();
            State currentDFAState = subsetToDFAState.get(currentSubset);
    
            // Debugging: Log each state being processed
            System.out.println("Processing DFA state with id " + currentDFAState.id + " and isAccept: " + currentDFAState.isAccept);
    
            // Add the current state to the DFA if it hasn't been added yet
            dfa.addState(currentDFAState);
    
            // For each symbol in the alphabet, compute the next subset of states
            for (char symbol : alphabet) {
                Set<State> nextSubset = move(currentSubset, symbol);
                nextSubset = epsilonClosure(nextSubset);  // Compute epsilon closure
    
                if (!nextSubset.isEmpty()) {
                    // Check if this subset already corresponds to a DFA state
                    State nextDFAState = subsetToDFAState.get(nextSubset);
                    if (nextDFAState == null) {
                        boolean isAccept = containsAcceptState(nextSubset);  // Check if any of the states in the subset is accepting
    
                        // Debugging: Print out the acceptance status of the new DFA state
                        System.out.println("Creating DFA state for subset with isAccept: " + isAccept);
    
                        nextDFAState = new State(generateId(), isAccept);  // Create a new DFA state
                        subsetToDFAState.put(nextSubset, nextDFAState);
                        queue.add(nextSubset);
                    }
    
                    // Add the transition from the current DFA state to the next DFA state
                    currentDFAState.addDFATransition(symbol, nextDFAState);
                }
            }
        }
    
        return dfa;
    }                   
    
    // Este método verifica si un subconjunto de estados contiene un estado de aceptación
    private static boolean containsAcceptState(Set<State> states) {
        for (State state : states) {
            // Debugging: Print which state is being checked
            System.out.println("Checking if state " + state.id + " is accepting: " + state.isAccept);
            if (state.isAccept) {
                return true;  // Return true if any NFA state in the set is accepting
            }
        }
        return false;  // Return false if none of the states in the subset are accepting
    }       
    

    private static Set<State> epsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Queue<State> queue = new LinkedList<>(states);
    
        while (!queue.isEmpty()) {
            State state = queue.poll();
            List<State> epsilonTransitions = state.transitions.get('e');
            if (epsilonTransitions != null) {
                for (State nextState : epsilonTransitions) {
                    if (closure.add(nextState)) {  // Si el estado no está en el cierre, agrégalo
                        queue.add(nextState);
                    }
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

    private static int idCounter = 0;

    private static int generateId() {
        return idCounter++;
    }
}
