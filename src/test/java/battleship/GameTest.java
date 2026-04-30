package battleship;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Game.
 * Author: britoeabreu
 * Date: 2024-03-19
 * Time: 15:30
 * Cyclomatic Complexity for each method:
 * - Game (constructor): 1
 * - fire: 7
 * - getShots: 1
 * - getRepeatedShots: 1
 * - getInvalidShots: 1
 * - getHits: 1
 * - getSunkShips: 1
 * - getRemainingShips: 1
 * - validShot: 3
 * - repeatedShot: 2
 * - printBoard: 1
 * - printValidShots: 1
 * - printFleet: 1
 */
public class GameTest {

	private Game game;

	// Backup do scoreboard.json para o teste de over() não poluir um ficheiro existente.
	private static final Path SCOREBOARD = Paths.get("scoreboard.json");
	private boolean scoreboardExisted;
	private byte[] scoreboardBackup;

	private final PrintStream originalOut = System.out;

	@BeforeEach
	void setUp() throws IOException {
		game = new Game(new Fleet()); // Assuming Fleet is a concrete implementation of IFleet

		scoreboardExisted = Files.exists(SCOREBOARD);
		if (scoreboardExisted) {
			scoreboardBackup = Files.readAllBytes(SCOREBOARD);
		}
	}

	@AfterEach
	void tearDown() throws IOException {
		game = null;
		System.setOut(originalOut);

		if (scoreboardExisted) {
			Files.write(SCOREBOARD, scoreboardBackup);
		} else {
			Files.deleteIfExists(SCOREBOARD);
		}
	}

	// =========================================================================
	// Testes originais
	// =========================================================================

	@Test
	void constructor() {
		assertNotNull(game, "Game instance should not be null after construction.");
		assertNotNull(game.getAlienMoves(), "Shots list should not be null after initialization.");
		assertTrue(game.getAlienMoves().isEmpty(), "Shots list should be empty upon initialization.");
		assertEquals(0, game.getInvalidShots(), "Invalid shots count should be zero upon initialization.");
		assertEquals(0, game.getRepeatedShots(), "Repeated shots count should be zero upon initialization.");
		assertEquals(0, game.getHits(), "Hits count should be zero upon initialization.");
		assertEquals(0, game.getSunkShips(), "Sunk ships count should be zero upon initialization.");
	}

	@Test
	void fire2() {
		Position invalidPosition = new Position(-1, 5);
		game.fireSingleShot(invalidPosition, false);
		assertEquals(1, game.getInvalidShots(), "Invalid shots counter should increase for an invalid shot.");
	}

	@Test
	void fire3() {
		Position position = new Position(2, 3);
		game.fireSingleShot(position, false);
		game.fireSingleShot(position, true);
		assertEquals(1, game.getRepeatedShots(), "Repeated shots counter should increase for a repeated shot.");
	}

