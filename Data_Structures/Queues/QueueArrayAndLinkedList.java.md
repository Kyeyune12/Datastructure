# QueueArrayAndLinkedList.java

A **queue** is **FIFO** (First In, First Out): the first item added is the first removed, like a line at the bank. This file builds the same queue **two ways**.

**Group project using a queue:** [Warehouse Conveyor Dispatch](Warehouse_Conveyor_Dispatch/README.md) · C version in the team's first folder: [queue.c](../../arrays/queue.c.md)

## How to run

```sh
javac QueueArrayAndLinkedList.java
java QueueArrayAndLinkedList
```

## The two versions

| | `CircularArrayQueue` | `LinkedQueue` |
| --- | --- | --- |
| Storage | `int[] data`, plus `front`, `rear` and `count` | Chain of `Node`s with `head` (front) and `tail` (back) |
| `enqueue` | `rear = (rear + 1) % data.length; data[rear] = value; count++;` | Old tail points to the new node, which becomes the tail |
| `dequeue` | Read `data[front]`; `front = (front + 1) % data.length; count--;` | Read `head.value`; `head = head.next` |
| Full? | `count == data.length` prints `Queue is full!` | Never full |
| Empty? | `count == 0` | `head == null` |

All operations are **O(1)**.

## Why "circular"?

Without wrap-around, `front` and `rear` only move right, and the array "runs out" even when the front slots are free. `% data.length` sends an index back to 0 after the last slot, so freed slots are reused **without shifting anything**.

Trace with capacity 3: add 10, 20, 30 (slots 0, 1, 2; full). Remove 10 (front moves to 1). Add 40: `rear = (2 + 1) % 3 = 0`, so 40 goes into **slot 0**.

## Output

```
*** Queue: circular array vs linked list ***
Array queue full: true
10 = 10
Peek: 20, 20
20 = 20
30 = 30
40 = 40
```

## Questions you may be asked

- **Why keep `count`?** When `front` and `rear` are in certain positions, a full queue and an empty queue look the same. `count` tells them apart.
- **Why is a plain (non-circular) array queue bad?** Either `dequeue` shifts every item left (O(n)), or the free space at the front is wasted.
- **Where are queues used?** Printer jobs, customer-service call lines, CPU task scheduling, and breadth-first search.
