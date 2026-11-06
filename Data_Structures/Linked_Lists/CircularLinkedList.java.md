# CircularLinkedList.java

A **circular** singly linked list: the last node links back to the first, so there is no `null` at the end. We store only `tail`; the head is always `tail.next`.

```
   +--> [1] --> [2] --> [3] --+
   |                    tail  |
   +--------------------------+
```

## How to run

```sh
javac CircularLinkedList.java
java CircularLinkedList
```

## Operations

| Method | What it does | Cost |
| --- | --- | --- |
| `insertHead(v)` | New node goes between `tail` and the old head. The first node points to itself | O(1) |
| `insertTail(v)` | **Trick:** `insertHead(v)`, then move `tail` one step forward, so the new node becomes the tail | O(1) |
| `insert(i, v)` | Walk to the node before i and link in | O(n) |
| `deleteHead()` | `tail.next = head.next` (tail skips the old head) | O(1) |
| `deleteTail()` | Walk to the second-last node and make it the tail | O(n) |
| `delete(i)` | Walk to the node before i and skip the deleted one | O(n) |
| `search(v)` | Walk exactly `size` steps (there is no `null` to stop at) | O(n) |
| `mergeFrom(other)` | Joins two rings in **O(1)** by swapping two links, then empties `other` | O(1) |
| `display()` | Print once round the ring | O(n) |

Merging two rings needs only two link changes:

```java
Node head = tail.next;
tail.next = other.tail.next;   // our tail -> other's head
other.tail.next = head;        // other's tail -> our head
tail = other.tail;             // other's tail is now the last node
```

## Output

```
*** Circular Linked List ***
Merged ring: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> (back to 1)
Donor ring empty: true
3 is at position 2
Deleted head: 1
Deleted tail: 6
Deleted [1]:  3
2 -> 4 -> 5 -> (back to 2)
```

## Questions you may be asked

- **Array or linked list?** An array gives O(1) access by index but costs O(n) to insert in the middle. A linked list inserts in O(1) once you are at the spot, but has to walk O(n) to find a position.
- **Why keep a `tail` pointer?** Without it, adding at the end means walking the whole list: O(n) instead of O(1).
- **What does Java do with a deleted node?** Nothing points to it any more, so the garbage collector frees it. In C you would call `free()` yourself.
- **What happens on an empty list?** The delete methods print `List is empty!` and return -1, the same way the team's C stack prints `Stack underflow!`.
- **Why store only `tail` and not `head`?** From `tail` we reach the head in one step (`tail.next`), so both ends are O(1) with one pointer.
- **Why do loops count to `size` instead of checking for `null`?** There is no `null` in a ring. A `while (current != null)` loop would never end.
- **Real example?** Round-robin CPU scheduling, or taking turns in a multiplayer game: after the last player it goes back to the first.
