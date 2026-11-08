public class SearchDemo {
    static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] nums = {12, 34, 56, 78, 90};
        int target = 56;
        int result = linearSearch(nums, target);
        System.out.println("Linear Search: Target " + target + " found at index " + result);
    }
}
