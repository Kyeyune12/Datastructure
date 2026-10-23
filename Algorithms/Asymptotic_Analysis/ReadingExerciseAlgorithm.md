# Reading exercise: recognize the structure, then derive T(n)

No separate reading-exercise sheet or concrete loops were supplied in the specification. This chapter therefore provides explicit representative exercises, with assumptions and full equations; it does not pretend to reproduce an unseen lecturer's questions. Exact counts use the stated unit-cost model, while asymptotic bounds are machine-independent abstractions.

## Exercise 1: traversal with early exit — linear search

```java
static int linearSearch(int[] a, int key) {
    for (int i = 0; i < a.length; i++) {
        if (a[i] == key) return i;
    }
    return -1;
}
```

**Recognition:** the data need not be sorted; every element is potentially relevant. Comparing sequentially is the appropriate general search algorithm when no index exists.

Count loop initialization, each loop comparison, each equality comparison, each increment and the return as one unit. If the match is at zero-based index $r$, there are $r+1$ loop tests/equality tests and $r$ increments:

$$T_{hit}(n,r)=1+(r+1)+(r+1)+r+1=3r+4.$$

If absent, all $n$ increments and a final failed loop test occur:

$$T_{miss}(n)=1+(n+1)+n+n+1=3n+3.$$

- Best: key at index 0; $T=4=\Theta(1)$, hence $O(1)$ and $\Omega(1)$.
- Worst: absent, or at the end asymptotically; $\Theta(n)$, hence $O(n)$ and $\Omega(n)$.
- Average **conditional on a successful search with a uniformly distributed position**:

$$E[T\mid hit]=\frac1n\sum_{r=0}^{n-1}(3r+4)=\frac{3(n-1)}2+4=\Theta(n).$$

If failure probability is $f$, and successful positions remain uniform:

$$E[T]=(1-f)\left(\frac{3(n-1)}2+4\right)+f(3n+3).$$

An average-case claim without a probability model is incomplete. A heavily front-biased distribution can behave differently. Auxiliary storage is $O(1)$.

## Exercise 2: rectangular traversal — matrix sum

```java
long sum = 0;
for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) sum += a[i][j];
}
```

Assume a valid rectangular matrix and that each load-plus-add body is one unit under this model. The sum initializer costs one; outer control costs $2r+2$; each inner loop costs $3c+2$:

$$T(r,c)=1+(2r+2)+r(3c+2)=3rc+4r+3.$$

For positive $r,c$, $T=\Theta(rc)$, giving $O(rc)$ and $\Omega(rc)$. With $r=c=n$, this becomes $\Theta(n^2)$. Best, worst and average all coincide because no data-dependent exit exists. Values that are all zero do not reduce the work. A sparse representation with explicitly stored nonzeros changes the input representation and may permit $\Theta(z+r)$ traversal for CSR; that is a different algorithm/input contract.

## Exercise 3: interval halving — binary search

Use sorted ascending data and a half-open interval `[low, high)`:

```java
int low = 0, high = a.length;
while (low < high) {
    int mid = low + (high - low) / 2;
    if (a[mid] == key) return mid;
    if (a[mid] < key) low = mid + 1;
    else high = mid;
}
return -1;
```

The safe midpoint avoids `low + high` overflow. Sorted order is essential to discard half the candidates. After an unsuccessful comparison round, at most $\lfloor s/2\rfloor$ candidates remain from a size-$s$ interval. Thus at most $L=\lfloor\log_2 n\rfloor+1$ rounds occur for $n>0$.

Count the two initializations separately; midpoint, each condition, each assignment and return each cost one. An unsuccessful search taking $h$ rounds costs:

$$T_{miss}(n,h)=2+(h+1)+h+h+h+h+1=5h+4.$$

A successful search on its $h$th round skips the ordering comparison and bound update in that final round:

$$T_{hit}(n,h)=2+h+h+h+(h-1)+(h-1)+1=5h+1.$$

Best successful case is the first midpoint, $\Theta(1)$. Worst is $\Theta(\log n)$; the usual average over uniformly selected successful keys in a balanced search tree is also $\Theta(\log n)$. For a perfect tree with $n=2^h-1$, its level $d$ has $2^{d-1}$ keys, so the expected number of rounds is:

