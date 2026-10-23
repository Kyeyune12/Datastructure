# MultiplierList.java

Multiplies a number stored as a **linked list of digits** by an ordinary `int` k. Because the number is never converted to an `int` or `long`, it can have any number of digits.

**Language:** Java · **Related:** [SinglyLinkedList](SinglyLinkedList.java.md)

## How to run

```sh
javac MultiplierList.java
java MultiplierList
```

## How the number is stored

Digits are stored **least significant digit (LSD) first**, so 495 is stored as:

```
5 ──► 9 ──► 4 ──► null
```

LSD-first is what makes the code simple: written multiplication starts from the units digit, so the loop can walk the list in its natural order while carrying to the next node.

`ListNode`'s constructor accepts only digits 0–9. Anything else throws `IllegalArgumentException`.

## How `multiplyLinkedList(head, k)` works

1. **Check the input.**
   - `k` must be ≥ 0.
   - The list must not loop back on itself. This is checked with **Floyd's tortoise-and-hare**: `slow` moves one node at a time and `fast` moves two. If the list has a cycle they eventually land on the same node; otherwise `fast` reaches `null`.
   - Every digit must be 0–9.
2. **Find the last non-zero digit.** Any zeros after it, such as `5 → 0 → 0`, are leading zeros of the number and can be ignored.
3. **Zero shortcut.** If the number is 0 (an empty list or all zeros) or k is 0, return a single node `0`.
4. **School multiplication with a carry.** For each digit:
   ```java
   long value = digit * k + carry;
   newDigit   = value % 10;      // goes into the result list
   carry      = value / 10;      // carried to the next digit
   ```
   `value` is a `long` because `9 * Integer.MAX_VALUE + carry` doesn't fit in an `int`.
5. **Flush the carry.** Whatever carry is left becomes extra high-order digits, one per `% 10`.
6. **Trim.** Leading zeros are cut off the result.

The input list is never changed; a fresh list is returned.

**Loop invariant:** after processing i digits,

k × (the digits read so far) = (the digits written so far) + carry × 10ⁱ

When the loop ends and the carry has been flushed, the result is exactly k × the number.

## Output

```
99 * 5, LSD first: 5 -> 9 -> 4 -> null
```

The input is 99 (stored as `9 → 9`), and 99 × 5 = **495**, stored as `5 → 9 → 4`. Step by step:

| Digit | digit·5 + carry | Write | Carry |
| --- | --- | --- | --- |
| 9 | 45 + 0 = 45 | 5 | 4 |
| 9 | 45 + 4 = 49 | 9 | 4 |
| (end) | carry 4 | 4 | 0 |

## Complexity

- **Time:** O(n + log₁₀ k). That is one pass over the n digits, plus the extra digits produced by flushing the carry.
- **Extra space:** O(1), not counting the result list.

## Edge cases

| Input | Result |
| --- | --- |
| `null` list (the number 0) | `0` |
| `0 → 0 → 0` | `0` |
| `k = 0` | `0` |
| `5 → 0 → 0` (the number 5, with two redundant leading zeros) times 2 | `0 → 1` (10). The zeros are dropped |
| A list that loops back on itself | `IllegalArgumentException("cyclic input")` |
| Negative `k` | `IllegalArgumentException` |
