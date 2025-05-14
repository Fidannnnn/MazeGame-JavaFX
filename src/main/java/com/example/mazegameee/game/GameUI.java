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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameUI {
    private final BorderPane mainLayout;
    private final GridPane gridPane;
    private final HBox statsPanel;

    private final Label healthLabel;
    private final Label keysLabel;
    private final Label crowbarsLabel;
    private Label enemyHealthLabel;


    private final Room[][] worldGrid;
    private final List<Npc> npcs;
    private final Hero hero;
    private final ImageView heroImage;

    private final FillMaze fillMaze;
    private final GameController gameController;
    private final KeyHandler keyHandler;

    private boolean isHeroOnRight = false;
    private final static int GRID_SIZE = 10;
    private final static int CELL_SIZE = 75;

    public GameUI() {
        mainLayout = new BorderPane();
        statsPanel = new HBox(20);
        statsPanel.setAlignment(Pos.CENTER);

        healthLabel = new Label();
        keysLabel = new Label();
        crowbarsLabel = new Label();
        enemyHealthLabel = new Label();
        statsPanel.getChildren().addAll(healthLabel, keysLabel, crowbarsLabel, enemyHealthLabel);
        mainLayout.setTop(statsPanel);

        gridPane = new GridPane();
        mainLayout.setCenter(gridPane);

        worldGrid = new Room[GRID_SIZE][GRID_SIZE];
        npcs = new ArrayList<>();
        hero = new Hero(0, 0, 100, 100);

        heroImage = new ImageView(new Image("hero.png"));
        heroImage.setFitWidth(CELL_SIZE / 3);
        heroImage.setFitHeight(CELL_SIZE / 3);
        heroImage.setMouseTransparent(true);

        fillMaze = new FillMaze(gridPane, worldGrid, npcs, hero, CELL_SIZE);
        gameController = new GameController(worldGrid, npcs, hero, gridPane, healthLabel, keysLabel, crowbarsLabel, enemyHealthLabel, CELL_SIZE);
        keyHandler = new KeyHandler(hero, 0, 0, heroImage, gameController, CELL_SIZE);

        setupGame();
    }

    private void setupGame() {
        fillMaze.drawRooms();

        Room entrance = worldGrid[0][0];
        Room exit;
        Random random = new Random();
        do {
            int exitRow = GRID_SIZE - 1;
            int exitCol = GRID_SIZE - 1;
            exit = worldGrid[exitRow][exitCol];
        } while (exit == entrance);

        MazeLayout layout;
        try {
            layout = MazeLayout.loadFromCSV("/solvable-maze-layout.csv", GRID_SIZE, GRID_SIZE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        fillMaze.markExit(exit);
        gameController.setExitCoordinates(exit.getY(), exit.getX());
        fillMaze.addHeroVisual(0, 0, heroImage);
        fillMaze.addNPCs(10, 0, 0);
        fillMaze.addChest(20, 0, 0);

        fillMaze.addDoors(layout);

        int maxAttempts = 10, attempts = 0;
        do {
            gameController.randomLockAllDoors(0.5);
            attempts++;
        } while (!gameController.isSolvableWithResources() && attempts < maxAttempts);

        if (attempts == maxAttempts && !gameController.isSolvableWithResources()) {
            // last‐ditch warning, but we fall through so the window appears
            new Alert(Alert.AlertType.WARNING,
                    "Couldn’t make a winnable maze in " + maxAttempts + " tries.\n" +
                            "You may be trapped from the start!")
                    .showAndWait();
        }

// now this method returns, Main.start can call stage.show()
        gameController.updateStats();
        startNPCMovement();



    }

    private void startNPCMovement() {
        Timeline npcMove = new Timeline(new KeyFrame(Duration.seconds(1), event -> gameController.moveNPCs()));
        npcMove.setCycleCount(Timeline.INDEFINITE);
        npcMove.play();
    }

    public Scene getScene() {
        Scene scene = new Scene(mainLayout);
        scene.setOnKeyPressed(this::handleKeyPress);
        return scene;
    }

    public void handleKeyPress(KeyEvent event) {
        keyHandler.handle(event, isHeroOnRight);

        if (event.getCode().toString().equals("LEFT")) isHeroOnRight = false;
        else if (event.getCode().toString().equals("RIGHT")) isHeroOnRight = true;
    }
}
