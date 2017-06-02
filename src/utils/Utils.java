package utils;

public class Utils {
    
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
