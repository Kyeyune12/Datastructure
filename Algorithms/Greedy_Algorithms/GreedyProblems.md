# Greedy algorithms: choices, proofs and complete implementations

A greedy algorithm commits to a locally preferred choice and does not revisit it. This is justified only when a proof shows an optimal solution can include that choice. Optimal substructure alone is insufficient: the locally chosen step must also be safe.

## 1. Fractional knapsack

### Problem and assumptions

Item $i$ has positive integer weight $w_i$, nonnegative integer value $v_i$, and can be split into arbitrary fractions $x_i\in[0,1]$. With capacity $W\ge0$:

$$\max\sum_{i=1}^n v_i x_i\quad\text{subject to}\quad\sum_{i=1}^n w_i x_i\le W.$$

Sort by decreasing density $\rho_i=v_i/w_i$. Take each item completely if it fits; otherwise take the fraction that fills the remaining capacity and stop. Zero-value items may be taken without affecting optimal value. Negative weights/values, zero weights and negative capacity are rejected by this implementation. These restrictions make the model explicit rather than inventing ad hoc semantics.

### Pseudocode

```text
FRACTIONAL-KNAPSACK(items, capacity)
    sort item references by decreasing value/weight
    fractions[original indices] = 0
    remaining = capacity
    for each sorted item while remaining > 0
        takenWeight = min(remaining, item.weight)
        fractions[item.originalIndex] = takenWeight / item.weight
        totalValue += fractions[item.originalIndex] * item.value
        remaining -= takenWeight
    return totalValue and fractions
```

### Worked example

Capacity $W=50$:

| Item | Weight | Value | Density | Weight taken | Fraction | Value gained |
|---|---:|---:|---:|---:|---:|---:|
| A | 10 | 60 | 6 | 10 | 1 | 60 |
| B | 20 | 100 | 5 | 20 | 1 | 100 |
| C | 30 | 120 | 4 | 20 | $2/3$ | 80 |

Result: $60+100+80=240$. Remaining capacity evolves $50\to40\to20\to0$. The output fractions refer to original input indices, even though the internal references have been sorted.

### Exchange argument

Suppose an alleged optimum takes some weight $\delta>0$ of lower-density item $j$ while a higher-density item $i$ remains available. Replace that weight with $\delta$ of $i$ (or the largest feasible smaller amount). Total weight stays fixed; value changes by:

$$\Delta V=\delta(\rho_i-\rho_j)\ge0.$$

Repeating exchanges creates an optimum that agrees with the greedy choice, then with each subsequent choice. Fractions make this exchange possible. Equal densities can be ordered arbitrarily.

**Why this fails for 0/1 knapsack:** with the same example, density-greedy takes A+B for value 160 and cannot fit C, while B+C fits exactly for value 220. Taking $2/3$ of C is illegal in the 0/1 problem. Do not claim this program solves 0/1 knapsack.

### Complexity and numerical choices

The hand-written merge sort costs $\Theta(n\log n)$ for $n\ge2$; the scan costs $O(n)$. Sorting buffers and fractions cost $O(n)$ and recursion uses $O(\log n)$ stack frames. Input objects/order remain unchanged. If already density-sorted under a separate contract, the selection scan alone is $\Theta(n)$ in the worst case.

Densities are compared using exact cross-products `valueA * weightB` and `valueB * weightA` in `long`. Since each field is a nonnegative `int`, these products fit signed `long`. This avoids rounding/overflow from division-based density comparison. Fractions and the total use `double`, so the returned numeric value may have roundoff. The combinatorial order is exact for the supported integer inputs.

## 2. Activity selection

### Problem

Choose the largest number of mutually compatible activities on one resource. Activity $i$ occupies the half-open interval $[s_i,f_i)$, with $s_i<f_i$. Consecutive activities are compatible when the next start is at least the previous finish. Negative times are allowed; zero-duration/reversed intervals are rejected. Each selected activity has equal reward; weighted scheduling is a different problem.

The greedy rule is to select the compatible activity with the **earliest finish**, not the earliest start or shortest duration.

```text
ACTIVITY-SELECTION(activities)
    sort by increasing finish (ties by start, then original index)
    selected = empty
    lastFinish is initially unset
    for each activity
        if lastFinish is unset or activity.start >= lastFinish
            append activity.originalIndex to selected
            lastFinish = activity.finish
    return selected
```

### Worked selection

Input already shown in finish order:

| Activity | Interval | Decision | Reason |
|---|---|---|---|
| A | [1,4) | Select | First available |
| B | [3,5) | Skip | $3<4$ |
| C | [0,6) | Skip | $0<4$ |
| D | [5,7) | Select | $5\ge4$ |
| E | [8,9) | Select | $8\ge7$ |
| F | [5,9) | Skip | $5<9$ |

A valid optimum is A,D,E with count 3. With tie-breaking by start, F may be examined before E; it is still skipped after D and the selected set is unchanged in this example.

### Exchange proof

Let $g$ have the earliest finish and let $o$ be the first activity in an optimal schedule. Since $f_g\le f_o$, replacing $o$ by $g$ leaves every later scheduled activity compatible and keeps the count unchanged. An optimum therefore exists starting with $g$. Restrict to activities starting at or after $f_g$ and repeat the argument inductively.

An earliest-start rule can choose [0,100), blocking [1,2), [2,3), [3,4). A shortest-duration rule can choose [3,5), blocking both [0,4) and [4,8); choosing the latter two gives more activities. Earliest finish is supported by the proof; those alternative rules are not.

Sorting takes $\Theta(n\log n)$, the scan takes $\Theta(n)$, and auxiliary arrays/output take $O(n)$. If finish-sorted input is guaranteed, selection alone is linear. For weighted activities, use a method such as dynamic programming rather than this equal-reward greedy choice.

