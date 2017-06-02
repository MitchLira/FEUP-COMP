

package p2;


public class foo {
    public static int test() {
        int i = 0;
        /* for(i = 0; i < 10; i++){
        //@BasicBlock F1
        if( (i % 2) == 0){
        //@BasicBlock T
        System.out.println("Even!");
        }else{
        //@BasicBlock E
        System.out.println("Odd!");
        }
        
        //@BasicBlock F2
        continue;
        }
         */
        // @BasicBlock F2
        cflow.logic.Utils.dfa.transition("F2");
        return 0;
    }
}

