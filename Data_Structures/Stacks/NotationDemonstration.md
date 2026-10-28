# Stacks, expressions and notation

## 1. Why a stack?

A stack implements **last in, first out** (LIFO): `push` adds at the top, `pop` removes the top, and `peek` reads it. Parentheses nest in the same way: the last unmatched opening parenthesis is the first one closed. A stack also remembers pending operators while later operands arrive.

`StackArrayAndLinkedList.java` implements both a fixed-size array stack and a linked stack. The array stack keeps a `top` index that starts at −1 (empty); `push` adds 1 to `top` and stores the value, and `isFull()` reports when the last slot is used. Linked pushes create one node and update the top pointer. Push, pop and peek are $O(1)$ for both. Popping or peeking an empty stack throws an error rather than returning a value that could be mistaken for a real element.

`ExpressionEvaluator.java` uses the simplest kind of stack of all: a plain array and a `top` index, with no separate stack class.

## 2. Three ways to write one expression

| Notation | Operator position | Example |
|---|---|---|
| Infix | Between operands | `(8 - 3) * (2 + 4)` |
| Prefix / Polish | Before operands | `* - 8 3 + 2 4` |
| Postfix / Reverse Polish | After operands | `8 3 - 2 4 + *` |

All evaluate to $30$. Prefix and postfix need no parentheses when operators have known arity. Multi-digit numbers require token separators: `12 3 +` is unambiguous; `123+` is not. Parentheses in infix override precedence. Subtraction/division are left-associative, so `8-3-2` means `(8-3)-2`. Exponentiation in this implementation is right-associative: `2^3^2 = 2^(3^2) = 512`. The caret is this evaluator's exponent operator, not Java's XOR operator.

## 3. Infix-to-postfix: shunting-yard algorithm

Maintain an output sequence and an operator stack:

1. Append each operand to output immediately.
2. Push `(`.
3. For `)`, pop operators to output until `(`; remove the opening parenthesis. If none exists, reject the expression.
4. For a binary operator, pop operators with higher precedence; also pop equal-precedence operators when the incoming operator is left-associative. Then push the incoming operator.
5. At end, pop remaining operators. Any remaining parenthesis is an error.

The implementation reports unmatched brackets and invalid characters. In the code, the precedence test in step 4 lives in its own small method, `mustPopFirst(onStack, incoming)`.

### Worked conversion: `3 + 4 * 2`

Stacks below are shown bottom to top.

| Token | Action | Operator stack | Output |
|---|---|---|---|
| `3` | Emit operand | empty | `3` |
| `+` | Push | `+` | `3` |
| `4` | Emit operand | `+` | `3 4` |
| `*` | Higher precedence; push | `+ *` | `3 4` |
| `2` | Emit operand | `+ *` | `3 4 2` |
| End | Pop `*`, then `+` | empty | `3 4 2 * +` |

Each token is pushed and popped at most once: $\Theta(L)$ time and $O(L)$ storage for expression length $L$.

### Unary signs (not supported)

The simplified evaluator does **not** support a leading minus such as `-2` or `3 * -4`. A `-` is always read as subtraction, so `-2` is reported as an error (`not enough numbers for -`). To use a negative number, write it as a subtraction, e.g. `(0 - 2)`.

## 4. Infix-to-prefix using stacks

The implementation first produces valid postfix, then reads it left to right with a stack of **strings**:

1. Operand: push it.
2. Binary operator: pop the **right** part, then the **left** part, and push the single string `"operator left right"`.
3. At the end, the one string left on the stack is the prefix expression.

For `8 3 - 2 4 + *`, the stack evolves from `8`, to `8, 3`, to `- 8 3`, to `- 8 3, 2, 4`, to `- 8 3, + 2 4`, and finally to `* - 8 3 + 2 4`.

This is the same idea as building an expression tree, but each "subtree" is kept as a ready-made string, so no tree class is needed. Joining strings repeatedly copies them, which can cost more than $O(L)$ for very long expressions, but for classroom-sized expressions it is the clearest method.

Prefix evaluation, if desired, scans right-to-left: push operands; for a binary operator pop the left operand first, then the right, compute and push. For `* - 8 3 + 2 4`, scanning from the right first forms `2+4=6`, then `8-3=5`, then `5*6=30`.

## 5. Postfix evaluation: complete trace

For `8 3 - 2 4 + *`, scan left-to-right. A binary operator pops the right operand **before** the left operand.

| Token | Operation | Value stack, bottom to top |
|---|---|---|
| `8` | Push 8 | `8` |
| `3` | Push 3 | `8, 3` |
| `-` | $8-3=5$ | `5` |
| `2` | Push 2 | `5, 2` |
| `4` | Push 4 | `5, 2, 4` |
| `+` | $2+4=6$ | `5, 6` |
| `*` | $5\times6=30$ | `30` |

At completion there must be exactly one value. An operator with too few operands indicates underflow; multiple leftover values indicate missing operators. Swapping pop order silently breaks subtraction and division.

## 6. Running the implementation

```sh
javac ExpressionEvaluator.java
java ExpressionEvaluator
java ExpressionEvaluator "(8 - 3) * (2 + 4)"
java ExpressionEvaluator "2^3^2"
```

The default demonstration gives postfix `3 4 2 * 1 5 - 2 ^ / +` and result `3.5`. `(8 - 3) * (2 + 4)` gives `30.0` and `2^3^2` gives `512.0`. Numbers can be whole or decimal (`3.5`). Unary signs, variable names, scientific notation, functions and implicit multiplication (`2(3+4)`) are not supported.

Arithmetic uses `double`, so decimal roundoff is possible. Division by zero is reported as an error. `Math.pow` supplies the exponent results, including `0^0 = 1`.

## 7. List and queue comparison for revision

| Structure | Efficient operations | Main invariant |
|---|---|---|
| Singly linked list with tail | Insert ends, remove head: $O(1)$ | Tail points to null |
| Doubly linked list | Insert/remove ends: $O(1)$ | Neighbor links agree |
| Singly circular list with tail | Insert ends, remove head, ring merge: $O(1)$ | Tail links to head |
| Array/linked stack | Pop/peek: $O(1)$ | Only the top is removed |
| Circular array queue | Enqueue/dequeue/peek: $O(1)$ | Count distinguishes full from empty |
| Linked queue with head/tail | Enqueue/dequeue/peek: $O(1)$ | Empty implies both pointers null |

Search is $O(n)$ in the lists. A singly circular list still needs $O(n)$ to remove the tail because it must locate the predecessor. A queue is FIFO, not LIFO; after enqueuing 10,20,30, the first removal is 10. For circular array capacity $c$, the next insertion slot is $(front+size) \bmod c$. The linked queue has no fixed limit, so it has no `isFull()`.

### Multiplier-list trace

For LSD-first `9 -> 9 -> null` and $k=5$:

| Input digit | Carry in | $digit\times k+carry$ | Output digit | Carry out |
|---|---:|---:|---:|---:|
| 9 | 0 | 45 | 5 | 4 |
| 9 | 4 | 49 | 9 | 4 |
| End | 4 | 4 | 4 | 0 |

Result: `5 -> 9 -> 4 -> null`, representing $495$. The whole number is never packed into an `int` or `long`; only one digit times k (plus the carry) is ever held in a variable.
