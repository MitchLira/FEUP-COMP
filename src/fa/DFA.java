package fa;

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
}
