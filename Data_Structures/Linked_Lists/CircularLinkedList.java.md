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
- No `next` link is ever `null`. Loops therefore count `size` steps instead of stopping at `null`; a `null` would never come, and the loop would run forever.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` | Links the new node between `tail` and the old head | **O(1)** |
| `insertTail(v)` | Calls `insertHead(v)`, then moves `tail` forward by one, so the new node becomes the tail | **O(1)** |
| `insert(index, v)` | Inserts at a position from 0 to `size` | O(n) |
| `deleteHead()` | `tail.next = head.next`. If it was the only node, the list becomes empty | **O(1)** |
| `deleteTail()` | Walks to the node before the tail to relink the ring | O(n) |
| `delete(index)` | Removes the node at a position | O(n) |
| `get(index)`, `search(v)` | Walk the ring, counting up to `size` steps | O(n) |
| `traverse()` | Values starting from the head, as an `int[]` | Θ(n) |
| **`mergeFrom(other)`** | Joins the other ring onto the end of this one | **O(1)** |

### The `insertTail` trick

In a ring, the new tail sits between the old tail and the head, which is exactly where `insertHead` puts a node. So `insertTail` calls `insertHead` and then moves `tail` one step forward. The new node is now the tail instead of the head.

### How the O(1) merge works

Two rings can be joined by changing just two links:

```java
Node head = tail.next;
tail.next = other.tail.next;   // our tail -> other's head
other.tail.next = head;        // other's tail -> our head
tail = other.tail;             // other's tail is now the last node
```

The other list is then **emptied**. If it weren't, both list objects would share the same nodes, and changing one would silently change the other.

## Output

```
Merged ring: [1, 2, 3, 4, 5, 6]; donor empty: true
3 at 2
Deleted 1, 6, 3
[2, 4, 5]
```

The demo builds `a = [1, 2, 3, 4]` and `b = [5, 6]` and merges `b` into `a`. It then deletes the head (1), the tail (6) and position 1 (3), which leaves `[2, 4, 5]`.

## When a circular list is useful

- **Round-robin scheduling**, such as taking turns between players or processes: keep moving to `next` forever.
- **Playlists and buffers** that loop.
- Any problem that "wraps around", such as the Josephus problem.

## What changed (simplified version)

| Before | Now |
| --- | --- |
| Short one-line `if` statements such as `if (index == 0) { insertHead(value); return; }` | Every `if` and loop on its own lines, with braces |
| Loops like `for (int i = 0; i < size; i++, p = p.next)` that moved two things at once | Loops that move one thing at a time, with `current = current.next` on its own line |
| `toString()` used a `StringBuilder` | Plain `String` joining |
| Few comments | Comments on each step, a ring diagram at the top, and comments in `main` showing the list after each call |
| Overflow checks mentioned in the old notes | Not in the code, so no longer mentioned here |
