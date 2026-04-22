package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the GameRecord class.
 *
 * Cyclomatic Complexity:
 * - constructor: 1
 */
public class GameRecordTest {

    @Test
    void constructor() {
        GameRecord record = new GameRecord(15, 8, 4);

        assertEquals(15, record.getTotalMoves(), "totalMoves should be 15.");
        assertEquals(8, record.getHits(), "hits should be 8.");
        assertEquals(4, record.getSunkShips(), "sunkShips should be 4.");
        assertNotNull(record.getDate(), "date should not be null.");
        assertFalse(record.getDate().isEmpty(), "date should not be empty.");
        // Date format: "yyyy-MM-dd HH:mm:ss" (no nanoseconds)
        assertFalse(record.getDate().contains("T"), "date should not contain 'T' character.");
    }

    @Test
    void constructor_zeroValues() {
        GameRecord record = new GameRecord(0, 0, 0);
        assertEquals(0, record.getTotalMoves());
        assertEquals(0, record.getHits());
        assertEquals(0, record.getSunkShips());
        assertNotNull(record.getDate());
    }

    @Test
    void constructor_largeValues() {
        GameRecord record = new GameRecord(999, 500, 11);
        assertEquals(999, record.getTotalMoves());
        assertEquals(500, record.getHits());
        assertEquals(11, record.getSunkShips());
    }
}
