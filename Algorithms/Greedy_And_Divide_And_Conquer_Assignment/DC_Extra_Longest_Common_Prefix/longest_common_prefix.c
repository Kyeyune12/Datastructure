/*
 * EXTRA DIVIDE & CONQUER PROBLEM: Longest Common Prefix of a List of Words
 *
 * Situation: We have a list of words, e.g. "interview", "internet",
 * "interval", "internal", "interact".
 *
 * Goal: Find the longest beginning (prefix) that ALL the words share.
 * Here the answer is "inter".
 *
 * Divide & Conquer idea:
 *   DIVIDE : split the list into a left half and a right half.
 *   CONQUER: find the common prefix of each half (by calling ourselves).
 *   COMBINE: the answer is the common prefix of those two results.
 *   BASE CASE: a list with one word -> the word itself is the prefix.
 *
 * Trick used in C: instead of building new strings, we only work with the
 * LENGTH of the common prefix. Every word starts with that many shared
 * letters, so at the end we print that many letters of the first word.
 *
 * How to run:
 *   gcc longest_common_prefix.c -o longest_common_prefix
 *   ./longest_common_prefix
 */
#include <stdio.h>
#include <string.h>

/* COMBINE step: how many starting letters do words a and b share,
   looking at no more than 'limit' letters? */
int commonLength(const char *a, const char *b, int limit) {
    int i = 0;
    while (i < limit && a[i] != '\0' && b[i] != '\0' && a[i] == b[i]) {
        i++;
    }
    return i;
}

/* Length of the common prefix of words[low..high] */
int lcp(const char *words[], int low, int high) {
    /* BASE CASE: only one word -> its whole length */
    if (low == high) {
        return strlen(words[low]);
    }

    /* DIVIDE */
    int mid = (low + high) / 2;

    /* CONQUER */
    int left  = lcp(words, low, mid);
    int right = lcp(words, mid + 1, high);

    /* COMBINE: the shorter of the two, then compare one word from each half */
    int limit = (left < right) ? left : right;
    int result = commonLength(words[low], words[mid + 1], limit);

    printf("  combine \"%.*s\" + \"%.*s\" -> \"%.*s\"\n",
           left, words[low], right, words[mid + 1], result, words[low]);
    return result;
}

int main(void) {
    const char *words[] = {"interview", "internet", "interval", "internal", "interact"};
    int n = sizeof(words) / sizeof(words[0]);

    printf("Words: ");
    for (int i = 0; i < n; i++) printf("%s ", words[i]);
    printf("\n\n");

    int length = lcp(words, 0, n - 1);

    printf("\nLongest common prefix: \"%.*s\"\n", length, words[0]);
    return 0;
}
