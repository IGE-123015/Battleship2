package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Ship.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - getCategory: 1
 * - getSize: 1
 * - getBearing: 1
 * - getPositions: 1
 * - stillFloating: 2
 * - shoot: 2
 * - occupies: 2
 * - tooCloseTo (IShip): 2
 * - tooCloseTo (IPosition): 2
 * - getTopMostPos: 2
 * - getBottomMostPos: 2
 * - getLeftMostPos: 2
 * - getRightMostPos: 2
 * - buildShip: 6 (5 ship types + default null)
 * - getAdjacentPositions: 2
 * - sink: 1
 * - getPosition: 1
 * - toString: 1
 */
public class ShipTest {

    private Ship ship;

    @BeforeEach
    void setUp() {
        // Since Ship is abstract, instantiate it with a concrete subclass (e.g., Barge)
        ship = new Barge(Compass.NORTH, new Position(5, 5));
    }

    @AfterEach
    void tearDown() {
        ship = null;
    }

    /**
     * Test for the constructor.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testConstructor() {
        assertNotNull(ship, "Error: Ship instance should not be null.");
        assertEquals("Barca", ship.getCategory(), "Error: Ship category is incorrect.");
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing is incorrect.");
        assertEquals(1, ship.getSize(), "Error: Ship size is incorrect.");
        assertFalse(ship.getPositions().isEmpty(), "Error: Ship positions should not be empty.");
    }

    /**
     * Test for the getCategory method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetCategory() {
        assertEquals("Barca", ship.getCategory(), "Error: Ship category should be 'Barca'.");
    }

    /**
     * Test for the getSize method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetSize() {
        assertEquals(1, ship.getSize(), "Error: Ship size should be 1.");
    }

    /**
     * Test for the getBearing method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetBearing() {
        assertEquals(Compass.NORTH, ship.getBearing(), "Error: Ship bearing should be NORTH.");
    }

    /**
     * Test for the getPositions method.
     * Cyclomatic Complexity: 1
     */
    @Test
    void testGetPositions() {
        List<IPosition> positions = ship.getPositions();
        assertNotNull(positions, "Error: Ship positions should not be null.");
        assertEquals(1, positions.size(), "Error: Ship should have exactly one position.");
        assertEquals(5, positions.get(0).getRow(), "Error: Position's row should be 5.");
        assertEquals(5, positions.get(0).getColumn(), "Error: Position's column should be 5.");
    }

    /**
     * Test for the stillFloating method (all positions intact).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testStillFloating1() {
        assertTrue(ship.stillFloating(), "Error: Ship should still be floating.");
    }

    /**
     * Test for the stillFloating method (all positions hit).
     */
    @Test
    void testStillFloating2() {
        ship.getPositions().get(0).shoot();
        assertFalse(ship.stillFloating(), "Error: Ship should no longer be floating after being hit.");
    }

    /**
     * Test for the shoot method (valid position).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testShoot1() {
        Position target = new Position(5, 5);
        ship.shoot(target);
        assertTrue(ship.getPositions().get(0).isHit(), "Error: Position should be marked as hit.");
    }

    /**
     * Test for the shoot method (invalid position).
     */
    @Test
    void testShoot2() {
        Position target = new Position(0, 0);
        ship.shoot(target); // No exception expected
        assertFalse(ship.getPositions().get(0).isHit(), "Error: Position should not be marked as hit for an invalid target.");
    }

    /**
     * Test for the occupies method (position occupied).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testOccupies1() {
        Position pos = new Position(5, 5);
        assertTrue(ship.occupies(pos), "Error: Ship should occupy position (5, 5).");
    }

    /**
     * Test for the occupies method (position not occupied).
     */
    @Test
    void testOccupies2() {
        Position pos = new Position(1, 1);
        assertFalse(ship.occupies(pos), "Error: Ship should not occupy position (1, 1).");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships too close).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToShip1() {
        Ship nearbyShip = new Barge(Compass.NORTH, new Position(5, 6));
        assertTrue(ship.tooCloseTo(nearbyShip), "Error: Ships should be too close.");
    }

