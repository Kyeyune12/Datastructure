# array_shuffle.c

The **Fisher–Yates** shuffle in C. Using only the standard `rand()`, it shuffles an array so that every ordering is equally likely, and it avoids modulo bias.

**Language:** C11 · **Java version:** [ArrayShuffle.java](ArrayShuffle.java.md) · **Proof of correctness:** section 6 of [MatrixMergeAnalysis.md](MatrixMergeAnalysis.md)

## How to run

```sh
gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_shuffle.c -o shuffle
./shuffle
```

## What is inside

| Function | What it does |
| --- | --- |
| `uniform_below(bound)` *(static)* | Returns a random whole number in `[0, bound)` with every value equally likely, using rejection sampling |
| `shuffleInPlace(arr, n)` | Shuffles `arr` in place. Returns `false` if `arr` is `NULL` (with n > 0) or `n` is too big |
| `main` | Calls `srand(42)` **once**, shuffles `{1, 2, 3, 4, 5, 6}` and prints the result |

## Why rejection sampling?

`rand()` returns a number from 0 to `RAND_MAX`. To get a number below `bound`, people often write `rand() % bound`. Unless `RAND_MAX + 1` divides exactly by `bound`, that makes some results slightly more likely than others (*modulo bias*).

`uniform_below` works round this:

```c
limit = range - range % bound;          // largest multiple of bound that fits
do { r = rand(); } while (r >= limit);  // throw away the uneven top end
return r % bound;                       // now every value is equally likely
```

## The shuffle loop

```c
for (remaining = n; remaining > 1; --remaining) {
    i = remaining - 1;
    j = uniform_below(remaining);    // uniform in [0, i]
    swap(arr[i], arr[j]);
}
```

This is the same as the Java version: fill the array from the back, each time picking one of the elements not yet placed.

## Output

On Linux with glibc:

```
4 3 5 2 6 1
```

The result **depends on your C library**. The C standard doesn't say which numbers `rand()` produces, so Windows (MinGW/MSVC) or macOS will print a different order for the same seed. The shuffle is still correct on every platform.

## Complexity

- **Swaps:** exactly n − 1.
- **Time:** *expected* Θ(n). Because rejection sampling can retry, there is no fixed worst-case limit, but retries are rare: each draw is rejected with probability below ½.
- **Extra space:** O(1).

## Notes

- Call `srand` **once** at the start of the program. Calling it before every shuffle with the same seed makes every shuffle produce the same order.
- `n` must be at most `RAND_MAX + 1`. Otherwise `rand()` can't produce every index needed, and the function returns `false`.
- `rand()` is not cryptographically secure, and rejection sampling can't fix a weak generator. It only removes the modulo bias.
