# AsymptoticCounters.java

A runnable check of [`LoopAnalysis.md`](LoopAnalysis.md) and [`ReadingExerciseAlgorithm.md`](ReadingExerciseAlgorithm.md). Every loop from those notes is rewritten with a counter that adds one unit for each initialisation, condition test (including the final failed one), update and body statement. The program then compares the measured count with the formula in the notes.

## How to run

```sh
javac AsymptoticCounters.java
java AsymptoticCounters
```

## Formulas checked

| Loop | Formula |
|---|---|
| single `while` / `for` | 3n + 2 |
| halving loop | 3L + 2, L = floor(log2 n) + 1 |
| two independent loops | 3n^2 + 4n + 2 |
| triangular loops | 1.5n^2 + 5.5n + 2 |
| `for` containing a halving `while` | 3nL + 4n + 2 |
| shared pointer | 6n + 3 |
| linear search | hit at index r: 3r + 4; miss: 3n + 3 |
| matrix sum | 3rc + 4r + 3 |
| binary search | miss after h rounds: 5h + 4; hit on round h: 5h + 1 |
| duplicate check (distinct data) | 1.5n(n-1) + 4n + 3 |
| insertion sort | 6n - 4 + 3I, I = inversions |

## How to read it

Every formula is tested for many sizes (n up to 200; r, c up to 30; every present and absent key in binary search). The program prints `FAIL` lines if any differ, then a small growth table. The table shows the point of Big-O: from n = 1 to 1024 the single loop grows about 600x, but the nested loop grows about 350,000x.

## Output

```
*** Asymptotic counters: measured units vs formula ***

   n | while 3n+2 | nested 3n^2+4n+2 | for+halving
   1 |          5 |                 9 |           9
   2 |          8 |                22 |          22
   4 |         14 |                66 |          54
   8 |         26 |               226 |         130
  16 |         50 |               834 |         306
1024 |       3074 |           3149826 |       37890

All formulas match the measured counts.
```

## Questions you may be asked

- **Why count the final failed test?** The condition runs once more than the body; leaving it out gives a wrong exact count (though the same Big-O).
- **Do exact counts matter?** No: Theta(n) is the same whether the count is 3n + 2 or 5n. The exact count only shows where the bound comes from.
- **Why does the shared-pointer loop stay linear?** `j` is never reset, so the inner body runs n times in total, not n times per outer round.
