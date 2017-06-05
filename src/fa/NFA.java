package fa;

import java.util.*;

import logic.*;
import utils.Pair;
import utils.Utils;



public class NFA extends FA {
    // string=null works as EPSILON transition
    private static final String EPSILON = null;
    
    private Convertable convertable;



    public NFA(Convertable convertable ) {
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
                startState.addEdge(term.getName(), state);
                lastState = state;
            }  
        } else if (lower != null && upper != null) {
            for (int i = 0; i < lower; i++) {
                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state);
                lastState = state;
            }

            for (int i = lower; i < upper; i++) {

                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state);
                lastState.addEdge(EPSILON, state);
                lastState = state;
            }
        } else if (lower != null && upper == null) {

            for (int i = 0; i < lower; i++) {
                NfaState state = new NfaState();
                this.addState(state);
                lastState.addEdge(term.getName(), state);
                lastState = state;
            }

            lastState.addEdge(term.getName(), lastState);
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

            lastState.addEdge(EPSILON, currentNFA.startState);
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
        	startState.addEdge(EPSILON, currentNFA.startState);

        	currentNFA.lastState.addEdge(EPSILON, lastState);
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
                    lastState.addEdge(EPSILON, clone.startState);
                    lastState = clone.lastState;
                }
            } else if (lower != null && upper != null) {//{2,4}

                boolean firstIteration=true;
                for (int i = 0; i < lower; i++) {
                    if(firstIteration){
                        //the nfa already has its states no need to clone
                        firstIteration=false;
                    }else {
                        NFA clone = nfaSet.convert();
                        states.putAll(clone.getStates());
                        lastState.addEdge(EPSILON, clone.startState);
                        lastState = clone.lastState;
                    }
                }

                for (int i = lower; i < upper; i++) {
                    if(firstIteration){
                        startState.addEdge(EPSILON,lastState);
                        firstIteration=false;
                    }else {
                        NFA clone = nfaSet.convert();
                        states.putAll(clone.getStates());
                        lastState.addEdge(EPSILON, clone.startState);
                        lastState.addEdge(EPSILON, clone.lastState);
                        lastState = clone.lastState;
                    }


                }
            } else if (lower != null && upper == null) { // * , +, {2,}

                if(Operators.STAR.getValue() == lower){// *
                    lastState.addEdge(EPSILON,startState);
                }else {// + , {2, }

                    State backEdgeState = startState;

                    for (int i = 1; i < lower; i++){
                        NFA clone = nfaSet.convert();
                        states.putAll(clone.getStates());
                        lastState.addEdge(EPSILON, clone.startState);
                        lastState = clone.lastState;
                        backEdgeState=clone.startState;
                    }

                    lastState.addEdge(EPSILON, backEdgeState);

                }
            }

        }
	}

	public void handleDots(){
        String DOT = ".";

        for (Map.Entry<Integer,State> state: states.entrySet()){

            NfaState currState = ((NfaState)state.getValue());

            if(currState.getOut_edges().get(DOT) == null)
                continue;

            ArrayList<Integer> dotEdges = new ArrayList<>(currState.getOut_edges().get(DOT));


            for (Integer edgeId : dotEdges){
                NfaState dotReachedState = (NfaState) states.get(edgeId);


                //remove DOT edge
                currState.getOut_edges().get(DOT).remove((Integer) dotReachedState.getId());
                dotReachedState.getIn_edges().get(DOT).remove((Integer) currState.getId());

                //add edges will all the identifiers
                identifiers.remove(DOT);
                Iterator iter = identifiers.iterator();

                while (iter.hasNext()) {
                    String identifier = (String) iter.next();
                    currState.addEdge(identifier,dotReachedState);
                }
            }

            currState.getOut_edges().remove(DOT);
        }
    }



	public void optimize(){//removes epsilon transactions when possible

        NfaState currState;
        HashMap<Integer, State> statesCloned = (HashMap)states.clone();


        boolean removed;

        for(Map.Entry<Integer, State> entry : statesCloned.entrySet()) {//for each state of the nfa

            do {
                removed=false;
                Integer id = entry.getKey();

                if (states.get(id) == null)//state already been removed
                    continue;
                else
                    currState = (NfaState) entry.getValue();

                HashMap<String, ArrayList<Integer>> startEdges = currState.getOut_edges();

                for (Map.Entry<String, ArrayList<Integer>> startEdge : startEdges.entrySet()) {//for each edge of the current state being optimezed

                    ArrayList<Integer> transactions = new ArrayList(startEdge.getValue());


                    for (Integer state2DegID : transactions) {// for each transaction the 2nd degree state
                        NfaState stateDeg2 = (NfaState) states.get(state2DegID);

                        if (stateDeg2.getOut_edges() == null)
                            continue;



                        ArrayList<Integer> edgesDeg2 = stateDeg2.getOut_edges().get(EPSILON);

                        if (edgesDeg2 != null && edgesDeg2.size() == 1 && stateDeg2.getOut_edges().size() == 1) {//if the next state has only a epsilon transaction, we can remove this state
                            NfaState successor = (NfaState) states.get(edgesDeg2.get(0));


                            for (Map.Entry<String, ArrayList<Integer>> inEdgesDeg2 : stateDeg2.getIn_edges().entrySet()) {//for every in_edge of the state being removed, update the elements accordingly
                                String key = inEdgesDeg2.getKey();

                                if (inEdgesDeg2.getValue().size() > 0)
                                    removed = true;

                                for (Integer stateBeingUpdatedId : inEdgesDeg2.getValue()) {//state to be updated
                                    NfaState stateBeingUpdated = (NfaState) states.get(stateBeingUpdatedId);
                                    //add new edge
                                    stateBeingUpdated.addEdge(key, successor);

                                    //remove out and in edge to intermediate
                                    stateBeingUpdated.getOut_edges().get(key).remove((Integer) stateDeg2.getId());
                                }
                            }

                            //remove out_edges
                            for (Map.Entry<String, ArrayList<Integer>> outEdgesDeg2 : stateDeg2.getOut_edges().entrySet()) {
                                for (Integer sucId : outEdgesDeg2.getValue()){
                                    ((NfaState)states.get(sucId)).getIn_edges().get(outEdgesDeg2.getKey()).remove((Integer) stateDeg2.getId());
                                }
                            }
                            //remove state
                            states.remove(stateDeg2.getId());
                        }
                    }

                }

            }while (removed);
        }
    }


    public String toDotFormat(){
        String res = "";

        res+= startState.getId() + "[style=filled fillcolor=orange]";

        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            NfaState state = (NfaState) entry.getValue();

            if(state.isAcceptState())
                res += state.getId() + "[style=filled fillcolor=green]";
        }

        for(Map.Entry<Integer, State> entry : states.entrySet()) {
            NfaState state = (NfaState) entry.getValue();
            Integer stateId =  entry.getKey();


            for(Map.Entry<String, ArrayList<Integer>> entry1 : state.getOut_edges().entrySet())
                for (Integer id:entry1.getValue())
                    res += stateId + "->" + id + "[label=" + entry1.getKey() + "];";
        }

        return res;
    }


    //DFA STUFF

    public ArrayList<Integer> eClosure(int stateId){
        //eClosure, the state as well as all the states reached with EPSILON(null)

        NfaState s = (NfaState) states.get(stateId);
        ArrayList<Integer> res;


        if(s.getOut_edges().get(EPSILON) != null ) {
            res = new ArrayList<>(s.getOut_edges().get(EPSILON));

            for (int i = 0 ;i < res.size();i++) {
                NfaState s1 = (NfaState) states.get(res.get(i));
                ArrayList<Integer> t = s1.getOut_edges().get(EPSILON);

                if(t != null) {

                    for (Integer reachedState : t) {
                        if (!res.contains(reachedState))
                            res.add(reachedState);
                    }
                }

            }

            if(!res.contains(stateId))
                res.add(stateId);

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

        System.out.println("Eclosure-> " + eClosure(this.startState.getId()));
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
                    ArrayList<Integer> statesReached = state.getOut_edges().get(currIdentifier);

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
        HashMap<String,State> ids = new HashMap<>();



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

            ids.put(state.toString(),newDfaState);

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




}