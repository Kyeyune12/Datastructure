# multiplier_list.c

The **C version** of [`MultiplierList.java`](MultiplierList.java). Multiplies a big number stored one digit per node (least significant digit first) by a whole number `k`. It prints exactly the same output as the Java program.

## How to run

```sh
gcc -std=c11 -Wall -Wextra multiplier_list.c -o multiplier_list
./multiplier_list
```

## What is different from Java

**C versus Java.** `struct` replaces the class; nodes are made with `malloc` and every node that is removed is `free`d by hand (Java's garbage collector does this for you). A `NULL` pointer plays the role of Java's `null`, and the list/stack/queue itself is a small struct that you pass by pointer (`List *l`) instead of calling a method on `this`.

## Operations

School multiplication: each digit times `k` plus the carry; the last decimal digit is stored and the rest is carried on. A leftover carry becomes extra digits at the end. A stack-allocated `dummy` node removes the "is the answer list empty yet?" check.

## Output

```
99 * 5, LSD first: 5 -> 9 -> 4 -> null
```

## Questions you may be asked

- **Why store the digits backwards?** The ones digit is first, so we can start at the head and carry forward the way we do by hand.
- **Complexity?** O(d) time and space for d digits (plus O(log k) for the final carry).
- **Why free both lists?** `head` and `result` are separate allocations; `free_list` walks each one.
