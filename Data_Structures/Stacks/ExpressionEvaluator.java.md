# ExpressionEvaluator.java

Converts arithmetic expressions from **infix** (`3 + 4 * 2`) to **postfix** and **prefix**, then evaluates them. It is built entirely on its own linked-list stack; no Java collection classes are used.

**Language:** Java · **Theory and traces:** [NotationDemonstration.md](NotationDemonstration.md) · **Stack basics:** [StackArrayAndLinkedList.java](StackArrayAndLinkedList.java.md)

## How to run

```sh
javac ExpressionEvaluator.java

java ExpressionEvaluator                            # built-in example
java ExpressionEvaluator "-2^2 + 3*(4+5)"           # convert and evaluate your own infix expression
java ExpressionEvaluator --postfix "2 3 2 ^ ^"      # evaluate a postfix expression directly
```

Wrap the whole expression in quotes so the shell passes it as one argument.

## What it supports

- **Numbers:** `42`, `3.5`, `.5`, `1e-3`.
- **Names** such as `x`, which are allowed only when converting, not when evaluating.
- **Operators:** `+ - * / ^`, plus brackets.
- **Unary signs:** a `-` or `+` with nothing on its left, as in `-2` or `3*-x`. In postfix and prefix output they are written as **`neg`** and **`pos`**.
- **Precedence**, from highest to lowest: `^`, then the unary signs, then `* /`, then `+ -`.
  - `^` is **right-associative**: `2^3^2 = 2^(3^2) = 512`.
  - `^` binds tighter than a leading minus: `-2^2 = -(2^2) = -4`. This matches normal maths.

It does not support implicit multiplication (`2x`) or functions (`sin`).

## How it is built

| Part | What it does |
| --- | --- |
| `Stack<T>` *(private)* | A hand-written generic linked stack: `push`, `pop`, `peek`, `isEmpty`, `size`. Popping an empty stack throws an error |
| `Token` *(private)* | A singly linked list of token strings |
| `tokens(expr)` | Splits an infix string into tokens with a regular expression: numbers, names, operators and brackets. It rejects invalid characters and the reserved words `neg`/`pos` |
| `infixToPostfix(expr)` | The **shunting-yard** algorithm (below) |
| `infixToPrefix(expr)` | Builds an **expression tree** from the postfix, then reads it out in preorder |
| `evaluatePostfix(expr)` | Evaluates postfix using a stack of `Double` values |

### 1. Infix to postfix (shunting-yard)

The code reads tokens left to right, keeping a stack of operators and a flag `needOperand`.

- **Number or name:** write it to the output.
- **`(`:** push it.
- **`)`:** pop operators to the output until the matching `(` is reached, then discard the `(`.
- **Binary operator:** first pop every operator on top of the stack that should be done before it: those with higher precedence, and those with *equal* precedence unless the new operator is `^` (which is right-associative). Then push the new operator.
- **`+` or `-` when an operand was expected:** it is a unary sign, so push `pos` or `neg`.
- **End of input:** pop everything left on the stack. A leftover `(` means the brackets don't match.

`needOperand` is what catches mistakes such as `2 +`, `) 3` or `2 3`, with a clear error message.

### 2. Infix to prefix (with a tree)

1. Read the postfix tokens and build a tree:
   - an operand becomes a leaf;
   - a binary operator pops two subtrees and becomes their parent;
   - `neg`/`pos` pops one subtree.
2. Print the tree in **preorder** (node, then left subtree, then right subtree) using an explicit stack, pushing the right child before the left.

This is a direct use of **depth-first tree traversal**: preorder of an expression tree *is* prefix notation, and postorder is postfix.

### 3. Evaluating postfix

- **Number:** push it.
- **Binary operator:** pop the right operand, then the left (in that order), compute, and push the result.
- **`neg`/`pos`:** pop one value and push the result.
- **At the end:** exactly one value must be left on the stack.

Errors are reported for too few operands, extra operands, division by zero, invalid numbers, and results that are not finite (overflow to ∞ or NaN).

## Output

Built-in example:

```
Infix:   3 + 4 * 2 / (1 - 5)^2
Postfix: 3 4 2 * 1 5 - 2 ^ / +
Prefix:  + 3 / * 4 2 ^ - 1 5 2
Result:  3.5
```

That is 3 + (4·2) / (−4)² = 3 + 8/16 = 3.5.

More runs:

```
$ java ExpressionEvaluator "-2^2 + 3*(4+5)"
Infix:   -2^2 + 3*(4+5)
Postfix: 2 2 ^ neg 3 4 5 + * +
Prefix:  + neg ^ 2 2 * 3 + 4 5
Result:  23.0

$ java ExpressionEvaluator --postfix "2 3 2 ^ ^"
512.0

$ java ExpressionEvaluator "2 +"
Error: empty/incomplete expression
```

## Complexity

For an input of length L, each conversion and the evaluation take **O(L) time and O(L) space**, counting each arithmetic operation as constant time. Every token is pushed and popped at most once.

## Notes

- Arithmetic uses `double`, so results like `0.1 + 0.2` are approximate.
- The program exits with status 1 and prints `Error: …` for any invalid input.
