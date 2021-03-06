package fa;


import java.util.Map;

public class DFA extends FA {
    private boolean onDeathState;
    private String description="";

    public String getDescription() {
        return description;
    }

    public void transition(String edgeID) {

        if(onDeathState)
           return;

        int oldState = currentState.getId();
        Integer stateId = (Integer) currentState.transition(edgeID);

        if (stateId == null)
            onDeathState=true;
        else {
            currentState = states.get(stateId);
            description += "Transaction from state(" + oldState + ") with EdgeId: " + edgeID + " to state(" + currentState.getId() + ") <br>";
            System.out.println("\tTransaction with EdgeId: " + edgeID + " | New state id: " + currentState.getId() + " aceppting state(" +  currentState.isAcceptState() + ")");
        }
    }

    public String toDotFormat(){
        String res = "";
        res+= startState.getId() + "[style=filled fillcolor=orange]";
        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            DfaState state = (DfaState) entry.getValue();
            Integer stateId =  entry.getKey();

            if(state.isAcceptState())
                res += state.getId() + "[style=filled fillcolor=green]";


            for(Map.Entry<String, Integer> entry1 : state.getOut_edges().entrySet())
                res += stateId + "->" + entry1.getValue() + "[label=" + entry1.getKey() + "];";

        }

        return res;
    }
}
