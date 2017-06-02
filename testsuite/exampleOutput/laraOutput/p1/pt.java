

package p1;

import p2.foo;

public class pt {
    public static void main(String[] args) throws Exception {
        cflow.utils.Utils.initDfa("F2*");
        System.out.println("Test Program App");
        foo.test();
        cflow.utils.Utils.dfaStatistics();
    }
}

