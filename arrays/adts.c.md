# adts.c

A **fixed-size stack** stored in an array: the team's first abstract data type (ADT), written in plain C. It has `push`, `pop`, `peekStack` and `isStackEmpty`, plus a short demo. This file sets the **house style** that the other C files in this repo follow.

**Queue in the same style:** [queue.c](queue.c.md) · **Linked-list stack:** [mountTracker.c](../Data_Structures/Stacks/Search_and_Rescue_Backtrack/mountTracker.c.md) · **Java version:** [StackArrayAndLinkedList.java](../Data_Structures/Stacks/StackArrayAndLinkedList.java.md)

## How to run

```sh
gcc adts.c -o adts
./adts
```

## How the stack is stored

```c
#define MAX 5

struct Stack {
    int arr[MAX];
    int top;      // index of the top item, -1 = empty
};
```

| Function | What it does |
| --- | --- |
| `push(s, value)` | If `top == MAX - 1`, prints `Stack overflow!`. Otherwise `s->arr[++(s->top)] = value` (move `top` up, then store) |
| `pop(s)` | If `top == -1`, prints `Stack underflow!` and returns -1. Otherwise returns `s->arr[(s->top)--]` (read, then move `top` down) |
| `peekStack(s)` | The top value without removing it, or -1 if empty |
| `isStackEmpty(s)` | `top == -1` |

Every function takes `struct Stack* s` (a pointer) so it changes the caller's stack, not a copy. `s->top` means "the `top` field of the struct that `s` points to".

## Output

```
***Welcome to ADTS***
top item (Peek): 13
Popped: 13

top item (Peek): 10
```

## The house style (what to copy when you write new C code)

- `#define` for sizes, and `struct Name { ... };` without `typedef`.
- Functions that take `struct Name* s` and use `s->field`.
- Error cases print a short message (`Stack overflow!`) and return -1 instead of crashing.
- `int main()` sets up the struct, runs a few calls and `return 0;`.

## Questions you may be asked

- **What is the difference between `++(s->top)` and `(s->top)++`?** Prefix (`++x`) changes `x` first and then uses it; postfix (`x++`) uses the old value and then changes it. Push needs "move up, then store"; pop needs "read, then move down".
- **Why -1 for an empty stack?** Index 0 is a real slot, so "nothing here" needs a value that is not a valid index.
- **What is an ADT?** A type defined by *what it does* (push, pop, peek) rather than *how it is stored*. The same stack ADT is also built with a linked list in mountTracker.c.
