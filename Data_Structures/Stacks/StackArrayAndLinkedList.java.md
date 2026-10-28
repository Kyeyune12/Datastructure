# StackArrayAndLinkedList.java

Two implementations of a **stack** (last in, first out) with the same operations: one stores items in a fixed-size array, the other in a linked list.

**Language:** Java · **Related:** [ExpressionEvaluator.java](ExpressionEvaluator.java.md) uses stacks · [QueueArrayAndLinkedList.java](../Queues/QueueArrayAndLinkedList.java.md) is the first-in, first-out counterpart

## How to run

```sh
javac StackArrayAndLinkedList.java
java StackArrayAndLinkedList
```

## The operations both stacks provide

`push(v)`, `pop()`, `peek()` and `isEmpty()`. The array stack also has `isFull()`.

- `pop()` or `peek()` on an empty stack throws `IllegalStateException("stack is empty")`.
- `push` on a full array stack throws `IllegalStateException("stack is full")`.

## `ArrayStack`: array-backed

```
index:  [0]  [1]  [2]  [3]  [4]
data:   10   20   30
                   ▲
                 top = 2
```

- `top` is the index of the top item. It starts at **−1**, which means the stack is empty.
- `push`: add 1 to `top`, then store the value at `data[top]`.
- `pop`: read `data[top]`, then subtract 1 from `top`. The old value is left in the array and is simply overwritten by the next push.
- `isFull()` is true when `top == data.length - 1`, meaning the last slot is used.

## `LinkedStack`: linked-list-backed

```
top ──► [30] ──► [20] ──► [10] ──► null
```

- `push` creates a new node pointing down at the old top, and the new node becomes `top`.
- `pop` moves `top` down to `top.next`.
- It has no fixed size, so there is no `isFull()`.

## Complexity

| Operation | ArrayStack | LinkedStack |
| --- | --- | --- |
| `push`, `pop`, `peek` | O(1) | O(1) |
| Space | O(capacity), reserved up front | O(n) nodes, plus one link per item |

## Which one to use?

| | ArrayStack | LinkedStack |
| --- | --- | --- |
| Size limit | Yes, fixed when created | No |
| Memory per item | One `int` (plus any unused slots) | One `int` plus a link plus the object overhead |
| Speed | Faster in practice: items sit next to each other in memory | Slightly slower: nodes are scattered |

## Output

```
Peek: 30, 30
30 = 30
20 = 20
10 = 10
```

The demo pushes 10, 20 and 30 onto both stacks. Both then pop in the same LIFO order: 30, 20, 10.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.NoSuchElementException;` | `IllegalStateException` | It is in `java.lang`, so no import is needed |
| The array **doubled in size** when full (amortised O(1) push) | The array has a **fixed capacity**, and `isFull()` tells you when it is full | The classic textbook array stack; easier to explain |
| Tracked `size`, with the top item at `data[size - 1]` | Tracks `top`, starting at −1 | Matches how stacks are usually taught |
| `pop` reset the used slot to 0 | Removed | The slot is overwritten by the next push anyway |
| `size()` and `clear()` methods; `isFull()` on the linked stack | Removed | Not used by the demo; a linked stack is never full |
| Short tricks like `data[size++] = value` and `data[--size] = 0` | Separate steps: `top = top + 1;` then `data[top] = value;` | No hidden "change, then use" inside one expression |
| The demo array started at capacity 2 to show it growing | Capacity 5 | The array no longer grows |
