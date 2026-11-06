/*
 * DIVIDE & CONQUER PROBLEM 1: Locating the Source of a Water Leak
 *
 * Situation: A long water pipe has 16 pressure sensors placed along it
 * (sensor 0 is at the water source, sensor 15 is at the far end).
 * Water flows from sensor 0 towards sensor 15.
 * Every sensor BEFORE the leak reads normal pressure.
 * Every sensor AFTER the leak reads low pressure (water has escaped).
 *
 * Goal: Find the first sensor that reads low pressure -> the leak is
 * just before it. We want to do this with as FEW checks as possible
 * (each check = sending an engineer to read a sensor).
 *
 * Divide & Conquer idea (like binary search):
 *   DIVIDE : check the sensor in the MIDDLE of the section.
 *   CONQUER: if it is low, the leak is in the LEFT half (or right here);
 *            if it is normal, the leak is in the RIGHT half.
 *   Keep halving until one sensor is left.
 *
 * How to run:
 *   gcc water_leak.c -o water_leak
 *   ./water_leak
 */
#include <stdio.h>

#define SENSORS 16

/* Pressure readings (in bar). Normal is about 5. The leak is between 9 and 10. */
int pressure[SENSORS] = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 2, 2, 2, 2, 2};
int normalPressure = 5;
int checks = 0; /* how many sensors we had to read */

/* Returns 1 if this sensor shows low pressure, 0 otherwise */
int isLow(int sensor) {
    checks++;
    printf("  Checking sensor %d -> pressure %d\n", sensor, pressure[sensor]);
    return pressure[sensor] < normalPressure;
}

/* Recursively find the first low-pressure sensor between 'low' and 'high' */
int findLeak(int low, int high) {
    /* BASE CASE: only one sensor left, this is the answer */
    if (low == high) {
        return low;
    }

    /* DIVIDE: look at the middle sensor */
    int mid = (low + high) / 2;

    /* CONQUER: search only the half that must contain the leak */
    if (isLow(mid)) {
        return findLeak(low, mid);       /* leak is at mid or to the left */
    } else {
        return findLeak(mid + 1, high);  /* leak is to the right of mid  */
    }
}

int main(void) {
    int last = SENSORS - 1;

    printf("Searching %d sensors for the leak...\n", SENSORS);

    if (!isLow(last)) {
        printf("No leak: the last sensor reads normal pressure.\n");
        return 0;
    }

    int firstLow = findLeak(0, last);

    printf("\nFirst low-pressure sensor: %d\n", firstLow);
    if (firstLow == 0) {
        printf("The leak is at the water source, before sensor 0.\n");
    } else {
        printf("The leak is between sensor %d and sensor %d.\n", firstLow - 1, firstLow);
    }
    printf("Sensors checked: %d (instead of up to %d)\n", checks, SENSORS);
    return 0;
}
