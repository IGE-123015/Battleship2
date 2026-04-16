package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Position.
 * Author: 94334
 * Date: 2026-04-16
 *
 * Cyclomatic Complexity per method:
 * - Position(int,int)  : 1
 * - Position(char,int) : 1
 * - getRow             : 1
 * - getColumn          : 1
 * - getClassicRow      : 1
 * - getClassicColumn   : 1
 * - isInside           : 4  (row<0 | col<0 | row>=SIZE | col>=SIZE)
 * - isAdjacentTo       : 4  (both true | row-short-circuit | row-ok-col-false | null)
 * - adjacentPositions  : 2  (isInside true / false)
 * - isOccupied         : 1
 * - isHit              : 1
 * - occupy             : 1
 * - shoot              : 1
 * - equals             : 3  (same ref | instanceof+coords | not instanceof)
 * - hashCode           : 1
 * - toString           : 1
 * - randomPosition     : 1
 */
@DisplayName("Position – unit tests")
public class PositionTest {

    private Position position;

    @BeforeEach
    void setUp() {
        position = new Position(2, 3);
    }

    @AfterEach
    void tearDown() {
        position = null;
    }

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Position(int,int) – creates position with correct coordinates, not occupied, not hit")
    void constructor_intInt() {
        Position pos = new Position(1, 1);
        assertNotNull(pos, "Failed to create Position: object is null");
        assertEquals(1, pos.getRow(), "Row should be 1");
        assertEquals(1, pos.getColumn(), "Column should be 1");
        assertFalse(pos.isOccupied(), "New position should not be occupied");
        assertFalse(pos.isHit(), "New position should not be hit");
    }

    @Test
    @DisplayName("Position(char,int) – converts classic notation to internal row/column (previously untested)")
    void constructor_charInt() {
        // 'A' → row 0,  classicColumn 1 → column 0
        Position posA1 = new Position('A', 1);
        assertEquals(0, posA1.getRow(),    "Row for 'A' should be 0");
        assertEquals(0, posA1.getColumn(), "Column for classicColumn 1 should be 0");
        assertFalse(posA1.isOccupied(), "New position should not be occupied");
        assertFalse(posA1.isHit(),      "New position should not be hit");

        // 'C' → row 2,  classicColumn 4 → column 3
        Position posC4 = new Position('C', 4);
        assertEquals(2, posC4.getRow(),    "Row for 'C' should be 2");
        assertEquals(3, posC4.getColumn(), "Column for classicColumn 4 should be 3");

        // Lowercase — toUpperCase() inside constructor must handle it
        Position posLower = new Position('e', 3);
        assertEquals(4, posLower.getRow(),    "Row for 'e' (lowercase) should be 4");
        assertEquals(2, posLower.getColumn(), "Column for classicColumn 3 should be 2");
    }

    // ------------------------------------------------------------------
    // Getters
    // ------------------------------------------------------------------

    @Test
    @DisplayName("getRow – returns internal row index")
    void getRow() {
        assertEquals(2, position.getRow(), "Row should be 2");
    }

    @Test
    @DisplayName("getColumn – returns internal column index")
    void getColumn() {
        assertEquals(3, position.getColumn(), "Column should be 3");
    }

    @Test
    @DisplayName("getClassicRow – returns letter corresponding to row (row 2 → 'C')")
    void getClassicRow() {
        assertEquals('C', position.getClassicRow(), "Classic row for row 2 should be 'C'");
    }

    @Test
    @DisplayName("getClassicColumn – returns 1-based column (column 3 → 4) [bug fix: was calling getColumn()]")
    void getClassicColumn() {
        assertEquals(4, position.getClassicColumn(),
                "Classic column for column 3 should be 4 (1-based)");
    }

    // ------------------------------------------------------------------
    // isInside – 4 independent boolean conditions
    // ------------------------------------------------------------------

    @Test
    @DisplayName("isInside – (0,0) is a valid board position")
    void isInside_valid() {
        assertTrue(new Position(0, 0).isInside(), "Position (0,0) should be inside");
    }

    @Test
    @DisplayName("isInside – negative row is outside the board")
    void isInside_negativeRow() {
        assertFalse(new Position(-1, 5).isInside(), "Negative row should be outside");
    }

    @Test
    @DisplayName("isInside – negative column is outside the board")
    void isInside_negativeColumn() {
        assertFalse(new Position(5, -1).isInside(), "Negative column should be outside");
    }

