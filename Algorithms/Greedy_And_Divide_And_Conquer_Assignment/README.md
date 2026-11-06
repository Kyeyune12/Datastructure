# Group 2 EVE – Greedy and Divide & Conquer Assignment

Data Structures & Algorithms group assignment, Software Engineering, Makerere University.

Every question has its own folder with:

- a **C** program (`*.c`) and a **Java** program (`*.java`), with the same algorithm and the same output
- a **`.md` explanation for each program** (`file.c.md`, `File.java.md`): the problem, the idea, a section-by-section walkthrough of the code, a hand trace on the program's data, why the method is correct, time and space complexity, and C-vs-Java differences
- `output.txt`: the program's output (C and Java outputs were compared and are identical)

## Questions at a glance

| Folder | Technique | Problem | Result | Explanations |
|---|---|---|---|---|
| `Greedy_Q1_Bandwidth_Allocation` | Greedy | Share 100 Mbps between users by priority (fractional knapsack) | Benefit **445**, O(n log n) | [C](Greedy_Q1_Bandwidth_Allocation/bandwidth_allocation.c.md) · [Java](Greedy_Q1_Bandwidth_Allocation/BandwidthAllocation.java.md) |
| `Greedy_Q2_Kruskal_MST` | Greedy | Cable 6 campus buildings at least cost (Kruskal + Union-Find) | MST cost **14**, O(m log m) | [C](Greedy_Q2_Kruskal_MST/kruskal_mst.c.md) · [Java](Greedy_Q2_Kruskal_MST/KruskalMST.java.md) |
| `Greedy_Extra_Text_Wrap` | Greedy | Wrap text into lines of at most 20 characters, fewest lines | **5** lines, O(total characters) | [C](Greedy_Extra_Text_Wrap/text_wrap.c.md) · [Java](Greedy_Extra_Text_Wrap/TextWrap.java.md) |
| `DC_Q1_Water_Leak` | Divide & Conquer | Locate a pipe leak among 16 sensors (binary search) | Leak between sensors **9 and 10**, 5 checks, Θ(log n) | [C](DC_Q1_Water_Leak/water_leak.c.md) · [Java](DC_Q1_Water_Leak/WaterLeak.java.md) |
| `DC_Q2_Strassen_Matrix` | Divide & Conquer | Strassen 4 × 4 matrix multiplication, checked against the ordinary method | 49 multiplications instead of 64, Θ(n^2.81) | [C](DC_Q2_Strassen_Matrix/strassen_matrix.c.md) · [Java](DC_Q2_Strassen_Matrix/StrassenMatrix.java.md) |
| `DC_Extra_Longest_Common_Prefix` | Divide & Conquer | Longest common prefix of a list of words | `"inter"`, O(n · m) | [C](DC_Extra_Longest_Common_Prefix/longest_common_prefix.c.md) · [Java](DC_Extra_Longest_Common_Prefix/LongestCommonPrefix.java.md) |
| `DC_Extra_Route_Summarization` | Divide & Conquer | Summarise 4 IP routes into one (LCP on 32-bit strings) | **192.168.4.0/22**, O(n) | [C](DC_Extra_Route_Summarization/route_summarization.c.md) · [Java](DC_Extra_Route_Summarization/RouteSummarization.java.md) |

## How to build and run

C (gcc):
```
gcc -O2 -o prog file.c
./prog
```

Java (JDK 11+, needed for `String.repeat` in TextWrap):
```
javac ClassName.java
java ClassName
```

Each program is self-contained (no shared headers or packages), so any folder can be compiled on its own.
