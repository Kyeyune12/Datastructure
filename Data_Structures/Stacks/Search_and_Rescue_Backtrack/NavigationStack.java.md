# NavigationStack.java

**Project A (Stack), in Java.** The same *Search & Rescue Backtrack Assistant* as the C version, as a linked-list stack built by hand (no `java.util.Stack`). It adds three extra operations: `depth()`, `emergencyRecall()` and `alreadyVisited()`.

**Language:** Java · **C version:** [mountTracker.c](mountTracker.c.md) · **Project overview:** [README.md](README.md) · **Queue counterpart:** [ConveyorDispatch.java](../../Queues/Warehouse_Conveyor_Dispatch/ConveyorDispatch.java.md)

## How to run

```sh
javac NavigationStack.java
java NavigationStack
```

> The file was uploaded as `NavigationStack (1) (1).java`, a browser's duplicate-download name. It has been renamed to `NavigationStack.java` because Java requires a `public class` to sit in a file with exactly the same name. Under the old name `javac` refuses to compile it.

## Why a stack

The rescuer always retreats to the junction entered **most recently**, which is LIFO. The top of the stack is the current position; the bottom is the first junction after the trailhead.

## Structure of the class

```java
public class NavigationStack {
    static class Node {           // one junction
        String junction;
        Node next;                // the junction below it
        Node(String junction, Node next) { ... }
    }
    private Node top;             // head of the list = current position
    ...
}
```

- **`Node`** is a `static` nested class: it belongs to `NavigationStack` but doesn't need an instance of it. The two-argument constructor lets `push` create and link a node in one line.
- **`top`** is `private`, so code outside the class can only change the stack through `push` and `pop`. This is **encapsulation**, and it's what makes it an *abstract* data type.
- There is no capacity limit, so no `isFull()`. The header comment explains this choice: a linked list exists precisely so the trail can be any depth.

## Core operations

### `push(String junction)` — O(1)

```java
Node newNode = new Node(junction, top);   // new node points at the old top
top = newNode;                            // and becomes the new top
```

Creating the node with `next = top` links it in front of the old top before `top` moves, so nothing is lost. Java strings are immutable, so the node can safely keep a reference to the caller's string without copying it. C has to `strcpy`.

### `pop()` — O(1)

```java
if (isEmpty()) { System.out.println("[WARNING] Already at the trailhead..."); return null; }
String junction = top.junction;
top = top.next;   // unlink the head node
return junction;
```

There's no `free` here. Once `top` moves past the old node, nothing refers to it any more and the **garbage collector** reclaims it. The C version must call `free(temp)` and copy the name to a `static` buffer first; Java can simply return the `String`. Popping an empty stack returns `null` instead of throwing, and `main` checks for `null`.

### `peek()` and `isEmpty()` — O(1)

`peek` returns `top.junction` (or `null` with a warning when empty) without changing `top`. `isEmpty` returns `top == null`.

### `display()` — O(n)

Walks the list with a separate `current` reference and marks the first line `[HERE]` (`current == top`), so the output reads from current position back to the trailhead.

## Scenario extensions (beyond the four required operations)

| Method | What it does | How | Time |
|---|---|---|---|
| `depth()` | how many junctions deep the rescuer is | counts nodes from `top` to `null` | O(n) |
| `emergencyRecall()` | retreat all the way to the trailhead | `while (!isEmpty()) pop();`, printing each junction passed | O(n) |
| `alreadyVisited(name)` | is this junction on the current path? | read-only walk with `current`; `equalsIgnoreCase`, so "river" matches "River" | O(n) |

Two design points worth mentioning in the report:

- **`emergencyRecall` reuses `pop()`** instead of just setting `top = null`. Each step goes through the same tested operation and the retreat path is printed in the correct LIFO order.
- **`alreadyVisited` never moves `top`.** It uses its own `current` pointer, so checking the path doesn't destroy it. A version that popped to search would wipe out the trail.

`depth()` walks the whole list every time it's called. Keeping a `size` field that `push` increases and `pop` decreases would make it O(1).

## `main` — the menu

```java
try {
    choice = Integer.parseInt(scanner.nextLine().trim());
} catch (NumberFormatException e) {
    System.out.println("Invalid input. Please enter a number.");
    continue;
}
```

The whole line is read first with `scanner.nextLine()`, then turned into a number with `Integer.parseInt`. If the line is not a number, `parseInt` throws a `NumberFormatException`; the `catch` prints a message and `continue` shows the menu again. So typing a letter doesn't crash or loop forever, unlike the C version. Choices 1–8 call the operations above, and 9 exits the `do … while (choice != 9)` loop. Junction names are read with `scanner.nextLine()` too, so a name can contain spaces ("North Fork").

The `{ }` braces around some `case` blocks give each case its own scope, so both case 1 and case 2 can declare a local variable called `junction`.

## Sample run

Input: push Gate, River, Cave → peek → display → depth → visited "river"? → pop → emergency recall → isEmpty → exit. Menu lines are omitted.

```
[OK] Moved into junction "Gate". Path recorded.
[OK] Moved into junction "River". Path recorded.
[OK] Moved into junction "Cave". Path recorded.
[CURRENT] Rescuer is at: Cave
--- Explored Path (Current -> Trailhead) ---
[HERE] Cave
       River
       Gate
---------------------------------------------
[DEPTH] Rescuer is 3 junction(s) deep.
[CHECK] "river" already visited on this path: true
[RETREAT] Backtracked from "Cave".
[EMERGENCY RECALL] Retreating to trailhead...
   ...passed back through River
   ...passed back through Gate
[EMERGENCY RECALL] Rescuer is back at the trailhead.
isEmpty (at trailhead): true

Exiting Search & Rescue Backtrack Assistant. Goodbye!
```

## Complexity

| Operation | Time |
|---|---|
| `push`, `pop`, `peek`, `isEmpty` | O(1) |
| `display`, `depth`, `emergencyRecall`, `alreadyVisited` | O(n) |
| Space | O(n) nodes |

## C vs Java

| | `mountTracker.c` | `NavigationStack.java` |
|---|---|---|
| Node memory | `malloc` / `free` | `new` / garbage collector |
| Returning the popped name | copy into a `static` buffer before `free` | return the `String` directly |
| Name storage | fixed `char[30]`, copied with `strcpy` | `String` reference |
| Bad menu input | loops forever (see the C doc) | handled with `parseInt` in `try`/`catch` |
| Extra operations | – | `depth`, `emergencyRecall`, `alreadyVisited` |

## What changed (simplified version)

The program still uses `java.util.Scanner`. Only the way it reads the menu choice changed:

| Before | Now | Why |
|---|---|---|
| `scanner.hasNextInt()`, then `scanner.nextInt()` for the menu choice | `Integer.parseInt(scanner.nextLine().trim())` inside `try`/`catch` | Every input is read the same way, as a whole line |
| `scanner.next()` read junction names as a single word | `scanner.nextLine()` reads the whole line | Names with spaces now work ("North Fork") |

The stack itself (`push`, `pop`, `peek`, `display`, `depth`, `emergencyRecall`, `alreadyVisited`) did not change.
