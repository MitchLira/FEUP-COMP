

package p1;

import p2.foo;

public class pt {
    public static void main(String[] args) throws Exception {
        utils.Utils.initDfa("(F1 (T|E)F2{1})* F2T");
        System.out.println("Test Program App");
        foo.test();
        utils.Utils.dfaStatistics();
    }
}

