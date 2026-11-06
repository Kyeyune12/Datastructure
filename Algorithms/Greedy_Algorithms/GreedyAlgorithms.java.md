# GreedyAlgorithms.java

The runnable Java program for [`GreedyProblems.md`](GreedyProblems.md). It was previously only a code block inside that file; it now lives in its own `.java` file so it can be compiled directly.

## How to run

```sh
javac GreedyAlgorithms.java
java GreedyAlgorithms
```

## What it does

Two greedy algorithms: **fractional knapsack** (sort by value/weight, take whole items, then a fraction of the last) and **activity selection** (sort by finish time, keep every activity that starts after the last chosen one finished). Both sort with a hand-written merge sort; the value/weight comparison uses integer cross-multiplication (`v1*w2 > v2*w1`) so no rounding error can change the order.

## The example

Capacity 50 with items (10,60), (20,100), (30,120): take items 0 and 1 fully (weight 30, value 160) and 20/30 of item 2 (value 80): **240**. For the six activities the chosen indices are 0, 3 and 4.

## Output

```
Optimal fractional value: 240.0
Item 0: 1.0
Item 1: 1.0
Item 2: 0.6666666666666666
Selected original indices: 0 3 4 
```

## Questions you may be asked

- **Why is greedy correct here?** Exchange argument: swapping any optimal choice for the greedy one never makes it worse (see the proofs in `GreedyProblems.md`).
- **Why does greedy fail for 0/1 knapsack?** Items cannot be split, so the best-ratio item may waste capacity. That needs dynamic programming (see `Dynammic_Programming/PartB_Q3_Knapsack`).
- **Complexity?** O(n log n), dominated by the sort.
