# WaterLeak.java

**Divide & Conquer Problem 1, in Java.** Finds a pipe leak among 16 pressure sensors using a **binary-search** strategy, halving the search area after every reading. Its output is identical to [water_leak.c](water_leak.c.md).

**Language:** Java · **C version:** [water_leak.c](water_leak.c.md) · **Saved output:** [output.txt](output.txt)

## How to run

```sh
javac WaterLeak.java
java WaterLeak
```

## The problem in one paragraph

Sensors 0–15 sit along a pipe, with water flowing from 0 to 15. Sensors before the leak read normal pressure (5); sensors after it read low (2). Each reading costs an engineer's trip. We want the **first low sensor** using as few readings as possible. The leak is just before that sensor.

```
sensor:   0 1 2 3 4 5 6 7 8 9 | 10 11 12 13 14 15
pressure: 5 5 5 5 5 5 5 5 5 5 |  2  2  2  2  2  2
```

## The divide & conquer idea

Read the middle sensor. If it's **low**, the first low sensor is at `mid` or to its left, so keep `[low, mid]`. If it's **normal**, the leak is to the right, so keep `[mid + 1, high]`. Stop when one sensor remains. This works because the readings change only once (normal → low), the same condition binary search needs.

## Walking through the code

### Static fields

```java
static int[] pressure = {5,5,5,5,5,5,5,5,5,5,2,2,2,2,2,2};
static int normalPressure = 5;
static int checks = 0;
```

These are `static` so the static methods `isLow`, `findLeak` and `main` can share them, much like globals in the C version. Unlike C, there's no `SENSORS` constant: the size comes from `pressure.length`, so adding readings to the array is enough.

### `isLow` — one reading

```java
static boolean isLow(int sensor) {
    checks++;
    System.out.println("  Checking sensor " + sensor + " -> pressure " + pressure[sensor]);
    return pressure[sensor] < normalPressure;
}
```

Returns a real `boolean` (C returns `int` 0/1). Every reading goes through here, so `checks` is always accurate.

### `findLeak` — the recursion

```java
static int findLeak(int low, int high) {
    if (low == high) return low;              // BASE CASE
    int mid = (low + high) / 2;               // DIVIDE
    if (isLow(mid)) return findLeak(low, mid);        // keep left, including mid
    else            return findLeak(mid + 1, high);   // keep right
}
```

- **Base case:** one sensor left, so it must be the first low one. No extra reading is needed.
- **`mid` stays in the left range** when it's low, because it might be the first low sensor.
- Integer division rounds down, so `mid < high` and every call works on a strictly smaller range. The recursion always ends.

### `main` — guard, then search

```java
int last = pressure.length - 1;
if (!isLow(last)) { System.out.println("No leak: ..."); return; }
int firstLow = findLeak(0, last);
```

`findLeak` assumes at least one low sensor exists. Checking the last sensor first handles the "no leak" case. Without the guard, the search would wrongly report a leak at sensor 15. Afterwards, `firstLow == 0` means the leak is at the source; otherwise it's between `firstLow − 1` and `firstLow`.

## Trace

| Step | Range | `mid` | Reading | Next range |
|---|---|---|---|---|
| guard | – | 15 | 2 low | search |
| 1 | [0, 15] | 7 | 5 normal | [8, 15] |
| 2 | [8, 15] | 11 | 2 low | [8, 11] |
| 3 | [8, 11] | 9 | 5 normal | [10, 11] |
| 4 | [10, 11] | 10 | 2 low | [10, 10] → **10** |

5 readings instead of up to 16.

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

`T(n) = T(n/2) + O(1)`, which gives **Θ(log n)** by the Master Theorem (a = 1, b = 2).

| | Value |
|---|---|
| Readings | about log₂ n + 1 (5 for n = 16) |
| Time | Θ(log n) |
| Extra space | Θ(log n) recursion depth |

## C vs Java differences

| | C | Java |
|---|---|---|
| Shared data | global variables | `static` fields |
| Number of sensors | `#define SENSORS 16` | `pressure.length` |
| `isLow` returns | `int` (0 or 1) | `boolean` |
| Exit when no leak | `return 0;` from `main` | `return;` from `main` |