    /**
     * Test for the tooCloseTo method with another IShip (ships not close).
     */
    @Test
    void testTooCloseToShip2() {
        Ship farShip = new Barge(Compass.NORTH, new Position(10, 10));
        assertFalse(ship.tooCloseTo(farShip), "Error: Ships should not be too close.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions adjacent).
     * Cyclomatic Complexity: 2
     */
    @Test
    void testTooCloseToPosition1() {
        Position pos = new Position(5, 6); // Adjacent position
        assertTrue(ship.tooCloseTo(pos), "Error: Ship should be too close to the given position.");
    }

    /**
     * Test for the tooCloseTo method with an IPosition (positions not adjacent).
     */
    @Test
    void testTooCloseToPosition2() {
        Position pos = new Position(7, 7); // Non-adjacent position
        assertFalse(ship.tooCloseTo(pos), "Error: Ship should not be too close to the given position.");
    }

    /**
     * Test for the getTopMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetTopMostPos() {
        assertEquals(5, ship.getTopMostPos(), "Error: The topmost position should be 5.");
    }

    /**
     * Test for the getBottomMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetBottomMostPos() {
        assertEquals(5, ship.getBottomMostPos(), "Error: The bottommost position should be 5.");
    }

    /**
     * Test for the getLeftMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetLeftMostPos() {
        assertEquals(5, ship.getLeftMostPos(), "Error: The leftmost position should be 5.");
    }

    /**
     * Test for the getRightMostPos method.
     * Cyclomatic Complexity: 2
     */
    @Test
    void testGetRightMostPos() {
        assertEquals(5, ship.getRightMostPos(), "Error: The rightmost position should be 5.");
    }

    // ------------------------------------------------------------------
    // buildShip – static factory (CC: 6 – one branch per ship type + default)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("buildShip – 'barca' creates a Barge of size 1")
    void testBuildShip_barca() {
        Ship s = Ship.buildShip("barca", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "Error: buildShip('barca') should not return null.");
        assertEquals("Barca", s.getCategory(), "Error: Expected category 'Barca'.");
        assertEquals(1, s.getSize(), "Error: Barge size should be 1.");
    }

    @Test
    @DisplayName("buildShip – 'caravela' creates a Caravel of size 2")
    void testBuildShip_caravela() {
        Ship s = Ship.buildShip("caravela", Compass.EAST, new Position(0, 0));
        assertNotNull(s, "Error: buildShip('caravela') should not return null.");
        assertEquals("Caravela", s.getCategory(), "Error: Expected category 'Caravela'.");
        assertEquals(2, s.getSize(), "Error: Caravel size should be 2.");
    }

    @Test
    @DisplayName("buildShip – 'nau' creates a Carrack of size 3")
    void testBuildShip_nau() {
        Ship s = Ship.buildShip("nau", Compass.SOUTH, new Position(0, 0));
        assertNotNull(s, "Error: buildShip('nau') should not return null.");
        assertEquals("Nau", s.getCategory(), "Error: Expected category 'Nau'.");
        assertEquals(3, s.getSize(), "Error: Carrack size should be 3.");
    }

    @Test
    @DisplayName("buildShip – 'fragata' creates a Frigate of size 4")
    void testBuildShip_fragata() {
        Ship s = Ship.buildShip("fragata", Compass.WEST, new Position(0, 0));
        assertNotNull(s, "Error: buildShip('fragata') should not return null.");
        assertEquals("Fragata", s.getCategory(), "Error: Expected category 'Fragata'.");
        assertEquals(4, s.getSize(), "Error: Frigate size should be 4.");
    }

    @Test
    @DisplayName("buildShip – 'galeao' creates a Galleon of size 5")
    void testBuildShip_galeao() {
        Ship s = Ship.buildShip("galeao", Compass.NORTH, new Position(0, 0));
        assertNotNull(s, "Error: buildShip('galeao') should not return null.");
        assertEquals("Galeao", s.getCategory(), "Error: Expected category 'Galeao'.");
        assertEquals(5, s.getSize(), "Error: Galleon size should be 5.");
    }

    @Test
    @DisplayName("buildShip – unknown type returns null (default branch)")
    void testBuildShip_default() {
        Ship s = Ship.buildShip("unknown", Compass.NORTH, new Position(0, 0));
        assertNull(s, "Error: buildShip with unknown type should return null.");
    }

    // ------------------------------------------------------------------
    // getAdjacentPositions – CC: 2 (inner != check both branches)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getAdjacentPositions – returns positions adjacent to the ship but not part of it")
    void testGetAdjacentPositions_barge() {
        // Barge at (5,5) has 1 position. Adjacent positions are the surrounding cells.
        List<IPosition> adj = ship.getAdjacentPositions();
        assertNotNull(adj, "Error: Adjacent positions should not be null.");
        assertFalse(adj.isEmpty(), "Error: Barge at (5,5) should have adjacent positions.");
        // None of the adjacent positions should be the ship's own position
        for (IPosition p : adj)
            assertFalse(ship.occupies(p), "Error: Adjacent position should not be occupied by the ship itself.");
    }

    @Test
    @DisplayName("getAdjacentPositions – multi-cell ship does not include own cells; no duplicates")
    void testGetAdjacentPositions_frigate() {
        // Frigate NORTH at (3,3) occupies 4 cells vertically (3,3),(4,3),(5,3),(6,3)
        Ship frigate = new Frigate(Compass.NORTH, new Position(3, 3));
        List<IPosition> adj = frigate.getAdjacentPositions();
        assertNotNull(adj, "Error: Adjacent positions must not be null.");
        // Verify no ship cell appears in the adjacent list
        for (IPosition p : adj)
            assertFalse(frigate.occupies(p),
                    "Error: Adjacent position " + p + " should not be occupied by the frigate.");
        // Verify no duplicates
        long distinct = adj.stream().distinct().count();
        assertEquals(adj.size(), distinct, "Error: getAdjacentPositions must not return duplicates.");
    }

    // ------------------------------------------------------------------
    // sink – CC: 1 (single loop, always executes)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("sink – marks all positions as hit; ship is no longer floating")
    void testSink() {
        Ship frigate = new Frigate(Compass.EAST, new Position(2, 2));
        assertTrue(frigate.stillFloating(), "Error: Frigate should be floating before sink().");
        frigate.sink();
        assertFalse(frigate.stillFloating(), "Error: Frigate should not be floating after sink().");
        for (IPosition p : frigate.getPositions())
            assertTrue(p.isHit(), "Error: Every position should be hit after sink().");
    }

    // ------------------------------------------------------------------
    // getPosition – CC: 1 (returns anchor position)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getPosition – returns the anchor (top-left) position used to construct the ship")
    void testGetPosition() {
        IPosition anchor = ship.getPosition();
        assertNotNull(anchor, "Error: getPosition() should not return null.");
        assertEquals(5, anchor.getRow(),    "Error: Anchor row should be 5.");
        assertEquals(5, anchor.getColumn(), "Error: Anchor column should be 5.");
    }

    // ------------------------------------------------------------------
    // toString – CC: 1
    // ------------------------------------------------------------------

    @Test
    @DisplayName("toString – returns a non-empty string containing category, bearing and position")
    void testToString() {
        String s = ship.toString();
        assertNotNull(s, "Error: toString() should not return null.");
        assertFalse(s.isEmpty(), "Error: toString() should not return an empty string.");
        assertTrue(s.contains("Barca"),  "Error: toString() should contain the category 'Barca'.");
        assertTrue(s.contains("n"),      "Error: toString() should contain the bearing character 'n'.");
        assertTrue(s.contains("F6"),     "Error: toString() should contain the position 'F6' (row=5, col=5).");
    }
}