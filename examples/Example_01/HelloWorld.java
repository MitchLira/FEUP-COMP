public class HelloWorld {
	public static void main(String[] args) {
		// @BasicBlock Hello
		System.out.println("Hello World");

        // @BasicBlock Assign
        int a = 3;

        int result = 0;
        for (int i = 0; i < 10; i++) {
            // @BasicBlock Loop
            result += a;
        }

        // @BasicBlock Final
        System.out.println("Result: " + result);
	}
}