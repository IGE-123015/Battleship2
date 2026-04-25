package battleship;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Test class for Fleet.
 * Author: ${user.name}
 * Date: ${current_date}
 * Time: ${current_time}
 * Cyclomatic Complexity for each method:
 * - Constructor: 1
 * - addShip: 3
 * - getShips: 1
 * - getShipsLike: 2
 * - getFloatingShips: 2
 * - shipAt: 2
 * - isInsideBoard: 3
 * - colisionRisk: 2
 * - getSunkShips: 2
 * - createRandom: 1
 * - printShips: 2
 * - printAllShips: 1
 * - printFloatingShips: 1
 * - printShipsByCategory: 1
 * - getShipsLike (no match): 2
 */
	public class FleetTest {

		private Fleet fleet;

		@BeforeEach
		void setUp() {
			fleet = new Fleet();
		}

		@AfterEach
		void tearDown() {
			fleet = null;
		}

		/**
		 * Test for the Fleet constructor.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testConstructor() {
			assertNotNull(fleet, "Error: Instance of Fleet should not be null.");
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet should be initialized with empty ships list.");
		}

		/**
		 * Test for the addShip method (all conditions true).
		 * Cyclomatic Complexity: 3
		 */
		@Test
		void testAddShip1() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			assertTrue(fleet.addShip(ship), "Error: Valid ship should be added successfully.");
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should contain one ship after addition.");
		}

		/**
		 * Test for the addShip method (fleet size limit reached).
		 */
		@Test
		void testAddShip2() {
			for (int i = 0; i < Fleet.FLEET_SIZE; i++) {
				fleet.addShip(new Barge(Compass.NORTH, new Position(i, 0)));
			}
			IShip anotherShip = new Barge(Compass.NORTH, new Position(10, 10));
			assertFalse(fleet.addShip(anotherShip), "Error: Should not add ship when fleet size limit is reached.");
		}

		/**
		 * Test for the addShip method (ship outside the board).
		 */
		@Test
		void testAddShip3() {
			IShip shipOutside = new Barge(Compass.NORTH, new Position(99, 99));
			assertFalse(fleet.addShip(shipOutside), "Error: Should not add ship outside the board.");
		}

		/**
		 * Test for the addShip method (collision risk).
		 */
		@Test
		void testAddShip4() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);
			assertFalse(fleet.addShip(ship2), "Error: Should not add ship with a collision risk.");
		}

		/**
		 * Test for the getShips method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testGetShips() {
			assertTrue(fleet.getShips().isEmpty(), "Error: Fleet's ships list should initially be empty.");
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertEquals(1, fleet.getShips().size(), "Error: Fleet should have size 1 after adding a ship.");
			assertEquals(ship, fleet.getShips().get(0), "Error: Fleet's first ship should match the added ship.");
		}

		/**
		 * Test for the getShipsLike method (ships of specific category).
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetShipsLike() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(2, 1));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> barges = fleet.getShipsLike("Barca");
			assertEquals(1, barges.size(), "Error: There should be exactly one ship of category 'Barca'.");
			assertEquals(ship1, barges.get(0), "Error: The ship of category 'Barca' does not match.");
		}

		/**
		 * Test for the getFloatingShips method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testGetFloatingShips() {
			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Caravel(Compass.NORTH, new Position(4, 4));
			fleet.addShip(ship1);
			fleet.addShip(ship2);

			List<IShip> floatingShips = fleet.getFloatingShips();
			assertEquals(2, floatingShips.size(), "Error: All ships should be floating initially.");

			ship1.getPositions().get(0).shoot();  // Sink ship1
			floatingShips = fleet.getFloatingShips();
			assertEquals(1, floatingShips.size(), "Error: Only one ship should be floating after sinking one.");
			assertEquals(ship2, floatingShips.get(0), "Error: The floating ship should match the expected result.");
		}

		/**
		 * Test for the shipAt method.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testShipAt() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);

			assertEquals(ship, fleet.shipAt(new Position(1, 1)), "Error: Should return the correct ship at the position.");
			assertNull(fleet.shipAt(new Position(5, 5)), "Error: Should return null for empty positions in the fleet.");
		}

		/**
		 * Test for private method isInsideBoard.
		 * Cyclomatic Complexity: 3
		 */
		@Test
		void testIsInsideBoard() throws Exception {
			// Use reflection to access private methods
			var method = Fleet.class.getDeclaredMethod("isInsideBoard", IShip.class);
			method.setAccessible(true);

			IShip insideShip = new Barge(Compass.NORTH, new Position(1, 1));
			IShip outsideShip = new Barge(Compass.NORTH, new Position(99, 99));

			assertTrue((Boolean) method.invoke(fleet, insideShip), "Error: Ship inside the board should return true.");
			assertFalse((Boolean) method.invoke(fleet, outsideShip), "Error: Ship outside the board should return false.");
		}

		/**
		 * Test for private method colisionRisk.
		 * Cyclomatic Complexity: 2
		 */
		@Test
		void testColisionRisk() throws Exception {
			var method = Fleet.class.getDeclaredMethod("colisionRisk", IShip.class);
			method.setAccessible(true);

			IShip ship1 = new Barge(Compass.NORTH, new Position(1, 1));
			IShip ship2 = new Barge(Compass.NORTH, new Position(1, 1));  // Overlapping position
			fleet.addShip(ship1);

			assertTrue((Boolean) method.invoke(fleet, ship2), "Error: Overlapping ships should be at collision risk.");
			assertFalse((Boolean) method.invoke(fleet, new Barge(Compass.NORTH, new Position(5, 5))),
					"Error: Ships at non-overlapping positions should not have a collision risk.");
		}

		/**
		 * Test for the printStatus method.
		 * Cyclomatic Complexity: 1
		 */
		@Test
		void testPrintStatus() {
			IShip ship = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(ship);
			assertDoesNotThrow(fleet::printStatus, "Error: printStatus should not throw any exceptions.");
		}

		// ------------------------------------------------------------------
		// getSunkShips – CC: 2 (stillFloating true/false)
		// ------------------------------------------------------------------

		@Test
		@DisplayName("getSunkShips – empty fleet returns empty list")
		void testGetSunkShips_empty() {
			assertTrue(fleet.getSunkShips().isEmpty(),
					"Error: getSunkShips on an empty fleet should return an empty list.");
		}

		@Test
		@DisplayName("getSunkShips – returns only ships with all positions hit")
		void testGetSunkShips_afterSinking() {
			IShip barge = new Barge(Compass.NORTH, new Position(1, 1));
			IShip caravel = new Caravel(Compass.EAST, new Position(4, 4));
			fleet.addShip(barge);
			fleet.addShip(caravel);

			// Initially nothing is sunk
			assertEquals(0, fleet.getSunkShips().size(),
					"Error: No ships should be sunk initially.");

			// Sink the barge (single cell)
			barge.getPositions().get(0).shoot();
			List<IShip> sunk = fleet.getSunkShips();
			assertEquals(1, sunk.size(), "Error: Exactly one ship should be sunk after shooting the barge.");
			assertEquals(barge, sunk.get(0), "Error: The sunk ship should be the barge.");

			// The caravel is still floating
			assertEquals(1, fleet.getFloatingShips().size(),
					"Error: The caravel should still be floating.");
		}

		// ------------------------------------------------------------------
		// getShipsLike – branch: no ship matches the category (returns empty)
		// ------------------------------------------------------------------

		@Test
		@DisplayName("getShipsLike – returns empty list when no ship matches the category")
		void testGetShipsLike_noMatch() {
			fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
			List<IShip> galleons = fleet.getShipsLike("Galeao");
			assertNotNull(galleons, "Error: getShipsLike should never return null.");
			assertTrue(galleons.isEmpty(),
					"Error: getShipsLike('Galeao') should return empty list when no galleons are present.");
		}

		// ------------------------------------------------------------------
		// createRandom – CC: 1 (static factory, always builds a full fleet)
		// ------------------------------------------------------------------

		@Test
		@DisplayName("createRandom – produces a valid fleet with exactly FLEET_SIZE ships, all inside the board")
		void testCreateRandom() {
			IFleet random = Fleet.createRandom();
			assertNotNull(random, "Error: createRandom() should not return null.");
			assertEquals(Fleet.FLEET_SIZE, random.getShips().size(),
					"Error: Random fleet should contain exactly FLEET_SIZE ships.");

			// Every ship must be afloat (no pre-existing hits)
			assertEquals(Fleet.FLEET_SIZE, random.getFloatingShips().size(),
					"Error: All ships in a new random fleet should be floating.");

			// No sunk ships
			assertTrue(random.getSunkShips().isEmpty(),
					"Error: A brand-new fleet should have no sunk ships.");
		}

		// ------------------------------------------------------------------
		// printShips – CC: 2 (0 ships / ≥1 ships)
		// ------------------------------------------------------------------

		@Test
		@DisplayName("printShips – does not throw with an empty list (loop has 0 iterations)")
		void testPrintShips_empty() {
			assertDoesNotThrow(() -> fleet.printShips(new ArrayList<>()),
					"Error: printShips with an empty list should not throw.");
		}

		@Test
		@DisplayName("printShips – does not throw with a non-empty list (loop body executed)")
		void testPrintShips_nonEmpty() {
			List<IShip> ships = new ArrayList<>();
			ships.add(new Barge(Compass.NORTH, new Position(1, 1)));
			ships.add(new Caravel(Compass.EAST, new Position(4, 4)));
			assertDoesNotThrow(() -> fleet.printShips(ships),
					"Error: printShips with ships should not throw.");
		}

		// ------------------------------------------------------------------
		// printAllShips – CC: 1 (delegates to printShips)
		// ------------------------------------------------------------------

		@Test
		@DisplayName("printAllShips – does not throw; prints all ships in the fleet")
		void testPrintAllShips() {
			fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
			fleet.addShip(new Barge(Compass.NORTH, new Position(5, 5)));
			assertDoesNotThrow(fleet::printAllShips,
					"Error: printAllShips should not throw.");
		}

		// ------------------------------------------------------------------
		// printFloatingShips – CC: 1
		// ------------------------------------------------------------------

		@Test
		@DisplayName("printFloatingShips – does not throw; only floating ships printed")
		void testPrintFloatingShips() {
			IShip barge = new Barge(Compass.NORTH, new Position(1, 1));
			fleet.addShip(barge);
			barge.getPositions().get(0).shoot(); // sink it
			fleet.addShip(new Caravel(Compass.EAST, new Position(4, 4))); // still floating

			assertDoesNotThrow(fleet::printFloatingShips,
					"Error: printFloatingShips should not throw.");
		}

		// ------------------------------------------------------------------
		// printShipsByCategory – CC: 1 (delegates to printShips(getShipsLike))
		// ------------------------------------------------------------------

		@Test
		@DisplayName("printShipsByCategory – does not throw for a present or absent category")
		void testPrintShipsByCategory() {
			fleet.addShip(new Barge(Compass.NORTH, new Position(1, 1)));
			fleet.addShip(new Caravel(Compass.EAST, new Position(4, 4)));

			assertDoesNotThrow(() -> fleet.printShipsByCategory("Barca"),
					"Error: printShipsByCategory('Barca') should not throw.");
			assertDoesNotThrow(() -> fleet.printShipsByCategory("Galeao"),
					"Error: printShipsByCategory('Galeao') with no match should not throw.");
		}
	}