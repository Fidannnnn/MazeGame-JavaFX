package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.objects.*;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.structures.Room;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;

public class GameController {
    private final Room[][] worldGrid;
    private final List<Npc> npcs;
    private final Hero hero;
    private final GridPane gridPane;
    private final Label healthLabel;
    private final Label keysLabel;
    private final Label crowbarsLabel;
    private final Label enemyHealthLabel;
    private final int CELL_SIZE;

    private int exitRow;
    private int exitCol;


    public GameController(Room[][] worldGrid, List<Npc> npcs, Hero hero,
                          GridPane gridPane, Label healthLabel,
                          Label keysLabel, Label crowbarsLabel, Label enemyHealthLabel, int cellSize) {
        this.worldGrid = worldGrid;
        this.npcs = npcs;
        this.hero = hero;
        this.gridPane = gridPane;
        this.healthLabel = healthLabel;
        this.keysLabel = keysLabel;
        this.crowbarsLabel = crowbarsLabel;
        this.enemyHealthLabel = enemyHealthLabel;
        this.CELL_SIZE = cellSize;
    }

    public void updateStats() {

        healthLabel.setText("Health: " + hero.getHealth());
        keysLabel.setText("Keys: " + hero.getNumOfKeys());
        crowbarsLabel.setText("Crowbars: " + hero.getNumOfCrowbars());

        // Fix this logic
        boolean enemyFound = false;
        for (Npc npc : npcs) {
            if (hero.getX() == npc.getX() && hero.getY() == npc.getY()) {
                enemyHealthLabel.setText("Enemy HP: " + npc.getHealth());
                enemyFound = true;
                break;
            }
        }
        if (!enemyFound) {
            enemyHealthLabel.setText("Enemy HP: 0");
        }
    }


    public boolean moveHero(int newRow, int newCol, int heroRow, int heroCol, ImageView heroImage, Pos alignment) {
        if (newRow < 0 || newRow >= worldGrid.length || newCol < 0 || newCol >= worldGrid[0].length) return false;

        Room currentRoom = worldGrid[heroRow][heroCol];
        Room targetRoom = worldGrid[newRow][newCol];

        if (!currentRoom.isConnectedTo(targetRoom)) {
            System.out.println("No unlocked door between current room and target room.");
            return false;
        }

        // Remove hero image from current cell
        gridPane.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null &&
                        GridPane.getRowIndex(node) != null &&
                        GridPane.getColumnIndex(node) == heroCol &&
                        GridPane.getRowIndex(node) == heroRow &&
                        node instanceof StackPane &&
                        ((StackPane) node).getChildren().contains(heroImage)
        );

        // Update hero's position
        hero.setX(newCol);
        hero.setY(newRow);

