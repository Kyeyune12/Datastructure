# linkedlist.c

Builds a four-node **singly linked list by hand**, walks it to print the values, then frees every node. It is the simplest possible picture of what a linked list *is*.

**Full linked-list operations:** [SinglyLinkedList.java](../Data_Structures/Linked_Lists/SinglyLinkedList.java.md)

## How to run

```sh
gcc linkedlist.c -o linkedlist
./linkedlist
```

## What it does

```c
struct Node {
    int data;
    char name[12];
    struct Node* next;   // address of the next node (NULL = end of list)
};
```

1. `malloc(sizeof(struct Node))` four times to get memory for four nodes.
2. Fill each node and chain them: `head -> first -> second -> third -> NULL`.
3. Walk the list with `current = current->next` until `current == NULL`. The head prints its name; the others print their data.
4. `free` each node to give the memory back.

## Output

```
*** Linked list built by hand ***
name: head
Clue data: 10
Clue data: 20
Clue data: 30
```

## What was fixed

- The function was called `maiin` (a typo), so the program had no `main` and could not run.
- `#include <stdlib.h>` was missing, which `malloc` and `free` need.

## Questions you may be asked

- **Why `malloc`?** A local variable disappears when its function ends. Memory from `malloc` stays until `free`, so nodes can be added and removed while the program runs.
- **What does `->` mean?** `p->data` is short for `(*p).data`: "the `data` field of the struct that `p` points to".
- **What happens if you forget `free`?** A memory leak: the memory stays reserved until the program ends.
