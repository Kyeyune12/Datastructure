# ConveyorDispatch.java

**Project B (Queue), in Java.** The *Warehouse Conveyor Package Dispatch System*: a **circular queue in a fixed array of 5 slots**, written by hand (no imports, no `java.util.Queue`, no `ArrayList`, no shifting). Packages are dispatched first in, first out.

**Language:** Java · **C version:** [circular_queue-array.c](circular_queue-array.c.md) · **Project overview:** [README.md](README.md) · **Stack counterpart:** [NavigationStack.java](../../Stacks/Search_and_Rescue_Backtrack/NavigationStack.java.md)

## How to run

```sh
javac ConveyorDispatch.java
java ConveyorDispatch
```

> **Renamed and moved.** This file was uploaded as `Package.java` inside `Project_A_Stack/java/src`. Two things were wrong with that:
> 1. It is the **queue** project, not the stack, so it now lives in `Queues/`.
> 2. Its `public` class is `ConveyorDispatch`, and Java requires a public class to be in a file of the same name. As `Package.java` it fails with *"class ConveyorDispatch is public, should be declared in a file named ConveyorDispatch.java"*. As `ConveyorDispatch.java` it compiles.
>
> `javac` produces three class files (`Package.class`, `ConveyorQueue.class`, `ConveyorDispatch.class`), one for each class in the file. Run the one that has `main`: `ConveyorDispatch`.

## Three classes, three layers

The file splits the program into layers, as its comments say:

| Class | Layer | Job |
|---|---|---|
| `Package` | system model | one package: `id` and `destination` |
| `ConveyorQueue` | data structure | the circular-queue mechanics only |
| `ConveyorDispatch` | application | menu, input, and the "conveyor moving…" story around a dequeue |

Keeping the queue logic out of the menu code means `ConveyorQueue` could be reused in any program, and it can be marked on its own. Only `ConveyorDispatch` is `public`; the other two are package-private helper classes in the same file.

A side note: `java.lang.Package` already exists in Java's standard library. A class you declare yourself with the same name takes priority inside this file, so it works, but a name like `Parcel` would avoid any confusion.

## `Package`

```java
class Package {
    String id;
    String destination;
    Package(String id, String destination) { ... }
    @Override public String toString() { return "ID: " + id + " | Destination: " + destination; }
}
```

Overriding `toString()` lets the code print a package with `"  " + pkg`, so the format is defined in one place.

## `ConveyorQueue` — the circular queue

### State

```java
private static final int CAPACITY = 5;
private Package[] packages;   // fixed array, never resized
private int front;            // index of the next package to dispatch
private int rear;             // index of the most recently added package
private int count;            // number of packages waiting
```

`CAPACITY` is `static final`, a single constant shared by all queues. The fields are `private`, so only the queue's own methods can move `front` and `rear`.

`initializeQueue()` sets `front = 0`, `rear = -1`, `count = 0`, so the first enqueue goes to slot `(−1 + 1) % 5 = 0`.

**Why `count`:** with only `front` and `rear`, "empty" and "full" look the same (in both, `rear` is one behind `front`, wrapping around). `count` tells them apart, so `isEmpty()` is `count == 0`, `isFull()` is `count == CAPACITY`, and all 5 slots are usable.

### `enqueue(Package pkg)` — O(1)

```java
if (isFull()) { System.out.println("Conveyor buffer is full."); return false; }
rear = (rear + 1) % CAPACITY;    // advance and wrap
packages[rear] = pkg;
count = count + 1;
return true;
```

Overflow is checked first. `% CAPACITY` turns index 5 back into 0, so freed slots at the start of the array are reused. It returns `true` or `false`, so a caller can tell whether the package was accepted.

### `dequeue()` — O(1)

```java
if (isEmpty()) { System.out.println("No packages waiting."); return null; }
Package pkg = packages[front];
packages[front] = null;          // clear reference (not a shift)
front = (front + 1) % CAPACITY;
count = count - 1;
return pkg;
```

Setting the slot to `null` is a Java-specific touch. Otherwise the array would still hold a reference to a dispatched package, and the garbage collector couldn't free it until the slot was overwritten. It doesn't shift anything; the other packages stay where they are.

### `peek()` — O(1)

Returns `packages[front]` without changing `front`, `rear` or `count` (the code comments say so explicitly).

### `displayQueue()` — O(n)

```java
int index = front;
for (int i = 0; i < count; i++) {
    System.out.println("  [" + (i + 1) + "] " + packages[index]);
    index = (index + 1) % CAPACITY;
}
```

