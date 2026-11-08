#include <stdio.h>
#include <string.h>

#define TABLE_SIZE 10

struct HashItem {
    char key[10];
    char value[30];
};

struct HashItem hashArray[TABLE_SIZE];

// Simple hash function
int hashCode(char* key) {
    int sum = 0;
    for(int i = 0; key[i] != '\0'; i++) {
        sum += key[i];
    }
    return sum % TABLE_SIZE;
}

void insert(char* key, char* value) {
    int index = hashCode(key);
    strcpy(hashArray[index].key, key);
    strcpy(hashArray[index].value, value);
}

char* search(char* key) {
    int index = hashCode(key);
    if (strcmp(hashArray[index].key, key) == 0) {
        return hashArray[index].value;
    }
    return NULL;
}

int main() {
    insert("LIB", "Library");
    insert("LAB", "Computing Laboratory");

    char* result = search("LIB");
    if (result != NULL) {
        printf("Found in Hash Table (C): LIB -> %s\n", result);
    }
    return 0;
}
