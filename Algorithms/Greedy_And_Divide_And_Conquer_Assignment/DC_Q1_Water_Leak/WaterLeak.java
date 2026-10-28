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
 *   javac WaterLeak.java
 *   java WaterLeak
 */
public class WaterLeak {

    // Pressure readings (in bar). Normal is about 5. The leak is between 9 and 10.
    static int[] pressure = {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 2, 2, 2, 2, 2};
    static int normalPressure = 5;
    static int checks = 0; // how many sensors we had to read

    // Returns true if this sensor shows low pressure
    static boolean isLow(int sensor) {
        checks++;
        System.out.println("  Checking sensor " + sensor + " -> pressure " + pressure[sensor]);
        return pressure[sensor] < normalPressure;
    }

    // Recursively find the first low-pressure sensor between 'low' and 'high'
    static int findLeak(int low, int high) {
        // BASE CASE: only one sensor left, this is the answer
        if (low == high) {
            return low;
        }

        // DIVIDE: look at the middle sensor
        int mid = (low + high) / 2;

        // CONQUER: search only the half that must contain the leak
        if (isLow(mid)) {
            return findLeak(low, mid);       // leak is at mid or to the left
        } else {
            return findLeak(mid + 1, high);  // leak is to the right of mid
        }
    }

    public static void main(String[] args) {
        int last = pressure.length - 1;

        System.out.println("Searching " + pressure.length + " sensors for the leak...");

        if (!isLow(last)) {
            System.out.println("No leak: the last sensor reads normal pressure.");
            return;
        }

        int firstLow = findLeak(0, last);

        System.out.println("\nFirst low-pressure sensor: " + firstLow);
        if (firstLow == 0) {
            System.out.println("The leak is at the water source, before sensor 0.");
        } else {
            System.out.println("The leak is between sensor " + (firstLow - 1)
                               + " and sensor " + firstLow + ".");
        }
        System.out.println("Sensors checked: " + checks + " (instead of up to " + pressure.length + ")");
    }
}
