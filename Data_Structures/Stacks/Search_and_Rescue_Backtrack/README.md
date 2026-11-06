# Project A – Stack (LIFO): Search & Rescue Backtrack Assistant

Group project for the Data Structures course (Makerere University, BSSE). This is the **Stack** half of the LIFO & FIFO implementation project. The **Queue** half is in [`Queues/Warehouse_Conveyor_Dispatch`](../../Queues/Warehouse_Conveyor_Dispatch/README.md).

## The application

A rescuer exploring a trail system records each junction as they go deeper (**push**). When they reach a dead end they retreat to the junction they came from (**pop**). The most recent junction is always the one to return to, which makes this a stack.

## Files

| File | What it is |
|---|---|
| `mountTracker.c` | C implementation: linked-list stack, menu with push / pop / view trail |
| `mountTracker.c.md` | line-by-line explanation, sample run, complexity, known weaknesses |
| `NavigationStack.java` | Java implementation: linked-list stack plus `depth`, `emergencyRecall`, `alreadyVisited` |
| `NavigationStack.java.md` | explanation, sample run, C-vs-Java comparison |

## How to run

C:
```sh
gcc mountTracker.c -o mountTracker
./mountTracker
```

Java:
```sh
javac NavigationStack.java
java NavigationStack
```

## Rules from the brief

- No built-in Java `Stack` / `Queue` classes or C++ STL. Raw logic only.
- Focus on correctness and clarity, not performance.
- Comment non-obvious pointer and index logic.

## Team

| Name | Role |
|---|---|
| Allan Victor | Lead / Repo Manager |
