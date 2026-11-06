# mountTracker.c

**Project A (Stack), in C.** A *Search & Rescue Backtrack Assistant*. A rescuer pushes each trail junction onto a stack as they move deeper, and pops back one junction at a time when they hit a dead end. The stack is built from scratch on a **linked list**.

**Language:** C · **Java version:** [NavigationStack.java](NavigationStack.java.md) · **Project overview:** [README.md](README.md) · **Queue counterpart:** [circular_queue-array.c](../../Queues/Warehouse_Conveyor_Dispatch/circular_queue-array.c.md)

## How to run

```sh
gcc mountTracker.c -o mountTracker
./mountTracker
```

The program is menu-driven. Type a number, then a junction name when asked.

## Why a stack fits this problem

A rescuer exploring a trail system always retreats to the junction they visited **most recently**. That is **LIFO**, last in, first out:

```
move: Gate → River → Cave          stack (top first): Cave, River, Gate
dead end at Cave → pop             stack:             River, Gate     (back at River)
dead end at River → pop            stack:             Gate            (back at Gate)
```

The top of the stack is always the rescuer's current position, and popping always returns them along the exact path they came in by.

## The data structures

```c
#define NAME_LEN 30

struct Node {
    char junctionName[NAME_LEN];
    struct Node* next;
};

struct Stack {
    struct Node* top;
};
```

- Each **`Node`** holds one junction name (up to 29 characters plus `'\0'`) and a pointer to the node **below** it.
- **`Stack`** holds only `top`, a pointer to the most recent junction. An empty stack has `top == NULL`.

```
top → [Cave | •] → [River | •] → [Gate | NULL]
```

The stack lives in a linked list, so it has **no fixed capacity**: it can grow as deep as memory allows. That's why there is no `isFull()`.

## Walking through the operations

### `push` — advance to a new junction, O(1)

```c
struct Node* newNode = malloc(sizeof(struct Node));
if (newNode == NULL) { printf("Memory allocation failed! ..."); return; }
strcpy(newNode->junctionName, junctionName);
newNode->next = s->top;   /* new node points at the old top */
s->top = newNode;         /* new node becomes the top */
```

1. Allocate a node on the heap. The `NULL` check handles running out of memory.
2. Copy the name into the node. The node needs its own copy, because the caller's `junctionName` buffer is reused for every input.
3. Link the new node **in front of** the old top, then move `top` to it.

The two linking lines must run in this order. If `top` were moved first, the pointer to the old top would be lost and the rest of the trail with it.

### `pop` — backtrack one step, O(1)

```c
if (s->top == NULL) { printf("Already at trailhead..."); return NULL; }
struct Node* temp = s->top;
static char junction[NAME_LEN];
strcpy(junction, temp->junctionName);
s->top = temp->next;   /* unlink the top node */
free(temp);            /* release its memory */
return junction;
```

- **Underflow check:** popping an empty stack prints a warning and returns `NULL` instead of crashing.
- `temp` keeps hold of the old top so it can still be freed after `top` moves down.
- **Why `static char junction[]`:** the name has to be returned *after* the node is freed. Returning `temp->junctionName` would hand back a pointer into freed memory (a *dangling pointer*). Copying into a `static` buffer keeps the text valid after the function returns. The trade-off is that the next `pop` overwrites it, so the caller must use the result before popping again. `main` does exactly that.

### `peekStack` and `isStackEmpty` — O(1)

`peekStack` returns `top->junctionName` without removing anything, or `NULL` if the stack is empty. `isStackEmpty` returns `top == NULL`. `main` uses `peekStack` after a pop to report where the rescuer now stands.

### `printTrail` — O(n)

```c
struct Node* current = s->top;
while (current != NULL) {
    printf("%s", current->junctionName);
    if (current->next != NULL) printf(" -> ");
    current = current->next;
}
```

Walks from the top down with a separate `current` pointer, so `top` itself never moves. The `->` arrow is printed only *between* names, which is why the check `current->next != NULL` is there.

### `main` — the menu loop

`struct Stack s; s.top = NULL;` creates an empty stack on `main`'s stack frame. The `while (1)` loop shows the menu and handles the choices:

| Choice | Action |
|---|---|
| 1 | read a name, `push` it |
| 2 | `pop`; if it worked, print the junction left behind and the new top (or "Trailhead") |
| 3 | `printTrail` |
| 4 | `break` out of the loop and exit |

## Sample run

Input: push Gate, River, Cave → view trail → pop → view trail → pop → pop → pop (empty) → exit. Menu lines are omitted.

```
*** Search & Rescue Backtrack Assistant ***
Advanced to Gate.
Advanced to River.
Advanced to Cave.
Current trail (most recent first): Cave -> River -> Gate
Dead end. Backtracking... left Cave.
Now at River.
Current trail (most recent first): River -> Gate
Dead end. Backtracking... left River.
Now at Gate.
Dead end. Backtracking... left Gate.
Now at Trailhead.
Already at trailhead. No further junctions to retrace.
Exiting Search & Rescue Assistant.
```

## Complexity

| Operation | Time | Space |
|---|---|---|
| `push`, `pop`, `peekStack`, `isStackEmpty` | O(1) | one node per push |
| `printTrail` | O(n) | O(1) extra |
| Whole stack | – | O(n) for n junctions |

## Known weaknesses (worth fixing before submission)

1. **Typing a letter at the menu loops forever.** If `scanf("%d", &choice)` fails, the bad text stays in the input buffer and is read again on every pass. The queue program already handles this. Check `scanf`'s return value and clear the line:
   ```c
   if (scanf("%d", &choice) != 1) { while (getchar() != '\n'); printf("Invalid input.\n"); continue; }
   ```
2. **Long names can overflow the buffer.** `scanf("%s", junctionName)` has no length limit, but the buffer holds 30 bytes. Use `scanf("%29s", junctionName)`.
3. **Names can't contain spaces.** `%s` stops at the first space, so "Old Bridge" would be read as two inputs.
4. **Memory isn't freed on exit.** Choosing 4 while junctions remain leaves their nodes allocated. The OS reclaims them when the program ends, but a short loop calling `pop` before `break` would be cleaner, and it's what a marker checking `malloc`/`free` pairs would expect.
