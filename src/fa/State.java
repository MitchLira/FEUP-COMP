package fa;

public abstract class State<T> {
    private static int count = 0;
    private int id;
    private boolean acceptState;

    State(boolean acceptState) {
        this.id = count++;
        this.acceptState = acceptState;
    }

    public abstract void addEdge(String edgeID, State state);
    public abstract void addInEdge(String edgeID, int stateId);

    public abstract T transition(String edgeID);

    public static int getLastId() {
        return count;
    }

    public int getId() {
        return id;
    }

    public boolean isAcceptState() {
        return acceptState;
    }

    public void setAcceptState(boolean acceptState) {
        this.acceptState = acceptState;
    }
}
