# QueueArrayAndLinkedList.java

Two implementations of a **queue** (first in, first out) with the same operations: a circular array and a linked list. Both enqueue, dequeue and peek in O(1).

**Language:** Java · **Related:** [StackArrayAndLinkedList.java](../Stacks/StackArrayAndLinkedList.java.md), the last-in, first-out counterpart

## How to run

```sh
javac QueueArrayAndLinkedList.java
java QueueArrayAndLinkedList
```

## The operations both queues provide

`enqueue(v)`, `dequeue()`, `peek()` and `isEmpty()`. The array queue also has `isFull()`.

- `dequeue()` or `peek()` on an empty queue throws `IllegalStateException("empty queue")`.
- `enqueue` on a full array queue throws `IllegalStateException("full queue")`.

## `CircularArrayQueue`: fixed capacity

```
capacity 3, after enqueue 10, 20, 30, dequeue, enqueue 40:

index:  [0]  [1]  [2]
data:   40   20   30
              ▲
           front = 1      size = 3   (40 went into slot (front + size) % capacity = (1 + 2) % 3 = 0)
```

- It keeps **`front`** (the position of the oldest item) and **`size`**.
- The back of the queue is `(front + size) % capacity`. The `%` makes the position **wrap around** to the start of the array, so space freed at the front is reused and items never have to be shifted.
- `dequeue` reads `data[front]`, then moves `front = (front + 1) % capacity`.
- Keeping `size` is how the queue tells **empty** (`size == 0`) from **full** (`size == capacity`).

## `LinkedQueue`: no fixed limit

```
head ──► [20] ──► [30] ──► [40] ──► null
                             ▲
                            tail
```

- **Enqueue at `tail`, dequeue at `head`.** Both are O(1) because the queue keeps both links.
- When the **last** item is removed, `tail` is set back to `null` too. Otherwise it would still point at a node that has left the queue.

## Complexity

| | CircularArrayQueue | LinkedQueue |
| --- | --- | --- |
| `enqueue`, `dequeue`, `peek` | O(1) | O(1) |
| Storage | O(capacity), reserved up front | O(size), grows and shrinks |
| Can grow? | No | Yes |

## Output

```
Array queue full: true
10 = 10
Peek: 20, 20
20 = 20
30 = 30
40 = 40
```

The demo puts 10, 20 and 30 into both queues, which fills the array queue (capacity 3). It dequeues 10, then enqueues 40, which in the array queue **wraps around** into slot 0. Then it empties both queues, and both give back the items in the same FIFO order.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.NoSuchElementException;` | `IllegalStateException` | It is in `java.lang`, so no import is needed |
| The array queue called its front index `head` | Renamed to `front` | Matches the usual queue words "front" and "rear" |
| `(int)(((long)head+size)%data.length)` to avoid overflow | `(front + size) % data.length` | Same result for any realistic size, and much easier to read |
| `dequeue` and `clear` reset used slots to 0 | Removed | The slot is simply overwritten later |
| `LinkedQueue` had an optional capacity limit and `isFull()` | Removed | A linked queue does not need a fixed limit |
| `size()` and `clear()` methods | Removed | Not used by the demo |
| Constructors rejected a capacity of 0 or less | Removed | Keeps the class short |
| `main` pushed values in a loop on one line, e.g. `{ a.enqueue(x); b.enqueue(x); }` | Each call on its own line, with comments | Easier to follow |
| The first output line was `Full: true, true` | Now `Array queue full: true` | The linked queue no longer has a limit, so only the array queue can be full |
