package fa;

import java.util.HashMap;
import java.util.Map;

public class DfaState extends State<Integer> {
    private HashMap<String, Integer> edges;

    public DfaState(boolean accept) {
        super(accept);
        edges = new HashMap<>();
    }

    @Override
    public void addEdge(String edgeID, Integer stateID) {
        edges.put(edgeID, stateID);
    }

    @Override
    public Integer transition(String edgeID) {
        return edges.get(edgeID);
    }

    @Override
    public String toString() {
        String res= "---------DfaState--------- \nId -> " + getId() + "\nAcceptState-> " + isAcceptState() + "\nEdges: ";

        for (Map.Entry<String, Integer> entry : edges.entrySet()){
            String key = entry.getKey();
            Integer state = entry.getValue();

            res += "\n\t String-> " + key + "\n \t State-> " + state;

            res += "\n";
        }

        if(edges.size() == 0)
            res += "\n\t This state doesn't have any edges";

        res += "\n----------------------------";


        return res;
    }

}
