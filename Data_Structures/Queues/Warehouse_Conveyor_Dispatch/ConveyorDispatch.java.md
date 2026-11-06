# ConveyorDispatch.java

**Project B (Queue), Java version.** The same Warehouse Conveyor Dispatch system as [circular_queue-array.c](circular_queue-array.c.md): a **circular array** queue of 5 packages, built by hand with no `java.util.Queue` and no `ArrayList`.

**Project overview:** [README.md](README.md)

## How to run

```sh
javac ConveyorDispatch.java
java ConveyorDispatch
```

## What is inside

One class, `ConveyorDispatch`, with:

- a nested `Package` class (`id`, `destination`)
- the queue itself as `static` fields: `Package[] items`, `front = 0`, `rear = -1`, `count = 0`
- the operations as `static` methods, in the same order and with the same names as the C file

| Method | What it does | Cost |
| --- | --- | --- |
| `isEmpty()` / `isFull()` | `count == 0` / `count == CAPACITY` | O(1) |
| `enqueue(p)` | `rear = (rear + 1) % CAPACITY; items[rear] = p; count++;` | O(1) |
| `dequeue()` | Read `items[front]`, clear the slot, `front = (front + 1) % CAPACITY; count--;` Returns `null` if empty | O(1) |
| `peek()` | Front package without removing it | O(1) |
| `display()` | Every package from front to rear, with its slot number | O(n) |

## Menu and sample run

The menu is the same as the C version: `1` load · `2` dispatch · `3` peek · `4` view · `5` isEmpty/isFull · `6` exit. IDs are given out automatically.

```
Loaded package #1 -> A at slot 0.     ... (#2 to #5 fill slots 1 to 4)
Belt is full! Cannot load package #6.
Dispatched package #1 -> A.
Loaded package #7 -> G at slot 0.     <- wrap-around
Belt (front to rear):
  Slot 1: Package #2 -> B
  ...
  Slot 0: Package #7 -> G
```

## C vs Java

| | C | Java |
| --- | --- | --- |
| Package | `struct Package` with a `char[30]` name | `class Package` with a `String` |
| Queue state | `struct Queue` passed by pointer | `static` fields in the class |
| "Nothing to return" | Function returns 0 | Method returns `null` |
| Bad menu input | `scanf` return value checked | `scanner.hasNextInt()` checked |
| Destinations with spaces | No (`%s` stops at a space) | Yes (`nextLine()`) |

## Questions you may be asked

- **Why set `items[front] = null` in `dequeue`?** Not to shift anything. It lets Java's garbage collector free the package once it has left the belt.
- **Why is there no `ArrayList`?** The brief says to build the structure by hand, and a fixed belt of 5 slots is exactly what a circular array models.
- **What happens when the belt is full?** `enqueue` prints `Belt is full!` and nothing is stored; the package ID still advances.
