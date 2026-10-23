# Loop analysis: derive the count before naming the bound

## 1. Cost model and definitions

Assume $n$ is a nonnegative integer, integer operations fit the chosen type, array access is constant time, and each primitive statement costs one unit unless stated otherwise. Exact equations are **relative to this model**, not milliseconds or literal CPU instructions. We count initialization, each condition test (including the final failed one), each update, and a constant-time body statement separately.

For sufficiently large $n$ and positive constants:

- $T(n)\in O(g(n))$ means $T(n)\le c g(n)$: an asymptotic upper bound.
- $T(n)\in\Omega(g(n))$ means $T(n)\ge c g(n)$: an asymptotic lower bound.
- $T(n)\in\Theta(g(n))$ means both inequalities hold, possibly with different constants.

These are not synonyms for worst, best and average case. Each case function can have its own $O$, $\Omega$ and $\Theta$ bounds. For a fixed-count loop independent of element values, all cases usually coincide.

## 2. Single `while`: linear progress

```java
int i = 0;
while (i < n) {
    sum += i;
    i++;
}
```

Assume `sum` already exists and its addition is one unit. The loop sees $i=0,1,\ldots,n-1$: exactly $n$ iterations. Initialization costs 1, condition checks $n+1$, body $n$, and updates $n$:

$$T(n)=1+(n+1)+n+n=3n+2.$$

For $n\ge1$, $3n\le3n+2\le5n$. Therefore $O(n)$, $\Omega(n)$ and $\Theta(n)$. Working storage is $O(1)$. Replacing `i++` by a positive fixed step $d$ gives $\lceil n/d\rceil$ iterations and the same asymptotic bound.

## 3. Single `for`: same count, different syntax

```java
for (int i = 0; i < n; i++) {
    sum += i;
}
```

The initializer executes once, not $n$ times. The same count gives $T(n)=3n+2=\Theta(n)$. Whether the syntax is `for` or `while` does not determine complexity; progress and termination do.

### A logarithmic single loop

```java
for (int i = n; i > 0; i /= 2) {
    count++;
}
```

For $n\ge1$, values are $n,\lfloor n/2\rfloor,\lfloor n/4\rfloor,\ldots,1$. Let $L=\lfloor\log_2 n\rfloor+1$. Then:

$$T(n)=1+(L+1)+L+L=3L+2=\Theta(\log n).$$

For all $n\ge0$, write $\Theta(1+\log(n+1))$ to include the constant work at zero. Repeated division avoids multiplication overflow in a doubling loop. The upper and lower bounds are both logarithmic for growing positive $n$.

## 4. Independent nested `for` loops

```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j < n; j++) {
        count++;
    }
}
```

The inner loop restarts at zero for each outer iteration. Body count:

$$B(n)=\sum_{i=0}^{n-1}\sum_{j=0}^{n-1}1=n^2.$$

Outer control costs $1+(n+1)+n=2n+2$. Each complete inner loop costs $3n+2$, so:

$$T(n)=(2n+2)+n(3n+2)=3n^2+4n+2.$$

For $n\ge1$, $3n^2\le T(n)\le9n^2$, proving $\Theta(n^2)$, and hence $O(n^2)$ and $\Omega(n^2)$. Rectangular bounds $r$ and $c$ instead give $\Theta(rc+r+1)$ including empty dimensions, or $\Theta(rc)$ when both are positive. Do not collapse independent input dimensions without justification.

## 5. Dependent triangular loops

```java
for (int i = 0; i < n; i++) {
    for (int j = 0; j <= i; j++) {
        count++;
    }
}
```

Inner iterations are $1,2,\ldots,n$:

$$B(n)=\sum_{i=0}^{n-1}(i+1)=\frac{n(n+1)}2.$$

For a fixed $i$, inner control/body cost is $1+(i+2)+(i+1)+(i+1)=3i+5$. Thus:

$$T(n)=2n+2+\sum_{i=0}^{n-1}(3i+5)
=\frac32n^2+\frac{11}2n+2=\Theta(n^2).$$

Half of a quadratic grid is still quadratic. Starting `j=i+1` and testing `j<n` instead gives $n(n-1)/2$ body executions: all unordered pairs of distinct indices.

## 6. Combined `for` and `while`: reset matters

### Resetting a halving loop

```java
for (int i = 0; i < n; i++) {
    int j = n;
    while (j > 0) {
        count++;
        j /= 2;
    }
}
```

For $n\ge1$, let $L=\lfloor\log_2 n\rfloor+1$. Each outer iteration performs a full inner loop costing $3L+2$:

$$T(n)=2n+2+n(3L+2)=3nL+4n+2=\Theta(n\log n).$$

Thus the tight upper and lower bounds are $O(n\log n)$ and $\Omega(n\log n)$. The variables occupy $O(1)$ space.

### A shared pointer is not reset

```java
int j = 0;
for (int i = 0; i < n; i++) {
    while (j < n) {
        count++;
        j++;
    }
}
```

The first outer iteration advances $j$ all the way to $n$. Later iterations each perform one failed condition check. Across the entire program, the inner body runs only $n$ times. For $n\ge1$, inner tests total $n+n=2n$, updates $n$, body operations $n$. Therefore:

$$T(n)=1+(2n+2)+2n+n+n=6n+3=\Theta(n).$$

The same equation evaluates to 3 at $n=0$, when neither loop body executes. This is the aggregate-analysis principle behind many two-pointer algorithms: nested syntax alone does not imply quadratic work.

## 7. Sequential loops add

A loop costing $an+b$ followed by a loop costing $cn^2+d$ has:

$$T(n)=an+b+cn^2+d=\Theta(n^2),$$

not $\Theta(n^3)$. Multiply counts only when one complete block repeats within another. If a body calls a nonconstant-time routine, include its cost explicitly: $n$ iterations each sorting $n$ values is not a linear loop.

## 8. A repeatable analysis checklist

1. State input sizes and the cost model.
2. Identify progress variables, their initial values and update rules.
3. Solve how many successful iterations occur; include final condition failures.
4. Check whether an inner loop resets or shares progress across iterations.
5. Write a sum before simplifying: $\sum_i work(i)$.
6. Establish both an upper and a lower bound to justify a tight bound.
7. Separate input-dependent cases from bounds on any particular case.
8. Count live auxiliary memory separately from allocated output and input storage.

Integer overflow can invalidate a mathematical loop argument. For example, repeated `i *= 2` on a finite signed integer can wrap around and stop progressing toward the intended bound. The halving examples avoid that trap. These snippets illustrate operation counts; actual accumulator types and range checks must match the data size in a full application.
