# trial.c

**Search & Rescue Backtrack Assistant.** An interactive menu program that uses a **linked-list stack** to record the junctions a rescuer passes through. Advancing pushes a junction, and hitting a dead end pops back to the previous one. It is a real-world picture of why a stack fits *backtracking*.

**Language:** C · **Array-based stack in this folder:** [adts.c](adts.c.md) · **Status:** works (one input-safety fix recommended)

## How to run

```sh
gcc -o trial trial.c
./trial
```

## How the stack is stored

```c
#define NAME_LEN 30
struct Node  { char junctionName[NAME_LEN]; struct Node *next; };
struct Stack { struct Node *top; };
```

```
top ──► [Cave] ──► [River] ──► [Gate] ──► NULL      (most recent junction first)
```

## Functions

| Function | What it does | Time |
| --- | --- | --- |
| `push(&s, name)` | `malloc`s a node, copies the name, links it on top, and prints `Advanced to <name>.` If `malloc` fails it prints an error | O(1) |
| `pop(&s)` | Removes the top node, frees it, and returns its name. Prints a message and returns `NULL` if the stack is empty | O(1) |
| `peekStack(&s)` | Returns the top junction's name without removing it, or `NULL` | O(1) |
| `isStackEmpty(&s)` | `top == NULL` | O(1) |
| `printTrail(&s)` | Prints the trail from the most recent junction back to the trailhead, e.g. `Cave -> River -> Gate` | O(n) |
| `main` | A menu loop: 1 advance, 2 backtrack, 3 view trail, 4 exit | — |

**How `pop` returns the name:** it copies the name into a `static char junction[NAME_LEN]` *before* freeing the node. Returning `temp->junctionName` directly would return a pointer to memory that had just been freed. The catch with a `static` buffer is that the next `pop` overwrites it, so use or copy the name before popping again.

## Example session

Input: advance to Gate, River and Cave, view the trail, backtrack twice, view, backtrack twice more, then exit.

```
Advanced to Gate.
Advanced to River.
Advanced to Cave.
Current trail (most recent first): Cave -> River -> Gate
Dead end. Backtracking... left Cave.
Now at River.
Dead end. Backtracking... left River.
Now at Gate.
Current trail (most recent first): Gate
Dead end. Backtracking... left Gate.
Now at Trailhead.
Already at trailhead. No further junctions to retrace.
Exiting Search & Rescue Assistant.
```

The menu printed before each choice has been left out.

## Why a stack?

Backtracking always returns to the **most recently visited** junction that hasn't been fully explored. That is last-in, first-out, exactly what a stack gives. Depth-first search and the *backtracking* design strategy work the same way.

## Problems to fix

| Problem | Why it matters | Fix |
| --- | --- | --- |
| `scanf("%s", junctionName)` has no length limit | A name longer than 29 characters writes past the end of the 30-byte buffer (a **buffer overflow**) | `scanf("%29s", junctionName)` |
| `scanf("%d", &choice)` isn't checked | Typing a letter leaves it unread in the input, so the menu loops forever | `if (scanf("%d", &choice) != 1) { /* clear the input */ }` |
| Nodes still on the stack aren't freed when you choose Exit | The operating system reclaims the memory anyway, but it is tidier to free them | Pop until empty before `break` |
| Names can't contain spaces (`%s` stops at a space) | "Old Bridge" would be read as two names | Use `fgets` and strip the newline |
