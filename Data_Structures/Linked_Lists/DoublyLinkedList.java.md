# DoublyLinkedList.java

A doubly linked list of `int` values. Every node links both to the next node and to the previous one, so the list can be walked in either direction, and either end can be removed in O(1).

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

- Each `Node` holds a `value`, a `next` link and a `prev` link.
- The first node's `prev` is `null` and the last node's `next` is `null`.
- For every node in the middle: going `next` then `prev` brings you back to the same node.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` / `insertTail(v)` | Link a new node at either end | **O(1)** |
| `insert(index, v)` | Finds the node currently at `index` and links the new node in front of it, fixing four links | O(n) |
| `deleteHead()` / `deleteTail()` | Unlink the end node | **O(1)** |
| `delete(index)` | Finds the node, then unlinks it | O(n) |
| `get(index)` | The value at `index` | O(n) |
| `search(v)` | First position of `v`, or −1 | O(n) |
| `printForward()` / `printBackward()` | Print from head to tail using `next`, or from tail to head using `prev` | Θ(n) |
| `clear()`, `size()`, `isEmpty()`, `toString()` | Helpers | — |

### One `unlink(node)` handles every delete

```java
// fix the link coming from the left
if (n.prev == null) {
    head = n.next;           // n was the first node
} else {
    n.prev.next = n.next;
}

// fix the link coming from the right
if (n.next == null) {
    tail = n.prev;           // n was the last node
} else {
    n.next.prev = n.prev;
}
```

Because a node knows its previous node, removing it is O(1). This is why `deleteTail()` is O(1) here but O(n) in a singly linked list, which has to walk the whole list to find the second-last node.

## Output

```
Forward: 10 20 30 40
Backward: 40 30 20 10
30 at 2
Deleted 10, 40, 30
[20]
```

## Trade-off compared with a singly linked list

|  | Singly linked | Doubly linked |
| --- | --- | --- |
| Links per node | 1 | 2 (more memory) |
| Delete tail | O(n) | O(1) |
| Walk backwards | Not possible | Yes |

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.NoSuchElementException;` | `IllegalStateException` | It is in `java.lang`, so no import is needed |
| `nodeAt` walked from whichever end was nearer | `nodeAt` always walks from the head | Simpler. The "nearer end" trick only halves the walk; it is still O(n) |
| `room()` size-overflow check | Removed | Not needed for a class exercise |
| `traverseForward()` / `traverseBackward()` returned arrays | `printForward()` / `printBackward()` print directly | Shows the `next` and `prev` walks plainly |
| `toString()` used a `StringBuilder` | Plain `String` joining | Easier to read |
| Code squeezed onto single lines, e.g. `room(); Node n=new Node(value); n.next=head;` | One statement per line, braces everywhere, and comments on each step | Much easier to follow |
| The forward output looked like `[10, 20, 30, 40]` | Now `10 20 30 40`, the same style as the backward line | Both directions look alike |
