import java.util.*;

class State {
    int id;
    boolean isAccept;
    Map<Character, List<State>> transitions = new HashMap<>(); // Un mapa que asocia un símbolo de entrada con una lista
                                                               // de estados de destino

    State(int id, boolean isAccept) {
        this.id = id;
        this.isAccept = isAccept;
    }

    // Lo que hacemos acá es agregar una transición desde este estado a otro estado
    // dado un símbolo de entrada.
    void addTransition(char symbol, State toState) {
        transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(toState);
    }

    @Override
    public String toString() {
        return "State{id=" + id + ", isAccept=" + isAccept + "}";
    }
}
