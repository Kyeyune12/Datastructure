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
| `shuffleInPlace(int[] arr)` | Shuffles using a new `Random`, so the order is different on every run |
| `shuffleInPlace(int[] arr, Random random)` | Shuffles using the `Random` you pass in. Give it a fixed seed for repeatable results |
| `main` | Shuffles `{1, 2, 3, 4, 5, 6}` with seed 42 and prints the result |

## How Fisher–Yates works

```java
for (int i = arr.length - 1; i > 0; i--) {
    int j = random.nextInt(i + 1);    // uniform in [0, i]
    swap(arr, i, j);
}
```

Work from the back of the array. At each step, pick one of the elements not yet placed, uniformly at random (it can be the current one), and swap it into position `i`. Position `i` is then final, and the loop moves one step left.

**Why every ordering is equally likely:** the last position has n equally likely choices, the next has n − 1, and so on. That gives n·(n−1)·…·1 = **n!** equally likely sequences of choices. Each sequence produces a different ordering, and there are exactly n! orderings, so each one has probability 1/n!.

A common mistake is to pick `j` from the *whole* array every time. That produces nⁿ equally likely sequences of choices. nⁿ is not a multiple of n!, so some orderings must come up more often than others.

## Output

```
2 1 5 6 4 3
```

The seed is fixed at 42, so this output is the same on every run.

## Complexity

- **Time:** Θ(n), from exactly n − 1 swaps.
- **Extra space:** O(1). The array is shuffled in place.

## Notes

- `Random.nextInt(bound)` is used instead of `random.nextInt() % bound`, because the `%` version favours small numbers (*modulo bias*).
- `java.util.Random` is a pseudo-random generator. It is fine for games and demos, but not for anything security-related; use `SecureRandom` for that.
- A `null` array or `null` generator throws `NullPointerException` straight away.
