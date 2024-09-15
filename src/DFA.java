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
}
