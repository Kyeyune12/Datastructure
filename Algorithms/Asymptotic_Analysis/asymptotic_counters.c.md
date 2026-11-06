# asymptotic_counters.c

The **C version** of [`AsymptoticCounters.java`](AsymptoticCounters.java.md). Same loops, same formulas, same output.

## How to run

```sh
gcc -std=c11 -Wall -Wextra asymptotic_counters.c -o counters
./counters
```

## What is different from Java

The counter is a `static long` and a `TICK()` macro replaces `ops++`. Arrays are `malloc`ed in `main` and freed at the end. The program returns a non-zero exit code if any formula fails, so it can be used in a script.

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

- **Why `long`?** For n = 1024 the nested loop already counts over 3 million units, and larger n would overflow a 16 or 32-bit `int` sooner.
