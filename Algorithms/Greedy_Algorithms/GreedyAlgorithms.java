public final class GreedyAlgorithms {
    private GreedyAlgorithms() { }
    public static final class Item {
        public final int weight, value;
        public Item(int weight, int value) {
            if (weight <= 0 || value < 0)
                throw new IllegalArgumentException("positive weight and nonnegative value required");
            this.weight = weight; this.value = value;
        }
    }
    private static final class IndexedItem {
        final Item item; final int index;
        IndexedItem(Item item, int index) { this.item = item; this.index = index; }
    }
    public static final class KnapsackResult {
        public final double value;
        private final double[] fractions;
        private KnapsackResult(double value, double[] fractions) {
            this.value = value; this.fractions = fractions;
        }
        public int size() { return fractions.length; }
        public double fraction(int index) { return fractions[index]; }
    }
    private static boolean itemBefore(IndexedItem a, IndexedItem b) {
        long left = (long) a.item.value * b.item.weight;
        long right = (long) b.item.value * a.item.weight;
        return left > right || (left == right && a.index <= b.index);
    }
    private static void sortItems(IndexedItem[] a, IndexedItem[] temp, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        sortItems(a, temp, lo, mid); sortItems(a, temp, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi)
            temp[k++] = itemBefore(a[i], a[j]) ? a[i++] : a[j++];
        while (i < mid) temp[k++] = a[i++];
        while (j < hi) temp[k++] = a[j++];
        for (k = lo; k < hi; k++) a[k] = temp[k];
    }
    public static KnapsackResult fractionalKnapsack(Item[] items, long capacity) {
        if (items == null) throw new NullPointerException("items");
        if (capacity < 0) throw new IllegalArgumentException("negative capacity");
        IndexedItem[] order = new IndexedItem[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) throw new IllegalArgumentException("null item");
            order[i] = new IndexedItem(items[i], i);
        }
        sortItems(order, new IndexedItem[order.length], 0, order.length);
        double[] fractions = new double[items.length];
        long remaining = capacity;
        double value = 0;
        for (int i = 0; i < order.length && remaining > 0; i++) {
            IndexedItem entry = order[i];
            long taken = remaining < entry.item.weight ? remaining : entry.item.weight;
            double fraction = (double) taken / entry.item.weight;
            fractions[entry.index] = fraction;
            value += fraction * entry.item.value;
            remaining -= taken;
        }
        return new KnapsackResult(value, fractions);
    }
    public static final class Activity {
        public final int start, finish;
        public Activity(int start, int finish) {
            if (start >= finish) throw new IllegalArgumentException("start must precede finish");
            this.start = start; this.finish = finish;
        }
    }
    private static boolean activityBefore(int ia, int ib, Activity[] a) {
        if (a[ia].finish != a[ib].finish) return a[ia].finish < a[ib].finish;
        if (a[ia].start != a[ib].start) return a[ia].start < a[ib].start;
        return ia <= ib;
    }
    private static void sortActivities(int[] order, int[] temp, Activity[] a, int lo, int hi) {
        if (hi - lo < 2) return;
        int mid = lo + (hi - lo) / 2;
        sortActivities(order, temp, a, lo, mid);
        sortActivities(order, temp, a, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi)
            temp[k++] = activityBefore(order[i], order[j], a) ? order[i++] : order[j++];
        while (i < mid) temp[k++] = order[i++];
        while (j < hi) temp[k++] = order[j++];
        for (k = lo; k < hi; k++) order[k] = temp[k];
    }
    public static int[] selectActivities(Activity[] activities) {
        if (activities == null) throw new NullPointerException("activities");
        int n = activities.length;
        int[] order = new int[n], temp = new int[n], selected = new int[n];
        for (int i = 0; i < n; i++) {
            if (activities[i] == null) throw new IllegalArgumentException("null activity");
            order[i] = i;
        }
        sortActivities(order, temp, activities, 0, n);
        int count = 0, lastFinish = 0;
        for (int index : order) {
            Activity activity = activities[index];
            if (count == 0 || activity.start >= lastFinish) {
                selected[count++] = index; lastFinish = activity.finish;
            }
        }
        int[] result = new int[count];
        for (int i = 0; i < count; i++) result[i] = selected[i];
        return result;
    }
    public static void main(String[] args) {
        Item[] items = {new Item(10,60), new Item(20,100), new Item(30,120)};
        KnapsackResult r = fractionalKnapsack(items, 50);
        System.out.println("Optimal fractional value: " + r.value);
        for (int i = 0; i < r.size(); i++) System.out.println("Item " + i + ": " + r.fraction(i));
        Activity[] activities = {new Activity(1,4), new Activity(3,5),
            new Activity(0,6), new Activity(5,7), new Activity(8,9), new Activity(5,9)};
        System.out.print("Selected original indices: ");
        for (int i : selectActivities(activities)) System.out.print(i + " ");
        System.out.println();
    }
}
