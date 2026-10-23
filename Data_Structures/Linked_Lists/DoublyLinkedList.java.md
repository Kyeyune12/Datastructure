# DoublyLinkedList.java

A doubly linked list of `int` values. Every node links both to the next node and to the previous one, so the list can be walked in either direction and any node can be removed in O(1) once found.

**Language:** Java · **Related:** [SinglyLinkedList](SinglyLinkedList.java.md) · [CircularLinkedList](CircularLinkedList.java.md)

## How to run

```sh
javac DoublyLinkedList.java
java DoublyLinkedList
```

## How the list is stored

```
null ◄── [10] ◄──► [20] ◄──► [30] ◄──► [40] ──► null
          ▲                              ▲
         head                           tail
```

**Invariant:**

- `head.prev == null` and `tail.next == null`;
- for every node x, `x.next.prev == x` and `x.prev.next == x` wherever those links exist.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` / `insertTail(v)` | Link a new node at either end | **O(1)** |
| `insert(index, v)` | Finds the node currently at `index` and links the new node in front of it, fixing four pointers | O(min(i, n−i)) |
| `deleteHead()` / `deleteTail()` | Unlink the end node | **O(1)** |
| `delete(index)` | Finds the node, then unlinks it | O(min(i, n−i)) |
| `get(index)` | The value at `index` | O(min(i, n−i)) |
| `search(v)` | First index of `v`, or −1 | O(n) |
| `traverseForward()` / `traverseBackward()` | Values from head to tail, or from tail to head, as an `int[]` | Θ(n) |
| `clear()`, `size()`, `isEmpty()`, `toString()` | Helpers | — |

### Two ideas worth noticing

1. **`nodeAt(index)` starts from whichever end is nearer.** If `index < size/2` it walks forward from `head`; otherwise it walks backward from `tail`. That halves the worst-case walk.
2. **One `unlink(node)` handles every delete.** It fixes the neighbour on each side, using the `head` or `tail` pointer when the node is at an end. Because a node knows its predecessor, unlinking is O(1). A singly linked list has to search for the predecessor first.

## Output

```
Forward: [10, 20, 30, 40]
Backward: 40 30 20 10
30 at 2
Deleted 10, 40, 30
[20]
```

## Trade-off compared with a singly linked list

|  | Singly linked | Doubly linked |
| --- | --- | --- |
| Pointers per node | 1 | 2 (more memory) |
| Delete tail | O(n) | O(1) |
| Walk backwards | Not possible | Yes |
| Remove a node you already hold | O(n), to find its predecessor | O(1) |

## Notes

- Bad indices throw `IndexOutOfBoundsException`, and deleting from an empty list throws `NoSuchElementException`.
- `Node` is private, so the invariant can't be broken from outside the class.
- Not thread-safe.
