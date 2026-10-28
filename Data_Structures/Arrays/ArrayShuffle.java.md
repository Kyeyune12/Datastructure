# ArrayShuffle.java

Shuffles an `int[]` into a random order in place, using the **Fisher–Yates** algorithm, so that every possible ordering is equally likely.

**Language:** Java · **C version:** [array_shuffle.c](array_shuffle.c.md) · **Proof of correctness:** section 6 of [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
javac ArrayShuffle.java
java ArrayShuffle
```

## What is inside

| Method | What it does |
| --- | --- |
| `shuffle(int[] arr)` | Shuffles the array in place using Fisher–Yates |
| `print(int[] arr)` | Prints all the values on one line |
| `main` | Prints `{1, 2, 3, 4, 5, 6}`, shuffles it, and prints it again |

## How Fisher–Yates works

```java
for (int i = arr.length - 1; i > 0; i--) {
    int j = (int) (Math.random() * (i + 1));   // random index from 0 to i

    int temp = arr[i];                         // swap arr[i] and arr[j]
    arr[i] = arr[j];
    arr[j] = temp;
}
```

Start at the back of the array. At each step, pick a random element from the part that is not placed yet (it can be the current element), and swap it into position `i`. Position `i` is then final, and the loop moves one step left.

**Getting a random index:** `Math.random()` gives a decimal from 0.0 up to, but not including, 1.0. Multiplying by `(i + 1)` gives a number from 0 up to just below `i + 1`. Casting to `int` cuts off the decimal part, so `j` is a whole number from 0 to `i`.

**Why every ordering is equally likely:** the last position has n equally likely choices, the next has n − 1, and so on. That gives n·(n−1)·…·1 = **n!** equally likely sequences of choices. Each one produces a different ordering, and there are exactly n! orderings, so each has probability 1/n!.

A common mistake is to pick `j` from the *whole* array every time. That gives nⁿ sequences of choices. nⁿ is not a multiple of n!, so some orderings would come up more often than others.

## Output (example)

```
Before: 1 2 3 4 5 6
After:  3 4 6 1 2 5
```

The "After" line is **different on every run**, because `Math.random()` is not given a fixed seed.

## Complexity

- **Time:** Θ(n), from exactly n − 1 swaps.
- **Extra space:** O(1). The array is shuffled in place.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| `import java.util.Random;` and `random.nextInt(i + 1)` | `(int) (Math.random() * (i + 1))` | `Math` is part of `java.lang`, so it needs no import |
| `import java.util.Arrays;` and `Arrays.toString(values)` | A small `print` method with a `for` loop | No import, and the loop is easy to explain |
| A fixed seed (42) gave the same result every run | The result is different every run | `Math.random()` can't be given a seed. This is also what a real shuffle should do |
| Only the shuffled array was printed | Both "Before" and "After" are printed | You can see the shuffle happen |
