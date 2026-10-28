/**
 * javac ArrayShuffle.java && java ArrayShuffle
 *
 * Shuffles an array into a random order using the Fisher-Yates algorithm.
 * Every possible order is equally likely, and it takes O(n) time.
 */
public class ArrayShuffle {

    // Fisher-Yates shuffle:
    // go from the last element down to the second one,
    // and swap each element with a random element at or before it
    static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            // pick a random index from 0 to i
            // Math.random() gives a decimal from 0.0 up to (but not including) 1.0
            int j = (int) (Math.random() * (i + 1));

            // swap arr[i] and arr[j]
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Prints all the values on one line
    static void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] values = {1, 2, 3, 4, 5, 6};

        System.out.print("Before: ");
        print(values);

        shuffle(values);

        System.out.print("After:  ");
        print(values);
    }
}
