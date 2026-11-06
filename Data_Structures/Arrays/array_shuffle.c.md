# array_shuffle.c

The **Fisher-Yates shuffle** in C: puts an array into a random order where every possible order is equally likely. Same idea as [ArrayShuffle.java](ArrayShuffle.java.md).

**Proof of correctness:** section 6 of [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
gcc array_shuffle.c -o shuffle
./shuffle
```

## What is inside

| Function | What it does |
| --- | --- |
| `shuffle(arr, n)` | Shuffles `arr` in place |
| `printArray(arr, n)` | Prints the values on one line |
| `main` | Seeds the random numbers once with `srand(42)`, then shuffles `{1, 2, 3, 4, 5, 6}` and prints before and after |

## The shuffle loop

```c
for (int i = n - 1; i > 0; i--) {
    int j = rand() % (i + 1);   // random index from 0 to i

    int temp = arr[i];          // swap arr[i] and arr[j]
    arr[i] = arr[j];
    arr[j] = temp;
}
```

Work from the back. Position i is filled with a random element from the part not yet fixed (indexes 0..i). After that swap, position i is final and the loop moves left.

## Output

On Linux (glibc):

```
Before: 1 2 3 4 5 6
After:  4 3 5 2 6 1
```

A different C library (Windows, macOS) produces a different order for the same seed. The shuffle is still correct.

## Complexity

- **Time O(n)**: exactly n - 1 swaps.
- **Extra space O(1)**: the shuffle happens inside the same array.

## Questions you may be asked

- **Why seed with `srand` only once?** Seeding again before every shuffle with the same number gives the same "random" order every time.
- **Why `% (i + 1)` and not `% n`?** Picking from the whole array every time (the "naive shuffle") makes some orders more likely than others. Picking only from 0..i gives each of the n! orders the same chance.
- **Is `rand() % (i + 1)` perfectly fair?** Very nearly. `RAND_MAX` is huge compared with 6, so the tiny "modulo bias" can't be seen here. For cards in a real casino you would use a cryptographic random generator.
- **Where is this used?** Shuffling a playlist, dealing cards in a game, randomising exam questions.
