import java.util.Objects;
import java.util.Random;

/**
 * javac ArrayShuffle.java && java ArrayShuffle
 * Fisher-Yates: at step i choose uniformly from [0,i], swap into position i.
 * Each permutation has probability 1/n! under independent uniform choices.
 * Exactly max(n-1,0) iterations: Theta(n) time, O(1) auxiliary space.
 * Random.nextInt(bound) avoids modulo bias; a fixed seed gives repeatable demos.
 * Pseudorandom shuffling is not a cryptographic/security guarantee.
 */
public final class ArrayShuffle {
    private ArrayShuffle() { }
    public static void shuffleInPlace(int[] arr) { shuffleInPlace(arr, new Random()); }
    public static void shuffleInPlace(int[] arr, Random random) {
        Objects.requireNonNull(arr, "arr"); Objects.requireNonNull(random, "random");
        for (int i=arr.length-1; i>0; i--) {
            int j=random.nextInt(i+1), tmp=arr[i]; arr[i]=arr[j]; arr[j]=tmp;
        }
    }
    public static void main(String[] args) {
        int[] values={1,2,3,4,5,6};
        shuffleInPlace(values, new Random(42));
        for (int value : values) System.out.print(value+" ");
        System.out.println();
    }
}
