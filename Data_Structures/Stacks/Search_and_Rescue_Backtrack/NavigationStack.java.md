# NavigationStack.java

**Project A (Stack), Java version.** The Search & Rescue Backtrack Assistant: a menu program where a rescuer moves deeper into a trail (**push**) and goes back from dead ends (**pop**). The stack is a **linked list**, built by hand with no `java.util.Stack`.

**C version:** [mountTracker.c](mountTracker.c.md) · **Project overview:** [README.md](README.md)

## How to run

```sh
javac NavigationStack.java
java NavigationStack
```

## What is inside

One class with a nested `Node` (junction name + link to the junction we came from) and one field, `top`.

| Method | What it does | Cost |
| --- | --- | --- |
| `push(junction)` | `top = new Node(junction, top)` | O(1) |
| `pop()` | Returns `top.junction`, then `top = top.next`. At the trailhead it prints a message and returns `null` | O(1) |
| `peek()` | Current junction, or `null` | O(1) |
| `isEmpty()` | `top == null` means at the trailhead | O(1) |
| `display()` | Prints the trail, newest first | O(n) |
| `depth()` | Counts the nodes | O(n) |
| `emergencyRecall()` | Pops until empty, printing each junction passed | O(n) |
| `alreadyVisited(name)` | Walks the list with a **separate pointer**, so `top` never moves (ignores capital letters) | O(n) |

## The menu

```
1. Move to a new junction (push)
2. Dead end - go back (pop)
3. Current position (peek)
4. Show the whole trail
5. How deep am I? (depth)
6. Emergency recall
7. Was a junction already visited?
8. Exit
```

Input is read with `Scanner`. `hasNextInt()` catches letters typed at the menu, and junction names are read with `nextLine()`, so names with spaces such as "River Bend" work.

## Sample run

```
Enter choice: 1   Enter junction name: Gate          -> Moved into junction "Gate".
Enter choice: 1   Enter junction name: River Bend    -> Moved into junction "River Bend".
Enter choice: 1   Enter junction name: Cave          -> Moved into junction "Cave".
Enter choice: 5                                      -> Depth: 3 junction(s).
Enter choice: 2                                      -> Dead end. Left Cave.  Now at River Bend.
Enter choice: 6                                      -> passed back through River Bend, Gate
```

## Questions you may be asked

- **Why a stack and not a queue?** The rescuer must go back to the **most recent** junction first. That is Last In, First Out.
- **Why a linked list and not an array?** We don't know how deep the trail goes. A linked list has no fixed size, so there is no `isFull()`.
- **Why does `alreadyVisited` use `current` instead of `top`?** Moving `top` would lose the trail. A second pointer reads the list without changing it.
- **What does `scanner.nextLine()` after `nextInt()` do?** `nextInt()` leaves the Enter key in the input. Without that line, the next name read would be empty.