## 3. Runnable implementation: `GreedyAlgorithms.java`

Save the complete block below as `GreedyAlgorithms.java` in any working directory:

```sh
javac GreedyAlgorithms.java
java GreedyAlgorithms
```

No collection classes, built-in sorting, built-in array copying, or growable library containers are used. Both sorts explicitly split, merge and copy raw arrays.

```java
public final class GreedyAlgorithms {
    private GreedyAlgorithms() { }
    public static final class Item {
        public final int weight, value;
        public Item(int weight, int value) {
            if (weight <= 0 || value < 0)
                throw new IllegalArgumentException("positive weight and nonnegative value required");
            this.weight = weight; this.value = value;
        }
    }
    private static final class IndexedItem {
        final Item item; final int index;
        IndexedItem(Item item, int index) { this.item = item; this.index = index; }
    }
    public static final class KnapsackResult {
        public final double value;
        private final double[] fractions;
        private KnapsackResult(double value, double[] fractions) {
            this.value = value; this.fractions = fractions;
        }
        public int size() { return fractions.length; }
        public double fraction(int index) { return fractions[index]; }
    }
    private static boolean itemBefore(IndexedItem a, IndexedItem b) {
        long left = (long) a.item.value * b.item.weight;
        long right = (long) b.item.value * a.item.weight;
        return left > right || (left == right && a.index <= b.index);
    }
    private static void sortItems(IndexedItem[] a, IndexedItem[] temp, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        sortItems(a, temp, lo, mid); sortItems(a, temp, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi)
            temp[k++] = itemBefore(a[i], a[j]) ? a[i++] : a[j++];
        while (i < mid) temp[k++] = a[i++];
        while (j < hi) temp[k++] = a[j++];
        for (k = lo; k < hi; k++) a[k] = temp[k];
    }
    public static KnapsackResult fractionalKnapsack(Item[] items, long capacity) {
        if (items == null) throw new NullPointerException("items");
        if (capacity < 0) throw new IllegalArgumentException("negative capacity");
        IndexedItem[] order = new IndexedItem[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) throw new IllegalArgumentException("null item");
            order[i] = new IndexedItem(items[i], i);
        }
        sortItems(order, new IndexedItem[order.length], 0, order.length);
        double[] fractions = new double[items.length];
        long remaining = capacity;
        double value = 0;
        for (int i = 0; i < order.length && remaining > 0; i++) {
            IndexedItem entry = order[i];
            long taken = remaining < entry.item.weight ? remaining : entry.item.weight;
            double fraction = (double) taken / entry.item.weight;
            fractions[entry.index] = fraction;
            value += fraction * entry.item.value;
            remaining -= taken;
        }
        return new KnapsackResult(value, fractions);
    }
    public static final class Activity {
        public final int start, finish;
        public Activity(int start, int finish) {
            if (start >= finish) throw new IllegalArgumentException("start must precede finish");
            this.start = start; this.finish = finish;
        }
    }
    private static boolean activityBefore(int ia, int ib, Activity[] a) {
        if (a[ia].finish != a[ib].finish) return a[ia].finish < a[ib].finish;
        if (a[ia].start != a[ib].start) return a[ia].start < a[ib].start;
        return ia <= ib;
    }
    private static void sortActivities(int[] order, int[] temp, Activity[] a, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        sortActivities(order, temp, a, lo, mid);
        sortActivities(order, temp, a, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi)
            temp[k++] = activityBefore(order[i], order[j], a) ? order[i++] : order[j++];
        while (i < mid) temp[k++] = order[i++];
        while (j < hi) temp[k++] = order[j++];
        for (k = lo; k < hi; k++) order[k] = temp[k];
    }
    public static int[] selectActivities(Activity[] activities) {
        if (activities == null) throw new NullPointerException("activities");
        int n = activities.length;
        int[] order = new int[n], temp = new int[n], selected = new int[n];
        for (int i = 0; i < n; i++) {
            if (activities[i] == null) throw new IllegalArgumentException("null activity");
            order[i] = i;
        }
        sortActivities(order, temp, activities, 0, n);
        int count = 0, lastFinish = 0;
        for (int index : order) {
            Activity activity = activities[index];
            if (count == 0 || activity.start >= lastFinish) {
                selected[count++] = index; lastFinish = activity.finish;
            }
        }
        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = selected[i];
        return result;
    }
    public static void main(String[] args) {
        Item[] items = {new Item(10,60), new Item(20,100), new Item(30,120)};
        KnapsackResult r = fractionalKnapsack(items, 50);
        System.out.println("Optimal fractional value: " + r.value);
        for (int i = 0; i < r.size(); i++) System.out.println("Item " + i + ": " + r.fraction(i));
        Activity[] activities = {new Activity(1,4), new Activity(3,5),
            new Activity(0,6), new Activity(5,7), new Activity(8,9), new Activity(5,9)};
        System.out.print("Selected original indices: ");
        for (int i : selectActivities(activities)) System.out.print(i + " ");
        System.out.println();
    }
}
```

Expected demonstration: value `240.0`, fractions `1.0`, `1.0`, approximately `0.6666666666666666`, selected indices `0 3 4`. Empty inputs return zero value/empty selections. Capacity zero returns zero fractions; capacity exceeding total weight takes all items.

## 4. How to defend a greedy solution

State the feasible set, proposed choice and optimization goal. Prove an optimum can be modified to include the choice without becoming worse. Show the remaining problem has the same form. Only then analyze sorting plus selection costs. Testing examples can catch a wrong greedy rule, but examples alone do not prove optimality.
