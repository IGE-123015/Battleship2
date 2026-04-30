package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Timer class.
 *
 * Cyclomatic Complexity for each method:
 * - constructor: 1
 * - start: 1
 * - stop: 1
 * - reset: 1
 * - getTimeMillis: 1
 * - getSeconds: 1
 */
public class TimerTest {

    private Timer timer;

    @BeforeEach
    void setUp() {
        timer = new Timer();
    }

    @AfterEach
    void tearDown() {
        timer = null;
    }

    @Test
    void constructor() {
        assertNotNull(timer, "Timer instance should not be null.");
        // Before starting, time should be 0
        assertEquals(0L, timer.getTimeMillis(), "Initial time should be 0 ms.");
        assertEquals(0.0, timer.getSeconds(), 0.001, "Initial seconds should be 0.0.");
    }

    @Test
    void startAndStop() throws InterruptedException {
        timer.start();
        Thread.sleep(50); // wait at least 50 ms
        timer.stop();

        long millis = timer.getTimeMillis();
        assertTrue(millis >= 50, "Elapsed time should be at least 50 ms, got: " + millis);
    }

    @Test
    void getSeconds() throws InterruptedException {
        timer.start();
        Thread.sleep(100);
        timer.stop();

        double seconds = timer.getSeconds();
        assertTrue(seconds >= 0.1, "Elapsed seconds should be at least 0.1 s, got: " + seconds);
        assertEquals(timer.getTimeMillis() / 1000.0, seconds, 0.001, "getSeconds should equal getTimeMillis / 1000.0.");
    }

    @Test
    void reset() throws InterruptedException {
        timer.start();
        Thread.sleep(50);
        timer.stop();
        timer.reset();

        assertEquals(0L, timer.getTimeMillis(), "After reset, time should be 0 ms.");
        assertEquals(0.0, timer.getSeconds(), 0.001, "After reset, seconds should be 0.0.");
    }

    @Test
    void startStopReset_canBeRestarted() throws InterruptedException {
        // First run
        timer.start();
        Thread.sleep(30);
        timer.stop();
        long firstTime = timer.getTimeMillis();
        assertTrue(firstTime >= 30);

        // Reset and second run
        timer.reset();
        timer.start();
        Thread.sleep(30);
        timer.stop();
        long secondTime = timer.getTimeMillis();
        assertTrue(secondTime >= 30, "Timer should work correctly after reset and restart.");
    }
}