$$E[R]=\frac{\sum_{d=1}^{h}d2^{d-1}}{2^h-1}
=\frac{(h-1)2^h+1}{2^h-1}=\Theta(h)=\Theta(\log n).$$

Worst and average functions consequently have both logarithmic upper and lower bounds; the best function has constant upper and lower bounds. Unsorted data must be sorted first or searched linearly. Paying $\Theta(n\log n)$ to sort for one lookup is usually unnecessary; repeated lookups may justify preprocessing. Iterative auxiliary space is $O(1)$.

## Exercise 4: all pairs — duplicate checking without an index

```java
for (int i = 0; i < n; i++) {
    for (int j = i + 1; j < n; j++) {
        if (a[i] == a[j]) return true;
    }
}
return false;
```

For an all-distinct dataset, comparisons total:

$$P(n)=\sum_{i=0}^{n-1}(n-i-1)=\frac{n(n-1)}2.$$

Treat inner initialization as one statement, not separate arithmetic and assignment operations. For each $i$, the inner loop costs $1+(t+1)+t+t=3t+2$, where $t=n-i-1$. Including outer control and final return gives:

$$T_{distinct}(n)=2n+2+\sum_i(3(n-i-1)+2)+1
=\frac32n(n-1)+4n+3=\Theta(n^2).$$

For $n\ge2$, the best case is `a[0]==a[1]`, yielding $\Theta(1)$. Worst datasets contain all distinct values, or a duplicate reached only near the end, yielding $\Theta(n^2)$. An average cannot be inferred without the domain size and distribution. Under a distribution over all-distinct inputs, the expected cost is exactly the quadratic equation above. Under the degenerate distribution where every value is equal, every input exits immediately and expected cost is $\Theta(1)$.

Sorting then checking adjacent elements offers $O(n\log n)$ total time, at the cost of reordering or copying input. Hashing can offer expected $O(n)$ with additional storage, but no hash-table shortcut is used in this portfolio's implementations.

## Exercise 5: insertion-sort-shaped shifting

An outer loop selecting the next element and an inner loop shifting larger elements leftward is insertion sort. Let $t_i$ be successful shifts during pass $i$, for $i=1,\ldots,n-1$. The exact number of shifts is the inversion count:

$$I=\sum_{i=1}^{n-1}t_i.$$

Under a simplified model charging one unit for a complete inner predicate (including its short-circuit work), one for each assignment/update, and excluding input validation, pass setup costs 2 (`key`, `j`), final placement costs 1, and the inner loop costs $(t_i+1)+2t_i$. Outer control costs $2n$ for $n\ge1$. Therefore:

$$T(n,I)=2n+\sum_{i=1}^{n-1}(3t_i+4)=6n-4+3I.$$

This is an exact statement-level count under the specified model; counting array comparisons separately changes constants because `j>=0 && ...` short-circuits.

- Sorted/nondecreasing input: $I=0$, giving $\Theta(n)$.
- Reverse-sorted distinct input: $I=n(n-1)/2$, giving $\Theta(n^2)$.
- Uniform random permutation of distinct keys: each pair is inverted with probability $1/2$, so $E[I]=n(n-1)/4$ and $E[T]=\Theta(n^2)$.

For each case, the stated tight bound implies both the corresponding $O$ and $\Omega$ bounds. “Nearly sorted” is useful only if quantified, for example $I=O(n)$ gives linear time. Storage is $O(1)$ when sorting in place.

## Summary table

| Algorithm / input model | Best tight bound | Worst tight bound | Average tight bound |
|---|---|---|---|
| Linear search | $\Theta(1)$ | $\Theta(n)$ | $\Theta(n)$, uniform successful position |
| Dense matrix traversal | $\Theta(rc)$ | $\Theta(rc)$ | $\Theta(rc)$, all values |
| Binary search | $\Theta(1)$ | $\Theta(\log n)$ | $\Theta(\log n)$, uniform successful key |
| Pairwise duplicate check | $\Theta(1)$ | $\Theta(n^2)$ | Depends on value distribution |
| Insertion sort | $\Theta(n)$ | $\Theta(n^2)$ | $\Theta(n^2)$, uniform permutation |

When identifying an algorithm, look for the invariant: “processed prefix,” “shrinking sorted interval,” “all prior pairs,” or “sorted prefix.” The names `for` and `while` are surface syntax; the invariant and changing search space explain the work.
