package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for PdfReport.
 * Author: 94334
 * Date: 2026-04-16
 *
 * Method under test: exportMovesToPDF(List<IMove> moves, String filename)
 *
 * Cyclomatic Complexity breakdown:
 *   - try block             : 1
 *   - for (IMove move)      : 2  (0 iterations / ≥1 iterations)
 *   - for (IPosition pos)   : 2  (0 shots / ≥1 shots)
 *   - catch (IOException)   : 1  (exception path)
 *   Total                   : ~5 independent paths
 */
public class PdfReportTest {

    /** Temporary directory cleaned up after each test. */
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("pdfReportTest_");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Delete all files inside the temp directory then the directory itself
        File dir = tempDir.toFile();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) f.delete();
        }
        dir.delete();
    }

    // ------------------------------------------------------------------
    // Branch: for (IMove move : moves) with 0 iterations  →  loop body never entered
    // ------------------------------------------------------------------

    /**
     * Empty moves list: the outer for-loop has zero iterations.
     * The PDF header line is still written; only the per-move section is skipped.
     * Verifies the file is created and is non-empty.
     */
    @Test
    void exportMovesToPDF_emptyMoves() {
        String path = tempDir.resolve("empty.pdf").toString();

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(new ArrayList<>(), path),
                "exportMovesToPDF should not throw for an empty moves list.");

        File f = new File(path);
        assertTrue(f.exists(), "PDF file should be created even with no moves.");
        assertTrue(f.length() > 0, "PDF file should be non-empty.");
    }

    // ------------------------------------------------------------------
    // Branch: for (IMove move : moves) with ≥1 iterations
    //         for (IPosition pos : move.getShots()) with ≥1 iterations
    // ------------------------------------------------------------------

    /**
     * One move with three shots: both for-loops execute their bodies.
     * Covers the inner loop with positions.
     */
    @Test
    void exportMovesToPDF_oneMoveWithShots() {
        String path = tempDir.resolve("one_move.pdf").toString();

        // Build a move manually: moveNumber=1, three positions, empty results
        List<IPosition> shots = List.of(
                new Position(0, 0),
                new Position(1, 1),
                new Position(2, 2));
        List<IGame.ShotResult> results = new ArrayList<>();
        Move move = new Move(1, shots, results);

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(List.of(move), path),
                "exportMovesToPDF should not throw for a valid move with shots.");

        File f = new File(path);
        assertTrue(f.exists(), "PDF file should exist after export.");
        assertTrue(f.length() > 0, "PDF file should have content.");
    }

    // ------------------------------------------------------------------
    // Branch: for (IPosition pos : move.getShots()) with 0 iterations
    // ------------------------------------------------------------------

    /**
     * One move with zero shots: inner for-loop has zero iterations.
     * Covers the false branch of the inner loop (loop body never entered).
     */
    @Test
    void exportMovesToPDF_oneMoveNoShots() {
        String path = tempDir.resolve("no_shots.pdf").toString();

        Move move = new Move(1, new ArrayList<>(), new ArrayList<>());

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(List.of(move), path),
                "exportMovesToPDF should not throw for a move with no shots.");

        assertTrue(new File(path).exists(), "PDF file should be created.");
    }

    // ------------------------------------------------------------------
    // Multiple moves and multiple shots per move
    // ------------------------------------------------------------------

    /**
     * Three moves each with different numbers of shots.
     * Ensures the outer loop body executes multiple times and
     * the inner loop handles varying shot counts per move.
     */
    @Test
    void exportMovesToPDF_multipleMoves() {
        String path = tempDir.resolve("multiple.pdf").toString();

        List<IMove> moves = new ArrayList<>();

        // Move 1 — 3 shots
        moves.add(new Move(1,
                List.of(new Position(0, 0), new Position(1, 1), new Position(2, 2)),
                new ArrayList<>()));

        // Move 2 — 1 shot
        moves.add(new Move(2,
                List.of(new Position(5, 5)),
                new ArrayList<>()));

        // Move 3 — 0 shots (inner loop = 0 iterations again)
        moves.add(new Move(3, new ArrayList<>(), new ArrayList<>()));

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(moves, path),
                "exportMovesToPDF should not throw for multiple moves.");

        File f = new File(path);
        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }

    // ------------------------------------------------------------------
    // Branch: catch (IOException e)  →  invalid path triggers save failure
    // ------------------------------------------------------------------

    /**
     * Passing a path whose parent directory does not exist causes
     * document.save() to throw IOException, which is caught internally.
     * The method must NOT propagate the exception to the caller.
     */
    @Test
    void exportMovesToPDF_invalidPath_ioExceptionCaught() {
        String invalidPath = "/nonexistent_dir_94334/battleship/output.pdf";

        // The catch block only calls e.printStackTrace(), so no exception escapes
        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(new ArrayList<>(), invalidPath),
                "exportMovesToPDF must swallow IOException and not rethrow it.");

        // The file must NOT have been created
        assertFalse(new File(invalidPath).exists(),
                "No file should exist at an invalid path.");
    }

    // ------------------------------------------------------------------
    // Verify created file is a valid PDF (starts with %PDF magic bytes)
    // ------------------------------------------------------------------

    @Test
    void exportMovesToPDF_outputIsValidPdf() throws IOException {
        String path = tempDir.resolve("valid.pdf").toString();
        Move move = new Move(1,
                List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3)),
                new ArrayList<>());

        PdfReport.exportMovesToPDF(List.of(move), path);

        byte[] header = Files.readAllBytes(Path.of(path));
        // Every PDF starts with the magic string %PDF
        assertEquals('%', (char) header[0], "PDF must start with '%'.");
        assertEquals('P', (char) header[1], "PDF must start with '%PDF'.");
        assertEquals('D', (char) header[2], "PDF must start with '%PDF'.");
        assertEquals('F', (char) header[3], "PDF must start with '%PDF'.");
    }
}
