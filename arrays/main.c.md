# main.c

An early experiment with passing arrays to functions in C: printing an array, and a loop that swaps neighbouring elements. It is part of the original Code::Blocks project in this folder.

**Language:** C · **Project file:** `arrays.cbp` (this is the only source file it builds) · **Status:** has no `main()`, so it doesn't run as it is

## What is inside

| Function | What it does |
| --- | --- |
| `void display(int a[], size_t length)` | Prints the elements separated by spaces |
| `int swapArray(int array[])` | Swaps `array[i]` with `array[i+1]` for i = 0 … 4, then returns the array |
| `main()` | **Commented out** (lines 4–11) |

### What `swapArray` actually does

The loop swaps each element with its right-hand neighbour, working left to right. That carries the **first element all the way to the end**; in effect it rotates the array one place to the left:

```
start      11  3  5  7  8  1
i = 0       3 11  5  7  8  1
i = 1       3  5 11  7  8  1
i = 2       3  5  7 11  8  1
i = 3       3  5  7  8 11  1
i = 4       3  5  7  8  1 11
```

Run with the commented-out `main` restored (using `{11, 3, 5, 7, 8, 1}`), it prints:

```
3 5 7 8 1 11
```

## Problems to fix

| Problem | Why it matters | Fix |
| --- | --- | --- |
| `main()` is commented out | Building `arrays.cbp` fails at link time with "undefined reference to `main`" | Uncomment it, and change it to `int main(void)` |
| `swapArray` returns `int`, but `return array;` returns a pointer | gcc warns: *returning 'int \*' from a function with return type 'int'*. On 64-bit systems a pointer doesn't fit in an `int`, so `display(swapArray(array), 6)` can crash | The array is changed in place, so make it `void swapArray(int array[], size_t n)` and call `display(array, 6)` afterwards |
| `display` ignores `length` and always prints 6 items | It reads past the end of any shorter array | Loop `for (size_t i = 0; i < length; i++)` |
| `swapArray` hard-codes `i < 5` | Works only for arrays of exactly 6 elements | Pass the length and loop to `n - 1` |
| Functions are indented as if inside `main` | Only looks odd; it compiles | Remove the indentation |

## A corrected version

```c
#include <stdio.h>

void display(const int a[], size_t length) {
    for (size_t i = 0; i < length; i++) printf("%d ", a[i]);
    printf("\n");
}

void rotateLeft(int a[], size_t n) {           /* what swapArray really does */
    for (size_t i = 0; i + 1 < n; i++) {
        int temp = a[i]; a[i] = a[i + 1]; a[i + 1] = temp;
    }
}

int main(void) {
    int array[] = {11, 3, 5, 7, 8, 1};
    size_t n = sizeof array / sizeof array[0];
    rotateLeft(array, n);
    display(array, n);                          /* 3 5 7 8 1 11 */
    return 0;
}
```

## Complexity

Both functions take O(n) time and O(1) extra space.
