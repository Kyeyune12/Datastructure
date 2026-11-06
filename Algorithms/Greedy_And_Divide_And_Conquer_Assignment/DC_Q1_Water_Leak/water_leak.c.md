# water_leak.c

**Divide & Conquer Problem 1, in C.** Finds where a pipe is leaking by checking pressure sensors with a **binary-search** strategy, halving the search area after every check. Its output is identical to [WaterLeak.java](WaterLeak.java.md).

**Language:** C · **Java version:** [WaterLeak.java](WaterLeak.java.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
gcc water_leak.c -o water_leak
./water_leak
```

## The problem

A pipe has **16 sensors**, numbered 0 (at the water source) to 15 (at the far end). Water flows from 0 toward 15.

- Sensors **before** the leak read normal pressure (5 bar).
- Sensors **after** the leak read low pressure (2 bar), because water has escaped.

```
sensor:   0 1 2 3 4 5 6 7 8 9 | 10 11 12 13 14 15
pressure: 5 5 5 5 5 5 5 5 5 5 |  2  2  2  2  2  2
                              ^ leak
```

Each reading means sending an engineer out, so we want **as few checks as possible**. The answer is the **first low sensor**; the leak lies just before it.

The key property is that the readings are **sorted** in a sense: all normal, then all low. There is only one switch from normal to low, and the search exploits that.

## The divide & conquer idea

Look at the middle sensor of the section still in question:

- **Low?** Then the leak is at or before `mid`, because everything after a low sensor is also low. Keep `[low, mid]`. `mid` itself stays in, since it might be the *first* low one.
- **Normal?** Then the leak is after `mid`. Keep `[mid + 1, high]`.
- **One sensor left?** That's the answer.

Each check throws away half the sensors. Only one half is ever searched, so this is the "decrease and conquer" kind of divide and conquer, the same pattern as binary search.

## Walking through the code

### Global data

```c
#define SENSORS 16
int pressure[SENSORS] = {5,5,5,5,5,5,5,5,5,5,2,2,2,2,2,2};
int normalPressure = 5;
int checks = 0;
```

`checks` counts how many sensors were read. It's global so `isLow` can increase it from anywhere.

### `isLow` — one sensor reading

```c
int isLow(int sensor) {
    checks++;
    printf("  Checking sensor %d -> pressure %d\n", sensor, pressure[sensor]);
    return pressure[sensor] < normalPressure;
}
```

This stands in for "send an engineer to read sensor X": it counts the check, prints it, and returns 1 (true) if the pressure is below normal. All sensor reads go through this one function, so the `checks` counter can't miss any.

### `findLeak` — the recursion

```c
int findLeak(int low, int high) {
    if (low == high) return low;              /* BASE CASE */
    int mid = (low + high) / 2;               /* DIVIDE */
    if (isLow(mid))
        return findLeak(low, mid);            /* leak at mid or to the left */
    else
        return findLeak(mid + 1, high);       /* leak to the right */
}
```

- **Base case:** when `low == high`, only one sensor can be the first low one, so it's returned *without* reading it again.
- **`mid = (low + high) / 2`** rounds down. Since `mid < high` whenever `low < high`, both `[low, mid]` and `[mid+1, high]` are strictly smaller than `[low, high]`, so the recursion always ends.
- **Why `findLeak(low, mid)` and not `mid - 1`:** if `mid` is low, it could be the first low sensor itself, so it must stay in the range.

### `main` — the guard check

```c
if (!isLow(last)) {
    printf("No leak: the last sensor reads normal pressure.\n");
    return 0;
}
int firstLow = findLeak(0, last);
```

`findLeak` assumes at least one sensor is low. Without this guard, a pipe with no leak would shrink the range down to sensor 15 and wrongly report "leak between 14 and 15". If the last sensor (the furthest downstream) is normal, every sensor before it is also normal, so there is no leak. This guard costs one extra check.

After the search, `firstLow == 0` is a special case: every sensor is low, so the leak is before sensor 0, at the source. Otherwise the leak is between `firstLow − 1` and `firstLow`.

## Trace

| Call | Range | `mid` | Reading | Decision |
|---|---|---|---|---|
| guard | – | 15 | 2 (low) | a leak exists, start the search |
| `findLeak(0, 15)` | 16 sensors | 7 | 5 (normal) | go right → `[8, 15]` |
| `findLeak(8, 15)` | 8 sensors | 11 | 2 (low) | go left → `[8, 11]` |
| `findLeak(8, 11)` | 4 sensors | 9 | 5 (normal) | go right → `[10, 11]` |
| `findLeak(10, 11)` | 2 sensors | 10 | 2 (low) | go left → `[10, 10]` |
| `findLeak(10, 10)` | 1 sensor | – | – | **return 10** |

5 checks in total (1 guard + 4 halvings), instead of up to 16 with a one-by-one walk.

## Output

```
Searching 16 sensors for the leak...
  Checking sensor 15 -> pressure 2
  Checking sensor 7 -> pressure 5
  Checking sensor 11 -> pressure 2
  Checking sensor 9 -> pressure 5
  Checking sensor 10 -> pressure 2

First low-pressure sensor: 10
The leak is between sensor 9 and sensor 10.
Sensors checked: 5 (instead of up to 16)
```

## Complexity

Recurrence: `T(n) = T(n/2) + O(1)`. By the Master Theorem (a = 1, b = 2, f(n) = Θ(1) = Θ(n^log₂1)), this is **Θ(log n)**.

| | Value |
|---|---|
| Sensor checks | about log₂ n, plus 1 for the guard (at most ⌈log₂ n⌉ + 1) → exactly 4 + 1 = 5 for n = 16 |
| Time | Θ(log n) |
| Extra space | Θ(log n) for the recursion stack (an iterative loop would make it O(1)) |
| Linear scan, for comparison | up to n = 16 checks |

With 1,000,000 sensors this would need about 21 checks instead of up to a million.

## Things to notice

- The method only works because readings change **once**, from normal to low. If pressure went up and down randomly, halving would give wrong answers.
- The search is **tail recursive** (the recursive call is the last thing done), so it converts easily into a `while (low < high)` loop.
- Because `mid` rounds down, it is always less than `high`, so the search never re-reads the last sensor that the guard already checked.
