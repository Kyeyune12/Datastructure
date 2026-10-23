# QueueArrayAndLinkedList.java

Two implementations of a **queue** (first in, first out) with the same operations: a circular array and a linked list. Both enqueue, dequeue and peek in O(1).

**Language:** Java · **Related:** [StackArrayAndLinkedList.java](../Stacks/StackArrayAndLinkedList.java.md), the last-in, first-out counterpart

## How to run

```sh
javac QueueArrayAndLinkedList.java
java QueueArrayAndLinkedList
```

## The operations both queues provide

`enqueue(v)`, `dequeue()`, `peek()`, `size()`, `isEmpty()`, `isFull()` and `clear()`.

- `dequeue()` or `peek()` on an empty queue throws `NoSuchElementException`.
- `enqueue` on a full queue throws `IllegalStateException`.

## `CircularArrayQueue`: fixed capacity

```
capacity 3, after enqueue 10, 20, 30, dequeue, enqueue 40:

index:  [0]  [1]  [2]
data:   40   20   30
              ▲
             head = 1      size = 3   (40 went into slot (head + size) % capacity = (1 + 2) % 3 = 0)
```

- It keeps **`head`** (the position of the oldest item) and **`size`**.
- The next free slot is `(head + size) % capacity`. The `%` makes the index **wrap around** to the start of the array, so space freed at the front is reused and items never need to be shifted.
- `dequeue` reads `data[head]`, sets that slot to 0, then moves `head = (head + 1) % capacity`.
- Keeping `size` is how the queue tells **empty** (`size == 0`) from **full** (`size == capacity`). With only head and tail pointers, both cases would look the same, `head == tail`.
- The tail is calculated using `long`, so `head + size` can't overflow.

## `LinkedQueue`: optional capacity limit

```
head ──► [20] ──► [30] ──► [40] ──► null
                             ▲
                            tail
```

- **Enqueue at `tail`, dequeue at `head`.** Both are O(1) because the queue keeps both pointers.
- When the **last** item is removed, `tail` is reset to `null` as well. Otherwise it would still point at a node that is no longer in the queue.
- The capacity defaults to `Integer.MAX_VALUE`, and a smaller limit can be passed in. A limit is useful for *backpressure*: refusing new work when the queue is already long.

## Complexity

| | CircularArrayQueue | LinkedQueue |
| --- | --- | --- |
| `enqueue`, `dequeue`, `peek` | O(1) | O(1) |
| Storage | O(capacity), reserved up front | O(size), grows and shrinks |
| Can grow? | No | Yes, up to its limit |

## Output

```
Full: true, true
10 = 10
Peek: 20, 20
20 = 20
30 = 30
40 = 40
```

The demo fills both queues with 10, 20 and 30 (both report full, since the capacity is 3). It dequeues 10, then enqueues 40, which in the array queue **wraps around** into slot 0. It then empties both queues, and both return the items in the same FIFO order.

## Notes

- `isFull()` reports the configured limit only. It doesn't check free memory, so allocating a node could still fail first.
- Not thread-safe.
