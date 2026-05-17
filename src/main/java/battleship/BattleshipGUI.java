package battleship;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class BattleshipGUI extends Application {

    private static final int TILE_SIZE = 40;
    private static GridPane grid;
    private static IGame activeGame;
    private static boolean showShips = true;

    private static BattleshipGUI instance;
    private static Stage stage;
    private static boolean launched = false;

    public static void setGame(IGame game) {
        activeGame = game;
    }

    public static void updateBoard() {
        if (instance != null) {
            Platform.runLater(() -> instance.refresh());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        stage = primaryStage;
        Platform.setImplicitExit(false); // Prevent app from closing when window is closed

        grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(2);
        grid.setVgap(2);

        refresh();

        Scene scene = new Scene(grid);
        stage.setTitle("Battleship Board");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> stage.hide()); // Just hide instead of exiting
        stage.show();
    }

    public void refresh() {
        if (activeGame == null)
            return;

        grid.getChildren().clear();

        // Add headers
        for (int col = 0; col < Game.BOARD_SIZE; col++) {
            grid.add(new CenteredText(String.valueOf(col + 1)), col + 1, 0);
        }

        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            grid.add(new CenteredText(String.valueOf((char) ('A' + row))), 0, row + 1);
        }

        char[][] map = generateMap();

        for (int row = 0; row < Game.BOARD_SIZE; row++) {
            for (int col = 0; col < Game.BOARD_SIZE; col++) {
                Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
                rect.setStroke(Color.LIGHTGRAY);

                char marker = map[row][col];
                switch (marker) {
                    case '#' -> rect.setFill(showShips ? Color.GRAY : Color.AZURE); // Ship
                    case '*' -> rect.setFill(Color.ORANGERED); // Hit
                    case 'o' -> rect.setFill(Color.LIGHTBLUE); // Miss
                    case '-' -> rect.setFill(Color.LIGHTYELLOW); // Adjacent
                    default -> rect.setFill(Color.AZURE); // Water
                }

                grid.add(rect, col + 1, row + 1);
            }
        }
    }

    private char[][] generateMap() {
        char[][] map = new char[Game.BOARD_SIZE][Game.BOARD_SIZE];
        for (int r = 0; r < Game.BOARD_SIZE; r++)
            for (int c = 0; c < Game.BOARD_SIZE; c++)
                map[r][c] = '.';

        IFleet fleet = activeGame.getMyFleet();
        List<IMove> moves = activeGame.getAlienMoves();

        if (fleet != null) {
            for (IShip ship : fleet.getShips()) {
                for (IPosition ship_pos : ship.getPositions())
                    map[ship_pos.getRow()][ship_pos.getColumn()] = '#';
                if (!ship.stillFloating())
                    for (IPosition adjacent_pos : ship.getAdjacentPositions())
                        map[adjacent_pos.getRow()][adjacent_pos.getColumn()] = '-';
            }
        }

        if (moves != null) {
            for (IMove move : moves) {
                for (IPosition shot : move.getShots()) {
                    if (shot.isInside()) {
                        int row = shot.getRow();
                        int col = shot.getColumn();
                        if (map[row][col] == '#')
                            map[row][col] = '*';
                        else if (map[row][col] == '.' || map[row][col] == '-')
                            map[row][col] = 'o';
                    }
                }
            }
        }
        return map;
    }

    private static class CenteredText extends StackPane {
        public CenteredText(String value) {
            Text text = new Text(value);
            getChildren().add(text);
            setPrefSize(TILE_SIZE, TILE_SIZE);
        }
    }

    public static void launchGUI() {
        if (launched) {
            Platform.runLater(() -> {
                if (stage != null) {
                    stage.show();
                    instance.refresh();
                }
            });
            return;
        }
        launched = true;

        try {
            Platform.startup(() -> {
                Stage newStage = new Stage();
                new BattleshipGUI().start(newStage);
            });
        } catch (IllegalStateException e) {
            // Toolkit already initialized
            Platform.runLater(() -> {
                Stage newStage = new Stage();
                new BattleshipGUI().start(newStage);
            });
        }
    }
}
