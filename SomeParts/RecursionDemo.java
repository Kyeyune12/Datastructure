public class RecursionDemo {

    // Situation: Calculating factorial for assigning priority slots
    static int factorial(int n) {
        // BASE CASE: 0! is 1, stop recursion
        if (n == 0) {
            return 1;
        }
        
        // RECURSIVE STEP: n * factorial of the remaining sub-problem
        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        System.out.println("--- Java Recursion Demo ---");
        int result = factorial(4); // 4 * 3 * 2 * 1
        System.out.println("Factorial of 4 is: " + result);
    }
}
