package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the Move class.
 * Covers: constructor, toString, getNumber, getShots, getShotResults, processEnemyFire.
 *
 * Cyclomatic Complexity for processEnemyFire: ~20+ branches tested below.
 */
public class MoveTest {

    // -------------------------------------------------------------------------
    // Helpers

    private static IGame.ShotResult invalidResult() {
        return new IGame.ShotResult(false, false, null, false);
    }
    private static IGame.ShotResult repeatedResult() {
        return new IGame.ShotResult(true, true, null, false);
    }
    private static IGame.ShotResult missResult() {
        return new IGame.ShotResult(true, false, null, false);
    }
    private static IGame.ShotResult hitResult(IShip ship) {
        return new IGame.ShotResult(true, false, ship, false);
    }
    private static IGame.ShotResult sunkResult(IShip ship) {
        return new IGame.ShotResult(true, false, ship, true);
    }

    private static List<IPosition> threePositions() {
        return List.of(new Position(0, 0), new Position(0, 1), new Position(0, 2));
    }

    // -------------------------------------------------------------------------
    // Constructor and simple getters

    @Test
    void constructor() {
        List<IPosition> shots = threePositions();
        List<IGame.ShotResult> results = List.of(missResult(), missResult(), missResult());
        Move move = new Move(1, shots, results);

        assertNotNull(move, "Move should not be null.");
        assertEquals(1, move.getNumber(), "Move number should be 1.");
        assertEquals(shots, move.getShots(), "Shots list should match.");
        assertEquals(results, move.getShotResults(), "ShotResults should match.");
    }

    @Test
    void toStringTest() {
        List<IPosition> shots = threePositions();
        List<IGame.ShotResult> results = List.of(missResult(), missResult(), missResult());
        Move move = new Move(5, shots, results);
        String s = move.toString();
        assertNotNull(s);
        assertTrue(s.contains("5"), "toString should include move number.");
    }

    @Test
    void getNumber() {
        Move move = new Move(42, threePositions(), new ArrayList<>());
        assertEquals(42, move.getNumber());
    }

    @Test
    void getShots() {
        List<IPosition> shots = threePositions();
        Move move = new Move(1, shots, new ArrayList<>());
        assertEquals(3, move.getShots().size());
    }

    @Test
    void getShotResults() {
        List<IGame.ShotResult> results = List.of(missResult(), missResult());
        Move move = new Move(1, threePositions(), results);
        assertEquals(2, move.getShotResults().size());
    }

    // -------------------------------------------------------------------------
    // processEnemyFire – verbose = false (just JSON returned)

    @Test
    void processEnemyFire_notVerbose_allMiss() {
        List<IGame.ShotResult> results = List.of(missResult(), missResult(), missResult());
        Move move = new Move(1, threePositions(), results);
        String json = move.processEnemyFire(false);
        assertNotNull(json, "JSON must not be null.");
        assertTrue(json.contains("validShots"), "JSON should contain 'validShots'.");
    }

    // -------------------------------------------------------------------------
    // processEnemyFire – verbose = true, various branch combinations

