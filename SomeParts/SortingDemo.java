import java.util.Arrays;

public class SortingDemo {

    // 1. BUBBLE SORT: Simple, swaps adjacent elements if out of order O(n^2)
    static void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // 2. QUICK SORT: Divide and conquer using a pivot element O(n log n)
    static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);  // Sort left side
            quickSort(arr, pivotIndex + 1, high); // Sort right side
        }
    }

    static int partition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Choose last element as pivot
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        int temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        return i + 1;
    }

    public static void main(String[] args) {
        int[] scores1 = {45, 12, 85, 32, 8};
        bubbleSort(scores1);
        System.out.println("Bubble Sorted: " + Arrays.toString(scores1));

        int[] scores2 = {45, 12, 85, 32, 8};
        quickSort(scores2, 0, scores2.length - 1);
        System.out.println("Quick Sorted:  " + Arrays.toString(scores2));
    }
}
