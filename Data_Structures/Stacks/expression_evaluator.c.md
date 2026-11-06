# expression_evaluator.c

The **C version** of [`ExpressionEvaluator.java`](ExpressionEvaluator.java). Converts an infix expression to postfix and prefix and evaluates it, using stacks made of plain arrays and a `top` index. Pass your own expression as the first argument. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra expression_evaluator.c -o expression_evaluator -lm
./expression_evaluator
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

The C version uses fixed buffers (`MAX = 256`) and `strtok` to split tokens; Java uses `String.split`. Whole-number results print as `30.0` to match Java's `double` output.

## Operations

1. **Shunting-yard** (`infix_to_postfix`): numbers go straight to the output; an operator first pops stacked operators that must come before it (`must_pop_first`), then is pushed; `)` pops until `(`.
2. **Prefix** (`infix_to_prefix`): build from the postfix tokens; each operator pops two strings and pushes `op left right`.
3. **Evaluate** (`evaluate_postfix`): numbers are pushed; an operator pops the right then the left operand and pushes the result.

`^` is right-associative (`2^3^2 = 512`). Errors (unmatched brackets, bad character, division by zero, missing operand) set a message that `main` prints as `Error: ...`.

## Output

```
Infix:   3 + 4 * 2 / (1 - 5) ^ 2
Postfix: 3 4 2 * 1 5 - 2 ^ / +
Prefix:  + 3 / * 4 2 ^ - 1 5 2
Result:  3.5
```

## Questions you may be asked

- **Why does `^` not pop an equal `^`?** It is right-associative: `2^3^2` must be `2^(3^2)`.
- **Why pop the RIGHT operand first?** It was pushed last. For `8 3 -` the stack holds 8, 3: popping gives 3 (right) then 8 (left), so the answer is 8-3.
- **What is not supported?** Unary minus; `-3` is rejected rather than guessed.
- **Complexity?** O(n) for each of the three passes.
