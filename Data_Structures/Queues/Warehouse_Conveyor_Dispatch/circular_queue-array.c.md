# circular_queue-array.c

**Project B (Queue), in C.** A *Warehouse Conveyor Belt Dispatch System*. Packages are loaded onto a belt and dispatched in the order they arrived. It's built as a **circular queue in a fixed array of 5 slots**, using `(rear + 1) % CAPACITY` so slots are reused without shifting.

**Language:** C · **Java version:** [ConveyorDispatch.java](ConveyorDispatch.java.md) · **Project overview:** [README.md](README.md) · **Stack counterpart:** [mountTracker.c](../../Stacks/Search_and_Rescue_Backtrack/mountTracker.c.md)

## How to run

```sh
gcc circular_queue-array.c -o conveyor
./conveyor
```

Menu-driven. Destinations are a single word (no spaces), and package IDs are numbered automatically.

## Why a queue, and why circular

A conveyor belt is the textbook **FIFO** (first in, first out) system: the first package loaded is the first to reach the end. New packages join at the **rear**; dispatch happens at the **front**.

In a plain array queue, dequeuing would either shift every remaining item one place left (O(n) each time) or leave dead slots behind at the start of the array. A **circular** queue does neither. `front` and `rear` move forward and **wrap around** to index 0 when they pass the end:

```
index:    0      1      2      3      4
        [ F  ] [ B  ] [ C  ] [ D  ] [ E  ]
          ^rear  ^front
```

After package #1 has left slot 0, slot 0 is reused for package #6. The modulo operator `% CAPACITY` turns index 5 back into 0.

## The data structures

```c
#define CAPACITY 5

typedef struct {
    int  packageId;
    int  numItems;          /* declared but never used by the program */
    char destination[30];
} Package;

typedef struct {
    Package items[CAPACITY];
    int front;   /* index of the next package to dispatch */
    int rear;    /* index of the most recently loaded package */
    int count;   /* how many packages are on the belt */
} ConveyorQueue;
```

`typedef` lets the code write `Package` and `ConveyorQueue` instead of `struct ...`. The whole queue, including its array, is one struct, so a single pointer `q` gives every function access to everything.

### Why keep a `count`?

With only `front` and `rear`, an **empty** queue and a **full** queue can look the same, because in both cases `rear` is just behind `front`. Tracking `count` separately removes the confusion. The queue is empty when `count == 0` and full when `count == CAPACITY`, and all 5 slots can be used. (The other common fix leaves one slot permanently empty.)

## Walking through the operations

### `initQueue` — start state

```c
q->front = 0;  q->rear = -1;  q->count = 0;
```

`rear = -1` means "nothing loaded yet". The first enqueue computes `(-1 + 1) % 5 = 0`, so the first package lands in slot 0.

### `isEmpty` / `isFull` — O(1)

`count == 0` and `count == CAPACITY`.

### `enqueue` — load a package, O(1)

```c
if (isFull(q)) { printf("[BELT FULL] ..."); return; }   /* overflow check */
q->rear = (q->rear + 1) % CAPACITY;                     /* wrap-around */
q->items[q->rear].packageId = id;
strncpy(q->items[q->rear].destination, destination, sizeof(...) - 1);
q->items[q->rear].destination[sizeof(...) - 1] = '\0';
q->count++;
```

- **Overflow check first**, so a full belt is refused with a message instead of overwriting the oldest package.
- **Move `rear`, then store.** `rear` always points at the last *filled* slot.
- **`strncpy` + manual `'\0'`.** `strncpy` copies at most 29 characters but doesn't guarantee a terminator, so the last byte is set to `'\0'` explicitly. This is the safe way to copy into a fixed buffer.

### `dequeue` — dispatch a package, O(1)

```c
if (isEmpty(q)) { printf("[BELT EMPTY] ..."); return 0; }   /* underflow check */
*dispatched = q->items[q->front];       /* copy the package out first */
q->front = (q->front + 1) % CAPACITY;   /* wrap-around */
q->count--;
return 1;
```

