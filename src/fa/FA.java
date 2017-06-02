package fa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public abstract class FA {
    protected HashMap<Integer, State> states;
    protected HashSet<String> identifiers; // contains all the different identifiers
    protected State currentState;
    protected State startState;
    protected State lastState;


    public FA() {
       states = new HashMap<>();
       identifiers = new HashSet<>();
    }

    public boolean onAcceptState(){
        return currentState.isAcceptState();
    }

    public void setIdentifiers(HashSet<String> identifiers) {
        this.identifiers = identifiers;
    }

    public void addState(State state) {
       states.put(state.getId(), state);
    }

    public void setStartState(State startState) {
        this.startState = startState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public HashSet<String> getIdentifiers() {
		return identifiers;
	}

	@Override
    public String toString() {
        String res ="----------------FA States----------------\n";


        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            State value = entry.getValue();

            res += value.toString() + "\n";
        }

        res += "******Start State*******\n" + startState.toString() + "\n";
        res += "******Current State*******\n" + currentState.toString() + "\n";


        return res;
    }
}
