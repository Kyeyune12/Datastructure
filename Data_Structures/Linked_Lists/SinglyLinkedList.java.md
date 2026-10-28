# SinglyLinkedList.java

A singly linked list of `int` values with both a `head` and a `tail` pointer. It supports inserting and deleting at either end or at any position, and searching.

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

- Each `Node` holds a `value` and a `next` link to the node after it.
- The list remembers `head` (first node), `tail` (last node) and `size` (number of nodes).
- An empty list has `head == null` and `tail == null`.

## Operations

| Method | What it does | Time |
| --- | --- | --- |
| `insertHead(v)` | New node points at the old head and becomes the head | **O(1)** |
| `insertTail(v)` | Old tail points at the new node, which becomes the tail | **O(1)** (thanks to the `tail` pointer) |
| `insert(index, v)` | Walks to the node *before* `index` and links the new node in after it | O(n) |
| `deleteHead()` | Moves `head` forward one node. If the list becomes empty, also clears `tail` | **O(1)** |
| `deleteTail()` | Must walk to the second-last node, because nodes have no backward link | O(n) |
| `delete(index)` | Walks to the node before `index` and makes it skip over the removed node | O(n) |
| `get(index)` | Walks `index` steps from the head | O(n) |
| `search(v)` | Position of the first match, or −1 | O(n) |
| `clear()`, `size()`, `isEmpty()`, `toString()` | Helpers | O(1), or O(n) for `toString` |

### Inserting in the middle, step by step

To insert 30 at position 2 in `[10, 20, 40]`:

```
1. before = nodeAt(1)            before ─► [20] ──► [40]
2. newNode.next = before.next             [30] ──► [40]
3. before.next = newNode         [20] ──► [30] ──► [40]
```

The order matters: if step 3 ran first, the link to `[40]` would be lost.

## Errors

- A position out of range throws `IndexOutOfBoundsException`.
- Deleting from an empty list throws `IllegalStateException`.

## Output

```
[10, 20, 30, 40]; 30 at 2
Deleted 10, 40, 30
[20]
Empty: true
```

The demo builds the list with `insertHead(20)`, `insertHead(10)`, `insertTail(40)` and `insert(2, 30)`. It then deletes the head (10), the tail (40) and position 1 (30), which leaves `[20]`. Finally it clears the list.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.NoSuchElementException;` | `IllegalStateException` | It is in `java.lang`, so no import is needed |
| `room()` checked that `size` could not pass `Integer.MAX_VALUE` | Removed | A list will never hold 2 billion nodes in a class exercise |
| `traverse()` copied the values into an array | Removed. `toString()` walks the nodes directly | One less step to explain |
| `toString()` used a `StringBuilder` | Plain `String` joining with `+` | Easier to read |
| Several statements squeezed onto one line, e.g. `room(); Node n=new Node(value); n.next=head; head=n;` | One statement per line, every `if` and loop in braces, and comments on each step | Much easier to follow |
| The `for` loop in `search` | A `while` loop that moves `current` forward | Shows the "walk the list" pattern clearly |
