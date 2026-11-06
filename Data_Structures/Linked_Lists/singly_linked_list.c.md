# singly_linked_list.c

The **C version** of [`SinglyLinkedList.java`](SinglyLinkedList.java). A singly linked list with `head`, `tail` and `size`. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra singly_linked_list.c -o singly_linked_list
./singly_linked_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

| Function | What it does | Cost |
|---|---|---|
| `insert_head` | new node points at old head | O(1) |
| `insert_tail` | old tail points at new node | O(1) |
| `insert_at` | walk to the node before the position, relink | O(n) |
| `delete_head` | head moves to second node, old head freed | O(1) |
| `delete_tail` | walk to the second-last node (no back links) | O(n) |
| `search` | walk until the value matches | O(n) |

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

- **Why does `delete_tail` call `free` before changing `tail`?** We still need `tail->value` and then the old tail's memory must be released; we save the value first, free the node, then point `tail` at the node before it. Reading a node after `free` is undefined behaviour.
- **What is a memory leak here?** Removing a node by only changing pointers (without `free`) leaves its memory allocated but unreachable. Every delete function frees what it removes.
- **Why return -1 on an empty list?** Same convention as the Java and the team's `arrays/` stack: print a message and return a value that is easy to spot. A real library would return an error code and use an out-parameter.
