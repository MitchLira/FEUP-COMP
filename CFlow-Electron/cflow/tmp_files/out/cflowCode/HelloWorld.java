

// default package (CtPackage.TOP_LEVEL_PACKAGE_NAME in Spoon= unnamed package)



// Write your code here!
public class HelloWorld {
    public static void main(String[] args) {
        utils.Utils.initDfa("");
        // @BasicBlock A
        utils.Utils.dfa.transition("A");
        // @BasicBlock Abc
        utils.Utils.dfa.transition("Abc");
        // @BasicBlock Ab123
        utils.Utils.dfa.transition("Ab123");
        // @BasicBlock Bas
        utils.Utils.dfa.transition("Bas");
        // @BasicBlock B
        utils.Utils.dfa.transition("B");
        // @BasicBlock A
        utils.Utils.dfa.transition("A");
        System.out.println("Hello World");
        utils.Utils.dfaStatistics();
    }
}