        // After updating hero's position and adding visual
        if (hero.getY() == exitRow && hero.getX() == exitCol) {
            javafx.application.Platform.runLater(() -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Victory");
                alert.setHeaderText(null);
                alert.setContentText("You reached the exit. You win!");
                alert.showAndWait();  // Wait until alert is closed

                // Close the application
                javafx.application.Platform.exit();
                System.exit(0); // Ensures JVM stops
            });
        }


        // Add hero to new position
        StackPane newHeroPane = new StackPane(heroImage);
        newHeroPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        newHeroPane.setAlignment(alignment);
        newHeroPane.setMouseTransparent(true);
        gridPane.add(newHeroPane, newCol, newRow);

        return true;
    }

    public void setExitCoordinates(int row, int col) {
        this.exitRow = row;
        this.exitCol = col;
    }

    public void tryUnlockDoorInDirection(int heroRow, int heroCol, int rowOffset, int colOffset) {
        int newRow = heroRow + rowOffset;
        int newCol = heroCol + colOffset;

        if (newRow < 0 || newRow >= worldGrid.length || newCol < 0 || newCol >= worldGrid[0].length)
            return;

        Room currentRoom = worldGrid[heroRow][heroCol];
        Room adjacentRoom = worldGrid[newRow][newCol];

        for (Door door : currentRoom.getDoors()) {
            if (door.getRooms().contains(adjacentRoom)) {
                if (door.isLocked()) {
                    if (hero.getNumOfKeys() > 0) {
                        hero.addNumOfKeys(-1);
                        door.setLocked(false);
                        door.updateVisual();
                        updateStats();
                        System.out.println("Unlocked Door " + door.getDoorID() + " with a key!");
                    } else {
                        System.out.println("Door is locked. You need a key.");
                    }
                } else {
                    System.out.println("Door " + door.getDoorID() + " is already open.");
                }
                return;
            }
        }
        System.out.println("No door in that direction.");
    }

    public void tryOpenChest(Chest chest, int heroRow, int heroCol) {
        if (chest.getX() == heroCol && chest.getY() == heroRow) {
            if (chest.unlock(hero)) {
                List<Objects> items = chest.getItems();
                for (Objects item : items) {
                    if (item instanceof Key) hero.addNumOfKeys(1);
                    else if (item instanceof Crowbar) hero.addNumOfCrowbars(1);
                    else if (item instanceof HealthPotion hp) hero.addHealth(hp.getHealthPoints());
                }

                // Remove the chest from the room's object list
                worldGrid[heroRow][heroCol].getObjects().remove(chest);

                // Remove the chest visual from the grid
                gridPane.getChildren().removeIf(node ->
                        GridPane.getColumnIndex(node) != null &&
                                GridPane.getRowIndex(node) != null &&
                                GridPane.getColumnIndex(node) == heroCol &&
                                GridPane.getRowIndex(node) == heroRow &&
                                node instanceof StackPane &&
                                "chest".equals(((StackPane) node).getId())
                );


                updateStats();
                System.out.println("Chest items added to the hero!");
            }
        } else {
            System.out.println("You must stand on the chest to open it.");
        }
    }

    public Chest getChestAt(int col, int row) {
        Room currentRoom = worldGrid[row][col];
        for (Objects obj : currentRoom.getObjects()) {
            if (obj instanceof Chest chest && chest.getX() == col && chest.getY() == row) {
                return chest;
            }
        }
        return null;
    }

    public Npc getNpcAt(int x, int y) {
        for (Npc npc : npcs) {
            if (npc.getX() == x && npc.getY() == y) {
                return npc;
            }
        }
        return null;
    }



    public void removeNpc(Npc npc) {
        gridPane.getChildren().remove(npc.getVisual());
        npcs.remove(npc);
    }


    public void moveNPCs() {
        for (Npc npc : npcs) {
            int oldX = npc.getX();
            int oldY = npc.getY();

            // If NPC is in same cell as hero, skip movement
            if (npc.getX() == hero.getX() && npc.getY() == hero.getY()) {
                npc.execute(hero);  // allow it to attack
                continue;
            }

            gridPane.getChildren().remove(npc.getVisual());

            // Try to move, but restore if target is the exit
            npc.moveRandomly(worldGrid.length);

            // If NPC lands in the exit cell, cancel move
            if (npc.getX() == CELL_SIZE - 1 &&
                    npc.getY() == CELL_SIZE - 1) {
                npc.setX(oldX);
                npc.setY(oldY);
            }


            npc.execute(hero);
            if (hero.getHealth() < 0) hero.setHealth(-1);
            checkHeroDeath();

            updateStats();

            gridPane.add(npc.getVisual(), npc.getX(), npc.getY());

        }

        updateStats();
    }

    private void checkHeroDeath() {
        if (hero.getHealth() == 0) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Game Over");
                alert.setHeaderText(null);
                alert.setContentText("Hero has died. Game over!");
                alert.showAndWait();
                Platform.exit();
                System.exit(0);
            });
        }
    }


    public Room[][] getWorldGrid() {
        return worldGrid;
    }
}
