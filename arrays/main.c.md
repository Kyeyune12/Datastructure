# main.c

Passing arrays to functions in C: printing an array, and a loop that swaps each element with its right-hand neighbour. This is the file the Code::Blocks project (`arrays.cbp`) builds.

## How to run

```sh
gcc main.c -o main
./main
```

Or open `arrays.cbp` in Code::Blocks and press Build and Run.

## What is inside

| Function | What it does |
| --- | --- |
| `display(a, length)` | Prints the elements separated by spaces |
| `swapArray(array, length)` | Swaps `array[i]` and `array[i+1]` for i = 0 ... length - 2 |
| `main` | Prints `{11, 3, 5, 7, 8, 1}` before and after `swapArray` |

## What `swapArray` really does

Swapping each element with its right-hand neighbour, left to right, carries the **first element all the way to the end**. It is a rotation one place to the left:

```
start      11  3  5  7  8  1
i = 0       3 11  5  7  8  1
i = 1       3  5 11  7  8  1
i = 2       3  5  7 11  8  1
i = 3       3  5  7  8 11  1
i = 4       3  5  7  8  1 11
```

## Output

```
Before: 11 3 5 7 8 1
After:  3 5 7 8 1 11
```

## What was fixed

- `main` was commented out, so the program could not run.
- `swapArray` was declared to return `int` but returned the array, which is a type error. Arrays are passed **by address**, so the function can change the caller's array directly and needs no return value (`void`).
- `display` ignored its `length` argument and always printed 6 elements; it now uses `length`.

## Questions you may be asked

- **Why does `main` see the change without a return?** In C an array argument is really a pointer to the first element, so the function works on the original array.
- **Why must we pass `length`?** Inside the function, `array` is only a pointer; C does not remember how long the array is.
