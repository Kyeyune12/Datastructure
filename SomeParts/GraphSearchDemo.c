#include <stdio.h>
#include <stdbool.h>

#define MAX_VERTICES 6

// Simple Graph BFS/DFS representation using Adjacency Matrix
void bfs(int adj[MAX_VERTICES][MAX_VERTICES], int vertices, int start) {
    bool visited[MAX_VERTICES] = {false};
    int queue[MAX_VERTICES];
    int front = 0, rear = 0;

    visited[start] = true;
    queue[rear++] = start;

    printf("BFS Traversal (C): ");
    while (front < rear) {
        int current = queue[front++];
        printf("%d ", current);

        for (int i = 0; i < vertices; i++) {
            if (adj[current][i] == 1 && !visited[i]) {
                visited[i] = true;
                queue[rear++] = i;
            }
        }
    }
    printf("\n");
}

int main() {
    // Adjacency matrix for 6 campus buildings
    int adj[MAX_VERTICES][MAX_VERTICES] = {
        {0, 1, 1, 0, 0, 0},
        {1, 0, 0, 1, 0, 0},
        {1, 0, 0, 1, 0, 0},
        {0, 1, 1, 0, 1, 0},
        {0, 0, 0, 1, 0, 1},
        {0, 0, 0, 0, 1, 0}
    };

    bfs(adj, MAX_VERTICES, 0);
    return 0;
}
