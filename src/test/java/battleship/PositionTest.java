package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for Position.
 * Author: britoeabreu / 94334
 * Date: 2024-03-19 (updated 2026-04-16)
 *
 * Cyclomatic Complexity for each method:
 * - Position(int,int)  constructor : 1
 * - Position(char,int) constructor : 1
 * - getRow             : 1
 * - getColumn          : 1
 * - getClassicRow      : 1
 * - getClassicColumn   : 1
 * - isInside           : 4  (row<0 | col<0 | row>=SIZE | col>=SIZE)
 * - isAdjacentTo       : 4
 * - adjacentPositions  : 2  (isInside true / false)
 * - isOccupied         : 1
 * - isHit              : 1
 * - occupy             : 1
 * - shoot              : 1
 * - equals             : 3
 * - hashCode           : 1
 * - toString           : 1
 * - randomPosition     : 1
 */
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

	@Test
	void constructor_intInt() {
		Position pos = new Position(1, 1);
		assertNotNull(pos, "Failed to create Position: object is null");
		assertEquals(1, pos.getRow(), "Failed to set row: expected 1 but got " + pos.getRow());
		assertEquals(1, pos.getColumn(), "Failed to set column: expected 1 but got " + pos.getColumn());
		assertFalse(pos.isOccupied(), "New position should not be occupied");
		assertFalse(pos.isHit(), "New position should not be hit");
	}

	/** Covers the Position(char, int) constructor — previously untested. */
	@Test
	void constructor_charInt() {
		// 'A' -> row 0, classicColumn 1 -> column 0
		Position posA1 = new Position('A', 1);
		assertEquals(0, posA1.getRow(),    "Row for 'A' should be 0.");
		assertEquals(0, posA1.getColumn(), "Column for classic column 1 should be 0.");
		assertFalse(posA1.isOccupied(), "New position should not be occupied.");
		assertFalse(posA1.isHit(),      "New position should not be hit.");

		// 'C' -> row 2, classicColumn 4 -> column 3
		Position posC4 = new Position('C', 4);
		assertEquals(2, posC4.getRow(),    "Row for 'C' should be 2.");
		assertEquals(3, posC4.getColumn(), "Column for classic column 4 should be 3.");

		// Lowercase letter — toUpperCase() inside constructor must handle it
		Position posLower = new Position('e', 3);
		assertEquals(4, posLower.getRow(),    "Row for 'e' (lowercase) should be 4.");
		assertEquals(2, posLower.getColumn(), "Column for classic column 3 should be 2.");
	}

	// ------------------------------------------------------------------
	// Getters

	@Test
	void getRow() {
		assertEquals(2, position.getRow(), "Failed to get row: expected 2 but got " + position.getRow());
	}

	@Test
	void getColumn() {
		assertEquals(3, position.getColumn(), "Failed to get column: expected 3 but got " + position.getColumn());
	}

	@Test
	void getClassicRow() {
		// position = (2, 3) -> classicRow = 'A' + 2 = 'C'
		assertEquals('C', position.getClassicRow(), "Classic row for row 2 should be 'C'.");
	}

	/** Fixed: original test incorrectly called getColumn() instead of getClassicColumn(). */
	@Test
	void getClassicColumn() {
		// position = (2, 3) -> classicColumn = 3 + 1 = 4
		assertEquals(4, position.getClassicColumn(),
				"Classic column for column 3 should be 4 (1-based).");
	}

	// ------------------------------------------------------------------
	// isInside — 4 independent boolean conditions

	@Test
	void isInside_valid() {
		position = new Position(0, 0);
		assertTrue(position.isInside(), "Position (0,0) should be inside.");
	}

	@Test
	void isInside_negativeRow() {
		position = new Position(-1, 5);
		assertFalse(position.isInside(), "Position with negative row should be outside.");
	}

	@Test
	void isInside_negativeColumn() {
		position = new Position(5, -1);
		assertFalse(position.isInside(), "Position with negative column should be outside.");
	}

	@Test
	void isInside_rowTooLarge() {
		position = new Position(Game.BOARD_SIZE, 5);
		assertFalse(position.isInside(), "Position with row == BOARD_SIZE should be outside.");
	}

	@Test
	void isInside_columnTooLarge() {
		position = new Position(5, Game.BOARD_SIZE);
		assertFalse(position.isInside(), "Position with column == BOARD_SIZE should be outside.");
	}

	// ------------------------------------------------------------------
	// isAdjacentTo

	@Test
	void isAdjacentTo_horizontal() {
		assertTrue(position.isAdjacentTo(new Position(2, 4)),
				"Failed to detect horizontally adjacent position.");
	}

	@Test
	void isAdjacentTo_vertical() {
		assertTrue(position.isAdjacentTo(new Position(3, 3)),
				"Failed to detect vertically adjacent position.");
	}

	@Test
	void isAdjacentTo_diagonal() {
		assertTrue(position.isAdjacentTo(new Position(3, 4)),
				"Failed to detect diagonally adjacent position.");
	}

	@Test
	void isAdjacentTo_notAdjacent() {
		assertFalse(position.isAdjacentTo(new Position(4, 5)),
				"Non-adjacent position incorrectly identified as adjacent.");
	}

	@Test
	void isAdjacentTo_null() {
		assertThrows(NullPointerException.class, () -> position.isAdjacentTo(null),
				"isAdjacentTo should throw NullPointerException for null input.");
	}

	// ------------------------------------------------------------------
	// adjacentPositions — PREVIOUSLY UNTESTED
	// Branch true : newPosition.isInside() → position added to list
	// Branch false: newPosition.isInside() → position discarded

	/**
	 * Centre position (5,5): all 8 directions stay inside a 10×10 board → 8 neighbours.
	 */
	@Test
	void adjacentPositions_centre() {
		Position centre = new Position(5, 5);
		List<IPosition> adjacents = centre.adjacentPositions();

		assertEquals(8, adjacents.size(),
				"A centre position should have exactly 8 adjacent positions.");
		for (IPosition adj : adjacents)
			assertTrue(adj.isInside(), "Every adjacent position of a centre cell must be inside.");
	}

	/**
	 * Top-left corner (0,0): 5 of 8 candidate directions leave the board → 3 valid neighbours.
	 * Covers the branch where isInside() == false (positions discarded).
	 */
	@Test
	void adjacentPositions_corner_topLeft() {
		Position corner = new Position(0, 0);
		List<IPosition> adjacents = corner.adjacentPositions();

		assertEquals(3, adjacents.size(),
				"Top-left corner should have exactly 3 adjacent positions.");
		assertTrue(adjacents.contains(new Position(0, 1)), "Should contain (0,1).");
		assertTrue(adjacents.contains(new Position(1, 0)), "Should contain (1,0).");
		assertTrue(adjacents.contains(new Position(1, 1)), "Should contain (1,1).");
	}

	/**
	 * Bottom-right corner (9,9): symmetric to top-left — exactly 3 valid neighbours.
	 */
	@Test
	void adjacentPositions_corner_bottomRight() {
		int last = Game.BOARD_SIZE - 1;
		Position corner = new Position(last, last);
		assertEquals(3, corner.adjacentPositions().size(),
				"Bottom-right corner should have exactly 3 adjacent positions.");
	}

	/**
	 * Top-edge (0,5): row-1 directions go outside → 5 valid neighbours.
	 */
	@Test
	void adjacentPositions_topEdge() {
		Position edge = new Position(0, 5);
		List<IPosition> adjacents = edge.adjacentPositions();

		assertEquals(5, adjacents.size(),
				"Top-edge position should have exactly 5 adjacent positions.");
		for (IPosition adj : adjacents)
			assertTrue(adj.isInside(), "All returned neighbours must be inside the board.");
	}

	/**
	 * Left-edge (5,0): col-1 directions go outside → 5 valid neighbours.
	 */
	@Test
	void adjacentPositions_leftEdge() {
		Position edge = new Position(5, 0);
		assertEquals(5, edge.adjacentPositions().size(),
				"Left-edge position should have exactly 5 adjacent positions.");
	}

	/**
	 * Exhaustive check: for every board cell, adjacentPositions() must only
	 * return positions that are inside the board.
	 */
	@Test
	void adjacentPositions_neverOutsideBoard() {
		for (int r = 0; r < Game.BOARD_SIZE; r++) {
			for (int c = 0; c < Game.BOARD_SIZE; c++) {
				for (IPosition adj : new Position(r, c).adjacentPositions()) {
					assertTrue(adj.isInside(),
							"adjacentPositions must never return an outside position. Found: " + adj);
				}
			}
		}
	}

	// ------------------------------------------------------------------
	// isOccupied / occupy / isHit / shoot

	@Test
	void isOccupied() {
		assertFalse(position.isOccupied(), "New position should not be occupied.");
		position.occupy();
		assertTrue(position.isOccupied(), "Position should be occupied after occupy().");
	}

	@Test
	void isHit() {
		assertFalse(position.isHit(), "New position should not be hit.");
		position.shoot();
		assertTrue(position.isHit(), "Position should be hit after shoot().");
	}

	// ------------------------------------------------------------------
	// equals — branches: same ref, same coords, null, other type, diff column, diff row

	@Test
	void equals_sameReference() {
		assertTrue(position.equals(position), "A position should equal itself.");
	}

	@Test
	void equals_sameCoords() {
		assertTrue(position.equals(new Position(2, 3)), "Equal positions should be equal.");
	}

	@Test
	void equals_null() {
		assertFalse(position.equals(null), "Position should not equal null.");
	}

	@Test
	void equals_differentType() {
		assertFalse(position.equals(new Object()), "Position should not equal a non-Position object.");
	}

	@Test
	void equals_differentColumn() {
		assertFalse(position.equals(new Position(2, 4)),
				"Positions with different columns should not be equal.");
	}

	@Test
	void equals_differentRow() {
		assertFalse(position.equals(new Position(3, 3)),
				"Positions with different rows should not be equal.");
	}

	// ------------------------------------------------------------------
	// hashCode

	@Test
	void hashCodeConsistency() {
		assertEquals(position.hashCode(), new Position(2, 3).hashCode(),
				"Hash codes must be consistent for positions with the same coordinates.");
	}

	// ------------------------------------------------------------------
	// toString

	@Test
	void toStringFormat() {
		// position = (row=2, col=3) → 'C' + (3+1) = "C4"
		assertEquals("C4", position.toString(),
				"toString should return 'C4' for position (row=2, col=3).");
	}

	// ------------------------------------------------------------------
	// randomPosition — static factory

	@Test
	void randomPosition_isInsideBoard() {
		for (int i = 0; i < 30; i++) {
			Position p = Position.randomPosition();
			assertNotNull(p, "randomPosition() must not return null.");
			assertTrue(p.isInside(),
					"randomPosition() must always return a position inside the board, got: " + p);
		}
	}
}
