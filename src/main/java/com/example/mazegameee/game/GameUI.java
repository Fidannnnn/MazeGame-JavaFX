package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.objects.Chest;
import com.example.mazegameee.objects.Crowbar;
import com.example.mazegameee.objects.HealthPotion;
import com.example.mazegameee.objects.Key;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.structures.Room;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class GameUI {
    private BorderPane mainLayout;
    private HBox statsPanel;
    private GridPane gridPane;

    private Label healthLabel;
    private Label keysLabel;
    private Label crowbarsLabel;
    private static final int CELL_SIZE = 50;

    private ImageView heroImage;
    private int heroRow = 0, heroCol = 0;

    private ImageView npcImage;
    private int npcRow = 5, npcCol = 5;
    private Random random = new Random();

    private Hero hero;

    private List<Door> doors = new ArrayList<>();

    private Room[][] roomGrid;



    public GameUI(int gridSize) {
        mainLayout = new BorderPane();
        statsPanel = new HBox(20);
        statsPanel.setAlignment(Pos.CENTER);

        healthLabel = new Label();
        keysLabel = new Label();
        crowbarsLabel = new Label();
        statsPanel.getChildren().addAll(healthLabel, keysLabel, crowbarsLabel);

        mainLayout.setTop(statsPanel);
        gridPane = new GridPane();
        mainLayout.setCenter(gridPane);
        drawGrid(gridSize);
        addDoors(roomGrid);
        addChest(gridSize);
        addHero();
        addNPC();
        updateStats();
        startNPCMovement();
    }

    private void drawGrid(int size) {
        roomGrid = new Room[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Room room = new Room(col, row, row * size + col, new ArrayList<>(), new ArrayList<>());
                roomGrid[row][col] = room;

                Image roomIcon = new Image("tile.png");
                ImageView roomImage = new ImageView(roomIcon);
                roomImage.setFitWidth(CELL_SIZE);
                roomImage.setFitHeight(CELL_SIZE);
                gridPane.add(roomImage, col, row);
            }
        }
    }


    public Scene getScene() {
        return new Scene(mainLayout);
    }


    public GridPane getGridPane() {
        return gridPane;
    }

    private void updateStats() {
        healthLabel.setText("Health: " + hero.getHealth());
        keysLabel.setText("Keys: " + hero.getNumOfKeys());
        crowbarsLabel.setText("Crowbars: " + hero.getNumOfCrowbars());
    }


    private void addRoom(int row, int col) {
        Image roomIcon = new Image("tile.png"); // Replace with your actual room image
        ImageView roomImage = new ImageView(roomIcon);
        roomImage.setFitWidth(CELL_SIZE);
        roomImage.setFitHeight(CELL_SIZE);

        gridPane.add(roomImage, col, row);
    }


    private void addHero() {
        Image heroIcon = new Image("hero.png");
        heroImage = new ImageView(heroIcon);
        heroImage.setFitWidth(CELL_SIZE / 2);
        heroImage.setFitHeight(CELL_SIZE / 2);
        heroImage.setMouseTransparent(true); // 👈 THIS FIXES IT!

        hero = new Hero(0, 0, 100, 100);

        StackPane heroPane = new StackPane(heroImage);
        heroPane.setAlignment(Pos.BOTTOM_RIGHT);
        gridPane.add(heroPane, heroCol, heroRow);
    }


    // Track the horizontal alignment (left or right)
    private boolean isHeroOnRight = false; // Initially on the left

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP, DOWN -> {
                if (isHeroOnRight) {
                    moveHero(heroRow + (event.getCode() == KeyCode.UP ? -1 : 1), heroCol, Pos.BOTTOM_RIGHT);
                } else {
                    moveHero(heroRow + (event.getCode() == KeyCode.UP ? -1 : 1), heroCol, Pos.BOTTOM_LEFT);
                }
            }
            case LEFT -> {
                isHeroOnRight = false; // Moving left, so align to bottom-left
                moveHero(heroRow, heroCol - 1, Pos.BOTTOM_LEFT);
            }
            case RIGHT -> {
                isHeroOnRight = true; // Moving right, so align to bottom-right
                moveHero(heroRow, heroCol + 1, Pos.BOTTOM_RIGHT);
            }
        }
    }

    private void moveHero(int newRow, int newCol, Pos alignment) {
        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
            Room currentRoom = roomGrid[heroRow][heroCol];
            Room targetRoom = roomGrid[newRow][newCol];

            // Only move if connected via an unlocked door
            if (!currentRoom.isConnectedTo(targetRoom)) {
                System.out.println("No unlocked door between current room and target room.");
                return;
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

            // Update hero position
            heroRow = newRow;
            heroCol = newCol;

            // Add hero to the new position
            StackPane newHeroPane = new StackPane(heroImage);
            newHeroPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            newHeroPane.setAlignment(alignment);
            newHeroPane.setMouseTransparent(true); // allows clicks to pass through

            gridPane.add(newHeroPane, heroCol, heroRow);
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
        int newRow = npcRow + random.nextInt(3) - 1;
        int newCol = npcCol + random.nextInt(3) - 1;

        if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10 && (newRow != heroRow || newCol != heroCol)) {
            gridPane.getChildren().remove(npcImage);
            npcRow = newRow;
            npcCol = newCol;
            gridPane.add(npcImage, npcCol, npcRow);
        }
    }


    private void addChest(int size) {
        int placed = 0;
        int maxChests = 20;

        while (placed < maxChests) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            // Prevent spawning chests on the hero or NPC positions
            if ((row == heroRow && col == heroCol) || (row == npcRow && col == npcCol)) continue;

            Rectangle rect = new Rectangle(CELL_SIZE / 2.0, CELL_SIZE / 2.0);
            rect.setFill(Color.GOLD);

            Chest chest = new Chest(row, col, true);

            StackPane chestPane = new StackPane();
            chestPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            chestPane.setAlignment(Pos.TOP_RIGHT);
            chestPane.getChildren().add(rect);

            chestPane.setOnMouseClicked(event -> {
                System.out.println("Chest clicked at: " + row + ", " + col);
                System.out.println("Hero at: " + heroRow + ", " + heroCol);

                if (heroRow == row && heroCol == col) {
                    if (chest.unlock(hero)) {
                        System.out.println("Chest opened!");
                        List<Objects> items = chest.getItems();
                        for (Objects item : items) {
                            if (item instanceof Key) {
                                hero.addNumOfKeys(1);
                            } else if (item instanceof Crowbar) {
                                hero.addNumOfCrowbars(1);
                            } else if (item instanceof HealthPotion) {
                                int health = ((HealthPotion) item).getHealthPoints();
                                hero.addHealth(health);
                            }
                        }
                        updateStats();
                        System.out.println("Chest items added to the hero!");
                        gridPane.getChildren().remove(chestPane);
                    }
                } else {
                    System.out.println("You must stand on the chest to open it.");
                }
            });

            gridPane.add(chestPane, col, row);
            placed++;
        }
    }

    private void addDoors(Room[][] rooms) {
        int id = 1;
        int size = rooms.length;
        Random random = new Random();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Room current = rooms[row][col];

                // Create vertical door (right)
                if (col < size - 1) {
                    Room right = rooms[row][col + 1];
                    boolean locked = random.nextBoolean(); // 50% chance locked
                    Door door = new Door(col, row, id++, locked, current, right);
                    addDoorVisual(door, true); // vertical
                }

                // Create horizontal door (down)
                if (row < size - 1) {
                    Room bottom = rooms[row + 1][col];
                    boolean locked = random.nextBoolean(); // 50% chance locked
                    Door door = new Door(col, row, id++, locked, current, bottom);
                    addDoorVisual(door, false); // horizontal
                }
            }
        }
    }


    private void addDoorVisual(Door door, boolean vertical) {
        Rectangle doorRect = vertical
                ? new Rectangle(5, CELL_SIZE)
                : new Rectangle(CELL_SIZE, 5);

        doorRect.setFill(door.isLocked() ? Color.DARKRED : Color.SADDLEBROWN);

        StackPane doorPane = new StackPane(doorRect);
        doorPane.setMouseTransparent(false); // allow interaction

        // Handle clicks on the door
        doorPane.setOnMouseClicked(event -> {
            // Check if hero is adjacent to either connected room
            Room room1 = door.getRooms().get(0);
            Room room2 = door.getRooms().get(1);
            boolean heroNearby = (heroRow == room1.getY() && heroCol == room1.getX()) ||
                    (heroRow == room2.getY() && heroCol == room2.getX());

            if (heroNearby) {
                if (door.isLocked()) {
                    if (hero.getNumOfKeys() > 0) {
                        hero.addNumOfKeys(-1);
                        door.setLocked(false);
                        System.out.println("You unlocked Door " + door.getDoorID());
                        doorRect.setFill(Color.SADDLEBROWN); // visually unlocked
                        updateStats(); // update top panel
                    } else {
                        System.out.println("Door is locked. You need a key.");
                    }
                } else {
                    System.out.println("Door " + door.getDoorID() + " is open!");
                }
            } else {
                System.out.println("You must be next to the door to interact.");
            }
        });

        // Set location in grid
        if (vertical) {
            GridPane.setColumnIndex(doorPane, door.getX());
            GridPane.setRowIndex(doorPane, door.getY());
            GridPane.setColumnSpan(doorPane, 2);
        } else {
            GridPane.setColumnIndex(doorPane, door.getX());
            GridPane.setRowIndex(doorPane, door.getY());
            GridPane.setRowSpan(doorPane, 2);
        }

        gridPane.getChildren().add(doorPane);
    }





}

