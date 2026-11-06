# Project B – Queue (FIFO): Warehouse Conveyor Dispatch

Group project for the Data Structures course (Makerere University, BSSE). This is the **Queue** half of the LIFO & FIFO implementation project. The **Stack** half is in [`Stacks/Search_and_Rescue_Backtrack`](../../Stacks/Search_and_Rescue_Backtrack/README.md).

## The application

Packages are loaded onto a conveyor belt (**enqueue**) and dispatched from the other end in the order they arrived (**dequeue**). The belt holds at most 5 packages, so it is a **circular queue in a fixed array**: `front` and `rear` wrap around with `(index + 1) % CAPACITY` and no element is ever shifted.

## Files

| File | What it is |
|---|---|
| `circular_queue-array.c` | C implementation: circular array queue with automatic package IDs |
| `circular_queue-array.c.md` | line-by-line explanation, wrap-around trace, sample run, complexity |
| `ConveyorDispatch.java` | Java implementation: `Package`, `ConveyorQueue` and `ConveyorDispatch` classes |
| `ConveyorDispatch.java.md` | explanation, sample run, C-vs-Java comparison |

## How to run

C:
```sh
gcc circular_queue-array.c -o conveyor
./conveyor
```

Java:
```sh
javac ConveyorDispatch.java
java ConveyorDispatch
```

## Rules from the brief

- No built-in Java `Stack` / `Queue` classes or C++ STL. Raw logic only.
- The circular array queue must use `(rear + 1) % size`. Both versions do.
- Focus on correctness and clarity, not performance.

## Team

| Name | Role |
|---|---|
| Allan Victor | Lead / Repo Manager |
