

package p2;


public class foo {
    public static int test() {
        int i = 0;
        for (i = 0; i < 10; i++) {
            // @BasicBlock F1
            utils.Utils.dfa.transition("F1");
            if ((i % 2) == 0) {
                // @BasicBlock T
                utils.Utils.dfa.transition("T");
                System.out.println("Even!");
            }else {
                // @BasicBlock E
                utils.Utils.dfa.transition("E");
                System.out.println("Odd!");
            }
            // @BasicBlock F2
            utils.Utils.dfa.transition("F2");
            continue;
        }
        // @BasicBlock F2
        utils.Utils.dfa.transition("F2");
        return 0;
    }
}