Visits exactly `count` slots starting at `front`, wrapping with `%`, and numbers them 1, 2, 3… in FIFO order.

### Getters

`getCount()`, `getFront()`, `getRear()` and `static getCapacity()` give read-only access for the "Show Buffer Status" option without making the fields public.

## `ConveyorDispatch` — the application

### `dispatchNextPackage`

Checks `isEmpty()` first, so an empty belt gets the application's own message ("No package available for dispatch."). Then it calls `dequeue()` and prints the conveyor story around the result.

### `main`

```java
String line = readLine();
try {
    choice = Integer.parseInt(line);
} catch (NumberFormatException e) {
    choice = -1;
}
```

Every input is read as a **whole line**, then converted to a number. Bad input becomes `-1`, which falls through to `"Invalid option."` and can never crash the program. Reading full lines also means destinations **can contain spaces** ("Fort Portal" works), unlike the C version's `%29s`.

### `readLine` — reading input without `Scanner`

The program has **no imports**, so instead of `java.util.Scanner` it has its own small `readLine()` method:

```java
static String readLine() {
    String line = "";
    try {
        int c = System.in.read();
        while (c != -1 && c != '\n') {   // stop at the end of the line
            if (c != '\r') {             // Windows adds \r before \n: skip it
                line = line + (char) c;
            }
            c = System.in.read();
        }
    } catch (Exception e) {
        // could not read input: return what we have so far
    }
    return line.trim();
}
```

`System.in.read()` gives back the next typed character as a number, or −1 when there is no more input. The loop adds characters to `line` until it reaches the end of the line (`\n`). `System.in.read()` can throw an `IOException`, so the call sits inside `try`/`catch`. Catching the general `Exception` avoids having to import `java.io.IOException`.

The `switch` handles 1 (add), 2 (dispatch), 3 (peek), 4 (list), 5 (status: capacity, count, available, front and rear index) and 6 (exit). The `do … while (choice != 6)` loop runs the menu at least once.

## Sample run (shows wrap-around and the full check)

Input: add P1 Kampala, P2 Entebbe, P3 Jinja → list → dispatch → add P4 Mbarara, P5 Gulu, P6 Mbale, P7 Lira → status → peek → exit. Menu lines are omitted.

```
Package added successfully.
Package added successfully.
Package added successfully.
Packages currently waiting (FIFO order):
  [1] ID: P1 | Destination: Kampala
  [2] ID: P2 | Destination: Entebbe
  [3] ID: P3 | Destination: Jinja
Dispatching package:
  ID: P1 | Destination: Kampala
Conveyor moving package...
Package ID: P1
Destination: Kampala
Package dispatched successfully.
Package added successfully.
Package added successfully.
Package added successfully.
Conveyor buffer is full.
Capacity: 5
Packages: 5
Available: 0
Front index: 1
Rear index: 0
Next package:
  ID: P2 | Destination: Entebbe
Exiting...
```

**Front index 1, rear index 0** shows the wrap-around. P6 was stored in slot 0, the slot P1 left, because `rear = (4 + 1) % 5 = 0`. The queue is full with `rear` *behind* `front`, which is exactly the case `count` exists to handle.

## Complexity

| Operation | Time |
|---|---|
| `enqueue`, `dequeue`, `peek`, `isEmpty`, `isFull` | **O(1)** |
| `displayQueue` | O(n) |
| Space | O(CAPACITY), fixed |

## C vs Java

| | `circular_queue-array.c` | `ConveyorDispatch.java` |
|---|---|---|
| Structure | one `ConveyorQueue` struct, free functions taking `q` | `ConveyorQueue` class with private fields |
| Return a dequeued package | output parameter + `int` success flag | return the object, or `null` |
| Dispatched slot | left as is (stale) | set to `null` for the garbage collector |
| Package ID | automatic number | typed in by the user |
| Destinations with spaces | no (`%29s`) | yes (`readLine()` reads the whole line) |
| Bad menu input | `scanf` check + clear the line | `parseInt` in `try/catch` |
| Layers | single file of functions | model / data structure / application classes |

## What changed (simplified version)

| Before | Now | Why |
|---|---|---|
| `import java.util.Scanner;` and `scanner.nextLine()` | A small `readLine()` method built on `System.in.read()` | No imports are needed. `System` is part of `java.lang` |
| `scanner.close()` at the end of `main` | Removed | There is no `Scanner` to close |
| `try { ... } catch { ... }` squeezed onto two lines | Spread over separate lines with braces | Easier to read |

The queue itself (`ConveyorQueue`) and the menu options did not change, so the program behaves exactly as before.
