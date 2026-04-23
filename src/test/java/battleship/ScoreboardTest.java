package battleship;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Scoreboard class.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - saveScore: 4
 * - printScoreboard: 4
 *
 * Strategy: FILE_NAME ("scoreboard.json") is a compile-time constant inlined
 * by the compiler, so it cannot be redirected via reflection. Instead, we
 * backup
 * and restore the real scoreboard.json around every test for full isolation.
 */
public class ScoreboardTest {

    private static final String SCORE_FILE = "scoreboard.json";

    /**
     * Backup of the original scoreboard.json content (null if file did not exist).
     */
    private byte[] backup;

    /**
     * Backup and remove scoreboard.json before each test so every test starts
     * clean.
     */
    @BeforeEach
    void setUp() throws Exception {
        Path p = Path.of(SCORE_FILE);
        if (Files.exists(p) && !Files.isDirectory(p)) {
            backup = Files.readAllBytes(p);
            Files.delete(p);
        } else if (Files.isDirectory(p)) {
            // Edge case: if previous test left a directory, delete it
            p.toFile().delete();
            backup = null;
        } else {
            backup = null;
        }
    }

    /**
     * Delete any file/directory created by the test and restore the original
     * backup.
     */
    @AfterEach
    void tearDown() throws Exception {
        Path p = Path.of(SCORE_FILE);
        if (Files.exists(p)) {
            File f = p.toFile();
            // setWritable so we can delete a read-only file left by exception-path tests
            f.setWritable(true);
            if (f.isDirectory()) {
                f.delete();
            } else {
                Files.delete(p);
            }
        }
        if (backup != null) {
            Files.write(Path.of(SCORE_FILE), backup);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Captures everything written to System.out during the runnable. */
    private String captureOut(Runnable r) {
        PrintStream original = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            r.run();
        } finally {
            System.setOut(original);
        }
        return baos.toString();
    }

    /** Captures everything written to System.err during the runnable. */
    private String captureErr(Runnable r) {
        PrintStream original = System.err;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        try {
            r.run();
        } finally {
            System.setErr(original);
        }
        return baos.toString();
    }

    // -------------------------------------------------------------------------
    // saveScore tests
    // -------------------------------------------------------------------------

    /**
     * saveScore1 — file does not exist yet.
     * Branch: file.exists() && file.length() > 0 → false (new file is created).
     * Cyclomatic Complexity path 1/4
     */
    @Test
    void testSaveScore1() throws Exception {
        assertFalse(Files.exists(Path.of(SCORE_FILE)),
                "Error: scoreboard.json should not exist before this test.");

        Scoreboard.saveScore(10, 5, 2);

        assertTrue(Files.exists(Path.of(SCORE_FILE)),
                "Error: scoreboard.json should have been created after saveScore.");
        String content = Files.readString(Path.of(SCORE_FILE));
        assertAll("Error: saved record should contain correct values",
                () -> assertTrue(content.contains("\"totalMoves\": 10"),
                        "Error: totalMoves should be 10."),
                () -> assertTrue(content.contains("\"hits\": 5"),
                        "Error: hits should be 5."),
                () -> assertTrue(content.contains("\"sunkShips\": 2"),
                        "Error: sunkShips should be 2."));
    }

    /**
     * saveScore2 — file already exists with one valid entry.
     * Branch: file.exists() && file.length() > 0 → true (history is read and
     * appended).
     * Cyclomatic Complexity path 2/4
     */
    @Test
    void testSaveScore2() throws Exception {
        // First save creates the file
        Scoreboard.saveScore(10, 5, 2);
        // Second save must read the existing file and append
        Scoreboard.saveScore(20, 8, 3);

        String content = Files.readString(Path.of(SCORE_FILE));
        assertAll("Error: both records should be present in the file",
                () -> assertTrue(content.contains("\"totalMoves\": 10"),
                        "Error: first record totalMoves should be 10."),
                () -> assertTrue(content.contains("\"totalMoves\": 20"),
                        "Error: second record totalMoves should be 20."));
    }

    /**
     * saveScore3 — file exists but Gson returns null (scores == null branch).
     * Writing "null" as JSON makes gson.fromJson return null, triggering the
     * null-fallback: if (scores == null) scores = new ArrayList<>().
     * Branch: scores == null → true.
     * Cyclomatic Complexity path 3/4
     */
    @Test
    void testSaveScore3() throws Exception {
        // "null" is valid JSON whose parsed value is Java null
        Files.writeString(Path.of(SCORE_FILE), "null");

        assertDoesNotThrow(() -> Scoreboard.saveScore(1, 1, 1),
                "Error: saveScore should not throw when file contains JSON null.");

        String content = Files.readString(Path.of(SCORE_FILE));
        assertTrue(content.contains("\"totalMoves\": 1"),
                "Error: record should be saved after null-JSON fallback.");
    }

    /**
     * saveScore4 — exception path: file cannot be written.
     * A read-only scoreboard.json blocks FileWriter → IOException → caught.
     * Branch: catch (Exception e) executed → error printed to stderr.
     * Cyclomatic Complexity path 4/4
     */
    @Test
    void testSaveScore4() throws Exception {
        // Write a valid file then make it read-only so FileWriter fails
        Files.writeString(Path.of(SCORE_FILE), "[]");
        assertTrue(Path.of(SCORE_FILE).toFile().setReadOnly(),
                "Error: could not make scoreboard.json read-only for this test.");

        String err = captureErr(() -> Scoreboard.saveScore(1, 1, 1));

        assertFalse(err.isBlank(),
                "Error: an error message should have been printed to stderr.");
        assertTrue(err.contains("Erro ao guardar o Scoreboard"),
                "Error: stderr message should indicate a save failure.");
    }

    // -------------------------------------------------------------------------
    // printScoreboard tests
    // -------------------------------------------------------------------------

    /**
     * printScoreboard1 — file does not exist.
     * Branch: !file.exists() → true → early return with "no games" message.
     * Cyclomatic Complexity path 1/4
     */
    @Test
    void testPrintScoreboard1() {
        assertFalse(Files.exists(Path.of(SCORE_FILE)),
                "Error: scoreboard.json should not exist before this test.");

        String out = captureOut(Scoreboard::printScoreboard);

        assertTrue(out.contains("Ainda não há jogos registados"),
                "Error: should print 'no games' message when file is missing.");
    }

    /**
     * printScoreboard2 — file exists but is empty (length == 0).
     * Branch: file.length() == 0 → true → early return with "no games" message.
     * Cyclomatic Complexity path 2/4
     */
    @Test
    void testPrintScoreboard2() throws Exception {
        Files.writeString(Path.of(SCORE_FILE), "");

        String out = captureOut(Scoreboard::printScoreboard);

        assertTrue(out.contains("Ainda não há jogos registados"),
                "Error: should print 'no games' message when file is empty.");
    }

    /**
     * printScoreboard3 — file exists with two valid records.
     * Branch: condition → false; for-loop body executed for each record.
     * Cyclomatic Complexity path 3/4
     */
    @Test
    void testPrintScoreboard3() {
        // Create the records via saveScore (tested independently above)
        Scoreboard.saveScore(10, 5, 2);
        Scoreboard.saveScore(15, 7, 3);

        String out = captureOut(Scoreboard::printScoreboard);

        assertAll("Error: output should list both games with correct data",
                () -> assertTrue(out.contains("SCOREBOARD"),
                        "Error: output should contain the SCOREBOARD header."),
                () -> assertTrue(out.contains("Jogo 1"),
                        "Error: output should list 'Jogo 1'."),
                () -> assertTrue(out.contains("Jogo 2"),
                        "Error: output should list 'Jogo 2'."),
                () -> assertTrue(out.contains("Jogadas: 10"),
                        "Error: first game should show 10 moves."),
                () -> assertTrue(out.contains("Jogadas: 15"),
                        "Error: second game should show 15 moves."));
    }

    /**
     * saveScore5 — file exists but has length == 0.
     * Covers the short-circuit false branch of: file.exists() && file.length() > 0
     * where file.exists() = true BUT file.length() = 0 → condition overall false.
     * This is the missing middle branch of the && compound condition.
     */
    @Test
    void testSaveScore5() throws Exception {
        // Empty file: exists() = true, length() = 0 → if block skipped
        Files.writeString(Path.of(SCORE_FILE), "");

        assertDoesNotThrow(() -> Scoreboard.saveScore(7, 3, 1),
                "Error: saveScore should not throw when file exists but is empty.");

        String content = Files.readString(Path.of(SCORE_FILE));
        assertTrue(content.contains("\"totalMoves\": 7"),
                "Error: record should be saved even when file was initially empty.");
    }

    /**
     * saveScore6 — scores != null (fromJson returns a valid list, not null).
     * Ensures the 'false' branch of: if (scores == null) is covered when
     * a well-formed JSON array is present so no fallback is needed.
     */
    @Test
    void testSaveScore6() throws Exception {
        // Pre-populate with a valid JSON array so fromJson returns a non-null list
        Files.writeString(Path.of(SCORE_FILE),
                "[{\"date\":\"2024-01-01 00:00:00\",\"totalMoves\":5,\"hits\":3,\"sunkShips\":1}]");

        assertDoesNotThrow(() -> Scoreboard.saveScore(9, 4, 2),
                "Error: saveScore should not throw when file has a valid existing list.");

        String content = Files.readString(Path.of(SCORE_FILE));
        assertAll("Error: both the pre-existing and new record must be present",
                () -> assertTrue(content.contains("\"totalMoves\": 5"),
                        "Error: pre-existing record should be retained."),
                () -> assertTrue(content.contains("\"totalMoves\": 9"),
                        "Error: new record should have been appended."));
    }

    /**
     * printScoreboard5 — file contains a valid but empty JSON array [].
     * Branch: for loop condition i < scores.size() → false immediately (0 records).
     * This covers the loop-not-entered branch of the for statement.
     * The condition || is false on both sides: file exists and length > 0.
     */
    @Test
    void testPrintScoreboard5() throws Exception {
        // Valid non-empty JSON file whose list has 0 elements
        Files.writeString(Path.of(SCORE_FILE), "[]");

        String out = captureOut(Scoreboard::printScoreboard);

        assertAll("Error: header and footer should print even with 0 records",
                () -> assertTrue(out.contains("SCOREBOARD"),
                        "Error: SCOREBOARD header should appear."),
                () -> assertFalse(out.contains("Jogo 1"),
                        "Error: 'Jogo 1' should not appear when list is empty."));
    }

    /**
     * printScoreboard6 — file with exactly one record.
     * Ensures the for-loop executes once (i=0 true) then exits (i=1 false).
     * Covers both the true and false outcomes of i < scores.size() in one test.
     */
    @Test
    void testPrintScoreboard6() {
        Scoreboard.saveScore(42, 11, 4);

        String out = captureOut(Scoreboard::printScoreboard);

        assertAll("Error: exactly one record should be listed",
                () -> assertTrue(out.contains("Jogo 1"),
                        "Error: 'Jogo 1' should be listed."),
                () -> assertFalse(out.contains("Jogo 2"),
                        "Error: 'Jogo 2' should not appear when only one record exists."),
                () -> assertTrue(out.contains("Jogadas: 42"),
                        "Error: the single record should show 42 moves."));
    }
}
