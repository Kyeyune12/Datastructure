# stack_array_and_linked_list.c

The **C version** of [`StackArrayAndLinkedList.java`](StackArrayAndLinkedList.java). Two stacks with the same operations: a fixed-size array stack and a linked stack. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra stack_array_and_linked_list.c -o stack_array_and_linked_list
./stack_array_and_linked_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

| Operation | Array stack | Linked stack |
|---|---|---|
| `push` | `data[++top] = v` (overflow check) | new node becomes `top` |
| `pop` | `data[top--]` (underflow check) | `top` moves down, old node freed |
| `peek` | `data[top]` | `top->value` |

All three are O(1) for both.

## Output

```
*** Stack: array vs linked list ***
Peek: 30, 30
30 = 30
20 = 20
10 = 10
Stack underflow!
```

## Questions you may be asked

- **When overflow?** Only the array stack can overflow (fixed capacity). The linked stack is limited only by memory.
- **Why `top = -1` for empty?** Index 0 is a valid slot, so -1 is the first value that cannot be one.
- **Where would you use a stack?** Undo, function calls, bracket matching, expression evaluation, backtracking.
