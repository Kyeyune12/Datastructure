# BandwidthAllocation.java

**Greedy Problem 1, in Java.** Shares 100 Mbps of internet bandwidth between five users so that the total benefit (priority × Mbps given) is as large as possible. Its output is identical to [bandwidth_allocation.c](bandwidth_allocation.c.md).

**Language:** Java · **C version:** [bandwidth_allocation.c](bandwidth_allocation.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac BandwidthAllocation.java
java BandwidthAllocation
```

## The problem in one paragraph

The link has 100 Mbps. Each user has a **demand** (Mbps wanted) and a **priority** (benefit per Mbps received). Total demand is 145 Mbps, so not everyone can be fully served. A user can receive part of their demand. The goal is to maximise `Σ given × priority`.

## The greedy idea

Each Mbps should go to whoever values it most. So: **sort by priority, highest first, then hand out bandwidth down the list**, giving each user their full demand or whatever is left. This is the **fractional knapsack** algorithm, with bandwidth as capacity, demand as weight, and priority as value per unit.

## Walking through the code

### The nested `Request` class

```java
static class Request {
    String name;
    int demand;     // Mbps the user asks for
    int priority;   // benefit per Mbps
    Request(String name, int demand, int priority) { ... }
}
```

Java's version of the C `struct`. It is `static` so `main` (a static method) can create `Request` objects without first making a `BandwidthAllocation` object. The constructor fills all three fields in one line, as in `new Request("Video Call", 30, 5)`.

### Sorting — the greedy choice

```java
// Bubble sort: keep swapping neighbours until the highest priority is first
static void sortByPriority(Request[] requests) {
    for (int i = 0; i < requests.length - 1; i++) {
        for (int j = 0; j < requests.length - 1 - i; j++) {
            if (requests[j].priority < requests[j + 1].priority) {
                Request temp = requests[j];
                requests[j] = requests[j + 1];
                requests[j + 1] = temp;
            }
        }
    }
}
```

`main` calls `sortByPriority(requests)`. This is the same **bubble sort** the C version uses:

- Each pass compares neighbouring requests and swaps them if the one on the left has a **lower** priority. That gives **descending** order, so the highest priority ends up first.
- After pass `i`, the `i + 1` lowest priorities have "bubbled" to the end and are in their final places, so the inner loop stops `i` positions earlier each time (`requests.length - 1 - i`).
- The test uses a strict `<`, so two requests with equal priority are never swapped. The sort is therefore **stable** (equal priorities keep their original order), just like the C version, so both programs print the rows in the same order.

### The allocation loop

```java
int remaining = totalBandwidth;
int totalBenefit = 0;
for (Request r : requests) {
    int given = Math.min(r.demand, remaining); // full demand, or what's left
    remaining -= given;
    totalBenefit += given * r.priority;
    System.out.printf("%-15s %-8d %-8d %-10d%n", ...);
}
```

- The enhanced `for` loop visits users in sorted order.
- `Math.min(r.demand, remaining)` replaces the C `if (given > remaining) given = remaining;`. It's the same logic in one expression. When bandwidth is short, this is where a user receives a **fraction** of their demand.
- After `remaining` reaches 0, `Math.min` returns 0 for every later user.
- `printf` with `%-15s` pads left-aligned columns. `%n` is Java's platform-independent newline.

## Trace

| User (sorted) | Demand | Left before | Given | Benefit |
|---|---|---|---|---|
| Online Exam (8) | 20 | 100 | 20 | 160 |
| Video Call (5) | 30 | 80 | 30 | 150 |
| Video Stream (3) | 40 | 50 | 40 | 120 |
| Email (2) | 5 | 10 | 5 | 10 |
| File Download (1) | 50 | 5 | **5** | 5 |

Total benefit **445**, 0 Mbps unused.

## Output

```
Total bandwidth available: 100 Mbps

User            Demand   Given    Priority
Online Exam     20       20       8
Video Call      30       30       5
Video Stream    40       40       3
Email           5        5        2
File Download   50       5        1

Bandwidth left unused: 0 Mbps
Total benefit (priority x Mbps): 445
```

## Why it is optimal

The exchange argument: if a lower-priority user got a Mbps while a higher-priority user still wanted more, moving that Mbps would raise the total by `priority(high) − priority(low) > 0`. Greedy never leaves such a gap, so its answer cannot be improved. This only works because bandwidth is divisible. With all-or-nothing demands it becomes 0/1 knapsack, which needs dynamic programming. [bandwidth_allocation.c.md](bandwidth_allocation.c.md) gives the full proof.

## Complexity

| Part | Time | Space |
|---|---|---|
| `sortByPriority` (bubble sort) | O(n²) | O(1) |
| Allocation loop | O(n) | O(1) |
| **Total** | **O(n²)** | **O(1)** extra |

With only five requests the O(n²) sort is instant. For a very large number of requests, a faster sort such as merge sort (O(n log n)) would be better; the greedy algorithm itself would not change.

## C vs Java differences

| | C | Java |
|---|---|---|
| Record type | `struct Request` | `static class Request` with constructor |
| Sorting | hand-written bubble sort, O(n²) | the same hand-written bubble sort, in `sortByPriority` |
| Cap at remaining | `if (given > remaining) given = remaining;` | `Math.min(r.demand, remaining)` |
| Array length | `sizeof(a) / sizeof(a[0])` | `requests.length` (via for-each) |

## What changed (simplified version)

| Before | Now | Why |
|---|---|---|
| `import java.util.Arrays;` | No imports | Only plain arrays and loops are used |
| `Arrays.sort(requests, (a, b) -> b.priority - a.priority)` | A hand-written bubble sort in `sortByPriority` | No library call or lambda to explain; every swap is visible. Matches the C version |
| Sorting took O(n log n) | Sorting takes O(n²) | No visible difference for five requests |

The output is exactly the same as before.
