package com.example.mazegameee.game;

import com.example.mazegameee.objects.Chest;
import com.example.mazegameee.structures.Room;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

public class GameUI {
    private GridPane gridPane;
    private static final int CELL_SIZE = 50; // Size of each room

    private ImageView heroImage;
    private int heroRow = 0, heroCol = 0; // Hero starts at (0,0)

    private ImageView npcImage;
    private int npcRow = 5, npcCol = 5; // Start in center
    private Random random = new Random();

    public GameUI(int gridSize) {
        gridPane = new GridPane();
        drawGrid(gridSize);
        addHero();
        addNPC();
        startNPCMovement();
    }

    private void drawGrid(int size) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.setStroke(Color.BLACK);
                cell.setFill(Color.LIGHTGRAY);
                gridPane.add(cell, col, row);
            }
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    private void addHero() {
        Image heroIcon = new Image("hero.png"); // Replace with actual image path
        heroImage = new ImageView(heroIcon);
        heroImage.setFitWidth(CELL_SIZE);
        heroImage.setFitHeight(CELL_SIZE);
        gridPane.add(heroImage, heroCol, heroRow);
    }

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP -> moveHero(heroRow - 1, heroCol);
            case DOWN -> moveHero(heroRow + 1, heroCol);
            case LEFT -> moveHero(heroRow, heroCol - 1);
            case RIGHT -> moveHero(heroRow, heroCol + 1);
        }
    }

    private void moveHero(int newRow, int newCol) {
        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            gridPane.getChildren().remove(heroImage);
            heroRow = newRow;
            heroCol = newCol;
            gridPane.add(heroImage, heroCol, heroRow);
        }
    }

    private void addNPC() {
        Image npcIcon = new Image("npc.png"); // Replace with actual image path
        npcImage = new ImageView(npcIcon);
        npcImage.setFitWidth(CELL_SIZE);
        npcImage.setFitHeight(CELL_SIZE);
        gridPane.add(npcImage, npcCol, npcRow);
    }

    private void startNPCMovement() {
        Timeline npcMove = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            moveNPC();
        }));
        npcMove.setCycleCount(Timeline.INDEFINITE);
        npcMove.play();
    }

    private void moveNPC() {
        int newRow = npcRow + random.nextInt(3) - 1; // Move up/down
        int newCol = npcCol + random.nextInt(3) - 1; // Move left/right

        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            gridPane.getChildren().remove(npcImage);
            npcRow = newRow;
            npcCol = newCol;
            gridPane.add(npcImage, npcCol, npcRow);
        }
    }

    /*
    private void addChest(int row, int col) {
        Rectangle chest = new Rectangle(CELL_SIZE, CELL_SIZE);
        chest.setFill(Color.GOLD);

        chest.setOnMouseClicked(event -> {
            if (Math.abs(heroRow - row) <= 1 && Math.abs(heroCol - col) <= 1) {
                System.out.println("Chest opened! You found a key.");
            } else {
                System.out.println("Move closer to open.");
            }
        });

        gridPane.add(chest, col, row);
    }

    private void addRandomChests(int totalChests) {
        Random random = new Random();
        int placed = 0;

        while (placed < totalChests) {
            int row = random.nextInt(board.getRows());
            int col = random.nextInt(board.getCols());

            Room room = gridPane.getRoom(row, col);
            if (!room.hasChest()) { // Ensure no duplicate chests
                room.setChest(new Chest()); // Add chest to game logic
                addChest(row, col); // Add chest to UI
                placed++;
                System.out.println("Chest added at: " + row + ", " + col);
            }
        }
    }
    */

}