- C functions can only return one value, so the package comes back through the **output parameter** `Package *dispatched`, and the `int` return value means success (1) or failure (0).
- `*dispatched = q->items[q->front]` copies the whole struct, including the destination text.
- Nothing is shifted and the old slot isn't erased. It's simply outside the `front … rear` range now and will be overwritten by a later enqueue.

### `peekFront` — O(1)

Same pattern as `dequeue` (output parameter plus a success flag), but it **doesn't move `front` or change `count`**.

### `displayQueue` — O(n)

```c
for (i = 0; i < q->count; i++) {
    int index = (q->front + i) % CAPACITY;
    printf("    Slot %d: Package #%d -> %s\n", index, ...);
}
```

Starts at `front` and steps `count` times, wrapping with `%`. Printing the **slot index** shows the circular behaviour: in the sample run below the list reads slots 1, 2, 3, 4, 0.

### `main` — the menu

- `if (scanf("%d", &choice) != 1)` catches non-numeric input, and `while (getchar() != '\n');` throws the bad line away. Typing a letter can't jam the program.
- `scanf("%29s", destination)` reads at most 29 characters, so the 30-byte buffer can't overflow.
- `nextId` gives each package an automatic number.
- Option 5 prints `isEmpty` and `isFull` directly, which is handy for demonstrating the boundary conditions.

## Sample run (shows wrap-around and the full check)

Input: load BayA, BayB, BayC → view → dispatch → load BayD, BayE, BayF, BayG → view → status → peek → letter "x" → exit. Menu lines are omitted.

```
  [LOADED] Package #1 -> BayA placed on belt at slot 0.
  [LOADED] Package #2 -> BayB placed on belt at slot 1.
  [LOADED] Package #3 -> BayC placed on belt at slot 2.
  [BELT STATUS] Queue contents from front to rear:
    Slot 0: Package #1 -> BayA
    Slot 1: Package #2 -> BayB
    Slot 2: Package #3 -> BayC
  [DISPATCHED] Package #1 -> BayA has left the belt.
  [LOADED] Package #4 -> BayD placed on belt at slot 3.
  [LOADED] Package #5 -> BayE placed on belt at slot 4.
  [LOADED] Package #6 -> BayF placed on belt at slot 0.
  [BELT FULL] Cannot load package #7. Conveyor belt has reached its 5-package capacity.
  [BELT STATUS] Queue contents from front to rear:
    Slot 1: Package #2 -> BayB
    Slot 2: Package #3 -> BayC
    Slot 3: Package #4 -> BayD
    Slot 4: Package #5 -> BayE
    Slot 0: Package #6 -> BayF
  isEmpty: NO   isFull: YES
  Next to dispatch: Package #2 -> BayB
Invalid input. Please enter a number.
Exiting. Goodbye!
```

Package #6 went into **slot 0** (`rear = (4 + 1) % 5 = 0`), reusing the slot package #1 left. That's the circular queue working.

### State trace

| After | front | rear | count | Slots 0–4 |
|---|---|---|---|---|
| `initQueue` | 0 | −1 | 0 | `_ _ _ _ _` |
| load 1, 2, 3 | 0 | 2 | 3 | `1 2 3 _ _` |
| dispatch (1 leaves) | 1 | 2 | 2 | `(1) 2 3 _ _` |
| load 4, 5 | 1 | 4 | 4 | `(1) 2 3 4 5` |
| load 6 (wraps) | 1 | **0** | 5 | `6 2 3 4 5` |
| load 7 | refused: `count == CAPACITY` | | | |

`(1)` marks a stale slot: still in memory, but outside the queue.

## Complexity

| Operation | Time |
|---|---|
| `enqueue`, `dequeue`, `peekFront`, `isEmpty`, `isFull` | **O(1)**, no shifting |
| `displayQueue` | O(n) |
| Space | O(CAPACITY), fixed |

## Small things to tidy

- `numItems` in `Package` is never set or used. Either use it (ask for the number of items when loading) or remove it.
- `nextId++` runs even when the belt is full, so a refused package still uses up an ID (#7 above), and the next accepted one becomes #8. Increasing `nextId` only when `enqueue` succeeds would need `enqueue` to return a success flag, like `dequeue` does.
