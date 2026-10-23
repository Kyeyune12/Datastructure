/* Build: gcc -std=c11 -Wall -Wextra -Wpedantic -Werror array_operations.c -o arrays
 * Run: ./arrays
 * C gives precise contiguous layout, sizeof and pointer arithmetic, with no
 * automatic bounds checking or garbage collection. Java is preferable for most
 * beginner/application tasks because bounds checks and managed lifetimes reduce
 * errors; C fits embedded systems and exact memory-layout tasks. malloc failure,
 * length overflow, invalid pointers and free are the C programmer's concern.
 * Fixed length, homogeneous element type and O(1) indexing describe ordinary
 * arrays. Resizing means allocating another array. C T[R][C] is contiguous;
 * T** and Java int[][] need not be contiguous rectangular matrices.
 * Zero-based byte formulas, base B and element size s:
 * 1D B+i*s; row-major 2D B+(i*cols+j)*s;
 * column-major 2D B+(j*rows+i)*s;
 * 3D [planes][depth][cols] B+(i*depth*cols+j*cols+k)*s.
 * Subtract any nonzero mathematical lower bounds. Column-first traversal does
 * not convert row-major storage into column-major storage.
 * APIs below accept FLAT arrays; callers must provide buffers of stated length.
 * A raw pointer carries no capacity metadata, so C cannot verify that contract.
 */
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <stdbool.h>
#include <stddef.h>

typedef struct { int value; size_t row, col; } Location;
static bool product(size_t a, size_t b, size_t *out) {
    if (b != 0 && a > SIZE_MAX / b) return false;
    *out = a * b; return true;
}
bool address_1d(size_t base, size_t i, size_t length, size_t bytes, size_t *out) {
    size_t offset;
    if (!out || i >= length || bytes == 0 || !product(i, bytes, &offset)
        || base > SIZE_MAX - offset) return false;
    *out = base + offset; return true;
}
bool address_2d(size_t base, size_t i, size_t j, size_t rows, size_t cols,
                size_t bytes, bool column_major, size_t *out) {
    size_t count, offset;
    if (!product(rows, cols, &count) || i >= rows || j >= cols) return false;
    offset = column_major ? j*rows+i : i*cols+j;
    return address_1d(base, offset, count, bytes, out);
}
bool address_3d(size_t base, size_t i, size_t j, size_t k, size_t planes,
                size_t depth, size_t cols, size_t bytes, size_t *out) {
    size_t plane_size, count;
    if (!product(depth, cols, &plane_size) || !product(planes, plane_size, &count)
        || i >= planes || j >= depth || k >= cols) return false;
    return address_1d(base, i*plane_size+j*cols+k, count, bytes, out);
}
/* Allocates output; caller frees it. Empty output is NULL with length zero.
 * out and length must be distinct writable output objects and not alias input.
 * On failure no allocation is retained. Time Theta(na+nb), output Theta(na+nb).
 */
bool merge_arrays(const int *a, size_t na, const int *b, size_t nb,
                  int **out, size_t *length) {
    size_t n, bytes;
    if (!out || !length) return false;
    *out = NULL; *length = 0;
    if ((na && !a) || (nb && !b) || na > SIZE_MAX-nb) return false;
    n = na+nb;
    if (!product(n, sizeof(int), &bytes)) return false;
    if (!n) return true;
    int *result = malloc(bytes);
    if (!result) return false;
    for (size_t i=0; i<na; ++i) result[i]=a[i];
    for (size_t j=0; j<nb; ++j) result[na+j]=b[j];
    *out=result; *length=n; return true;
}
bool traverse_1d(const int *a, size_t n) {
    if ((n && !a) || n > SIZE_MAX/sizeof(int)) return false;
    for (size_t i=0; i<n; ++i) printf("[%zu]=%d\n", i, a[i]);
    return true;
}
bool traverse_2d(const int *a, size_t rows, size_t cols, bool column_first) {
    size_t n;
    if (!a || !rows || !cols || !product(rows,cols,&n)
        || n > SIZE_MAX/sizeof(int)) return false;
    if (column_first) {
        for (size_t j=0; j<cols; ++j) for (size_t i=0; i<rows; ++i)
            printf("[%zu][%zu]=%d\n",i,j,a[i*cols+j]);
    } else {
        for (size_t i=0; i<rows; ++i) for (size_t j=0; j<cols; ++j)
            printf("[%zu][%zu]=%d\n",i,j,a[i*cols+j]);
    }
    return true;
}
bool traverse_3d(const int *a, size_t planes, size_t depth, size_t cols) {
    size_t plane_size,n;
    if (!a || !planes || !depth || !cols || !product(depth,cols,&plane_size)
        || !product(planes,plane_size,&n) || n > SIZE_MAX/sizeof(int)) return false;
    for (size_t i=0; i<planes; ++i) for (size_t j=0; j<depth; ++j)
        for (size_t k=0; k<cols; ++k)
            printf("[%zu][%zu][%zu]=%d\n",i,j,k,a[i*plane_size+j*cols+k]);
    return true;
}
static bool extreme(const int *a, size_t rows, size_t cols, Location *out, bool min) {
    size_t n;
    if (!a || !out || !rows || !cols || !product(rows,cols,&n)
        || n > SIZE_MAX/sizeof(int)) return false;
    size_t best=0;
    for (size_t i=1; i<n; ++i)
        if (min ? a[i]<a[best] : a[i]>a[best]) best=i;
    *out=(Location){a[best],best/cols,best%cols}; return true;
}
/* Separate public functions; equal extrema use the first row-major position. */
bool minimum_2d(const int *a, size_t rows, size_t cols, Location *out) {
    return extreme(a,rows,cols,out,true);
}
bool maximum_2d(const int *a, size_t rows, size_t cols, Location *out) {
    return extreme(a,rows,cols,out,false);
}
int main(void) {
    int a[]={1,2,3}, b[]={4,5}, *merged=NULL;
    size_t n=0, address=0;
    if (!merge_arrays(a,3,b,2,&merged,&n)) { fputs("Merge failed\n",stderr); return EXIT_FAILURE; }
    puts("Merged:"); (void)traverse_1d(merged,n); free(merged);
    const int matrix[]={3,-4,7,9,0,9};
    puts("Row-first:"); (void)traverse_2d(matrix,2,3,false);
    puts("Column-first:"); (void)traverse_2d(matrix,2,3,true);
    Location lo,hi;
    if (!minimum_2d(matrix,2,3,&lo) || !maximum_2d(matrix,2,3,&hi)) return EXIT_FAILURE;
    printf("Min %d at (%zu,%zu); max %d at (%zu,%zu)\n",lo.value,lo.row,lo.col,hi.value,hi.row,hi.col);
    const int cube[]={1,2,3,4,5,6,7,8};
    (void)traverse_3d(cube,2,2,2);
    if (!address_1d(1000,3,5,4,&address)) return EXIT_FAILURE;
    printf("1D address: %zu\n",address);
    if (!address_2d(1000,1,2,2,3,4,false,&address)) return EXIT_FAILURE;
    printf("2D address: %zu\n",address);
    if (!address_3d(1000,1,1,1,2,2,2,4,&address)) return EXIT_FAILURE;
    printf("3D address: %zu\n",address);
    return EXIT_SUCCESS;
}
