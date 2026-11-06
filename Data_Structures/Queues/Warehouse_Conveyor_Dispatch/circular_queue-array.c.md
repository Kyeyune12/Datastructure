# circular_queue-array.c

**Project B (Queue), C version.** The Warehouse Conveyor Dispatch system: packages are loaded onto a belt (**enqueue**) and dispatched in arrival order (**dequeue**). The belt is a **circular array** of 5 slots.

**Java version:** [ConveyorDispatch.java](ConveyorDispatch.java.md) · **Project overview:** [README.md](README.md)

## How to run

```sh
gcc circular_queue-array.c -o conveyor
./conveyor
```

## The data

```c
struct Package {
    int id;
    char destination[NAME_LEN];
};

struct Queue {
    struct Package items[CAPACITY];
    int front;   // index of the next package to leave
    int rear;    // index of the last package that joined
    int count;   // how many packages are on the belt
};
```

`main` starts the belt empty with `front = 0`, `rear = -1`, `count = 0`.

## The operations

| Function | What it does | Cost |
| --- | --- | --- |
| `isEmpty(q)` / `isFull(q)` | `count == 0` / `count == CAPACITY` | O(1) |
| `enqueue(q, id, destination)` | `rear = (rear + 1) % CAPACITY`, store the package there, `count++` | O(1) |
| `dequeue(q, &out)` | Copy `items[front]` into `out`, `front = (front + 1) % CAPACITY`, `count--`. Returns 1, or 0 if empty | O(1) |
| `peek(q, &out)` | Copy the front package without moving anything | O(1) |
| `display(q)` | Visit `count` slots starting at `front`, wrapping with `% CAPACITY` | O(n) |

Functions take `struct Queue* q` (a pointer) so that they change the real belt, not a copy.

## Wrap-around trace

Load 5 packages (slots 0 to 4; full). Dispatch #1 (front moves to 1). Load #7: `rear = (4 + 1) % 5 = 0`, so it goes into **slot 0**. The belt now reads, front to rear: slots 1, 2, 3, 4, 0.

## Menu

`1` load · `2` dispatch · `3` peek · `4` view belt · `5` isEmpty/isFull · `6` exit. Package IDs are given out automatically. Letters typed at the menu are caught (`scanf` returns 0, so the line is cleared), and `%29s` stops long names overflowing the buffer.

## Questions you may be asked

- **Why `% CAPACITY`?** It turns the end of the array back into the start, so freed slots get reused and nothing is ever shifted.
- **Why does `dequeue` take `struct Package* out` instead of returning a package?** It must also say whether it worked. It returns 1 or 0, and the package is copied into `out`.
- **Why `count` as well as `front` and `rear`?** It makes "full" and "empty" a one-line check that is never ambiguous.
