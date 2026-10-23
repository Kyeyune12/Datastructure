# adts.c

A **fixed-size stack** stored in an array, the first abstract data type (ADT) in this folder. It provides `push`, `pop`, `peek` and an "is empty" check, plus a short demo.

**Language:** C · **Linked-list stack in this folder:** [trial.c](trial.c.md) · **Fuller Java version:** [StackArrayAndLinkedList.java](../Data_Structures/Stacks/StackArrayAndLinkedList.java.md)

## How to run

```sh
gcc -o adts adts.c
./adts
```

## How the stack is stored

```c
#define MAX 5
struct Stack {
    int arr[MAX];
    int top;        /* index of the top item; -1 means empty */
};
```

```
arr:  [12][10][13][  ][  ]
                ▲
               top = 2
```

## Operations

| Function | What it does | Time |
| --- | --- | --- |
| `push(&s, v)` | If `top == MAX - 1`, prints "Stack overflow!" and does nothing. Otherwise stores `v` at `arr[++top]` | O(1) |
| `pop(&s)` | If empty, prints "Stack underflow!" and returns −1. Otherwise returns `arr[top--]` | O(1) |
| `peekStack(&s)` | Returns the top item without removing it, or −1 if the stack is empty | O(1) |
| `isStackEmpty(&s)` | `top == -1` | O(1) |

The stack is passed **by pointer** (`struct Stack *s`), so the functions change the caller's stack and not a copy of it.

## Output

```
***Welcome to ADTS***
top item (Peek): 13
Popped: 13

top item (Peek): 10
```

The demo pushes 12, 10 and 13. Peek shows 13 (last in), pop removes 13 (first out), and peek then shows 10.

## Problems to fix

| Problem | Fix |
| --- | --- |
| `void main()` isn't standard C (gcc `-Wall` warns that `main` should return `int`) | `int main(void) { … return 0; }` |
| `pop` and `peekStack` return −1 for "empty", but −1 could also be a real value on the stack | Return a `bool` success flag and pass the value back through a pointer, e.g. `bool pop(struct Stack *s, int *out)` |
| The capacity is fixed at 5 | Fine for a demo. Use `malloc`/`realloc` or a linked list for a stack that can grow |
| `s.top = -1` must be set by hand before use | Add an `initStack(struct Stack *s)` function |
