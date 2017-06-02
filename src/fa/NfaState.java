package fa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NfaState extends State<ArrayList<Integer>> {
    private HashMap<String, ArrayList<Integer>> edges;



    public NfaState(boolean accept) {
        super(accept);
        edges = new HashMap<>();
    }

    public NfaState() {
        super(false);
        edges = new HashMap<>();
    }

    public void addEdge(String edgeID, Integer stateID) {
        if (!edges.containsKey(edgeID)) {
            edges.put(edgeID, new ArrayList<>());
        }

        ArrayList<Integer> links = edges.get(edgeID);
        links.add(stateID);
    }

    public ArrayList<Integer> transition(String edgeID) {
        return edges.get(edgeID);
    }

    public HashMap<String, ArrayList<Integer>> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        String res= "---------NfaState--------- \nId -> " + getId() + "\nAcceptState-> " + isAcceptState() + "\nEdges: ";

        for (Map.Entry<String, ArrayList<Integer>> entry : edges.entrySet()){
            String key = entry.getKey();
            ArrayList<Integer> states = entry.getValue();

            res += "\n\t String-> " + key + "\n \t Attainable states-> |";

            for (Integer stateId: states){
                res += stateId + "|" ;
            }


            res += "\n";
        }

        if(edges.size() == 0)
            res += "\n\t This state doesn't have any edges";

        res += "\n----------------------------";


        return res;
    }
}
