import java.util.*;

public class DFAState {
    Set<State> nfaStates;
    boolean isAccept;
    int id;

    DFAState(Set<State> nfaStates, boolean isAccept, int id) {
        this.nfaStates = nfaStates;
        this.isAccept = isAccept;
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nfaStates);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        DFAState other = (DFAState) obj;
        return Objects.equals(nfaStates, other.nfaStates);
    }

    @Override
    public String toString() {
        return "State{id=" + id + "}";
    }
}