    @Test
    @DisplayName("isInside – row equal to BOARD_SIZE is outside the board")
    void isInside_rowTooLarge() {
        assertFalse(new Position(Game.BOARD_SIZE, 5).isInside(),
                "Row == BOARD_SIZE should be outside");
    }

    @Test
    @DisplayName("isInside – column equal to BOARD_SIZE is outside the board")
    void isInside_columnTooLarge() {
        assertFalse(new Position(5, Game.BOARD_SIZE).isInside(),
                "Column == BOARD_SIZE should be outside");
    }

    // ------------------------------------------------------------------
    // isAdjacentTo
    // ------------------------------------------------------------------

    @Test
    @DisplayName("isAdjacentTo – horizontal neighbour (same row, column ±1) is adjacent")
    void isAdjacentTo_horizontal() {
        assertTrue(position.isAdjacentTo(new Position(2, 4)),
                "Horizontal neighbour should be adjacent");
    }

    @Test
    @DisplayName("isAdjacentTo – vertical neighbour (column same, row ±1) is adjacent")
    void isAdjacentTo_vertical() {
        assertTrue(position.isAdjacentTo(new Position(3, 3)),
                "Vertical neighbour should be adjacent");
    }

    @Test
    @DisplayName("isAdjacentTo – diagonal neighbour (row ±1, column ±1) is adjacent")
    void isAdjacentTo_diagonal() {
        assertTrue(position.isAdjacentTo(new Position(3, 4)),
                "Diagonal neighbour should be adjacent");
    }

    @Test
    @DisplayName("isAdjacentTo – position 2 rows and 2 columns away is NOT adjacent (row diff > 1, short-circuits)")
    void isAdjacentTo_notAdjacent() {
        // rowDiff=2 → first operand of && is false → short-circuit, returns false
        assertFalse(position.isAdjacentTo(new Position(4, 5)),
                "Position 2 rows and 2 columns away should not be adjacent");
    }

    @Test
    @DisplayName("isAdjacentTo – same row but 3 columns away is NOT adjacent (rowDiff<=1 TRUE, colDiff>1 FALSE)")
    void isAdjacentTo_sameRowFarColumn() {
        // rowDiff=0 (<=1, first operand TRUE), colDiff=3 (>1, second operand FALSE)
        // Covers the branch missed without this test (95% → 100%)
        assertFalse(position.isAdjacentTo(new Position(2, 6)),
                "Same row but 3 columns away should not be adjacent");
    }

