# SinglyLinkedList.java

A **singly linked list**: each node holds a value and a link to the **next** node. We keep `head` (first node), `tail` (last node) and `size`.

```
head --> [10] --> [20] --> [30] --> null
                            ^
                           tail
```

## How to run

```sh
javac SinglyLinkedList.java
java SinglyLinkedList
```

## Operations

| Method | What it does | Cost |
| --- | --- | --- |
| `insertHead(v)` | New node points to the old head, then becomes the head | O(1) |
| `insertTail(v)` | Old tail points to the new node, then it becomes the tail | O(1) |
| `insert(i, v)` | Walk to the node **before** position i and link the new node after it | O(n) |
| `deleteHead()` | Move `head` to the second node | O(1) |
| `deleteTail()` | Walk to the second-last node, because there are no backward links | O(n) |
| `delete(i)` | Walk to the node before i and skip over the deleted node | O(n) |
| `search(v)` | Walk from the head; return the position or -1 | O(n) |
| `display()` | Print `10 -> 20 -> ... -> null` | O(n) |
| `nodeAt(i)` | Helper: walk i steps from the head | O(n) |

The key line in every delete is the **skip**:

```java
before.next = before.next.next;   // skip over the deleted node
```

## Output

```
*** Singly Linked List ***
10 -> 20 -> 30 -> 40 -> null
30 is at position 2
Deleted head: 10
Deleted tail: 40
Deleted [1]:  30
20 -> null
Empty: true
List is empty!
```

## Questions you may be asked

- **Array or linked list?** An array gives O(1) access by index but costs O(n) to insert in the middle. A linked list inserts in O(1) once you are at the spot, but has to walk O(n) to find a position.
- **Why keep a `tail` pointer?** Without it, adding at the end means walking the whole list: O(n) instead of O(1).
- **What does Java do with a deleted node?** Nothing points to it any more, so the garbage collector frees it. In C you would call `free()` yourself.
- **What happens on an empty list?** The delete methods print `List is empty!` and return -1, the same way the team's C stack prints `Stack underflow!`.
- **Why is `deleteTail` O(n) here but O(1) in the doubly linked list?** To remove the tail we need the node *before* it. A singly linked list can only find it by walking from the head.
