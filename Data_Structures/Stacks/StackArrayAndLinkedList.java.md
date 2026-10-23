# StackArrayAndLinkedList.java

Two implementations of a **stack** (last in, first out) behind the same set of operations: one stores items in an array that grows, the other in a linked list.

**Language:** Java · **Related:** [ExpressionEvaluator.java](ExpressionEvaluator.java.md) uses a stack · [QueueArrayAndLinkedList.java](../Queues/QueueArrayAndLinkedList.java.md) is the first-in, first-out counterpart

## How to run

```sh
javac StackArrayAndLinkedList.java
java StackArrayAndLinkedList
```

## The operations both stacks provide

`push(v)`, `pop()`, `peek()`, `size()`, `isEmpty()`, `isFull()` and `clear()`. Calling `pop()` or `peek()` on an empty stack throws `NoSuchElementException`.

## `ArrayStack`: array-backed

```
data: [10][20][30][  ]      size = 3     top item = data[size - 1]
```

- `push` writes to `data[size++]`. If the array is full, the code first makes a new array **twice the size**, copies the items across, then pushes.
- `pop` reads `data[--size]` and sets the empty slot back to 0.
- The array **never shrinks**. Shrinking as soon as it got small could cause *thrashing*: grow, shrink, grow again, over and over around one size.

| Operation | Time |
| --- | --- |
| `push` | **Amortised O(1)**. O(n) on the push that triggers a copy |
| `pop`, `peek` | O(1) |
| Space | O(capacity) |

**Why "amortised O(1)":** the copies cost 1 + 2 + 4 + … + n < 2n element moves over n pushes. That averages out to a constant amount of work per push.

## `LinkedStack`: linked-list-backed

```
top ──► [30] ──► [20] ──► [10] ──► null
```

- `push` creates a node pointing at the old top (`top = new Node(v, top)`).
- `pop` moves `top` to `top.next`.

| Operation | Time |
| --- | --- |
| `push`, `pop`, `peek` | **O(1)** every time, with no resizing |
| Space | O(n) nodes, plus one pointer per item |

## Which one to use?

| | ArrayStack | LinkedStack |
| --- | --- | --- |
| Worst-case `push` | O(n), on a resize | O(1) |
| Memory per item | One `int`, plus unused spare capacity | One `int` plus a pointer plus the object header |
| Cache friendliness | Good (items are next to each other in memory) | Poorer (nodes are scattered) |

## Output

```
Peek: 30, 30
30 = 30
20 = 20
10 = 10
```

The demo pushes 10, 20 and 30 onto both stacks. The array stack starts with capacity 2, so pushing 30 makes it grow to 4. Both stacks then pop in the same LIFO order: 30, 20, 10.

## Notes

- `isFull()` is true only at `Integer.MAX_VALUE` items. It does **not** check how much memory is free, so the JVM may run out of memory (`OutOfMemoryError`) well before that.
- Not thread-safe.
