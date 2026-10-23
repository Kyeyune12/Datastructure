# linkedlist.c

Builds a four-node **singly linked list** by hand, walks it to print the values, then frees every node. It is an early experiment with `struct`s, pointers and `malloc`.

**Language:** C · **Status:** the function is called `maiin` (a typo), so it never runs · **Full linked-list implementation:** [SinglyLinkedList.java](../Data_Structures/Linked_Lists/SinglyLinkedList.java.md)

## What it does

```c
struct Node {
    int data;
    char name[12];
    struct Node *next;
};
```

1. Allocates four nodes, `head`, `first`, `second` and `third`, with `malloc`.
2. Fills them in and chains them together:
   ```
   head(data 1, name "head") ──► first(10) ──► second(20) ──► third(30) ──► NULL
   ```
3. Walks the list with a `current` pointer until it reaches `NULL`:
   - for the head node it prints `name: head`;
   - for every other node it prints `Clue data: <data>`.
4. Frees all four nodes and returns 0.

With the function renamed to `main` and the missing header added, it prints:

```
name: head
Clue data: 10
Clue data: 20
Clue data: 30
```

## Problems to fix

| Problem | Why it matters | Fix |
| --- | --- | --- |
| The function is named `maiin` | It is never called, so nothing runs. A program needs exactly one `main` | Rename it to `int main(void)` |
| No `#include <stdlib.h>` | `malloc` and `free` are declared there. gcc warns *implicit declaration of function 'malloc'*, and newer compilers treat this as an error | Add `#include <stdlib.h>` |
| `malloc` results aren't checked | If memory runs out, `malloc` returns `NULL` and the next line crashes | `if (head == NULL) return 1;` and similar for each node |
| Only `head->name` is set | The other nodes' `name` fields hold garbage. They're never printed, so this is harmless here | Set every field, or leave `name` out of nodes that don't need it |
| The cast in `(struct Node*)malloc(...)` | Not needed in C (it is needed in C++). Harmless | Optional: `malloc(sizeof *head)` |

## Concepts it shows

- `struct Node *next` is a **self-referential structure**: each node holds a pointer to another node of the same type.
- **Traversal:** `current = current->next` until `NULL`, which takes O(n).
- **Manual memory management:** every `malloc` needs a matching `free`. The usual way to free a whole list is to save `next` *before* freeing each node:
  ```c
  while (current) { struct Node *next = current->next; free(current); current = next; }
  ```
