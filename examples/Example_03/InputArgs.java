public class InputArgs {
	public static void main(String[] args) {
		// @BasicBlock A
        String str = args[0];

        if (isPalindrome(str)) {
            // @BasicBlock B1
            System.out.println("Palindrome!");
        } else {
            // @BasicBlock B2
            System.out.println("Not palindrome...");
        }
	}

    public static boolean isPalindrome(String s) {
        int n = s.length();
        for (int i = 0; i < (n/2); ++i) {
            // @BasicBlock C
            if (s.charAt(i) != s.charAt(n - i - 1)) {
                return false;
            }
        }

        return true;
    }
}