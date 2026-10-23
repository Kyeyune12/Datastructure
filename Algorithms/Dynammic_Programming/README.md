# Dynamic Programming Assignments – Multistage Graphs, All-Pairs, Knapsack

Author: Allan Victor Mugisa – Software Engineering, Makerere University

Every question has its own folder with:

- `answer.md` – written answer: idea, pseudocode, hand trace, time–space complexity
- a **C** program (`*.c`) and a **Java** program (`*.java`) – same algorithm, same output
- `output.txt` – the program output (C and Java outputs were compared and are identical)

## Questions and answers at a glance

| Folder | Question | Key answer |
|---|---|---|
| `PartA_Q1_Backward_BCOST` | Backward BCOST, assuming BCOST(l) is ready | Shortest 1 → 12 = **16** (1-3-6-10-12). Θ(n + e) time, O(n) extra space |
| `PartA_Q2_General_Graph` | BCOST(l) may not be ready | Topological sort first (DAG); Bellman-Ford if there's a cycle, with negative-cycle detection |
| `PartA_Q3_Forward_Single_Destination` | Forward approach → every node to t | One pass gives COST(i) for all i; prints a path from every vertex to 12 |
| `PartA_Q4_Arbitrary_S_and_T` | Redefine S and t arbitrarily? | Yes, for a DAG with no negative cycles; unreachable → ∞, s = t → 0 |
| `PartB_Q1_All_Pairs_Floyd` | All-pairs (slide's k-i-j loop) | Floyd-Warshall, Θ(n³) time, Θ(n²) space, paths via next[][] |
| `PartB_Q2_Negative_Weights` | Consider negative weights | Floyd still works; A(i,i) < 0 detects a negative cycle; Dijkstra fails |
| `PartB_Q3_Knapsack` | 0/1 knapsack with DP | V[i][c] table, Θ(n·m); example answer = 6 with items {1, 3} |

## Graph used (slide 20)

```
Stage:   1        2            3          4           5
         1   ->  2,3,4,5  ->  6,7,8  ->  9,10,11  ->  12
Edges: 1-2:9 1-3:7 1-4:3 1-5:2 | 2-6:4 2-7:2 2-8:1 3-6:2 3-7:7 4-8:11 5-7:11 5-8:8
       6-9:6 6-10:5 7-9:4 7-10:3 8-10:5 8-11:6 | 9-12:4 10-12:2 11-12:5
```

## How to build and run

C (gcc):
```
gcc -O2 -o prog file.c
./prog
```

Java (JDK 11+):
```
javac ClassName.java
java ClassName
```

Each program is self-contained (no shared headers or packages), so any folder can be compiled on its own.
