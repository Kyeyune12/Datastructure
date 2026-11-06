# queue_array_and_linked_list.c

The **C version** of [`QueueArrayAndLinkedList.java`](QueueArrayAndLinkedList.java). Two queues: a circular array queue and a linked queue. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra queue_array_and_linked_list.c -o queue_array_and_linked_list
./queue_array_and_linked_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

The array queue keeps `front`, `rear` and `count`. `rear = (rear + 1) % capacity` wraps to index 0, so freed slots are reused. The linked queue adds at `tail` and removes at `head`. All operations are O(1).

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

- **Why a circular array?** A plain array queue wastes the slots in front of `front`. Wrapping with `%` reuses them.
- **How is a full queue told from an empty one?** With the separate `count`; `front == rear` alone would be ambiguous.
- **What does the demo prove?** After one dequeue and one enqueue, the 40 is stored at index 0 (wrapped), and both queues still return 20, 30, 40 in the same order.
