package utils;

import fa.DFA;
import fa.NFA;
import logic.NFASet;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import parser.GrammarParser;
import parser.SimpleNode;
import weaver.gui.KadabraLauncher;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;

public class Utils {

    public static DFA dfa;
    public static String tree;

    public static void initDfa(String regex)   {
        System.out.println("-------------Starting cflow execution------------\n");



        try {

            ByteArrayInputStream stream = new ByteArrayInputStream((regex + '\n').getBytes());
            System.setIn(stream);

            GrammarParser gp = new GrammarParser(System.in);
            SimpleNode root = gp.Start();

            //Get all the identifiers
            HashSet<String> identifiers = new HashSet<>();
            try (BufferedReader br = new BufferedReader(new FileReader("../identifiers.txt"))) {
                String sCurrentLine;

                while ((sCurrentLine = br.readLine()) != null)
                    identifiers.add(sCurrentLine);

            } catch (IOException e) {
                e.printStackTrace();
            }

            NFASet parser = new NFASet((SimpleNode) root.jjtGetChild(0), null);
            NFA nfa = parser.convert();
            nfa.getLastState().setAcceptState(true);
            nfa.setIdentifiers(identifiers);
            nfa.handleDots();
            nfa.optimize();
            System.out.println(nfa.toString());


            dfa = nfa.getDFA();

            writeToFile("nfa",nfa.toDotFormat());
            writeToFile("dfa",dfa.toDotFormat());



            tree =  parser.dump();;

        } catch (Throwable e) {
            System.out.println("Invalid REGEX!\n"+ e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    public static void writeToFile(String fileName,String content){
        try{
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.println(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void dfaStatistics(){

        try {
            JSONObject obj = new JSONObject();
            obj.put("result", Utils.dfa.onAcceptState());
            obj.put("description",dfa.getDescription());
            obj.put("tree",tree);

            //save the file
            writeToFile("statistics",obj.toString());

            if(Utils.dfa.onAcceptState())
                System.out.println("Regex acepted");
            else
                System.out.println("Regex not acepted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void generateParsedCode(String srcPath, String dstPath) throws IOException{


        File src = new File("cflow/cflow.jar");
        File dst = new File(dstPath + File.separator + src.getName());
        Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING);

        //get the identifiers
        String[] args = new String[5];
        args[0] = "cflow/lara/identifiers.lara";
        args[1] = "-p";
        args[2] = srcPath ;
        args[3] = "-o";
        args[4] = dstPath + "/cflowCode";
        KadabraLauncher.main(args);

        //generate the new code
        args = new String[5];
        args[0] = "cflow/lara/cflow.lara";
        args[1] = "-p";
        args[2] = srcPath ;
        args[3] = "-o";
        args[4] = dstPath + "/cflowCode";
        KadabraLauncher.main(args);

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
