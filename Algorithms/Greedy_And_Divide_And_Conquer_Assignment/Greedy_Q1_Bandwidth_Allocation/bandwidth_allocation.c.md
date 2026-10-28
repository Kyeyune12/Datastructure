# bandwidth_allocation.c

**Greedy Problem 1, in C.** Shares a fixed amount of internet bandwidth between several users so that the total benefit (priority × Mbps given) is as large as possible. Its output is identical to [BandwidthAllocation.java](BandwidthAllocation.java.md).

**Language:** C · **Java version:** [BandwidthAllocation.java](BandwidthAllocation.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc bandwidth_allocation.c -o bandwidth_allocation
./bandwidth_allocation
```

## The problem

A link has **100 Mbps** in total. Five users each ask for some bandwidth (their *demand*) and each has a *priority*, which is the benefit gained for every Mbps they receive.

| User | Demand (Mbps) | Priority (benefit per Mbps) |
|---|---|---|
| File Download | 50 | 1 |
| Video Call | 30 | 5 |
| Online Exam | 20 | 8 |
| Video Stream | 40 | 3 |
| Email | 5 | 2 |

Together they ask for 145 Mbps, but only 100 Mbps exists, so someone must get less than they asked for. A user can be given *part* of their demand. We want to maximise

```
total benefit = Σ (Mbps given to user i) × (priority of user i)
```

subject to giving out no more than 100 Mbps and never giving a user more than they asked for.

## The greedy idea

Every Mbps is worth `priority` to whoever receives it. So each Mbps should go to the user who values it most, as long as that user still wants more. That gives the rule:

1. Sort users from **highest priority to lowest**.
2. Walk down the list. Give each user their full demand if it fits; otherwise give them whatever is left.
3. Once the bandwidth runs out, everyone after that gets 0.

This is the **fractional knapsack** problem in disguise: bandwidth is the knapsack capacity, demand is an item's weight, and priority is the item's value per unit of weight.

## Walking through the code

### The `Request` struct

```c
struct Request {
    char name[20];
    int demand;    /* Mbps the user asks for */
    int priority;  /* benefit per Mbps (higher = more important) */
};
```

Holds one user's request. `name` is a fixed 20-character buffer, so every name in the program (the longest is `"File Download"`, 13 characters plus `'\0'`) fits.

### `sortByPriority` — the greedy ordering

```c
void sortByPriority(struct Request r[], int n) {
    for (int i = 0; i < n - 1; i++)
        for (int j = 0; j < n - 1 - i; j++)
            if (r[j].priority < r[j + 1].priority) { /* swap */ }
}
```

A **bubble sort** in *descending* order. The comparison is `<`, so a lower-priority request "bubbles" to the right. After pass `i`, the `i + 1` lowest priorities are already at the end, which is why the inner loop stops at `n - 1 - i`. Whole structs are swapped through `temp`, so a user's name, demand and priority stay together.

Bubble sort only swaps when one priority is strictly smaller, so it is **stable**: users with equal priority keep their original order. (No two priorities are equal in this data, but stability is what keeps the C and Java outputs identical in general.)

### `main` — setting up

```c
int totalBandwidth = 100;
struct Request requests[] = { ... };
int n = sizeof(requests) / sizeof(requests[0]);
```

`sizeof(requests) / sizeof(requests[0])` is the standard C way to count array elements: total bytes divided by bytes per element, which gives 5 here. Adding a sixth user to the list is enough; `n` updates by itself.

### `main` — the greedy allocation loop

```c
int remaining = totalBandwidth;
int totalBenefit = 0;
for (int i = 0; i < n; i++) {
    int given = requests[i].demand;
    if (given > remaining) given = remaining;   /* not enough left */
    remaining -= given;
    totalBenefit += given * requests[i].priority;
    printf(...);
}
```

- `remaining` tracks the bandwidth not yet handed out.
- `given` starts as the full demand and is cut down to `remaining` if there isn't enough. This line is the "take a fraction of the item" step from fractional knapsack.
- Once `remaining` hits 0, every later user gets `given = 0`. They still appear in the table, so you can see who missed out.

`printf("%-15s %-8d ...")` prints left-aligned columns: `%-15s` pads the name to 15 characters, which is what lines up the table.

## Trace with the program's data

After sorting: Online Exam (8), Video Call (5), Video Stream (3), Email (2), File Download (1).

| Step | User | Demand | `remaining` before | Given | `remaining` after | Benefit added |
|---|---|---|---|---|---|---|
| 1 | Online Exam | 20 | 100 | 20 | 80 | 20 × 8 = 160 |
| 2 | Video Call | 30 | 80 | 30 | 50 | 30 × 5 = 150 |
| 3 | Video Stream | 40 | 50 | 40 | 10 | 40 × 3 = 120 |
| 4 | Email | 5 | 10 | 5 | 5 | 5 × 2 = 10 |
| 5 | File Download | 50 | 5 | **5** (partial) | 0 | 5 × 1 = 5 |

Total benefit = 160 + 150 + 120 + 10 + 5 = **445**, with 0 Mbps left unused.

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

## Why greedy gives the best answer

Suppose some allocation gives a Mbps to a lower-priority user *L* while a higher-priority user *H* is still below their demand. Take that 1 Mbps away from *L* and give it to *H*. The total changes by `priority(H) − priority(L) > 0`, so the allocation gets better. An optimal allocation therefore never does this. Greedy is exactly the allocation where it never happens: no lower-priority user receives anything until every higher-priority user is fully served. This is the **exchange argument** used to prove fractional knapsack.

The proof depends on bandwidth being **divisible** (File Download can get 5 of its 50 Mbps). If each user had to get all or nothing, this would be the 0/1 knapsack problem, greedy could fail, and dynamic programming would be needed (see `Dynammic_Programming/PartB_Q3_Knapsack`).

## Complexity

| Part | Time | Space |
|---|---|---|
| Bubble sort | O(n²) | O(1), sorts in place |
| Allocation loop | O(n) | O(1) |
| **Total** | **O(n²)** | **O(1)** extra |

Replacing bubble sort with `qsort` would bring the total down to O(n log n). With only 5 users, the difference doesn't matter here.

## Things to notice

- The data is hard-coded. To test other cases, change `totalBandwidth` or the `requests` array.
- If total demand were *less* than 100 Mbps, everyone would get their full demand and "Bandwidth left unused" would be greater than 0.
- `int` is fine for these numbers. Very large demands × priorities could overflow `totalBenefit`.
