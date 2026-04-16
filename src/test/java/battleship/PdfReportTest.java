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
 *   - try block                        : 1
 *   - for (IMove move : moves)         : 2  (0 iterations / ≥1 iterations)
 *   - for (IPosition pos : move.getShots()) : 2  (0 shots / ≥1 shots)
 *   - catch (IOException e)            : 1
 *   Total                              : ~5 independent paths
 */
@DisplayName("PdfReport – unit tests")
public class PdfReportTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("pdfReportTest_");
    }

    @AfterEach
    void tearDown() {
        File dir = tempDir.toFile();
        File[] files = dir.listFiles();
        if (files != null)
            for (File f : files) f.delete();
        dir.delete();
    }

    // ------------------------------------------------------------------
    // Branch: for (IMove move : moves) – 0 iterations
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – empty moves list: outer loop has 0 iterations; file still created")
    void exportMovesToPDF_emptyMoves() {
        String path = tempDir.resolve("empty.pdf").toString();

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(new ArrayList<>(), path),
                "Should not throw for an empty moves list");

        File f = new File(path);
        assertTrue(f.exists(),   "PDF file should be created even with no moves");
        assertTrue(f.length() > 0, "PDF file should be non-empty");
    }

    // ------------------------------------------------------------------
    // Branch: for (IMove move : moves) – ≥1 iterations
    //         for (IPosition pos : move.getShots()) – ≥1 iterations
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – one move with 3 shots: both for-loops execute their bodies")
    void exportMovesToPDF_oneMoveWithShots() {
        String path = tempDir.resolve("one_move.pdf").toString();

        Move move = new Move(1,
                List.of(new Position(0, 0), new Position(1, 1), new Position(2, 2)),
                new ArrayList<>());

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(List.of(move), path),
                "Should not throw for a valid move with shots");

        assertTrue(new File(path).exists(),     "PDF file should exist");
        assertTrue(new File(path).length() > 0, "PDF file should have content");
    }

    // ------------------------------------------------------------------
    // Branch: for (IPosition pos : move.getShots()) – 0 iterations
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – one move with no shots: inner loop has 0 iterations")
    void exportMovesToPDF_oneMoveNoShots() {
        String path = tempDir.resolve("no_shots.pdf").toString();

        Move move = new Move(1, new ArrayList<>(), new ArrayList<>());

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(List.of(move), path),
                "Should not throw for a move with no shots");

        assertTrue(new File(path).exists(), "PDF file should be created");
    }

    // ------------------------------------------------------------------
    // Multiple moves – outer loop executes ≥3 times, inner loop varies
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – 3 moves (3 shots, 1 shot, 0 shots): both loops iterate multiple times")
    void exportMovesToPDF_multipleMoves() {
        String path = tempDir.resolve("multiple.pdf").toString();

        List<IMove> moves = new ArrayList<>();
        moves.add(new Move(1,
                List.of(new Position(0, 0), new Position(1, 1), new Position(2, 2)),
                new ArrayList<>()));
        moves.add(new Move(2,
                List.of(new Position(5, 5)),
                new ArrayList<>()));
        moves.add(new Move(3, new ArrayList<>(), new ArrayList<>()));

        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(moves, path),
                "Should not throw for multiple moves");

        assertTrue(new File(path).exists());
        assertTrue(new File(path).length() > 0);
    }

    // ------------------------------------------------------------------
    // Branch: catch (IOException e) – invalid path forces IOException
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – invalid path triggers IOException; method swallows it (no rethrow)")
    void exportMovesToPDF_invalidPath_ioExceptionCaught() {
        String invalidPath = "/nonexistent_dir_94334/battleship/output.pdf";

        // catch block only calls e.printStackTrace() — must NOT propagate
        assertDoesNotThrow(
                () -> PdfReport.exportMovesToPDF(new ArrayList<>(), invalidPath),
                "IOException must be caught internally and not propagated");

        assertFalse(new File(invalidPath).exists(),
                "No file should exist at an invalid path");
    }

    // ------------------------------------------------------------------
    // Verify output is a syntactically valid PDF
    // ------------------------------------------------------------------

    @Test
    @DisplayName("exportMovesToPDF – output file starts with %PDF magic bytes (valid PDF format)")
    void exportMovesToPDF_outputIsValidPdf() throws IOException {
        String path = tempDir.resolve("valid.pdf").toString();

        Move move = new Move(1,
                List.of(new Position('A', 1), new Position('B', 2), new Position('C', 3)),
                new ArrayList<>());

        PdfReport.exportMovesToPDF(List.of(move), path);

        byte[] header = Files.readAllBytes(Path.of(path));
        assertEquals('%', (char) header[0], "PDF must start with '%'");
        assertEquals('P', (char) header[1], "PDF must start with '%PDF'");
        assertEquals('D', (char) header[2], "PDF must start with '%PDF'");
        assertEquals('F', (char) header[3], "PDF must start with '%PDF'");
    }
}
