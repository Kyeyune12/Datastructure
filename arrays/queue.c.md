# queue.c

A **queue** (FIFO: First In, First Out) stored in a fixed-size **circular array**, written in the same style as the stack in [adts.c](adts.c.md). This used to be an empty placeholder; it now completes the pair of ADTs in this folder.

**Java version:** [QueueArrayAndLinkedList.java](../Data_Structures/Queues/QueueArrayAndLinkedList.java.md) · **Full menu program:** [circular_queue-array.c](../Data_Structures/Queues/Warehouse_Conveyor_Dispatch/circular_queue-array.c.md)

## How to run

```sh
gcc queue.c -o queue
./queue
```

## How the queue is stored

```c
#define SIZE 5

struct Queue {
    int arr[SIZE];
    int front;   // index of the next value to leave
    int rear;    // index of the last value that joined
    int count;   // how many values are in the queue
};
```

| Function | What it does |
| --- | --- |
| `enqueue(q, value)` | If full, prints `Queue overflow!`. Otherwise `rear = (rear + 1) % SIZE`, store, `count++` |
| `dequeue(q)` | If empty, prints `Queue underflow!` and returns -1. Otherwise read `arr[front]`, `front = (front + 1) % SIZE`, `count--` |
| `peekQueue(q)` | The front value, or -1 if empty |
| `isQueueEmpty(q)` / `isQueueFull(q)` | `count == 0` / `count == SIZE` |

`% SIZE` sends an index back to 0 after the last slot, so the array is used in a circle and nothing is ever shifted.

## Output

```
***Welcome to the Queue ADT***
front item (Peek): 12
Dequeued: 12
front item (Peek): 10
```

Compare with adts.c: the same three values went in, but the queue gives back **12** (the first in) where the stack gave back **13** (the last in).

## Questions you may be asked

- **Why start `rear` at -1?** The first `enqueue` does `rear = (-1 + 1) % 5 = 0`, so the first value lands in slot 0.
- **Why keep `count`?** It makes full and empty easy to tell apart, because `front` and `rear` alone can look the same in both cases.
