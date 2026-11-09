public class HashTableDemo {

    private static final int TABLE_SIZE = 10;
    private static HashItem[] hashArray = new HashItem[TABLE_SIZE];

    // Inner class representing key-value pairs (equivalent to C struct)
    static class HashItem {
        String key;
        String value;

        HashItem(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    // Hash function matching the ASCII sum modulo size from the C code
    private static int hashCode(String key) {
        int sum = 0;
        for (int i = 0; i < key.length(); i++) {
            sum += key.charAt(i);
        }
        return sum % TABLE_SIZE;
    }

    // 1. INSERT / PUT: Adds or updates an entry at the hash index
    public static void put(String key, String value) {
        int index = hashCode(key);
        hashArray[index] = new HashItem(key, value);
    }

    // 2. SEARCH / GET: Retrieves value by key; returns null if not found
    public static String get(String key) {
        int index = hashCode(key);
        if (hashArray[index] != null && hashArray[index].key.equals(key)) {
            return hashArray[index].value;
        }
        return null;
    }

    // 3. CONTAINS KEY: Returns true if key exists in the table
    public static boolean containsKey(String key) {
        int index = hashCode(key);
        return hashArray[index] != null && hashArray[index].key.equals(key);
    }

    // 4. DELETE / REMOVE: Removes the key-value pair from the table
    public static boolean remove(String key) {
        int index = hashCode(key);
        if (hashArray[index] != null && hashArray[index].key.equals(key)) {
            hashArray[index] = null; // Clear slot
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // --- INSERT OPERATIONS ---
        put("LIB", "Library");
        put("ADM", "Administration Block");
        put("LAB", "Computing Laboratory");

        // --- CONTAINS & SEARCH OPERATIONS ---
        String query = "ADM";
        if (containsKey(query)) {
            System.out.println("Code " + query + " stands for: " + get(query));
        }

        // --- DELETE OPERATION ---
        System.out.println("\nDeleting 'ADM'...");
        boolean isDeleted = remove(query);
        System.out.println("Deleted successfully? " + isDeleted);

        // Verify deletion
        if (!containsKey(query)) {
            System.out.println("Code " + query + " is no longer in the hash table.");
        }
    }
    }
