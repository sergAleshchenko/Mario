package org.example.util;

/**
 * @author Sergei Aleshchenko
 */
public class Time {
    public static float timeStarted = System.nanoTime();

    public static float getTime() {
        return (float)((System.nanoTime() - timeStarted) * 1E-9);
    }
}
