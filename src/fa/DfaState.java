package fa;

import java.util.HashMap;
import java.util.Map;

public class DfaState extends State<Integer> {
    private HashMap<String, Integer> out_edges;
    private HashMap<String, Integer> in_edges;


    public DfaState(boolean accept) {
        super(accept);
        out_edges = new HashMap<>();
        in_edges = new HashMap<>();
    }

    @Override
    public void addEdge(String edgeID, State state) {
        out_edges.put(edgeID, state.getId());
        addInEdge(edgeID,getId());
    }

    @Override
    public void addInEdge(String edgeID, int stateId) {
        in_edges.put(edgeID, stateId);
    }

    public HashMap<String, Integer> getOut_edges() {
        return out_edges;
    }



    @Override
    public Integer transition(String edgeID) {
        return out_edges.get(edgeID);
    }

    @Override
    public String toString() {
        String res= "---------DfaState--------- \nId -> " + getId() + "\nAcceptState-> " + isAcceptState() + "\nEdges: ";

        for (Map.Entry<String, Integer> entry : out_edges.entrySet()){
            String key = entry.getKey();
            Integer state = entry.getValue();

            res += "\n\t String-> " + key + "\n \t State-> " + state;

            res += "\n";
        }

        if(out_edges.size() == 0)
            res += "\n\t This state doesn't have any out_edges";

        res += "\n----------------------------";


        return res;
    }



}
