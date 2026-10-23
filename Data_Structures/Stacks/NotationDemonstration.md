# Stacks, expressions and notation

## 1. Why a stack?

A stack implements **last in, first out** (LIFO): `push` adds at the top, `pop` removes the top, and `peek` reads it. Parentheses nest in the same way: the last unmatched opening parenthesis is the first one closed. A stack also remembers pending operators while later operands arrive.

`StackArrayAndLinkedList.java` implements both a resizable array stack and a linked stack. The array doubles on exhaustion, making pushes amortized $O(1)$ even though an individual resizing push is $O(n)$. Over a doubling sequence, copied cells form a geometric sum $1+2+4+\cdots<2n$. Linked pushes allocate one node and update the top pointer. Pop and peek are $O(1)$ for both. Empty pop/peek throws rather than returning a data value that could be mistaken for a valid element.

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

The implementation also tracks whether an operand is expected, rejecting adjacent operands, empty parentheses and missing operands. This matters: blindly rearranging tokens can turn invalid input into a plausible but wrong expression.

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

### Unary signs

`-` is unary when an operand is expected: at the start, after an operator, or after `(`. Unary operators become explicit tokens `neg` and `pos`. They are pushed without popping earlier operators. Precedence is `^` above unary signs, then `* /`, then `+ -`.

| Infix | Postfix | Result |
|---|---|---:|
| `-2^2` | `2 2 ^ neg` | -4 |
| `(-2)^2` | `2 neg 2 ^` | 4 |
| `2^-3` | `2 3 neg ^` | 0.125 |
| `--5` | `5 neg neg` | 5 |

This avoids a common mistake: treating a unary sign exactly like a binary operator can pop an exponent before its right operand has been read.

## 4. Infix-to-prefix using stacks

The implementation first produces valid postfix, then builds an expression tree using a node stack:

1. Operand: push a leaf.
2. Unary operator: pop one subtree, attach it, push the new node.
3. Binary operator: pop the **right** subtree, then the **left** subtree, attach them, and push the new node.
4. The remaining node is the root. Perform iterative preorder traversal (operator, left, right), pushing right before left so the left child is visited first.

For `8 3 - 2 4 + *`, the stack evolves from leaf `8`, to leaves `8,3`, to subtree `(- 8 3)`, then to that subtree plus `(+ 2 4)`, and finally to `(* (- 8 3) (+ 2 4))`. Preorder yields `* - 8 3 + 2 4`.

This stack-based conversion is $\Theta(L)$ time and $O(L)$ space. Repeatedly concatenating growing subtree strings can produce quadratic copying; storing nodes and emitting once avoids that cost. The familiar “reverse infix, swap parentheses, convert, reverse” trick needs special associativity handling and is more fragile with unary operators. The tree method preserves the parser's decisions explicitly.

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
java ExpressionEvaluator "-2^2 + 3*(4+5)"
java ExpressionEvaluator --postfix "8 3 - 2 4 + *"
```

The default demonstration gives postfix `3 4 2 * 1 5 - 2 ^ / +` and result `3.5`. The fourth expression above gives `23.0`. Numbers can use decimals and scientific notation (`1.5e2`). Conversion supports identifiers such as `price`, but numeric evaluation rejects them because there is no variable environment. `neg` and `pos` are reserved internal names. Functions, implicit multiplication (`2(3+4)`), commas and assignment are unsupported and rejected.

Arithmetic uses `double`, so decimal roundoff is possible and this is not an exact financial calculator. Division by positive or negative zero is rejected. Nonfinite literals/results and invalid real exponentiation results are rejected. `Math.pow` supplies the exponent semantics, including `0^0 = 1`. All operations work on local stacks and do not evaluate executable Java or external code.

## 7. List and queue comparison for revision

| Structure | Efficient operations | Main invariant |
|---|---|---|
| Singly linked list with tail | Insert ends, remove head: $O(1)$ | Tail points to null |
| Doubly linked list | Insert/remove ends: $O(1)$ | Neighbor links agree |
| Singly circular list with tail | Insert ends, remove head, ring merge: $O(1)$ | Tail links to head |
| Array/linked stack | Pop/peek: $O(1)$ | Only the top is removed |
| Circular array queue | Enqueue/dequeue/peek: $O(1)$ | Count distinguishes full from empty |
| Linked queue with head/tail | Enqueue/dequeue/peek: $O(1)$ | Empty implies both pointers null |

Search is $O(n)$ in the lists. A singly circular list still needs $O(n)$ to remove the tail because it must locate the predecessor. A queue is FIFO, not LIFO; after enqueuing 10,20,30, the first removal is 10. For circular array capacity $c$, the next insertion slot is $(head+size)\bmod c$. The linked queue's `isFull()` tests its configured limit, not whether the runtime can promise another allocation.

### Multiplier-list trace

For LSD-first `9 -> 9 -> null` and $k=5$:

| Input digit | Carry in | $digit\times k+carry$ | Output digit | Carry out |
|---|---:|---:|---:|---:|
| 9 | 0 | 45 | 5 | 4 |
| 9 | 4 | 49 | 9 | 4 |
| End | 4 | 4 | 4 | 0 |

Result: `5 -> 9 -> 4 -> null`, representing $495$. A `long` intermediate prevents overflow when multiplying one digit by the largest nonnegative `int` multiplier. The complete large integer is never packed into a primitive numeric type.
