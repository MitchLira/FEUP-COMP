package fa;

import java.util.*;

import logic.*;
import utils.Pair;
import utils.Utils;



public class NFA extends FA {
    // string=null works as EPSILON transition
    private static final String EPSILON = null;
    
    private Convertable convertable;

    public NFA(Convertable convertable) {
        super();
        this.convertable = convertable;
        
        startState = new NfaState();
        addState(startState);
        currentState = startState;
        
        if (convertable instanceof Term)
        	createStatesTerm((Term) convertable);
        else if (convertable instanceof ExpressionSet)
        	createStatesExpressionSet((ExpressionSet) convertable);
        else
        	createStatesNFASet((NFASet) convertable);

        System.out.println(this.toDotFormat());
    }

	private void createStatesTerm(Term term) {
        identifiers.add(term.getName());
        lastState = startState;
        Integer lower = term.getPair().getLower();
        Integer upper = term.getPair().getUpper();
        
        if (lower == upper) {
            for (int i = 0; i < upper; i++) {
                NfaState state = new NfaState();
                this.addState(state);
                startState.addEdge(term.getName(), state.getId());
                lastState = state;
            }  
        } else if (lower != null && upper != null) {
            for (int i = 0; i < lower; i++) {
                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state.getId());
                lastState = state;
            }

            for (int i = lower; i < upper; i++) {

                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state.getId());
                lastState.addEdge(EPSILON, state.getId());
                lastState = state;
            }
        } else if (lower != null && upper == null) {

            for (int i = 0; i < lower; i++) {
                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state.getId());
                lastState = state;
            }

            lastState.addEdge(term.getName(), lastState.getId());
        }
    }
	
	
	
	private void createStatesExpressionSet(ExpressionSet expressionSet) {
        ArrayList<Convertable> list = expressionSet.getConvertables();
        
        NFA currentNFA;

        lastState  = startState;
        for (Convertable c : list) {
        	currentNFA = c.convert();
        	states.putAll(currentNFA.getStates());
        	identifiers.addAll(currentNFA.getIdentifiers());

            lastState.addEdge(EPSILON, currentNFA.startState.getId());
            lastState = currentNFA.lastState;

        }
    }

	
	private void createStatesNFASet(NFASet nfaSet) {
        ArrayList<Convertable> list = nfaSet.getConvertables();
        

        lastState = new NfaState();
        this.addState(lastState);
        for (Convertable c : list) {
            NFA currentNFA = c.convert();
            states.putAll(currentNFA.getStates());
            identifiers.addAll(currentNFA.getIdentifiers());
        	startState.addEdge(EPSILON, currentNFA.startState.getId());

        	currentNFA.lastState.addEdge(EPSILON, lastState.getId());
        }



        if(nfaSet.getOperator() != null){

            Pair operatorRange = Utils.getOperatorRange(nfaSet.getOperator());
            Integer lower = operatorRange.getLower();
            Integer upper = operatorRange.getUpper();

            nfaSet.setOperator(null);//stop recursion

            if (lower == upper) {


                for (int i = 0; i < upper; i++) {// 2
                    NFA clone = nfaSet.convert();
                    states.putAll(clone.getStates());
                    lastState.addEdge(EPSILON, clone.startState.getId());
                    lastState = clone.lastState;
                }
            } else if (lower != null && upper != null) {//{2,4}
                for (int i = 0; i < lower; i++) {
                    NFA clone = nfaSet.convert();
                    states.putAll(clone.getStates());
                    lastState.addEdge(EPSILON, clone.startState.getId());
                    lastState = clone.lastState;
                }

                for (int i = lower; i < upper; i++) {
                    NFA clone = nfaSet.convert();
                    states.putAll(clone.getStates());
                    lastState.addEdge(EPSILON, clone.startState.getId());
                    lastState.addEdge(EPSILON, clone.lastState.getId());
                    lastState = clone.lastState;
                }
            } else if (lower != null && upper == null) { // * , +

                if(Operators.STAR.getValue() == lower){
                    lastState.addEdge(EPSILON,startState.getId());
                }else {

                    NFA clone = nfaSet.convert();
                    states.putAll(clone.getStates());
                    lastState.addEdge(EPSILON, clone.startState.getId());
                    lastState = clone.lastState;

                    lastState.addEdge(EPSILON, clone.startState.getId());
                }
            }

        }
	}



    //DFA STUFF

    public ArrayList<Integer> eClosure(int stateId){
        //eClosure, the state as well as all the states reached with EPSILON(null)

        NfaState s = (NfaState) states.get(stateId);
        ArrayList<Integer> res = s.getEdges().get(EPSILON);

        if(res != null ) {

            ArrayList<Integer> tmp = new ArrayList<>();
            for (int i = 0 ;i < res.size();i++) {
                NfaState s1 = (NfaState) states.get(res.get(i));
                ArrayList<Integer> t = s1.getEdges().get(EPSILON);

                if(t!= null) {
                    tmp.addAll(t);

                    //if there is a new state reached with epsilon, add it to check if that state can lead to more unreached states
                    if(!t.contains(res.get(i)))
                        t.add(res.get(i));
                }

            }

            if(!res.contains(stateId))
                res.add(stateId);

            res.addAll(tmp);

        }else{
            res = new ArrayList<>();
            res.add(stateId);
        }

        //remove duplicates
        res = new ArrayList<>(new LinkedHashSet<>(res));

        return  res;
    }

    public DFA getDFA() {
        DFA dfa = new DFA();



        //the new states
        ArrayList<ArrayList<Integer>> newStates = new ArrayList<>();
        newStates.add(eClosure(this.startState.getId()));
        newStates.add(new ArrayList<>());//death state

        //rows of the table
        ArrayList<ArrayList<ArrayList<Integer>>> rows = new ArrayList<>();


        int currentRow=0;
        while (currentRow < newStates.size()){
            ArrayList<ArrayList<Integer>> row = new ArrayList<>();

            Iterator iterator = identifiers.iterator();

            while (iterator.hasNext()) { //fill the current row for each identifier
                String currIdentifier = (String) iterator.next();


                //get the current states array
                ArrayList<Integer> currState = newStates.get(currentRow);

                ArrayList<Integer> rowElement = new ArrayList<>();

                for (int i = 0 ; i < currState.size() ; i++){

                    //get the current state   A*Ba?C2
                    NfaState state = (NfaState) states.get(currState.get(i));

                    //get all the states reached by the current identifier without counting with EPSILON
                    ArrayList<Integer> statesReached = state.getEdges().get(currIdentifier);

                    if(statesReached != null) {

                        for (int j = 0; j < statesReached.size(); j++) {//add all the states reached by the current identifier counting with EPSILON
                            ArrayList<Integer> eClose = eClosure(statesReached.get(j));
                            if (eClose != null)
                                rowElement.addAll(eClose);

                        }
                        //remove duplicates
                        rowElement = new ArrayList<>(new LinkedHashSet<>(rowElement));
                    }


                }

                row.add(rowElement);

                //check if this row element is a new state and add it to the newStates if it is

                if(checkIfAlreadyIsState(newStates,rowElement))
                    newStates.add(rowElement);

            }


            rows.add(row);

            currentRow++;
        }

        //DEBUG----------------
        String res = "\n---------Table-------\n";
        Iterator iterator = identifiers.iterator();
        while (iterator.hasNext())  //fill the current row for each identifier
            res += iterator.next() + "  |  ";


        res +="\n";

        for (int i = 0 ; i < rows.size() ; i++){

            res +="\nROW-> " + i;

            res +="\n { ";
            ArrayList<Integer> newState = newStates.get(i);
            for (int j = 0; j < newState.size() ; j++){
                res +=newState.get(j) + "  | ";
            }
            res +=" }-> ";

            ArrayList<ArrayList<Integer>> row = rows.get(i);
            for (int j = 0; j < row.size() ; j++){

                ArrayList<Integer> currElement = row.get(j);

                res +="{ ";
                for(int k = 0; k < currElement.size() ; k++)
                    res +=currElement.get(k) + "  | ";

                res +=" }";
            }

        }
        System.out.println(res);

        //------------------

        //create dfa states

        ArrayList<DfaState> dfaStates = new ArrayList<>();
        HashMap<String,Integer> ids = new HashMap<>();



        for (ArrayList<Integer> state : newStates){

            boolean isAccept=false;
            for (int i = 0; i < state.size(); i++){
                if(states.get(state.get(i)).isAcceptState()){
                    isAccept = true;
                    break;
                }
            }

            DfaState newDfaState = new DfaState(isAccept);
            dfaStates.add(newDfaState);
            dfa.addState(newDfaState);

            ids.put(state.toString(),newDfaState.getId());

        }

        dfa.setStartState(dfaStates.get(0));
        dfa.setCurrentState(dfaStates.get(0));

        for(int j = 0 ; j < dfaStates.size(); j++){

            int i = 0;
            Iterator it = identifiers.iterator();
            while (it.hasNext()) { //add edge for each identifier
                String currIdentifier = (String) it.next();
                ArrayList<Integer> nextState = rows.get(j).get(i);

                dfaStates.get(j).addEdge(currIdentifier,ids.get(nextState.toString()));
                i++;
            }
        }

        System.out.println("------------DFA-----------\n" + dfa.toString());


        return dfa;
    }

    //check if aggregate of states that forma a single new state in dfa formation already exists
    private boolean checkIfAlreadyIsState(ArrayList<ArrayList<Integer>> states, ArrayList<Integer> state){
        boolean res=true;
        Collections.sort(state);

        for (int i = 0 ; i < states.size();i++){
            ArrayList<Integer> currState = states.get(i);

            if(currState.size() != state.size())
                continue;
            else{
                Collections.sort(currState);

                if(state.equals(currState)){
                    res = false;
                    break;
                }
            }
        }

        return res;
    }


    public String toDotFormat(){
        String res = "";

        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            NfaState state = (NfaState) entry.getValue();
            Integer stateId =  entry.getKey();

            if(state.isAcceptState())
                res += state.getId() + "[style=filled]";


            for(Map.Entry<String, ArrayList<Integer>> entry1 : state.getEdges().entrySet())
                for (Integer id:entry1.getValue())
                 res += stateId + "->" + id + "[label=" + entry1.getKey() + "];";
        }

        return res;
    }
}