	@Test
	void repeatedShot1() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		Position position = new Position(2, 3);
		assertTrue(game.repeatedShot(position), "Position (2,3) should be marked as repeated after firing.");
	}

	@Test
	void repeatedShot2() {
		Position position = new Position(2, 3);
		assertFalse(game.repeatedShot(position), "Position (2,3) should not be marked as repeated before firing.");
	}

	@Test
	void getAlienMoves() {
		List<IPosition> positions = List.of(new Position(2, 3), new Position(2, 4), new Position(2, 5));
		game.fireShots(positions);
		assertEquals(1, game.getAlienMoves().size(), "Shots list should contain one shot after firing once.");
	}

	@Test
	void getRemainingShips() {
		IFleet fleet = game.getMyFleet();
		Ship ship1 = new Barge(Compass.NORTH, new Position(1, 1));
		Ship ship2 = new Frigate(Compass.EAST, new Position(5, 5));

		fleet.addShip(ship1);
		assertEquals(1, game.getRemainingShips(), "Just one ship was created!");
		fleet.addShip(ship2);
		assertEquals(2, game.getRemainingShips(), "Two ships were created!");
		ship2.sink();
		assertEquals(1, game.getRemainingShips(), "Remaining ships count should be 1 after sinking one of two ships.");
	}

	// =========================================================================
	// Novos testes para atingir 100% de cobertura
	// =========================================================================

	@Test
	void getAlienFleet_returnsNonNull() {
		assertNotNull(game.getAlienFleet(), "Alien fleet getter should return a non-null fleet reference.");
	}

	@Test
	void getMyMoves_isEmptyInitially() {
		assertNotNull(game.getMyMoves(), "My moves list should not be null.");
		assertTrue(game.getMyMoves().isEmpty(), "My moves list should be empty on construction.");
	}

	@Test
	void jsonShots_returnsJsonWithRowsAndColumns() {
		String json = Game.jsonShots(List.of(new Position(0, 0), new Position(2, 5)));
		assertNotNull(json, "JSON should not be null.");
		assertTrue(json.contains("\"row\""), "JSON should contain a 'row' key.");
		assertTrue(json.contains("\"column\""), "JSON should contain a 'column' key.");
		assertTrue(json.contains("\"A\""), "JSON should contain the classic row 'A' for row 0.");
		assertTrue(json.contains("6"), "JSON should contain the classic column 6 for col 5.");
	}

	@Test
	void fireShots_wrongSize_throws() {
		List<IPosition> tooFew = List.of(new Position(0, 0));
		assertThrows(IllegalArgumentException.class, () -> game.fireShots(tooFew),
				"fireShots should throw when the shot count is not NUMBER_SHOTS.");
	}

	@Test
	void fireSingleShot_hitShip_stillFloats() {
		IFleet fleet = game.getMyFleet();
		Ship galleon = new Galleon(Compass.NORTH, new Position(0, 0));
		assertTrue(fleet.addShip(galleon), "Galleon should be addable to an empty fleet at (0,0).");

		game.fireSingleShot(new Position(0, 0), false);
		assertEquals(1, game.getHits(), "One hit expected after shooting a ship cell.");
		assertEquals(0, game.getSunkShips(), "Ship should still be afloat after a single hit.");
		assertTrue(galleon.stillFloating(), "Galleon has 5 cells and should still be floating.");
	}

	@Test
	void fireSingleShot_hitShip_sinks() {
		IFleet fleet = game.getMyFleet();
		Ship barge = new Barge(Compass.NORTH, new Position(3, 3));
		assertTrue(fleet.addShip(barge), "Barge should be addable.");

		game.fireSingleShot(new Position(3, 3), false);
		assertEquals(1, game.getHits(), "One hit expected.");
		assertEquals(1, game.getSunkShips(), "Single-cell barge should be sunk after one hit.");
		assertFalse(barge.stillFloating(), "Barge should no longer be floating.");
	}

	@Test
	void fireSingleShot_repeatedViaHistory() {
		// First fire three shots (normal move).
		game.fireShots(List.of(new Position(0, 0), new Position(0, 1), new Position(0, 2)));
		int beforeRepeats = game.getRepeatedShots();
		// Now fire a single shot at a position already present in alienMoves, with isRepeated=false.
		// This forces the repeatedShot(pos) branch to evaluate true.
		game.fireSingleShot(new Position(0, 0), false);
		assertEquals(beforeRepeats + 1, game.getRepeatedShots(),
				"Repeated-shot counter should increment via history lookup even when isRepeated=false.");
	}

	@Test
	void printBoard_noShotsNoLegend_producesOutput() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		Game.printBoard(new Fleet(), new ArrayList<>(), false, false);

		String output = out.toString();
		assertFalse(output.isEmpty(), "printBoard should print something even without shots/legend.");
		assertFalse(output.contains("LEGENDA"), "Legend should not appear when showLegend=false.");
	}

	@Test
	void printBoard_fullCoverage_withShotsAndLegend() {
		Fleet fleet = new Fleet();
		Ship sunkBarge = new Barge(Compass.NORTH, new Position(3, 3));
		Ship afloatBarge = new Barge(Compass.NORTH, new Position(7, 7));
		fleet.addShip(sunkBarge);
		fleet.addShip(afloatBarge);

		Game localGame = new Game(fleet);
		// Move 1: hit the barge at (3,3), miss at (5,5), out-of-board shot (-1,-1).
		localGame.fireShots(List.of(new Position(3, 3), new Position(5, 5), new Position(-1, -1)));
		// Move 2: fire at an adjacent cell of the sunk barge (3,2), a water cell (9,9), and another out-of-board.
		localGame.fireShots(List.of(new Position(3, 2), new Position(9, 9), new Position(-2, -2)));

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		Game.printBoard(fleet, localGame.getAlienMoves(), true, true);

		String output = out.toString();
		assertTrue(output.contains("LEGENDA"), "Legend should appear when showLegend=true.");
	}

	@Test
	void printMyBoard_delegatesToPrintBoard() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		game.printMyBoard(true, true);

		assertFalse(out.toString().isEmpty(), "printMyBoard should print something.");
	}

	@Test
	void printAlienBoard_delegatesToPrintBoard() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		game.printAlienBoard(false, false);

		assertFalse(out.toString().isEmpty(), "printAlienBoard should print something.");
	}

	@Test
	void randomEnemyFire_producesJsonAndRegistersMove() {
		String json = game.randomEnemyFire();
		assertNotNull(json, "JSON returned from randomEnemyFire should not be null.");
		assertFalse(json.isEmpty(), "JSON should not be empty.");
		assertEquals(1, game.getAlienMoves().size(), "One alien move should be recorded after randomEnemyFire.");
	}

	@Test
	void randomEnemyFire_fewerCandidatesBranch() {
		// Fire 33 moves * 3 shots each = 99 unique positions in row-major order, covering (0,0)..(9,8).
		// Only (9,9) remains unshot, which forces candidateShots.size() < NUMBER_SHOTS.
		int k = 0;
		for (int move = 0; move < 33; move++) {
			List<IPosition> batch = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				batch.add(new Position(k / 10, k % 10));
				k++;
			}
			game.fireShots(batch);
		}
		assertEquals(33, game.getAlienMoves().size(), "Setup: 33 alien moves expected.");

		String json = game.randomEnemyFire();
		assertNotNull(json, "JSON should still be returned when few candidates remain.");
		assertEquals(34, game.getAlienMoves().size(),
				"A 34th move should be added after randomEnemyFire, even with almost full board.");
	}

	@Test
	void readEnemyFire_separateTokens() {
		String json = game.readEnemyFire(new Scanner("A 1 B 2 C 3\n"));
		assertNotNull(json, "JSON should be returned for separate tokens input.");
		assertEquals(1, game.getAlienMoves().size(), "One move should be registered.");
	}

	@Test
	void readEnemyFire_combinedTokens() {
		String json = game.readEnemyFire(new Scanner("A1 B2 C3\n"));
		assertNotNull(json, "JSON should be returned for combined tokens input.");
		assertEquals(1, game.getAlienMoves().size(), "One move should be registered.");
	}

	@Test
	void readEnemyFire_missingRow_throws() {
		Scanner in = new Scanner("A\n");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(in),
				"Should throw when a column letter is not followed by a row number.");
	}

	@Test
	void readEnemyFire_tooFewShots_throws() {
		Scanner in = new Scanner("A 1 B 2\n");
		assertThrows(IllegalArgumentException.class, () -> game.readEnemyFire(in),
				"Should throw when the number of shots is not NUMBER_SHOTS.");
	}

	@Test
	void over_printsMaldictionAndScoreboard() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));

		game.over();

		String output = out.toString();
		assertTrue(output.contains("Maldito"), "over() should print the 'Maldito' banner.");
		assertTrue(output.contains("SCOREBOARD"), "over() should print the SCOREBOARD section.");
	}
}
