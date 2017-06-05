package fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NfaState extends State<ArrayList<Integer>> {
    private HashMap<String, ArrayList<Integer>> out_edges;
    private HashMap<String, ArrayList<Integer>> in_edges;


    public NfaState(boolean accept) {
        super(accept);
        out_edges = new HashMap<>();
        in_edges = new HashMap<>();
    }

    public NfaState() {
        super(false);
        out_edges = new HashMap<>();
        in_edges = new HashMap<>();

    }

    public void addEdge(String edgeID, State state) {
        if (!out_edges.containsKey(edgeID)) {
            out_edges.put(edgeID, new ArrayList<>());
        }

        ArrayList<Integer> links = out_edges.get(edgeID);
        links.add(state.getId());

        //add in edge to the edge reached state
        state.addInEdge(edgeID,getId());
    }

    @Override
    public void addInEdge(String edgeID, int stateId) {
        if (!in_edges.containsKey(edgeID)) {
            in_edges.put(edgeID, new ArrayList<>());
        }

        ArrayList<Integer> links = in_edges.get(edgeID);
        links.add(stateId);
    }


    public ArrayList<Integer> transition(String edgeID) {
        return out_edges.get(edgeID);
    }

    public HashMap<String, ArrayList<Integer>> getOut_edges() {
        return out_edges;
    }

    public HashMap<String, ArrayList<Integer>> getIn_edges() {
        return in_edges;
    }

    @Override
    public String toString() {
        String res= "---------NfaState--------- \nId -> " + getId() + "\nAcceptState-> " + isAcceptState() + "\nEdges: ";

        for (Map.Entry<String, ArrayList<Integer>> entry : out_edges.entrySet()){
            String key = entry.getKey();
            ArrayList<Integer> states = entry.getValue();

            res += "\n\t String-> " + key + "\n \t Attainable states-> |";

            for (Integer stateId: states){
                res += stateId + "|" ;
            }


            res += "\n";
        }

        if(out_edges.size() == 0)
            res += "\n\t This state doesn't have any out_edges";

        res += "\n----------------------------";


        return res;
    }
}
