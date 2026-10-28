/**
 * javac ExpressionEvaluator.java && java ExpressionEvaluator "3 + 4 * 2 / (1 - 5) ^ 2"
 *
 * Uses stacks to:
 *   1. convert infix   3 + 4 * 2   to postfix   3 4 2 * +
 *   2. convert infix   3 + 4 * 2   to prefix    + 3 * 4 2
 *   3. evaluate the postfix expression to get a number
 *
 * Supports numbers (like 12 or 3.5), + - * / ^ and parentheses.
 * ^ means "to the power of" and is worked out right to left:
 *   2 ^ 3 ^ 2  =  2 ^ (3 ^ 2)  =  2 ^ 9  =  512
 *
 * Each stack here is just an array plus a 'top' index (-1 = empty).
 */
public class ExpressionEvaluator {

    // true for the five operators we support
    static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // Which operator is done first? Higher number = done first.
    static int precedence(char op) {
        if (op == '^') {
            return 3;
        }
        if (op == '*' || op == '/') {
            return 2;
        }
        if (op == '+' || op == '-') {
            return 1;
        }
        return 0;   // '(' has the lowest precedence
    }

    // true if the text is a single operator, like "+"
    static boolean isOperatorToken(String token) {
        return token.length() == 1 && isOperator(token.charAt(0));
    }

    // ================= 1. Infix -> Postfix =================
    // Rules (the "shunting-yard" algorithm):
    //   - a number goes straight to the output
    //   - '(' is pushed on the stack
    //   - ')' pops operators to the output until the matching '('
    //   - an operator first pops any operators that must be done before it,
    //     then is pushed on the stack
    //   - at the end, pop all remaining operators to the output
    static String infixToPostfix(String infix) {
        String postfix = "";

        char[] stack = new char[infix.length()];   // stack of operators and '('
        int top = -1;

        int i = 0;
        while (i < infix.length()) {
            char c = infix.charAt(i);

            if (c == ' ') {
                // skip spaces
                i++;

            } else if (Character.isDigit(c) || c == '.') {
                // read the whole number, for example "12.5"
                String number = "";
                while (i < infix.length() && (Character.isDigit(infix.charAt(i)) || infix.charAt(i) == '.')) {
                    number = number + infix.charAt(i);
                    i++;
                }
                postfix = postfix + number + " ";

            } else if (c == '(') {
                // push '(' and wait for its ')'
                top++;
                stack[top] = c;
                i++;

            } else if (c == ')') {
                // pop operators until we reach the matching '('
                while (top >= 0 && stack[top] != '(') {
                    postfix = postfix + stack[top] + " ";
                    top--;
                }
                if (top < 0) {
                    throw new IllegalArgumentException("unmatched )");
                }
                top--;   // remove the '(' itself (it is not written to the output)
                i++;

            } else if (isOperator(c)) {
                // pop operators that must be done BEFORE this one:
                //   - ones with higher precedence, or
                //   - ones with equal precedence (left to right), except for ^
                //     which goes right to left
                while (top >= 0 && stack[top] != '(' && mustPopFirst(stack[top], c)) {
                    postfix = postfix + stack[top] + " ";
                    top--;
                }
                top++;
                stack[top] = c;
                i++;

            } else {
                throw new IllegalArgumentException("invalid character: " + c);
            }
        }

        // pop whatever operators are left
        while (top >= 0) {
            if (stack[top] == '(') {
                throw new IllegalArgumentException("unmatched (");
            }
            postfix = postfix + stack[top] + " ";
            top--;
        }

        return postfix.trim();   // remove the extra space at the end
    }

    // Should the operator on the stack be popped before pushing 'incoming'?
    static boolean mustPopFirst(char onStack, char incoming) {
        if (precedence(onStack) > precedence(incoming)) {
            return true;
        }
        if (precedence(onStack) == precedence(incoming) && incoming != '^') {
            return true;
        }
        return false;
    }

    // ================= 2. Infix -> Prefix =================
    // First get the postfix, then read it left to right:
    //   - a number is pushed on the stack
    //   - an operator pops two parts (right, then left)
    //     and pushes "operator left right"
    // At the end the stack holds the full prefix expression.
    static String infixToPrefix(String infix) {
        String[] tokens = infixToPostfix(infix).split(" ");

        String[] stack = new String[tokens.length];
        int top = -1;

        for (String token : tokens) {
            if (isOperatorToken(token)) {
                if (top < 1) {
                    throw new IllegalArgumentException("not enough numbers for " + token);
                }

                String right = stack[top];
                top--;
                String left = stack[top];
                top--;

                top++;
                stack[top] = token + " " + left + " " + right;
            } else {
                top++;
                stack[top] = token;
            }
        }

        // a correct expression leaves exactly one string on the stack
        if (top != 0) {
            throw new IllegalArgumentException("invalid expression");
        }
        return stack[top];
    }

    // ================= 3. Evaluate Postfix =================
    // Read the postfix left to right:
    //   - a number is pushed on the stack
    //   - an operator pops two numbers (right, then left),
    //     works out "left operator right" and pushes the answer
    // At the end the stack holds exactly one number: the result.
    static double evaluatePostfix(String postfix) {
        String[] tokens = postfix.split(" ");

        double[] stack = new double[tokens.length];
        int top = -1;

        for (String token : tokens) {
            if (isOperatorToken(token)) {
                if (top < 1) {
                    throw new IllegalArgumentException("not enough numbers for " + token);
                }

                double right = stack[top];
                top--;
                double left = stack[top];
                top--;

                double result = calculate(left, token.charAt(0), right);

                top++;
                stack[top] = result;
            } else {
                top++;
                stack[top] = Double.parseDouble(token);
            }
        }

        if (top != 0) {
            throw new IllegalArgumentException("invalid expression");
        }
        return stack[top];
    }

    // Works out one operation, for example calculate(6, '/', 2) = 3
    static double calculate(double left, char op, double right) {
        if (op == '+') {
            return left + right;
        }
        if (op == '-') {
            return left - right;
        }
        if (op == '*') {
            return left * right;
        }
        if (op == '/') {
            if (right == 0) {
                throw new ArithmeticException("division by zero");
            }
            return left / right;
        }
        return Math.pow(left, right);   // op == '^'
    }

    public static void main(String[] args) {
        // use the expression typed on the command line, or a default one
        String infix;
        if (args.length == 0) {
            infix = "3 + 4 * 2 / (1 - 5) ^ 2";
        } else {
            infix = args[0];
        }

        try {
            String postfix = infixToPostfix(infix);
            System.out.println("Infix:   " + infix);
            System.out.println("Postfix: " + postfix);
            System.out.println("Prefix:  " + infixToPrefix(infix));
            System.out.println("Result:  " + evaluatePostfix(postfix));
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
