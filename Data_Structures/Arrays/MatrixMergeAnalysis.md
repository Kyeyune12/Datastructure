# Arrays and matrix merging: implementation guide and analysis

## Portfolio build and navigation

This portfolio uses C11 and Java 8-compatible language features, verified with GCC and JDK 17. There are no third-party dependencies. Every `.java` and `.c` file contains a runnable `main` demonstration. Classes use the default package so each can be compiled independently. From a source file's directory:

```sh
javac ArrayOperations.java
java ArrayOperations
gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_operations.c -o arrays
./arrays
javac ArrayShuffle.java
java ArrayShuffle
gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_shuffle.c -o shuffle
./shuffle
```

Use the corresponding class name for lists, stacks and queues. `ExpressionEvaluator` additionally accepts a quoted expression. The greedy and divide-and-conquer chapters contain complete Java programs in fenced blocks: save each block using the filename specified above it, then compile and run. They remain in their required Markdown files to preserve the requested archive structure.

### Implementation policy: no collection shortcuts

All data structures are implemented using raw arrays, custom node classes, explicit link updates, and explicit loops. There is no `ArrayList`, library `LinkedList`, `Stack`, `Queue`, `Deque`, `ArrayDeque`, collection-based sorting, `Arrays.sort`, `Arrays.copyOf`, or `System.arraycopy` in the delivered implementations. Even the expression evaluator's operator/value stacks and token list are hand-written. Array-stack expansion allocates a new array and copies each element in a visible loop. Greedy preprocessing uses a hand-written merge sort.

Standard utilities provide exception types, null checks, pseudorandom numbers, lexical recognition, numeric parsing, arithmetic helpers and text output; they do not implement the data structures or algorithms being assessed. In particular, `StringBuilder` formats text and regular expressions recognize tokens; neither replaces the stack operations.

These implementations validate logical input errors and expose documented exceptions/status results. They are single-threaded teaching libraries with executable examples, not concurrent collections. Memory exhaustion remains possible; Java does not promise that every theoretically valid allocation will succeed. C callers must obey pointer/buffer-length contracts. Error checking cannot discover a lying buffer length.

## 1. What does “merge” mean?

Let $A$ be $m\times n$, $B$ be $p\times q$, and let each stored element occupy $s$ bytes. Define $N=mn+pq$. Without a placement rule, a merged rectangular matrix is not uniquely defined. Concatenating, adding, and merging sorted elements are different operations.

| Operation | Required dimensions | Shape of $C$ | Cells in $C$ |
|---|---|---|---|
| Vertical concatenation | $n=q$ | $(m+p)\times n$ | $N$ |
| Horizontal concatenation | $m=p$ | $m\times(n+q)$ | $N$ |
| Flatten then concatenate | None | One vector, or a chosen shape with $N$ cells | $N$ |
| Vertical concatenation with zero padding | None | $(m+p)\times\max(n,q)$ | $K=(m+p)\max(n,q)$ |
| Block diagonal placement | None | $(m+p)\times(n+q)$ | $K=(m+p)(n+q)$ |
| Elementwise sum (not concatenation) | $m=p$, $n=q$ | $m\times n$ | $mn$ |

The executable array merge concatenates the complete first vector followed by the second. It does not assume sorted inputs. For rectangular matrix discussion, vertical concatenation is the default when $n=q$. Flattening is the fallback when dimensions are unrelated. A reshape of $N$ elements to $r\times c$ requires $rc=N$.

## 2. Dense output: exact storage and lower bounds

Vertical concatenation allocates:

$$S_C=s(m+p)n=s(mn+pq)=sN\text{ bytes}.$$

With both inputs retained, element storage at peak is $sN+sN=2sN$, plus metadata, alignment and allocator/object overhead. Output storage is $\Theta(N)$; auxiliary storage **excluding inputs and output** is $\Theta(1)$ for direct copying. Do not call total space $O(1)$ merely because only loop indices are temporary.

Each of the $N$ output cells must be written, giving a lower bound $\Omega(N)$. A direct copy reads and writes each input element once, giving $O(N)$, hence:

$$T(m,n,p,q)=\Theta(mn+pq).$$

If zero padding creates $K>N$ output cells, materializing a dense result requires $\Theta(K)$ time and output space, including explicit initialization or its equivalent. A sparse virtual representation can avoid storing zeros but is a different output contract.

**Example:** $A:1000\times200$, $B:500\times200$, 4-byte elements. $C$ has 300,000 cells and uses 1,200,000 bytes (about 1.144 MiB). Keeping both inputs and output uses at least 2,400,000 bytes. Java `int[][]` adds outer arrays, row references and row-object overhead; these byte counts describe raw payload only.

