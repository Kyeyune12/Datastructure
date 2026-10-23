import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * javac ExpressionEvaluator.java && java ExpressionEvaluator "-2^2 + 3*(4+5)"
 * Or: java ExpressionEvaluator --postfix "2 3 2 ^ ^"
 * Infix operands: decimal/scientific numbers or identifiers. Operators:
 * + - * / ^, parentheses and unary signs. ^ is exponentiation, right-associative.
 * Exponentiation binds more tightly than a leading sign: -2^2 = -(2^2).
 * Unary signs on exponent operands are allowed: 2^-3 = 0.125.
 * Postfix/prefix tokens are space-delimited; unary operators are neg and pos.
 * Conversion accepts identifiers; numeric evaluation rejects unbound identifiers.
 * Double arithmetic is approximate; invalid grammar, zero division and nonfinite
 * inputs/results raise exceptions. No implicit multiplication or functions.
 * Each conversion/evaluation is O(L) time and O(L) space for input length L,
 * treating primitive arithmetic as constant time. Iterative prefix tree walk
 * avoids recursion and repeated concatenation. All stacks and token lists use custom nodes; no Java collection classes.
 * Standard regex handles lexical recognition only, and StringBuilder formats text.
 */
public final class ExpressionEvaluator {
    private ExpressionEvaluator() { }
    /** Hand-written linked stack: no collection classes or hidden resizing. */
    private static final class Stack<T> {
        private static final class Link<T> {
            final T value; final Link<T> next;
            Link(T value, Link<T> next) { this.value=value; this.next=next; }
        }
        private Link<T> top;
        private int count;
        void push(T value) { top=new Link<T>(value,top); count++; }
        T peek() {
            if (top==null) throw new IllegalArgumentException("stack underflow");
            return top.value;
        }
        T pop() { T value=peek(); top=top.next; count--; return value; }
        boolean isEmpty() { return top==null; }
        int size() { return count; }
    }
    /** A custom singly linked token sequence, built with explicit tail links. */
    private static final class Token {
        final String text; Token next;
        Token(String text) { this.text=text; }
    }
    private static void emit(StringBuilder out, String token) {
        if (out.length()>0) out.append(' ');
        out.append(token);
    }
    private static Token whitespaceTokens(String text) {
        Token dummy=new Token(""),tail=dummy;
        int i=0;
        while (i<text.length()) {
            while (i<text.length() && Character.isWhitespace(text.charAt(i))) i++;
            int start=i;
            while (i<text.length() && !Character.isWhitespace(text.charAt(i))) i++;
            if (i>start) { tail.next=new Token(text.substring(start,i)); tail=tail.next; }
        }
        return dummy.next;
    }
    private static final Pattern TOKEN=Pattern.compile(
        "(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?|[A-Za-z_][A-Za-z_0-9]*|[()+*/^\\-]");
    private static final Pattern NUMBER=Pattern.compile(
        "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?");
    private static boolean unary(String s) { return s.equals("neg") || s.equals("pos"); }
    private static boolean binary(String s) { return s.length()==1 && "+-*/^".contains(s); }
    private static int precedence(String s) {
        if (s.equals("^")) return 4;
        if (unary(s)) return 3;
        if (s.equals("*") || s.equals("/")) return 2;
        if (s.equals("+") || s.equals("-")) return 1;
        return 0;
    }
    private static Token tokens(String expression) {
        Objects.requireNonNull(expression,"expression");
        Token dummy=new Token(""),tail=dummy; Matcher matcher=TOKEN.matcher(expression);
        int position=0;
        while (position<expression.length()) {
            if (Character.isWhitespace(expression.charAt(position))) { position++; continue; }
            matcher.region(position,expression.length());
            if (!matcher.lookingAt()) throw new IllegalArgumentException("invalid token at "+position);
            String token=matcher.group();
            if (unary(token)) throw new IllegalArgumentException("reserved identifier: "+token);
            tail.next=new Token(token); tail=tail.next; position=matcher.end();
        }
        return dummy.next;
    }
    public static String infixToPostfix(String expression) {
        StringBuilder out=new StringBuilder(); Stack<String> ops=new Stack<>();
        boolean needOperand=true;
        for (Token token=tokens(expression); token!=null; token=token.next) {
            String t=token.text;
            if (t.equals("(")) {
                if (!needOperand) throw new IllegalArgumentException("missing operator before (");
                ops.push(t);
            } else if (t.equals(")")) {
                if (needOperand) throw new IllegalArgumentException("missing operand before )");
                while (!ops.isEmpty() && !ops.peek().equals("(")) emit(out,ops.pop());
                if (ops.isEmpty()) throw new IllegalArgumentException("unmatched )");
                ops.pop(); needOperand=false;
            } else if (binary(t)) {
                if (needOperand) {
                    if (!t.equals("+") && !t.equals("-"))
                        throw new IllegalArgumentException("operator needs left operand");
                    ops.push(t.equals("-")?"neg":"pos");
                } else {
                    while (!ops.isEmpty() && !ops.peek().equals("(")
                        && (precedence(ops.peek())>precedence(t)
                        || (precedence(ops.peek())==precedence(t) && !t.equals("^"))))
                        emit(out,ops.pop());
                    ops.push(t); needOperand=true;
                }
            } else {
                if (!needOperand) throw new IllegalArgumentException("missing operator");
                emit(out,t); needOperand=false;
            }
        }
        if (needOperand) throw new IllegalArgumentException("empty/incomplete expression");
        while (!ops.isEmpty()) {
            String op=ops.pop();
            if (op.equals("(")) throw new IllegalArgumentException("unmatched (");
            emit(out,op);
        }
        return out.toString();
    }
    private static final class Node {
        final String token; final Node left,right;
        Node(String token, Node left, Node right) { this.token=token; this.left=left; this.right=right; }
    }
    public static String infixToPrefix(String expression) {
        Stack<Node> nodes=new Stack<>();
        for (Token token=whitespaceTokens(infixToPostfix(expression)); token!=null; token=token.next) {
            String t=token.text;
            if (unary(t)) nodes.push(new Node(t,nodes.pop(),null));
            else if (binary(t)) {
                Node right=nodes.pop(),left=nodes.pop(); nodes.push(new Node(t,left,right));
            } else nodes.push(new Node(t,null,null));
        }
        Stack<Node> work=new Stack<>(); work.push(nodes.pop());
        StringBuilder out=new StringBuilder();
        while (!work.isEmpty()) {
            Node n=work.pop(); if (out.length()>0) out.append(' '); out.append(n.token);
            if (n.right!=null) work.push(n.right);
            if (n.left!=null) work.push(n.left);
        }
        return out.toString();
    }
    public static double evaluatePostfix(String expression) {
        Objects.requireNonNull(expression,"expression");
        if (expression.trim().isEmpty()) throw new IllegalArgumentException("empty postfix");
        Stack<Double> values=new Stack<>();
        for (Token token=whitespaceTokens(expression); token!=null; token=token.next) {
            String t=token.text;
            double result;
            if (unary(t)) {
                if (values.isEmpty()) throw new IllegalArgumentException("unary underflow");
                result=values.pop(); if (t.equals("neg")) result=-result;
            } else if (binary(t)) {
                if (values.size()<2) throw new IllegalArgumentException("binary underflow");
                double right=values.pop(),left=values.pop();
                switch (t) {
                    case "+": result=left+right; break;
                    case "-": result=left-right; break;
                    case "*": result=left*right; break;
                    case "/":
                        if (right==0.0) throw new ArithmeticException("division by zero");
                        result=left/right; break;
                    case "^": result=Math.pow(left,right); break;
                    default: throw new IllegalArgumentException("unknown operator");
                }
            } else {
                if (!NUMBER.matcher(t).matches()) throw new IllegalArgumentException("not a number: "+t);
                result=Double.parseDouble(t);
            }
            if (!Double.isFinite(result)) throw new ArithmeticException("nonfinite value/result");
            values.push(result);
        }
        if (values.size()!=1) throw new IllegalArgumentException("extra operands");
        return values.pop();
    }
    public static void main(String[] args) {
        try {
            if (args.length==2 && args[0].equals("--postfix")) {
                System.out.println(evaluatePostfix(args[1])); return;
            }
            if (args.length>1) throw new IllegalArgumentException("quote the complete expression");
            String infix=args.length==0?"3 + 4 * 2 / (1 - 5)^2":args[0];
            String postfix=infixToPostfix(infix);
            System.out.println("Infix:   "+infix);
            System.out.println("Postfix: "+postfix);
            System.out.println("Prefix:  "+infixToPrefix(infix));
            System.out.println("Result:  "+evaluatePostfix(postfix));
        } catch (IllegalArgumentException | ArithmeticException ex) {
            System.err.println("Error: "+ex.getMessage()); System.exit(1);
        }
    }
}