    @Test
    @DisplayName("isAdjacentTo – null argument throws NullPointerException")
    void isAdjacentTo_null() {
        assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
                "Null argument should throw NullPointerException");
    }

    // ------------------------------------------------------------------
    // adjacentPositions – previously completely untested
    // Branch true  : newPosition.isInside() → added to list
    // Branch false : newPosition.isInside() → discarded
    // ------------------------------------------------------------------

    @Test
    @DisplayName("adjacentPositions – centre (5,5) has exactly 8 neighbours, all inside the board")
    void adjacentPositions_centre() {
        Position centre = new Position(5, 5);
        List<IPosition> adjacents = centre.adjacentPositions();

        assertEquals(8, adjacents.size(), "Centre position should have exactly 8 neighbours");
        for (IPosition adj : adjacents)
            assertTrue(adj.isInside(), "Every neighbour of a centre cell must be inside");
    }

    @Test
    @DisplayName("adjacentPositions – top-left corner (0,0) has exactly 3 neighbours (covers isInside==false branch)")
    void adjacentPositions_corner_topLeft() {
        Position corner = new Position(0, 0);
        List<IPosition> adjacents = corner.adjacentPositions();

        assertEquals(3, adjacents.size(), "Top-left corner should have exactly 3 neighbours");
        assertTrue(adjacents.contains(new Position(0, 1)), "Should contain (0,1)");
        assertTrue(adjacents.contains(new Position(1, 0)), "Should contain (1,0)");
        assertTrue(adjacents.contains(new Position(1, 1)), "Should contain (1,1)");
    }

    @Test
    @DisplayName("adjacentPositions – bottom-right corner (9,9) has exactly 3 neighbours")
    void adjacentPositions_corner_bottomRight() {
        int last = Game.BOARD_SIZE - 1;
        assertEquals(3, new Position(last, last).adjacentPositions().size(),
                "Bottom-right corner should have exactly 3 neighbours");
    }

    @Test
    @DisplayName("adjacentPositions – top edge (0,5) has exactly 5 neighbours")
    void adjacentPositions_topEdge() {
        Position edge = new Position(0, 5);
        List<IPosition> adjacents = edge.adjacentPositions();

        assertEquals(5, adjacents.size(), "Top-edge position should have 5 neighbours");
        for (IPosition adj : adjacents)
            assertTrue(adj.isInside(), "All returned neighbours must be inside");
    }

    @Test
    @DisplayName("adjacentPositions – left edge (5,0) has exactly 5 neighbours")
    void adjacentPositions_leftEdge() {
        assertEquals(5, new Position(5, 0).adjacentPositions().size(),
                "Left-edge position should have 5 neighbours");
    }

    @Test
    @DisplayName("adjacentPositions – exhaustive: no returned position is ever outside the board")
    void adjacentPositions_neverOutsideBoard() {
        for (int r = 0; r < Game.BOARD_SIZE; r++)
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                for (IPosition adj : new Position(r, c).adjacentPositions())
                    assertTrue(adj.isInside(),
                            "adjacentPositions must never return an outside position. Found: " + adj);
    }

    // ------------------------------------------------------------------
    // isOccupied / occupy / isHit / shoot
    // ------------------------------------------------------------------

    @Test
    @DisplayName("isOccupied – false initially; true after occupy()")
    void isOccupied() {
        assertFalse(position.isOccupied(), "New position should not be occupied");
        position.occupy();
        assertTrue(position.isOccupied(), "Position should be occupied after occupy()");
    }

    @Test
    @DisplayName("isHit – false initially; true after shoot()")
    void isHit() {
        assertFalse(position.isHit(), "New position should not be hit");
        position.shoot();
        assertTrue(position.isHit(), "Position should be hit after shoot()");
    }

    // ------------------------------------------------------------------
    // equals
    // ------------------------------------------------------------------

    @Test
    @DisplayName("equals – same reference returns true")
    void equals_sameReference() {
        assertTrue(position.equals(position), "A position should equal itself");
    }

    @Test
    @DisplayName("equals – different object with same coordinates returns true")
    void equals_sameCoords() {
        assertTrue(position.equals(new Position(2, 3)), "Same coordinates should be equal");
    }

    @Test
    @DisplayName("equals – null returns false")
    void equals_null() {
        assertFalse(position.equals(null), "Position should not equal null");
    }

    @Test
    @DisplayName("equals – non-Position object returns false")
    void equals_differentType() {
        assertFalse(position.equals(new Object()), "Position should not equal a non-Position");
    }

    @Test
    @DisplayName("equals – same row, different column returns false (right operand of && is false)")
    void equals_differentColumn() {
        assertFalse(position.equals(new Position(2, 4)),
                "Different column should not be equal");
    }

    @Test
    @DisplayName("equals – different row returns false (left operand of && is false, short-circuits)")
    void equals_differentRow() {
        assertFalse(position.equals(new Position(3, 3)),
                "Different row should not be equal");
    }

    // ------------------------------------------------------------------
    // hashCode
    // ------------------------------------------------------------------

    @Test
    @DisplayName("hashCode – consistent for positions with the same coordinates")
    void hashCodeConsistency() {
        assertEquals(position.hashCode(), new Position(2, 3).hashCode(),
                "Hash codes must match for equal positions");
    }

    // ------------------------------------------------------------------
    // toString
    // ------------------------------------------------------------------

    @Test
    @DisplayName("toString – returns classic notation 'C4' for position (row=2, col=3)")
    void toStringFormat() {
        assertEquals("C4", position.toString(),
                "toString should return 'C4' for (row=2, col=3)");
    }

    // ------------------------------------------------------------------
    // randomPosition – static factory
    // ------------------------------------------------------------------

    @Test
    @DisplayName("randomPosition – always returns a position inside the board (30 samples)")
    void randomPosition_isInsideBoard() {
        for (int i = 0; i < 30; i++) {
            Position p = Position.randomPosition();
            assertNotNull(p, "randomPosition() must not return null");
            assertTrue(p.isInside(),
                    "randomPosition() must always be inside the board, got: " + p);
        }
    }
}
