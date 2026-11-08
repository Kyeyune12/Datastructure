import java.util.HashMap;

public class HashTableDemo {
    public static void main(String[] args) {
        HashMap<String, String> campusMap = new HashMap<>();

        campusMap.put("LIB", "Library");
        campusMap.put("ADM", "Administration Block");
        campusMap.put("LAB", "Computing Laboratory");

        String query = "ADM";
        if (campusMap.containsKey(query)) {
            System.out.println("Code " + query + " stands for: " + campusMap.get(query));
        }
    }
}
