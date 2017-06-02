package fa;


import java.util.Map;

public class DFA extends FA {
    private boolean onDeathState;

    public void transition(String edgeID) {

        if(onDeathState)
           return;


        Integer stateId = (Integer) currentState.transition(edgeID);

        if (stateId == null)
            onDeathState=true;
        else {
            currentState = states.get(stateId);
            System.out.println("EdgeId-> " + edgeID + " Accept: " + currentState.isAcceptState());
        }
    }

    public String toDotFormat(){
        String res = "";

        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            DfaState state = (DfaState) entry.getValue();
            Integer stateId =  entry.getKey();

            if(state.isAcceptState())
                res += state.getId() + "[style=filled]";


            for(Map.Entry<String, Integer> entry1 : state.getEdges().entrySet())
                res += stateId + "->" + entry1.getValue() + "[label=" + entry1.getKey() + "];";

        }

        return res;
    }
}