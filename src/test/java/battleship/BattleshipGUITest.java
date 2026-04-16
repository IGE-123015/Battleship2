package battleship;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for BattleshipGUI.
 *
 * <p>NOTE: these tests are order-sensitive on purpose. The JavaFX toolkit can only be
 * started once per JVM and {@link BattleshipGUI} keeps static state ({@code instance},
 * {@code stage}, {@code launched}, {@code activeGame}). The order defined here is designed
 * to reach every branch of the class:
 *
 * <ol>
 *     <li>Tests that need {@code instance == null} run first.</li>
 *     <li>Then the first {@code launchGUI()} call triggers the {@code Platform.startup(...)}
 *         happy path.</li>
 *     <li>Then the {@code launched == true} short-circuit is tested.</li>
 *     <li>Then we reset {@code launched} via reflection to trigger the
 *         {@code IllegalStateException} catch branch.</li>
 *     <li>Finally the refresh / generateMap scenarios.</li>
 * </ol>
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BattleshipGUITest {

	private static final long TIMEOUT_SECONDS = 5;

	// =========================================================================
	// Helpers
	// =========================================================================

	/**
	 * Runs a task on the JavaFX Application Thread and waits for it to complete.
	 * Requires the toolkit to have been started.
	 */
	private static void runAndWait(Runnable task) throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		Platform.runLater(() -> {
			try {
				task.run();
			} finally {
				latch.countDown();
			}
		});
		assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
				"Timed out waiting for JavaFX task.");
	}

	/** Waits for the FX queue to drain (used to synchronise after a fire-and-forget call). */
	private static void waitForFxThread() throws Exception {
		runAndWait(() -> {});
	}

	private static Object getStaticField(String name) throws Exception {
		Field f = BattleshipGUI.class.getDeclaredField(name);
		f.setAccessible(true);
		return f.get(null);
	}

	private static void setStaticField(String name, Object value) throws Exception {
		Field f = BattleshipGUI.class.getDeclaredField(name);
		f.setAccessible(true);
		f.set(null, value);
	}

	// =========================================================================
	// Tests
	// =========================================================================

	@Test
	@Order(1)
	void updateBoard_noInstance_isNoop() {
		// At this point no launchGUI / start has been called, so instance must be null.
		// updateBoard() must simply do nothing (no NPE, no exception).
		assertDoesNotThrow(BattleshipGUI::updateBoard,
				"updateBoard should be a no-op when the GUI has never been launched.");
	}

	@Test
	@Order(2)
	void setGame_storesActiveGame() throws Exception {
		Game g = new Game(new Fleet());
		BattleshipGUI.setGame(g);
		assertSame(g, getStaticField("activeGame"), "setGame should store the provided game.");
	}

	@Test
	@Order(3)
	void launchGUI_firstCall_initializesToolkitAndStage() throws Exception {
		// This must be the first FX call in the JVM so the try-branch of launchGUI
		// (Platform.startup) is executed successfully, covering the lambda body.
		BattleshipGUI.launchGUI();

		// Give the FX thread time to execute the startup runnable.
		long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS);
		while (getStaticField("instance") == null && System.currentTimeMillis() < deadline) {
			Thread.sleep(50);
		}

		assertNotNull(getStaticField("instance"), "instance should be set after start().");
		assertNotNull(getStaticField("stage"), "stage should be set after start().");
		assertEquals(Boolean.TRUE, getStaticField("launched"), "launched should be true.");

		// Drain the queue before the next test.
		waitForFxThread();
	}

	@Test
	@Order(4)
	void launchGUI_secondCall_showsExistingStage() throws Exception {
		// launched is true from the previous test, so this call must enter the
		// early-return branch and schedule a show+refresh via Platform.runLater.
		assertEquals(Boolean.TRUE, getStaticField("launched"),
				"Precondition: launched should still be true from previous test.");
		BattleshipGUI.launchGUI();
		waitForFxThread();
		// If we got here with no exception, the early-return branch was taken.
	}

	@Test
	@Order(5)
	void launchGUI_catchesIllegalStateWhenToolkitAlreadyUp() throws Exception {
		// Reset "launched" to false so the try/catch block inside launchGUI is entered.
		// The toolkit is already running from test order 3, so Platform.startup will throw
		// IllegalStateException and the catch branch will be exercised.
		setStaticField("launched", false);

		BattleshipGUI.launchGUI();
		waitForFxThread();

		assertEquals(Boolean.TRUE, getStaticField("launched"),
				"launched should be re-set to true by launchGUI.");
	}

	@Test
	@Order(6)
	void refresh_noGame_returnsEarly() throws Exception {
		// With activeGame == null, refresh() must return before touching the grid.
		setStaticField("activeGame", null);

		Object instance = getStaticField("instance");
		assertNotNull(instance, "Precondition: instance should have been created by launchGUI.");

		runAndWait(() -> ((BattleshipGUI) instance).refresh());
		// No exception == branch covered.
	}

	@Test
	@Order(7)
	void refresh_rendersFullBoard() throws Exception {
		// Build a game with:
		//  - A sunk Barge at (3,3): exercises the `!stillFloating` branch (adjacent '-' cells).
		//  - An afloat Barge at (7,7): leaves a '#' cell in the final map.
		//  - Shots: hit on (3,3) => '*', miss on (5,5) => 'o', outside (-1,-1) => skipped.
		Fleet fleet = new Fleet();
		assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(3, 3))));
		assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(7, 7))));
		Game game = new Game(fleet);
		game.fireShots(List.of(new Position(3, 3), new Position(5, 5), new Position(-1, -1)));

		BattleshipGUI.setGame(game);
		BattleshipGUI.updateBoard();
		waitForFxThread();

		AtomicInteger childCount = new AtomicInteger(-1);
		runAndWait(() -> {
			try {
				GridPane grid = (GridPane) getStaticField("grid");
				childCount.set(grid.getChildren().size());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		// 10 column headers + 10 row headers + 10x10 rectangles = 120 children.
		assertEquals(120, childCount.get(),
				"Grid should contain 10 col headers + 10 row headers + 100 cells.");
	}

	@Test
	@Order(8)
	void refresh_handlesNullFleetAndMoves() throws Exception {
		// Anonymous Game subclass where getMyFleet / getAlienMoves return null to exercise
		// the null-guarded branches inside generateMap.
		IGame nullGame = new Game(new Fleet()) {
			@Override
			public IFleet getMyFleet() {
				return null;
			}

			@Override
			public List<IMove> getAlienMoves() {
				return null;
			}
		};

		BattleshipGUI.setGame(nullGame);
		BattleshipGUI.updateBoard();
		waitForFxThread();

		AtomicInteger childCount = new AtomicInteger(-1);
		runAndWait(() -> {
			try {
				GridPane grid = (GridPane) getStaticField("grid");
				childCount.set(grid.getChildren().size());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		// Even with null fleet and null moves, refresh still renders headers and empty cells.
		assertEquals(120, childCount.get(),
				"Grid should still have headers + cells even when fleet/moves are null.");
	}

	@Test
	@Order(9)
	void refresh_withShowShipsFalse_takesTernaryElseBranch() throws Exception {
		// showShips is true by default and has no setter; flip it via reflection
		// to exercise the `showShips ? GRAY : AZURE` else branch for '#' cells.
		Fleet fleet = new Fleet();
		assertTrue(fleet.addShip(new Barge(Compass.NORTH, new Position(7, 7))));
		Game game = new Game(fleet);
		BattleshipGUI.setGame(game);

		setStaticField("showShips", false);
		try {
			BattleshipGUI.updateBoard();
			waitForFxThread();
		} finally {
			setStaticField("showShips", true);
		}
	}

	@Test
	@Order(10)
	void start_canBeCalledDirectly() throws Exception {
		// Call start(Stage) directly with a fresh instance to reinforce coverage of that method.
		AtomicReference<Throwable> error = new AtomicReference<>();
		runAndWait(() -> {
			try {
				Stage s = new Stage();
				new BattleshipGUI().start(s);
			} catch (Throwable t) {
				error.set(t);
			}
		});
		assertNull(error.get(), "Direct start(Stage) should not throw.");
	}
}
