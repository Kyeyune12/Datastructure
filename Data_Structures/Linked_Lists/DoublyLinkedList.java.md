# DoublyLinkedList.java

A **doubly linked list**: each node links to the **next** node *and* the **previous** node, so the list can be walked in both directions.

```
null <-- [10] <--> [20] <--> [30] --> null
         head                tail
```

## How to run

```sh
javac DoublyLinkedList.java
java DoublyLinkedList
```

## Operations

| Method | What it does | Cost |
| --- | --- | --- |
| `insertHead(v)` / `insertTail(v)` | Link the new node in at one end and fix both links | O(1) |
| `insert(i, v)` | Find the node at i (`after`) and its `prev` (`before`); set four links | O(n) |
| `unlink(n)` | Join n's left and right neighbours to each other. Used by **every** delete | O(1) |
| `deleteHead()` / `deleteTail()` | `unlink(head)` / `unlink(tail)` | O(1) |
| `delete(i)` | `unlink(nodeAt(i))` | O(n) |
| `search(v)` | Walk from the head; position or -1 | O(n) |
| `printForward()` / `printBackward()` | Walk using `next` / using `prev` | O(n) |

`unlink` is the heart of the class:

```java
if (n.prev == null) { head = n.next; } else { n.prev.next = n.next; }   // fix the left side
if (n.next == null) { tail = n.prev; } else { n.next.prev = n.prev; }   // fix the right side
```

## Output

```
*** Doubly Linked List ***
Forward:  10 20 30 40
Backward: 40 30 20 10
30 is at position 2
Deleted head: 10
Deleted tail: 40
Deleted [1]:  30
Forward:  20
```

## Questions you may be asked

- **Array or linked list?** An array gives O(1) access by index but costs O(n) to insert in the middle. A linked list inserts in O(1) once you are at the spot, but has to walk O(n) to find a position.
- **Why keep a `tail` pointer?** Without it, adding at the end means walking the whole list: O(n) instead of O(1).
- **What does Java do with a deleted node?** Nothing points to it any more, so the garbage collector frees it. In C you would call `free()` yourself.
- **What happens on an empty list?** The delete methods print `List is empty!` and return -1, the same way the team's C stack prints `Stack underflow!`.
- **What is the cost of the extra `prev` link?** One more reference per node (more memory), and every insert/delete must fix two links instead of one. The gain is O(1) delete at the tail and backward traversal.
- **Real example?** A browser's Back and Forward buttons, or an undo/redo list.