## 3. Working when only one matrix fits in RAM

A necessary feasibility check comes first: if $C$ alone exceeds RAM, there is no algorithm that returns a fully resident dense $C$ under that budget. The interface must permit a file, stream, memory-mapped output with controlled resident pages, compressed form, or lazy view.

### Streaming vertical concatenation

Assume row-major input files, output allowed on disk, and a buffer of $b$ elements:

1. Validate dimensions and checked products before allocation; reject incompatible widths or size overflow.
2. Open a temporary output and write shape/type metadata.
3. Read at most $b$ elements of $A$, write them, repeat until its declared payload is exhausted.
4. Reuse the same buffer for $B$.
5. Check read/write errors, truncated inputs and final byte count. Close successfully and rename the completed temporary output into place.

This needs $\Theta(b)$ working RAM, $\Theta(N)$ persistent output, and $\Theta(N)$ CPU work. With a block transfer containing $B_e$ elements, sequential I/O costs $\Theta(\lceil N/B_e\rceil)$ transfers, assuming a buffer at least one block. Larger buffers can reduce syscall overhead; they do not change asymptotic payload size. Using one element at a time achieves constant RAM but can cause poor throughput if the I/O layer does not buffer.

If the full first matrix already occupies nearly all RAM, release it after streaming it out before loading the second. Reading both simultaneously is unnecessary. If inputs may be destroyed, overwriting a suitably allocated destination can save storage; it cannot make an output larger than available RAM fit. Standard Java arrays cannot extend in place. `realloc` in C may allocate and copy elsewhere, so it does not guarantee low peak memory.

### Horizontal concatenation

For each output row, write the row from $A$ followed by the corresponding row from $B$. Two independent file cursors and small buffers suffice. If the entire first input arrives before a non-rewindable second input, horizontal interleaving requires spooling or seekable output; simply appending the files produces the wrong layout. If even one row exceeds the buffer, stream it in chunks. Disks with costly seeks may benefit from larger row-block batches, trading RAM for fewer cursor switches.

### Other representations and trade-offs

A read-only composite view stores references to $A$ and $B$ and translates indices: vertical access chooses $A[i,j]$ for $i<m$, otherwise $B[i-m,j]$. View construction is $O(1)$ with $O(1)$ metadata, but the inputs still occupy their original storage and must remain alive. Visiting all elements remains $\Theta(N)$. A memory map reserves virtual address space, not guaranteed resident RAM; page faults and access order still matter. Column-first scans of row-major files can generate far more I/O than sequential scans.

## 4. One sparse matrix, one dense matrix

Suppose $A$ has $z=\operatorname{nnz}(A)\ll mn$ stored nonzeros and $B$ is dense. Let $v$ denote value bytes and $u$ index bytes.

| Representation of $A$ | Approximate payload bytes | Strength / limitation |
|---|---:|---|
| Dense | $v mn$ | Constant-time indexing; stores every zero |
| COO | $(v+2u)z$ | Easy append/build; row/column stored for each nonzero |
| CSR | $(v+u)z+u(m+1)$ | Efficient row scans; random lookup within a row needs search |

CSR arrays are `values[z]`, `columnIndex[z]`, `rowPointer[m+1]`. Row $i$ occupies offsets `rowPointer[i]` through `rowPointer[i+1]-1`. Keep column indices sorted and combine duplicate coordinates if the input format permits duplicates. Explicit zeros should normally be eliminated. COO can be sorted by `(row,column)` in $O(z\log z)$; sorted COO to CSR takes $O(m+z)$. Constructing sparse storage from a dense input still requires $\Theta(mn)$ inspection.

For 8-byte values and 4-byte indices, CSR uses roughly $12z+4(m+1)$ bytes, while dense uses $8mn$. The rough storage break-even is:

$$12z+4(m+1)<8mn.$$

This ignores object overhead and performance costs. “Sparse” is not automatically “smaller” at moderate/high density.

### Option A: final output must be dense

A dense concatenated $C$ still requires $vN$ bytes and $\Theta(N)$ materialization time. Initialize the sparse region to zero, scatter its $z$ nonzeros, and copy $B$. Compression reduces the input footprint, not the required dense output. A streamed CSR row expands into bounded chunks; do not allocate a full dense copy of $A$ merely to write its rows.

### Option B: final output may be hybrid

Keep $A$ as CSR and $B$ dense, with shape/offset metadata. Storage becomes:

$$\Theta(m+z+pq)$$

in machine words. Copying those components costs $\Theta(m+z+pq)$; referencing existing components can construct a view in $O(1)$ with no data copy. A sparse-region lookup can cost $O(\log d_i)$ with binary search in a sorted row containing $d_i$ nonzeros, while the dense region remains $O(1)$. Iterating nonzeros is cheap, but enumerating all logical cells still costs $\Theta(N)$.

