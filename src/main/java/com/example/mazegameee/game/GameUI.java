package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.entities.MazeLayout;
import com.example.mazegameee.structures.Room;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

public class GameUI {
    // === UI Layout pieces ===
    private final BorderPane mainLayout;
    private final GridPane gridPane;
    private final HBox statsPanel;

    // === Stat Labels ===
    private final Label healthLabel;
    private final Label keysLabel;
    private final Label crowbarsLabel;
    private Label enemyHealthLabel;

    // === Core game components ===
    private final Room[][] worldGrid;
    private final List<Npc> npcs;
    private final Hero hero;
    private final ImageView heroImage;

    private Timeline npcMove;  // for automated NPC movement

    private final FillMaze fillMaze;
    private final GameController gameController;
    private final KeyHandler keyHandler;

    private boolean isHeroOnRight = false;
    private final static int GRID_SIZE = 10;
    private final static int CELL_SIZE = 75;

    public GameUI() {
        // === Set up main layout ===
        mainLayout = new BorderPane();
        statsPanel = new HBox(20);
        statsPanel.setAlignment(Pos.CENTER);

        // === Init stats ===
        healthLabel = new Label();
        keysLabel = new Label();
        crowbarsLabel = new Label();
        enemyHealthLabel = new Label();
        statsPanel.getChildren().addAll(healthLabel, keysLabel, crowbarsLabel, enemyHealthLabel);
        mainLayout.setTop(statsPanel);

        // === Center Grid ===
        gridPane = new GridPane();
        mainLayout.setCenter(gridPane);

        // === Game State ===
        worldGrid = new Room[GRID_SIZE][GRID_SIZE];
        npcs = new ArrayList<>();
        hero = new Hero(0, 0, 100, 100, 5, 5);  // starts with some HP and items

        // === Visuals ===
        heroImage = new ImageView(new Image("hero.png"));
        heroImage.setFitWidth(CELL_SIZE / 3);
        heroImage.setFitHeight(CELL_SIZE / 3);
        heroImage.setMouseTransparent(true);

        // === Helpers ===
        fillMaze = new FillMaze(gridPane, worldGrid, npcs, hero, CELL_SIZE);
        gameController = new GameController(worldGrid, npcs, hero, gridPane, healthLabel, keysLabel, crowbarsLabel, enemyHealthLabel, CELL_SIZE);
        keyHandler = new KeyHandler(hero, 0, 0, heroImage, gameController, CELL_SIZE);

        // run game setup logic
        setupGame();
    }

    private void setupGame() {
        fillMaze.drawRooms();

        // always starts at (0, 0)
        Room entrance = worldGrid[0][0];
        Room exit;

        // pick a far-away corner for the exit (usually bottom right)
        do {
            int exitRow = GRID_SIZE - 1;
            int exitCol = GRID_SIZE - 1;
            exit = worldGrid[exitRow][exitCol];
        } while (exit == entrance);

        // try loading the custom maze layout from CSV
        MazeLayout layout;
        try {
            layout = MazeLayout.loadFromCSV("/maze-layout-custom.csv", GRID_SIZE, GRID_SIZE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        // exit is marked visually and stored for logic
        fillMaze.markExit(exit);
        gameController.setExitCoordinates(exit.getY(), exit.getX());

        // add hero + enemies + chests to the grid
        fillMaze.addHeroVisual(0, 0, heroImage);
        fillMaze.addNPCs(10, 0, 0);
        fillMaze.addChest(20, 0, 0);

        // add walls and doors based on layout
        fillMaze.addDoorsAndWalls(layout);

        // randomly lock 45% of doors, but retry up to 10 times if the maze is unsolvable
        int maxAttempts = 10, attempts = 0;
        do {
            gameController.randomLockAllDoors(0.45);
            attempts++;
        } while (!gameController.isSolvableWithResources() && attempts < maxAttempts);

        // if all attempts fail, give a last warning but let game continue
        if (attempts == maxAttempts && !gameController.isSolvableWithResources()) {
            new Alert(Alert.AlertType.WARNING,
                    "Couldn’t make a winnable maze in " + maxAttempts + " tries.\n" +
                            "You may be trapped from the start!")
                    .showAndWait();
        }

        // update UI labels
        gameController.updateStats();

        // start the NPCs doing their thing
        startNPCMovement();
    }

    // used by controller to pause enemy behavior
    public void stopNPCMovement() {
        if (npcMove != null) {
            npcMove.stop();
        }
    }

    private void startNPCMovement() {
        npcMove = new Timeline(new KeyFrame(Duration.seconds(1), e -> gameController.moveNPCs()));
        npcMove.setCycleCount(Timeline.INDEFINITE);
        npcMove.play();
    }

    // used when Main.java asks for the game scene
    public Scene getScene() {
        Scene scene = new Scene(mainLayout);
        scene.setOnKeyPressed(this::handleKeyPress);
        return scene;
    }

    public GameController getController() {
        return gameController;
    }

    // key press logic is piped to keyHandler (WASD, arrows, E, F)
    public void handleKeyPress(KeyEvent event) {
        keyHandler.handle(event);

        // store which direction hero is facing (used for animation tweaks)
        if (event.getCode().toString().equals("LEFT")) isHeroOnRight = false;
        else if (event.getCode().toString().equals("RIGHT")) isHeroOnRight = true;
    }
}
