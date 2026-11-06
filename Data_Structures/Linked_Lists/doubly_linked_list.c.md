# doubly_linked_list.c

The **C version** of [`DoublyLinkedList.java`](DoublyLinkedList.java). A doubly linked list: every node has `prev` and `next`, so it can be read forwards and backwards. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra doubly_linked_list.c -o doubly_linked_list
./doubly_linked_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

| Function | What it does | Cost |
|---|---|---|
| `insert_head` / `insert_tail` | relink 2 pointers at the end | O(1) |
| `insert_at` | find `after`, then `before = after->prev` and relink 4 pointers | O(n) |
| `unlink_node` | `n->prev->next = n->next` and `n->next->prev = n->prev`, handling both ends | O(1) once you hold the node |
| `delete_tail` | `tail->prev` is the new tail, no walking | O(1) |
| `print_backward` | start at `tail`, follow `prev` | O(n) |

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

- **Why is `delete_tail` O(1) here but O(n) in a singly list?** The tail knows its predecessor through `prev`, so no walk is needed.
- **What is the price of the extra pointer?** One more pointer per node and more links to keep consistent on every insert and delete.
- **Which cases does `unlink_node` handle?** The first node (`prev == NULL`, so `head` moves), the last node (`next == NULL`, so `tail` moves) and a middle node (both neighbours are joined).
