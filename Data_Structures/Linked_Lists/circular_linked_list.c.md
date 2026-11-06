# circular_linked_list.c

The **C version** of [`CircularLinkedList.java`](CircularLinkedList.java). A circular singly linked list. Only `tail` is stored; the head is always `tail->next`. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra circular_linked_list.c -o circular_linked_list
./circular_linked_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

| Function | What it does | Cost |
|---|---|---|
| `insert_head` | new node goes between tail and old head | O(1) |
| `insert_tail` | insert at head, then move `tail` forward one step | O(1) |
| `delete_head` | `tail->next = head->next` | O(1) |
| `merge_from` | swap two links to join two rings | O(1) |
| `search` / `display` | loop `size` times, not until `NULL` | O(n) |

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

- **Why loop `size` times instead of until `NULL`?** In a ring no pointer is ever `NULL`, so a loop waiting for `NULL` would never end.
- **How can two rings be joined in O(1)?** Our tail is pointed at the other ring's head and the other ring's tail is pointed at our head; the other ring struct is then emptied so it does not own the same nodes twice.
- **What is special about a ring with one node?** Its `next` points to itself; deleting it sets `tail` to `NULL`.
