# MultiplierList.java

Multiplies a number stored as a **linked list of digits** by an ordinary `int` k, the same way you multiply on paper.

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

Storing it backwards is what makes the code simple. Written multiplication starts from the ones digit, so the loop can walk the list from the front while carrying to the next node.

## How `multiply(head, k)` works

1. **Zero shortcut.** If the list is empty or k is 0, return a single node `0`.
2. **Make a placeholder.** A `dummy` node sits in front of the answer, so the code can always write `tail.next = ...` without checking whether the answer list is still empty. At the end we return `dummy.next`.
3. **School multiplication with a carry.** For each digit:
   ```java
   int value = current.digit * k + carry;
   tail.next = new Node(value % 10);   // the last digit stays here
   carry = value / 10;                 // the rest is carried on
   ```
4. **Flush the carry.** Whatever carry is left becomes extra digits at the end, one digit at a time with `% 10` and `/ 10`.

The input list is never changed; a new list is returned.

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

- **Time:** O(n + number of digits in k). That is one pass over the n digits, plus a few extra digits from the carry.
- **Extra space:** O(1), not counting the new result list.

## What changed (simplified version)

| Before | Now | Why |
| --- | --- | --- |
| The method was called `multiplyLinkedList` and the node class `ListNode` | `multiply` and `Node` | Shorter names |
| Checked for a list that loops back on itself (Floyd's tortoise and hare) | Removed | The demo never builds such a list |
| Checked every digit was 0–9 and that k was not negative | Removed | Keeps the method focused on the multiplication |
| Removed extra leading zeros from the answer (for example `5 → 0 → 0`) | Removed | Not needed for normal input |
| Used `long` for `value` and `carry` | Uses `int` | Simpler. Fine unless k is close to `Integer.MAX_VALUE` |
| `for` loops with several statements on one line | `while` loops with one statement per line, and comments on each step | Easier to follow |
