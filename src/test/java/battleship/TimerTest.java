package battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Timer.
 * Author: ${user.name}
 * Date: 2026-04-16
 * Cyclomatic Complexity:
 * - constructor: 1
 * - start(): 1
 * - stop(): 1
 * - reset(): 1
 * - getTimeMillis(): 1
 * - getSeconds(): 1
 */
class TimerTest {

    private Timer timer;

    @BeforeEach
    void setUp() {
        timer = new Timer();
    }

    @AfterEach
    void tearDown() {
        timer = null;
    }

    // Test for constructor - CC = 1
    @Test
    void testConstructor() {
        Timer newTimer = new Timer();
        assertNotNull(newTimer, "Error: Timer instance should not be null");
    }

    // Test for start() - CC = 1
    @Test
    void start() {
        assertAll("Start method should execute without exceptions",
                () -> {
                    assertDoesNotThrow(() -> timer.start(), "Error: start() should not throw an exception");
                }
        );
    }

    // Test for stop() - CC = 1
    @Test
    void stop() {
        assertAll("Stop method should execute without exceptions",
                () -> {
                    timer.start();
                    assertDoesNotThrow(() -> timer.stop(), "Error: stop() should not throw an exception");
                }
        );
    }

    // Test for reset() - CC = 1
    @Test
    void reset() {
        assertAll("Reset method should execute without exceptions",
                () -> {
                    timer.start();
                    timer.stop();
                    long timeAfterStop = timer.getTimeMillis();
                    assertTrue(timeAfterStop >= 0, "Error: time should be non-negative after stop");
                    assertDoesNotThrow(() -> timer.reset(), "Error: reset() should not throw an exception");
                    long timeAfterReset = timer.getTimeMillis();
                    assertEquals(0, timeAfterReset, "Error: time should be 0 after reset");
                }
        );
    }

    // Test for getTimeMillis() - CC = 1
    @Test
    void getTimeMillis() {
        assertAll("getTimeMillis should return valid time measurement",
                () -> {
                    long initialTime = timer.getTimeMillis();
                    assertEquals(0, initialTime, "Error: initial time should be 0 before start");

                    timer.start();
                    // Small delay to ensure measurable time passes
                    Thread.sleep(10);
                    timer.stop();

                    long elapsedTime = timer.getTimeMillis();
                    assertTrue(elapsedTime >= 10, "Error: elapsed time should be at least 10ms");
                    assertTrue(elapsedTime >= 0, "Error: elapsed time should not be negative");
                }
        );
    }

    // Test for getSeconds() - CC = 1
    @Test
    void getSeconds() {
        assertAll("getSeconds should return valid time in seconds",
                () -> {
                    double initialSeconds = timer.getSeconds();
                    assertEquals(0.0, initialSeconds, 0.001, "Error: initial seconds should be 0 before start");

                    timer.start();
                    // Small delay to ensure measurable time passes
                    Thread.sleep(100);
                    timer.stop();

                    double elapsedSeconds = timer.getSeconds();
                    assertTrue(elapsedSeconds >= 0.1, "Error: elapsed seconds should be at least 0.1");
                    assertTrue(elapsedSeconds >= 0, "Error: elapsed seconds should not be negative");
                }
        );
    }

    // Integration test: verify time consistency between getTimeMillis() and getSeconds()
    @Test
    void testTimeConversion() {
        assertAll("Time conversion should be consistent",
                () -> {
                    timer.start();
                    Thread.sleep(50);
                    timer.stop();

                    long millis = timer.getTimeMillis();
                    double seconds = timer.getSeconds();
                    double expectedSeconds = millis / 1000.0;

                    assertEquals(expectedSeconds, seconds, 0.001,
                            "Error: getSeconds() should equal getTimeMillis() / 1000.0");
                }
        );
    }

    // Boundary test: multiple start/stop cycles
    @Test
    void testMultipleCycles() {
        assertAll("Multiple start/stop cycles should accumulate time",
                () -> {
                    // First cycle
                    timer.start();
                    Thread.sleep(10);
                    timer.stop();
                    long firstCycleTime = timer.getTimeMillis();

                    // IMPORTANT: StopWatch requires reset() before starting again
                    timer.reset();

                    // Second cycle
                    timer.start();
                    Thread.sleep(10);
                    timer.stop();
                    long secondCycleTime = timer.getTimeMillis();

                    assertTrue(secondCycleTime >= 10,
                            "Error: time in second cycle should be at least 10ms");
                }
        );
    }

    // Boundary test: reset during operation
    @Test
    void testResetDuringOperation() {
        assertAll("Reset should clear the timer",
                () -> {
                    timer.start();
                    Thread.sleep(10);
                    timer.stop();

                    long timeBeforeReset = timer.getTimeMillis();
                    assertTrue(timeBeforeReset > 0, "Error: time should be positive before reset");

                    timer.reset();
                    long timeAfterReset = timer.getTimeMillis();
                    assertEquals(0, timeAfterReset, "Error: time should be 0 after reset");
                }
        );
    }
}