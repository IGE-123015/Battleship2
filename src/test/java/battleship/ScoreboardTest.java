package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

/**
 * Test class for the Scoreboard class.
 *
 * Cyclomatic Complexity:
 * - saveScore: ~4 (file exists branch, null check, etc.)
 * - printScoreboard: ~3 (file not exists, file empty, normal read)
 */
public class ScoreboardTest {

    private static final String FILE_NAME = "scoreboard.json";

    @BeforeEach
    void setUp() {
        // Remove scoreboard file before each test for isolation
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {
        // Clean up after test
        File file = new File(FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /** Branch: file does not exist yet → new file created */
    @Test
    void saveScore_firstTime() {
        assertDoesNotThrow(() -> Scoreboard.saveScore(10, 5, 3),
                "saveScore should not throw on first save.");
        assertTrue(new File(FILE_NAME).exists(), "Scoreboard file should exist after saving.");
    }

    /** Branch: file already exists → existing scores are read and new one is appended */
    @Test
    void saveScore_appendsToExisting() {
        Scoreboard.saveScore(10, 5, 3);
        Scoreboard.saveScore(20, 8, 6);
        // File should exist and be non-empty
        File file = new File(FILE_NAME);
        assertTrue(file.exists());
        assertTrue(file.length() > 0, "Scoreboard file should have content.");
    }

    /** Branch: printScoreboard when file does not exist */
    @Test
    void printScoreboard_fileNotExists() {
        assertDoesNotThrow(() -> Scoreboard.printScoreboard(),
                "printScoreboard should not throw when file does not exist.");
    }

    /** Branch: printScoreboard when file exists with entries */
    @Test
    void printScoreboard_withEntries() {
        Scoreboard.saveScore(15, 7, 4);
        Scoreboard.saveScore(25, 10, 8);
        assertDoesNotThrow(() -> Scoreboard.printScoreboard(),
                "printScoreboard should not throw when scores exist.");
    }

    /** Branch: multiple saves followed by print */
    @Test
    void saveAndPrint_multiple() {
        for (int i = 1; i <= 3; i++) {
            Scoreboard.saveScore(i * 5, i * 2, i);
        }
        assertDoesNotThrow(() -> Scoreboard.printScoreboard());
    }
}
