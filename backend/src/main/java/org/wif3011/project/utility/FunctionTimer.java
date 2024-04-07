package org.wif3011.project.utility;

import java.util.concurrent.TimeUnit;

public class FunctionTimer {
    private final Timer timer;

    public FunctionTimer() {
        this.timer = new Timer();
    }

    public long getLambdaExecutionTime(Runnable function) {
        timer.start();
        function.run();
        timer.stop();
        return timer.getElapsedTimeMillis();
    }
}
