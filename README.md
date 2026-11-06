# Data Structures & Algorithms

Group coursework for Data Structures & Algorithms, BSc Software Engineering, Makerere University.

Every topic has a **C** version and a **Java** version of the same program with the same output, and a `.md` file next to each program that explains it. Each explanation ends with **"Questions you may be asked"**, which is the place to revise before the presentation.

## Map of the repository

| Folder | What is in it |
| --- | --- |
| [`arrays/`](arrays) | The team's first C files (Code::Blocks project): array stack [`adts.c`](arrays/adts.c.md), array queue [`queue.c`](arrays/queue.c.md), hand-built linked list, passing arrays to functions. **These set the code style used everywhere else.** |
| [`Data_Structures/Arrays`](Data_Structures/Arrays) | Address formulas (1D/2D/3D, row- and column-major), merge, traversal, min/max, Fisher-Yates shuffle |
| [`Data_Structures/Linked_Lists`](Data_Structures/Linked_Lists) | Singly, doubly and circular linked lists; big-number multiplication with a list |
| [`Data_Structures/Stacks`](Data_Structures/Stacks) | Array stack vs linked stack; infix → postfix/prefix and evaluation; **Project A: Search & Rescue Backtrack** |
| [`Data_Structures/Queues`](Data_Structures/Queues) | Circular-array queue vs linked queue; **Project B: Warehouse Conveyor Dispatch** |
| [`Data_Structures/Trees`](Data_Structures/Trees) | Lecture notes: tree traversal, backtracking, branch and bound (PDF) |
| [`Algorithms/Asymptotic_Analysis`](Algorithms/Asymptotic_Analysis) | Big-O of loops, reading-exercise algorithm |
| [`Algorithms/Greedy_Algorithms`](Algorithms/Greedy_Algorithms), [`Divide_And_Conquer`](Algorithms/Divide_And_Conquer) | Written problem sets |
| [`Algorithms/Greedy_And_Divide_And_Conquer_Assignment`](Algorithms/Greedy_And_Divide_And_Conquer_Assignment) | Bandwidth allocation, Kruskal MST, text wrapping, water-leak search, Strassen, longest common prefix, IP route summarisation |
| [`Algorithms/Dynammic_Programming`](Algorithms/Dynammic_Programming) | Multistage graphs (backward/forward), general graphs, Floyd-Warshall, negative weights, 0/1 knapsack |

## How to build and run

C:

```sh
gcc file.c -o prog
./prog
```

Java (JDK 11 or newer):

```sh
javac FileName.java
java FileName
```

Every program is one self-contained file, so any file can be compiled on its own.

## Team code style

All the code is written the same way so that anyone in the team can explain or change any file.

**C**

- `#include <stdio.h>` (and only the other headers really needed).
- Sizes are `#define` constants: `#define MAX 5`.
- Records are `struct Name { ... };` used as `struct Name`, with no `typedef`.
- Functions that change a structure take a pointer, `struct Stack* s`, and use `s->top`.
- `int main()` ... `return 0;`
- Braces on every `if` and loop, and one statement per line.
- Errors print a short message (`Stack overflow!`, `Queue is empty!`) and return -1 / 0 instead of crashing.
- Comments use `//` and say *why*, not just what.

**Java**

- One `public class` per file with `public static void main`.
- Nodes and records are small nested `static class`es with plain fields and one constructor.
- No getters/setters, no `@Override toString`, no built-in `Stack`, `Queue`, `ArrayList` or `LinkedList`: every structure is built by hand.
- Empty/full cases print a message and return -1 or `null`, matching the C version.
- `Scanner` is used for keyboard input in the two menu programs.
