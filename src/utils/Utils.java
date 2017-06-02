package utils;

import fa.DFA;
import fa.NFA;
import logic.NFASet;
import parser.GrammarParser;
import parser.SimpleNode;

import java.io.ByteArrayInputStream;

public class Utils {

    public static DFA dfa;

    public static void initDfa(String regex){
        System.out.println("-------------Starting cflow execution------------\n");


        try {

            ByteArrayInputStream stream = new ByteArrayInputStream((regex + '\n').getBytes());
            System.setIn(stream);

            GrammarParser gp = new GrammarParser(System.in);
            SimpleNode root = gp.Start();

            NFASet parser = new NFASet((SimpleNode) root.jjtGetChild(0), null);
            NFA nfa = parser.convert();
            nfa.getLastState().setAcceptState(true);
            System.out.println(nfa.toString());


            DFA dfa = nfa.getDFA();

        } catch (Throwable e) {
            System.out.println("Invalid REGEX!\n"+ e.getMessage());
            System.exit(1);
        }



        System.out.println("-------------User Code------------\n");
    }

    public static void dfaStatistics(){
        System.out.println("-------------Ended cflow execution------------\n");

        if(Utils.dfa.onAcceptState())
            System.out.println("Regex acepted");
        else
            System.out.println("Regex not acepted");
    }



    //get the range based on regex operator
    public static Pair getOperatorRange(String operator){
        Integer lower,upper;

        switch (operator){
            case "*":
                lower = 0;
                upper = null;
                break;
            case "+":
                lower = 1;
                upper = null;
                break;
            case "?":
                lower = 0;
                upper = 1;
                break;
            default:
                if(operator.length() == 1){//B{3}
                    lower= Character.getNumericValue(operator.charAt(0));
                    upper= lower;
                }else if(operator.length() == 2){//B{1,}
                    lower= Character.getNumericValue(operator.charAt(0));
                    upper= null;
                }else{//B{1,3}
                    lower= Character.getNumericValue(operator.charAt(0));
                    upper= Character.getNumericValue(operator.charAt(2));
                }
        }

        return  new Pair(lower,upper);
    }


}
