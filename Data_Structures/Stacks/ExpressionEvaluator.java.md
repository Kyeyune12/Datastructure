# ExpressionEvaluator.java

Converts arithmetic expressions from **infix** (`3 + 4 * 2`) to **postfix** and **prefix** using stacks, then evaluates the postfix to get a number. Each stack is a plain array plus a `top` index; no Java library classes are used.

**Language:** Java · **Theory and traces:** [NotationDemonstration.md](NotationDemonstration.md) · **Stack basics:** [StackArrayAndLinkedList.java](StackArrayAndLinkedList.java.md)

## How to run

```sh
javac ExpressionEvaluator.java

java ExpressionEvaluator                          # built-in example
java ExpressionEvaluator "2 ^ 3 ^ 2"              # your own expression
java ExpressionEvaluator "(1 + 2) * 3.5"
```

Wrap the expression in quotes so it reaches the program as one piece.

## What it supports

- **Numbers:** whole numbers and decimals, such as `42` or `3.5`.
- **Operators:** `+ - * / ^`, plus brackets `( )`.
- **Precedence**, from highest to lowest: `^`, then `* /`, then `+ -`.
- `^` is **right-associative**: `2 ^ 3 ^ 2 = 2 ^ (3 ^ 2) = 2 ^ 9 = 512`. The other operators work left to right.

It does **not** support negative numbers written with a leading minus (like `-2`), variable names, or functions such as `sin`.

## What is inside

| Method | What it does |
| --- | --- |
| `isOperator(c)` | True for `+ - * / ^` |
| `precedence(op)` | `^` → 3, `* /` → 2, `+ -` → 1, `(` → 0 |
| `isOperatorToken(token)` | True if a piece of text is a single operator, like `"+"` |
| `infixToPostfix(infix)` | The **shunting-yard** algorithm (below) |
| `mustPopFirst(onStack, incoming)` | Decides whether the operator on the stack must be written out before the new one is pushed |
| `infixToPrefix(infix)` | Builds the prefix form from the postfix, using a stack of strings |
| `evaluatePostfix(postfix)` | Works out the answer using a stack of numbers |
| `calculate(left, op, right)` | Does one operation, such as 6 / 2 |

### How the stacks work

Every stack is an array and a `top` index:

```java
char[] stack = new char[infix.length()];
int top = -1;                 // -1 means empty

top++;                        // push c
stack[top] = c;

char c = stack[top];          // pop
top--;
```

### 1. Infix to postfix (shunting-yard)

Read the expression left to right:

- **Space:** skip it.
- **Number:** read all its digits (and any `.`) and write the number to the output.
- **`(`:** push it on the stack.
- **`)`:** pop operators to the output until the matching `(` is reached, then throw the `(` away.
- **Operator:** first pop operators that must be done *before* this one (`mustPopFirst`): those with higher precedence, and those with equal precedence, unless the new operator is `^`. Then push the new operator.
- **End of input:** pop everything left on the stack to the output.

**Trace** for `3 + 4 * 2`:

| Read | Output | Stack |
| --- | --- | --- |
| 3 | `3` | |
| + | `3` | `+` |
| 4 | `3 4` | `+` |
| * | `3 4` | `+ *` (`*` is higher than `+`, so nothing is popped) |
| 2 | `3 4 2` | `+ *` |
| end | `3 4 2 * +` | |

### 2. Infix to prefix

First get the postfix, then read it left to right with a stack of **strings**:

- **Number:** push it.
- **Operator:** pop the right part, then the left part, and push `"operator left right"`.

For `3 4 2 * +`: push `3`, push `4`, push `2`; `*` makes `* 4 2`; `+` makes `+ 3 * 4 2`. The one string left on the stack is the prefix form.

### 3. Evaluating postfix

Same idea, but with a stack of **numbers**:

- **Number:** push it.
- **Operator:** pop the right number, then the left one (the order matters for `-`, `/` and `^`), work out the answer with `calculate`, and push it.
- **At the end:** exactly one number must be left. That is the result.

## Errors

| Input | Message |
| --- | --- |
| `10/(5-5)` | `Error: division by zero` |
| `(1+2` | `Error: unmatched (` |
| `1+2)` | `Error: unmatched )` |
| `2 # 3` | `Error: invalid character: #` |
| `2 +` or `-2` | `Error: not enough numbers for +` (or `-`) |
| `2 3` | `Error: invalid expression` (two numbers with no operator) |

The "not enough numbers" and "invalid expression" checks are in both `infixToPrefix` and `evaluatePostfix`: an operator needs two items on the stack, and a correct expression leaves exactly one item at the end.

## Output

Built-in example:

```
Infix:   3 + 4 * 2 / (1 - 5) ^ 2
Postfix: 3 4 2 * 1 5 - 2 ^ / +
Prefix:  + 3 / * 4 2 ^ - 1 5 2
Result:  3.5
```

That is 3 + (4·2) / (−4)² = 3 + 8/16 = 3.5.

Another run:

```
$ java ExpressionEvaluator "2^3^2"
Infix:   2^3^2
Postfix: 2 3 2 ^ ^
Prefix:  ^ 2 ^ 3 2
Result:  512.0
```

## Complexity

For an expression of length L, each conversion and the evaluation take **O(L) time and O(L) space**, because every token is pushed and popped at most once.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.regex.Pattern`, `Matcher` and `java.util.Objects` | Reads the expression one character at a time | No imports, and every step can be traced by hand |
| A generic linked `Stack<T>` class and a linked `Token` list | Plain arrays with a `top` index | The simplest possible stack |
| Prefix built from an **expression tree**, then printed in preorder | Prefix built from the postfix with a stack of strings | Same answer, far less code |
| Unary signs (`-2`, written `neg`/`pos`), variable names and scientific notation (`1e-3`) | Removed | Keeps the grammar small: numbers, `+ - * / ^` and brackets |
| A `needOperand` flag to catch errors like `2 +` | Removed. Brackets and bad characters are still checked | Shorter code |
| A `--postfix` command-line option | Removed | Only infix input now |
| The precedence test was one long `while` condition | Moved into its own method, `mustPopFirst`, with comments | Easier to read |
| `switch` on the operator inside the evaluator | A separate `calculate` method using `if` statements | Each operation on its own line |
| Code squeezed onto single lines | One statement per line, braces everywhere, and comments explaining each rule | Easier to follow |
