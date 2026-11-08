import java.util.*;

public class GraphSearchDemo {
    // BFS and DFS using an Adjacency List for Campus Buildings
    static void bfs(Map<String, List<String>> graph, String start) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        System.out.print("BFS Traversal: ");
        while (!queue.isEmpty()) {
            String current = queue.poll();
            System.out.print(current + " ");

            for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        System.out.println();
    }

    static void dfs(Map<String, List<String>> graph, String current, Set<String> visited) {
        visited.add(current);
        System.out.print(current + " ");

        for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                dfs(graph, neighbor, visited);
            }
        }
    }

    public static void main(String[] args) {
        Map<String, List<String>> campus = new HashMap<>();
        campus.put("Library", Arrays.asList("Admin", "Lab"));
        campus.put("Admin", Arrays.asList("Library", "Hostel"));
        campus.put("Lab", Arrays.asList("Library", "Hostel"));
        campus.put("Hostel", Arrays.asList("Admin", "Lab", "Cafeteria"));
        campus.put("Cafeteria", Arrays.asList("Hostel"));

        bfs(campus, "Library");
        
        System.out.print("DFS Traversal: ");
        dfs(campus, "Library", new HashSet<>());
        System.out.println();
    }
}