### Option C: store everything as CSR/COO

Storing dense $B$ in sparse format introduces an index per value (or two in COO); $\operatorname{nnz}(B)$ may equal $pq$. For vertical CSR concatenation, append value/index arrays and offset $B$'s row pointers by $z$. Complexity is $\Theta(m+p+z+\operatorname{nnz}(B))$ with that output size. If widths differ, define padding and its implicit zeros first. Horizontal concatenation additionally shifts $B$'s column indices by $n$. A hybrid usually avoids the dense block's index overhead.

## 5. Foundational array theory and address walkthrough

Ordinary array objects have fixed length and a declared homogeneous component type. Indexed access is constant time because an offset can be computed directly, without following $i$ links. Dynamic array containers replace their backing array when capacity grows. C specifies contiguous rectangular arrays. Java arrays expose indexed components but do not promise physical addresses; Java multidimensional arrays are arrays of references and may be jagged.

With zero-based indices and element width $s$:

$$\operatorname{addr}(A[i])=B+is.$$

$$\operatorname{addr}_{row}(A[i][j])=B+(i\,cols+j)s.$$

$$\operatorname{addr}_{col}(A[i][j])=B+(j\,rows+i)s.$$

For dimensions `[planes][depth][cols]`:

$$\operatorname{addr}(A[i][j][k])=B+(i\,depth\,cols+j\,cols+k)s.$$

The names matter: `depth` here is the middle dimension; `cols` is the innermost dimension. With $B=1000$, $s=4$, a $2\times3$ row-major array has $A[1][2]$ at $1000+(3+2)4=1020$. In a $2\times2\times2$ cube, $A[1][1][1]$ is at $1000+(4+2+1)4=1028$. These are illustrative numeric addresses, not actual addresses exposed by Java.

Traversal costs are $\Theta(n)$, $\Theta(rc)$ and $\Theta(pdc)$ respectively. Row-first and column-first both visit the same number of cells, but row-first usually has better locality on row-major storage. Separate min/max functions each inspect the whole matrix in $\Theta(rc)$ time and $O(1)$ auxiliary memory; ties return the first row-major coordinate. Combining the scans reduces constants, not their asymptotic order.

## 6. Fisher–Yates correctness

At position $i=n-1,n-2,\ldots,1$, choose $j$ uniformly from $0$ through $i$ and swap. The fixed suffix is never touched again. Any particular permutation has probability:

$$\frac1n\cdot\frac1{n-1}\cdots\frac12=\frac1{n!}.$$

There are $n-1$ swaps, so the algorithm is $\Theta(n)$ with $O(1)$ auxiliary storage in the unit-cost random-choice model. `% bound` applied directly to a random integer introduces bias unless the source range is divisible by the bound. Java uses bounded `nextInt`; the C implementation rejects the excess part of `rand()`'s range before taking the remainder. Rejection gives expected constant-time random choices, not a finite worst-case retry guarantee. Uniformity is conditional on independent uniform random draws; standard pseudorandom generators have finite state and are not information-theoretically perfect or suitable for security-sensitive lotteries.

## 7. Verification record

The delivered source was compiled with JDK 17 using `-Xlint:all -Werror` and GCC using `-std=c11 -Wall -Wextra -Wpedantic -Werror`. Both complete Java programs embedded in the algorithm Markdown files were extracted and compiled too. All eleven Java demonstrations and both C demonstrations ran successfully.

Additional development checks covered randomized list operations against an independent reference, forward/backward links, empty and singleton structures, circular-list ownership transfer, queue wrap-around/fullness, stack growth, checked address arithmetic, ragged matrices, shuffle preservation, expression precedence and malformed input, large digit-list multiplication against `BigInteger`, and sorting/search against reference results. Fractional knapsack and activity selection were compared against exhaustive small-instance solutions. The deterministic Java harness completed 1,060,364 checks. Reference-library use was confined to the external verification harness and is not part of the delivered implementations.

C boundary/permutation tests passed with AddressSanitizer and UndefinedBehaviorSanitizer. Leak detection was disabled because the execution environment did not support LeakSanitizer's process inspection; this is not a claim of a completed leak-sanitizer audit. A shuffle frequency smoke test checked gross anomalies, not a statistical proof of uniformity. The Fisher–Yates proof and the random-source assumptions above supply the theoretical argument.

The archive deliberately retains the exact requested seventeen-file structure; temporary compiler output, extracted Java copies and development harnesses are not included. Build/run instructions and executable demonstrations are included with the source and chapters. Testing establishes evidence for the documented behavior, not a guarantee against every possible runtime or deployment failure.
