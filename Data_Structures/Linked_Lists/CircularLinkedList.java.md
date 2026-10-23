# CircularLinkedList.java

A singly linked **circular** list (a ring): the last node points back to the first. The class stores only a `tail` pointer, because the head is always `tail.next`. Its standout feature is joining two rings in O(1).

**Language:** Java · **Related:** [SinglyLinkedList](SinglyLinkedList.java.md) · [DoublyLinkedList](DoublyLinkedList.java.md)

## How to run

```sh
javac CircularLinkedList.java
java CircularLinkedList
```

## How the list is stored

```
        ┌──────────────────────────────┐
        ▼                              │
      [1] ──► [2] ──► [3] ──► [4] ─────┘
      head                     tail          (head = tail.next)
```

- When the list is empty, `tail == null`.
- When it has one node, that node points to itself.
- No `next` pointer is ever `null`. Traversals therefore count `size` steps instead of stopping at `null`, which would never come.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` | Links the new node between `tail` and the old head | **O(1)** |
| `insertTail(v)` | Calls `insertHead(v)`, then moves `tail` forward by one, so the new node becomes the tail | **O(1)** |
| `insert(index, v)` | Inserts at a position from 0 to `size` | O(n) |
| `deleteHead()` | `tail.next = head.next`. If it was the only node, the list becomes empty | **O(1)** |
| `deleteTail()` | Walks to the node before the tail to relink the ring | O(n) |
| `delete(index)` | Checks the index *before* changing anything, then removes the node | O(n) |
| `get(index)`, `search(v)` | Walk the ring, counting up to `size` steps | O(n) |
| `traverse()` | Values starting from the head, as an `int[]` | Θ(n) |
| **`mergeFrom(donor)`** | Joins the donor's ring onto the end of this one | **O(1)** |

### How the O(1) merge works

Two rings can be joined by swapping just two `next` pointers:

```java
Node head = tail.next;               // this list's head
tail.next = donor.tail.next;         // our tail  -> donor's head
donor.tail.next = head;              // donor's tail -> our head
tail = donor.tail;                   // the combined ring ends at the donor's tail
```

The donor is then **emptied** (`donor.tail = null`, `donor.size = 0`). If it weren't, both list objects would point at the same nodes, and changing one would silently corrupt the other.

Merging a list with itself is rejected, and so is a merge whose combined size would overflow an `int`.

## Output

```
Merged ring: [1, 2, 3, 4, 5, 6]; donor empty: true
3 at 2
Deleted 1, 6, 3
[2, 4, 5]
```

The demo builds `a = [1, 2, 3, 4]` and `b = [5, 6]` and merges `b` into `a`. It then deletes the head (1), the tail (6) and index 1 (3), which leaves `[2, 4, 5]`.

## When a circular list is useful

- **Round-robin scheduling**, such as taking turns between players or processes: keep moving to `next` forever.
- **Rotating buffers and playlists** that loop.
- Any problem that "wraps around", such as the Josephus problem.

## Notes

- Not thread-safe.
- Nodes are private, so two different lists can never share nodes.
