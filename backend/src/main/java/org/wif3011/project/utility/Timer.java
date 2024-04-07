package org.wif3011.project.utility;

import java.util.concurrent.TimeUnit;

public class Timer {
    private long startTime;
    private long endTime;

    // Start the timer
    public void start() {
        startTime = System.nanoTime();
    }

    // Stop the timer
    public void stop() {
        endTime = System.nanoTime();
    }

    // Get the elapsed time in milliseconds
    public long getElapsedTimeMillis() {
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

}