# SinglyLinkedList.java

A singly linked list of `int` values with both a `head` and a `tail` pointer. It supports inserting and deleting at either end or at any index, searching, and turning the list into an array.

**Language:** Java · **Related:** [DoublyLinkedList](DoublyLinkedList.java.md) · [CircularLinkedList](CircularLinkedList.java.md)

## How to run

```sh
javac SinglyLinkedList.java
java SinglyLinkedList
```

## How the list is stored

```
head                         tail
 │                            │
 ▼                            ▼
[10] ──► [20] ──► [30] ──► [40] ──► null
```

- Each private `Node` holds a `value` and a `next` pointer.
- The list keeps `head`, `tail` and `size`.
- **Invariant**, true after every operation:
  - `size == 0` exactly when `head` and `tail` are both `null`;
  - otherwise `tail.next == null`, and exactly `size` nodes can be reached from `head`.

`Node` is a private class, so code outside the list can never relink nodes and break the invariant.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` | New node points at the old head and becomes the head | **O(1)** |
| `insertTail(v)` | Old tail points at the new node, which becomes the tail | **O(1)** (thanks to the `tail` pointer) |
| `insert(index, v)` | Valid indices are 0 to `size`. Walks to the node *before* `index` and links the new node in after it | O(n) |
| `deleteHead()` | Moves `head` forward. If the list becomes empty, also clears `tail` | **O(1)** |
| `deleteTail()` | Must walk to the second-last node, because nodes have no backward link | O(n) |
| `delete(index)` | Walks to the node before `index` and skips over the removed node | O(n) |
| `get(index)` | Walks `index` steps from the head | O(n) |
| `search(v)` | Index of the first match, or −1 | O(n) |
| `traverse()` | Copies the values into a new `int[]` | Θ(n) |
| `clear()`, `size()`, `isEmpty()`, `toString()` | Helpers | O(1), or O(n) for `toString` |

## Error handling

- An index out of range throws `IndexOutOfBoundsException`.
- Deleting from an empty list throws `NoSuchElementException`.
- `room()` refuses to grow the list beyond `Integer.MAX_VALUE` nodes, so `size` can't overflow.

## Output

```
[10, 20, 30, 40]; 30 at 2
Deleted 10, 40, 30
[20]
Empty: true
```

The demo builds the list with `insertHead(20)`, `insertHead(10)`, `insertTail(40)` and `insert(2, 30)`. It then deletes the head (10), the tail (40) and index 1 (30), which leaves `[20]`. Finally it clears the list.

## Notes

- Deleted nodes simply become unreachable, and Java's garbage collector frees them. There is no `free()` as in C.
- Deleting from the tail is the weak spot of a singly linked list. The [doubly linked list](DoublyLinkedList.java.md) makes it O(1).
- Not thread-safe. Two threads changing the list at once can corrupt it.
