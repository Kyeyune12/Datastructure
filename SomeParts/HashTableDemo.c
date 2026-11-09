#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define TABLE_SIZE 10

// Struct representing key-value pairs (equivalent to Java HashItem inner class)
typedef struct HashItem {
    char* key;
    char* value;
} HashItem;

// Array of pointers to HashItem (equivalent to HashItem[] in Java)
HashItem* hashArray[TABLE_SIZE];

// Hash function: ASCII sum modulo TABLE_SIZE
int hashCode(const char* key) {
    int sum = 0;
    for (int i = 0; key[i] != '\0'; i++) {
        sum += key[i];
    }
    return sum % TABLE_SIZE;
}

// 1. INSERT / PUT: Adds or updates an entry at the hash index
void put(const char* key, const char* value) {
    int index = hashCode(key);

    // If slot is already occupied, free existing strings and item first
    if (hashArray[index] != NULL) {
        free(hashArray[index]->key);
        free(hashArray[index]->value);
        free(hashArray[index]);
    }

    // Allocate memory for new item
    HashItem* newItem = (HashItem*)malloc(sizeof(HashItem));
    
    // Allocate space and copy key/value strings
    newItem->key = (char*)malloc(strlen(key) + 1);
    strcpy(newItem->key, key);
    
    newItem->value = (char*)malloc(strlen(value) + 1);
    strcpy(newItem->value, value);

    hashArray[index] = newItem;
}

// 2. SEARCH / GET: Retrieves value by key; returns NULL if not found
const char* get(const char* key) {
    int index = hashCode(key);
    if (hashArray[index] != NULL && strcmp(hashArray[index]->key, key) == 0) {
        return hashArray[index]->value;
    }
    return NULL;
}

// 3. CONTAINS KEY: Returns true if key exists in the table
bool containsKey(const char* key) {
    int index = hashCode(key);
    return (hashArray[index] != NULL && strcmp(hashArray[index]->key, key) == 0);
}

// 4. DELETE / REMOVE: Deallocates memory and removes key-value pair
bool removeKey(const char* key) {
    int index = hashCode(key);
    if (hashArray[index] != NULL && strcmp(hashArray[index]->key, key) == 0) {
        // Free allocated memory to prevent memory leaks
        free(hashArray[index]->key);
        free(hashArray[index]->value);
        free(hashArray[index]);
        
        hashArray[index] = NULL; // Clear slot
        return true;
    }
    return false;
}

// Helper function to free any remaining hash table memory before program exits
void cleanupHashTable() {
    for (int i = 0; i < TABLE_SIZE; i++) {
        if (hashArray[i] != NULL) {
            free(hashArray[i]->key);
            free(hashArray[i]->value);
            free(hashArray[i]);
            hashArray[i] = NULL;
        }
    }
}

int main() {
    // Initialize array to NULL pointers
    for (int i = 0; i < TABLE_SIZE; i++) {
        hashArray[i] = NULL;
    }

    // --- INSERT OPERATIONS ---
    put("LIB", "Library");
    put("ADM", "Administration Block");
    put("LAB", "Computing Laboratory");

    // --- CONTAINS & SEARCH OPERATIONS ---
    const char* query = "ADM";
    if (containsKey(query)) {
        printf("Code %s stands for: %s\n", query, get(query));
    }

    // --- DELETE OPERATION ---
    printf("\nDeleting 'ADM'...\n");
    bool isDeleted = removeKey(query);
    printf("Deleted successfully? %s\n", isDeleted ? "true" : "false");

    // Verify deletion
    if (!containsKey(query)) {
        printf("Code %s is no longer in the hash table.\n", query);
    }

    cleanupHashTable();
    return 0;
}
