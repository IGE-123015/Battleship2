package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * IGameTest – tests the IGame.ShotResult record.
 *
 * @author Francisco Santos
 * Date: 2026-04-25
 *
 * Cyclomatic Complexity:
 * - ShotResult (record constructor): 1
 * - ShotResult.valid():    1
 * - ShotResult.repeated(): 1
 * - ShotResult.ship():     1
 * - ShotResult.sunk():     1
 */
class IGameTest {

    // ------------------------------------------------------------------
    // ShotResult record – all four accessor methods + constructor
    // ------------------------------------------------------------------

    @Test
    @DisplayName("ShotResult – invalid shot: valid=false, repeated=false, ship=null, sunk=false")
    void shotResult_invalid() {
        IGame.ShotResult r = new IGame.ShotResult(false, false, null, false);
        assertAll("invalid shot result",
                () -> assertFalse(r.valid(),    "Error: valid should be false for an invalid shot."),
                () -> assertFalse(r.repeated(), "Error: repeated should be false for an invalid shot."),
                () -> assertNull(r.ship(),      "Error: ship should be null for an invalid shot."),
                () -> assertFalse(r.sunk(),     "Error: sunk should be false for an invalid shot.")
        );
    }

    @Test
    @DisplayName("ShotResult – repeated shot: valid=true, repeated=true, ship=null, sunk=false")
    void shotResult_repeated() {
        IGame.ShotResult r = new IGame.ShotResult(true, true, null, false);
        assertAll("repeated shot result",
                () -> assertTrue(r.valid(),     "Error: valid should be true for a repeated shot."),
                () -> assertTrue(r.repeated(),  "Error: repeated should be true for a repeated shot."),
                () -> assertNull(r.ship(),      "Error: ship should be null for a repeated shot."),
                () -> assertFalse(r.sunk(),     "Error: sunk should be false for a repeated shot.")
        );
    }

    @Test
    @DisplayName("ShotResult – miss: valid=true, repeated=false, ship=null, sunk=false")
    void shotResult_miss() {
        IGame.ShotResult r = new IGame.ShotResult(true, false, null, false);
        assertAll("miss result",
                () -> assertTrue(r.valid(),     "Error: valid should be true for a miss."),
                () -> assertFalse(r.repeated(), "Error: repeated should be false for a miss."),
                () -> assertNull(r.ship(),      "Error: ship should be null for a miss."),
                () -> assertFalse(r.sunk(),     "Error: sunk should be false for a miss.")
        );
    }

    @Test
    @DisplayName("ShotResult – hit (not sunk): valid=true, repeated=false, ship != null, sunk=false")
    void shotResult_hit_notSunk() {
        IShip frigate = new Frigate(Compass.NORTH, new Position(1, 1));
        IGame.ShotResult r = new IGame.ShotResult(true, false, frigate, false);
        assertAll("hit but not sunk",
                () -> assertTrue(r.valid(),       "Error: valid should be true for a hit."),
                () -> assertFalse(r.repeated(),   "Error: repeated should be false for a new hit."),
                () -> assertSame(frigate, r.ship(),"Error: ship should be the frigate."),
                () -> assertFalse(r.sunk(),       "Error: sunk should be false when ship still floats.")
        );
    }

    @Test
    @DisplayName("ShotResult – sunk: valid=true, repeated=false, ship != null, sunk=true")
    void shotResult_sunk() {
        IShip barge = new Barge(Compass.NORTH, new Position(3, 3));
        IGame.ShotResult r = new IGame.ShotResult(true, false, barge, true);
        assertAll("sunk result",
                () -> assertTrue(r.valid(),      "Error: valid should be true for a sunk shot."),
                () -> assertFalse(r.repeated(),  "Error: repeated should be false."),
                () -> assertSame(barge, r.ship(),"Error: ship should be the barge."),
                () -> assertTrue(r.sunk(),       "Error: sunk should be true when the ship is sunk.")
        );
    }

    @Test
    @DisplayName("ShotResult – two records with same values are equal (record auto-generated equals)")
    void shotResult_equality() {
        IGame.ShotResult r1 = new IGame.ShotResult(true, false, null, false);
        IGame.ShotResult r2 = new IGame.ShotResult(true, false, null, false);
        assertEquals(r1, r2, "Error: Two ShotResult records with identical fields must be equal.");
        assertEquals(r1.hashCode(), r2.hashCode(),
                "Error: Equal records must have the same hash code.");
    }

    @Test
    @DisplayName("ShotResult – toString contains field values (record auto-generated toString)")
    void shotResult_toString() {
        IGame.ShotResult r = new IGame.ShotResult(true, false, null, true);
        String s = r.toString();
        assertNotNull(s, "Error: toString() should not return null.");
        assertTrue(s.contains("true"),  "Error: toString should contain 'true' for valid.");
        assertTrue(s.contains("false"), "Error: toString should contain 'false' for repeated.");
    }
}