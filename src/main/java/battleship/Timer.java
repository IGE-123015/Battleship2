package battleship;

import org.apache.commons.lang3.time.StopWatch;

public class Timer {

    private StopWatch stopWatch;

    public Timer() {
        stopWatch = new StopWatch();
    }

    public void start() {
        stopWatch.start();
    }
    public void stop() {
        stopWatch.stop();
    }
    public void reset() {
        stopWatch.reset();
    }

    public long getTimeMillis() {
        return stopWatch.getTime();
    }

    public double getSeconds() {
        return stopWatch.getTime() / 1000.0;
    }
}