    /** Branch: all shots are valid misses (3 valid, 3 missed, 0 outside) */
    @Test
    void processEnemyFire_verbose_allMiss() {
        List<IGame.ShotResult> results = List.of(missResult(), missResult(), missResult());
        Move move = new Move(1, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: singular "tiro" when validShots == 1 */
    @Test
    void processEnemyFire_verbose_oneMiss_twoInvalid() {
        List<IGame.ShotResult> results = List.of(missResult(), invalidResult(), invalidResult());
        Move move = new Move(2, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: validShots == 0 && repeatedShots > 0 → "tiro(s) repetido(s)" only */
    @Test
    void processEnemyFire_verbose_allRepeated() {
        List<IGame.ShotResult> results = List.of(repeatedResult(), repeatedResult(), repeatedResult());
        Move move = new Move(3, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: singular "tiro repetido" when repeatedShots == 1 */
    @Test
    void processEnemyFire_verbose_oneRepeated() {
        List<IGame.ShotResult> results = List.of(repeatedResult(), invalidResult(), invalidResult());
        Move move = new Move(4, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: hit on ship (not sunk) – hitsPerBoat populated, sunkBoatsCount empty */
    @Test
    void processEnemyFire_verbose_hitNotSunk() {
        IShip frigate = new Frigate(Compass.NORTH, new Position(1, 1));
        List<IGame.ShotResult> results = List.of(hitResult(frigate), missResult(), missResult());
        Move move = new Move(5, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: multiple hits on same ship (hits > 1 → "tiros") */
    @Test
    void processEnemyFire_verbose_multipleHitsNotSunk() {
        IShip galleon = new Galleon(Compass.NORTH, new Position(0, 0));
        List<IGame.ShotResult> results = List.of(hitResult(galleon), hitResult(galleon), missResult());
        Move move = new Move(6, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: ship sunk – sunkBoatsCount populated, no missed shots → remove trailing " +" */
    @Test
    void processEnemyFire_verbose_sunk_noMiss() {
        IShip barge = new Barge(Compass.NORTH, new Position(2, 2));
        // 1 sunk shot, 2 invalid → outsideShots = 2
        List<IGame.ShotResult> results = List.of(sunkResult(barge), invalidResult(), invalidResult());
        Move move = new Move(7, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: ship sunk with missed shots → missedShots branch taken, no setLength needed */
    @Test
    void processEnemyFire_verbose_sunk_withMiss() {
        IShip barge = new Barge(Compass.NORTH, new Position(2, 2));
        List<IGame.ShotResult> results = List.of(sunkResult(barge), missResult(), missResult());
        Move move = new Move(8, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: mix of repeated and valid misses – repeatedShots > 0 && validShots > 0 → ", " appended */
    @Test
    void processEnemyFire_verbose_mixRepeatedAndMiss() {
        List<IGame.ShotResult> results = List.of(repeatedResult(), missResult(), missResult());
        Move move = new Move(9, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: outsideShots > 0 and output is empty (all invalid, no verbose parts) */
    @Test
    void processEnemyFire_verbose_allInvalid_outsideShots() {
        List<IGame.ShotResult> results = List.of(invalidResult(), invalidResult(), invalidResult());
        Move move = new Move(10, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: outsideShots > 1 → plural "exteriores"; output not empty → ", " prefix */
    @Test
    void processEnemyFire_verbose_oneMiss_twoOutside() {
        List<IGame.ShotResult> results = List.of(missResult(), invalidResult(), invalidResult());
        Move move = new Move(11, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: multiple missed shots (missedShots > 1 → "tiros") */
    @Test
    void processEnemyFire_verbose_multipleMissedShots() {
        List<IGame.ShotResult> results = List.of(missResult(), missResult(), missResult());
        Move move = new Move(12, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: count > 1 → sunk ship type with 's' suffix */
    @Test
    void processEnemyFire_verbose_twoSunkSameType() {
        IShip b1 = new Barge(Compass.NORTH, new Position(2, 2));
        IShip b2 = new Barge(Compass.NORTH, new Position(5, 5));
        // sink both barges
        b1.sink();
        b2.sink();
        List<IGame.ShotResult> results = List.of(sunkResult(b1), sunkResult(b2), missResult());
        Move move = new Move(13, threePositions(), results);
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Branch: hitsPerBoat has entry that IS in sunkBoatsCount → skip (not printed in hits section) */
    @Test
    void processEnemyFire_verbose_sunkShip_hitsSkipped() {
        IShip barge = new Barge(Compass.NORTH, new Position(2, 2));
        // Both a hit and a sunk result for same ship
        List<IGame.ShotResult> results = List.of(hitResult(barge), sunkResult(barge), missResult());
        Move move = new Move(14, threePositions(), results);
        // Should not throw and sunk branch should prevent hit from being double-reported
        assertDoesNotThrow(() -> move.processEnemyFire(true));
    }

    /** Verify JSON structure contains expected keys */
    @Test
    void processEnemyFire_jsonContainsKeys() {
        IShip barge = new Barge(Compass.NORTH, new Position(0, 0));
        List<IGame.ShotResult> results = List.of(sunkResult(barge), missResult(), repeatedResult());
        Move move = new Move(1, threePositions(), results);
        String json = move.processEnemyFire(false);
        assertTrue(json.contains("validShots"));
        assertTrue(json.contains("outsideShots"));
        assertTrue(json.contains("repeatedShots"));
        assertTrue(json.contains("missedShots"));
        assertTrue(json.contains("sunkBoats"));
        assertTrue(json.contains("hitsOnBoats"));
    }
}
