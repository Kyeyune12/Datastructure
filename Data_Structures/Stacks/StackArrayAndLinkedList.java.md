# StackArrayAndLinkedList.java

A **stack** is **LIFO** (Last In, First Out): the last item pushed is the first one popped, like a pile of plates. This file builds the same stack **two ways** so they can be compared side by side.

**Related:** [ExpressionEvaluator.java](ExpressionEvaluator.java.md) (stacks in action) · [NotationDemonstration.md](NotationDemonstration.md) · C array stack: [adts.c](../../arrays/adts.c.md)

## How to run

```sh
javac StackArrayAndLinkedList.java
java StackArrayAndLinkedList
```

## The two versions

| | `ArrayStack` | `LinkedStack` |
| --- | --- | --- |
| Storage | `int[] data` plus `top` = index of the top item (`-1` = empty) | Chain of `Node`s; `top` = the top node (`null` = empty) |
| `push` | `top = top + 1; data[top] = value;` | `top = new Node(value, top);` (new node points down to the old top) |
| `pop` | Read `data[top]`, then `top = top - 1` | Read `top.value`, then `top = top.next` |
| `isFull` | Yes: `top == data.length - 1` | Not needed: it grows until memory runs out |
| Push to a full stack | Prints `Stack overflow!` | Never full |
| Pop from an empty stack | Prints `Stack underflow!`, returns -1 | Same |

`push`, `pop` and `peek` are **O(1)** in both versions.

## Output

```
*** Stack: array vs linked list ***
Peek: 30, 30
30 = 30
20 = 20
10 = 10
Stack underflow!
```

They went in as 10, 20, 30 and came out as 30, 20, 10: LIFO.

## Questions you may be asked

- **Why does `pop` in the array version not clear the slot?** The slot is outside the stack once `top` moves down, and the next `push` overwrites it.
- **Array or linked list?** The array is simpler and uses less memory per item, but has a fixed size. The linked list never fills up, but each item needs an extra reference.
- **Where are stacks used?** Undo in an editor, the browser Back button, function calls (the "call stack"), checking brackets, and evaluating expressions (see ExpressionEvaluator).
