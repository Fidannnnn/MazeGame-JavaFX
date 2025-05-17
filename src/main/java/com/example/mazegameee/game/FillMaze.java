package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.objects.Chest;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.structures.Room;
import com.example.mazegameee.entities.MazeLayout;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FillMaze {
    private final GridPane gridPane;
    private final Room[][] worldGrid;
    private final List<Npc> npcs;
    private final Hero hero;
    private final int CELL_SIZE;

    public FillMaze(GridPane gridPane, Room[][] worldGrid, List<Npc> npcs, Hero hero, int CELL_SIZE) {
        this.gridPane = gridPane;
        this.worldGrid = worldGrid;
        this.npcs = npcs;
        this.hero = hero;
        this.CELL_SIZE = CELL_SIZE;
    }

    public void drawRooms() {
        String[] tileFiles = {
                "tilesand.png",
                "tilestone.png"
        };
        Random rnd = new Random();

        for (int row = 0; row < worldGrid.length; row++) {
            for (int col = 0; col < worldGrid[0].length; col++) {
                // Model
                Room room = new Room(col, row, row * worldGrid.length + col,
                        new ArrayList<>(), new ArrayList<>());
                worldGrid[row][col] = room;

                // Pick a random tile
                String path = tileFiles[rnd.nextInt(tileFiles.length)];
                InputStream is = getClass()
                        .getClassLoader()
                        .getResourceAsStream(path);
                if (is == null) {
                    throw new IllegalStateException("Cannot load resource: " + path);
                }
                Image tile = new Image(is);
                ImageView iv = new ImageView(tile);
                iv.setFitWidth(CELL_SIZE);
                iv.setFitHeight(CELL_SIZE);

                gridPane.add(iv, col, row);
            }
        }
    }



    public void markExit(Room exit) {
        ImageView exitImage = new ImageView(new Image("opengate.png"));
        exitImage.setFitWidth(CELL_SIZE);
        exitImage.setFitHeight(CELL_SIZE);

        StackPane exitPane = new StackPane(exitImage);
        exitPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        exitPane.setMouseTransparent(true);
        exitPane.setAlignment(Pos.CENTER);

        gridPane.add(exitPane, exit.getX(), exit.getY());
    }

    public void addHeroVisual(int row, int col, ImageView heroImage) {
        StackPane heroPane = new StackPane(heroImage);
        heroPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        heroPane.setAlignment(Pos.CENTER);
        gridPane.add(heroPane, col, row);
    }

    public void addNPCs(int count, int heroRow, int heroCol) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(worldGrid.length);
            int col = random.nextInt(worldGrid[0].length);

            if (row == heroRow && col == heroCol) continue;

            Npc npc = new Npc(col, row, 50);
            npcs.add(npc);

            Image npcIcon = new Image("npc.png");
            ImageView npcImage = new ImageView(npcIcon);
            npcImage.setFitWidth(CELL_SIZE / 3);
            npcImage.setFitHeight(CELL_SIZE / 3);

            StackPane npcPane = new StackPane(npcImage);
            npcPane.setAlignment(Pos.BOTTOM_CENTER);
            npcPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            npcPane.setMouseTransparent(true);
            npc.setVisual(npcPane);

            gridPane.add(npcPane, col, row);
            placed++;
        }
    }

    public void addChest(int count, int heroRow, int heroCol) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(worldGrid.length);
            int col = random.nextInt(worldGrid[0].length);

            if (row == heroRow && col == heroCol) continue;
            boolean npcThere = npcs.stream().anyMatch(n -> n.getX() == col && n.getY() == row);
            if (npcThere) continue;

            // 1️⃣ Create the model object
            Chest chest = new Chest(col, row, true);
            worldGrid[row][col].getObjects().add(chest);

            // 2️⃣ Load the chest image
            Image chestIcon = new Image("chest.png");
            ImageView chestImage = new ImageView(chestIcon);
            chestImage.setFitWidth(CELL_SIZE / 3);
            chestImage.setFitHeight(CELL_SIZE / 3);

            // 3️⃣ Wrap it in a StackPane just like before
            StackPane chestPane = new StackPane(chestImage);
            chestPane.setId("chest");
            chestPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            chestPane.setAlignment(Pos.TOP_RIGHT);
            chestPane.setMouseTransparent(true);

            // 4️⃣ Add to the grid
            gridPane.add(chestPane, col, row);

            placed++;
        }
    }


    public void addDoors(MazeLayout layout) {
        int rows = worldGrid.length;
        int cols = worldGrid[0].length;
        int id = 1;
        Random rnd = new Random();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Room here = worldGrid[r][c];

                // ─── EAST side between (r,c) and (r,c+1)
                if (c < cols - 1) {
                    Room there = worldGrid[r][c + 1];
                    if (layout.hasEastOpening(r, c)) {
                        // Create a door
                        boolean locked = rnd.nextBoolean();
                        Door door = new Door(c, r, id++, locked, here, there);
                        here.getDoors().add(door);
                        there.getDoors().add(door);
                        addDoorVisual(door, true);
                    } else {
                        // Draw a solid wall spanning both columns c and c+1
                        Rectangle wall = new Rectangle(5, CELL_SIZE);
                        wall.setFill(Color.DARKSLATEGRAY);
                        StackPane wallPane = new StackPane(wall);
                        GridPane.setColumnIndex(wallPane, c);
                        GridPane.setRowIndex   (wallPane, r);
                        GridPane.setColumnSpan(wallPane, 2);
                        gridPane.getChildren().add(wallPane);
                    }
                }

                // ─── SOUTH side between (r,c) and (r+1,c)
                if (r < rows - 1) {
                    Room there = worldGrid[r + 1][c];
                    if (layout.hasSouthOpening(r, c)) {
                        // Create a door
                        boolean locked = rnd.nextBoolean();
                        Door door = new Door(c, r, id++, locked, here, there);
                        here.getDoors().add(door);
                        there.getDoors().add(door);
                        addDoorVisual(door, false);
                    } else {
                        // Draw a solid wall spanning both rows r and r+1
                        Rectangle wall = new Rectangle(CELL_SIZE, 5);
                        wall.setFill(Color.DARKSLATEGRAY);
                        StackPane wallPane = new StackPane(wall);
                        GridPane.setColumnIndex(wallPane, c);
                        GridPane.setRowIndex   (wallPane, r);
                        GridPane.setRowSpan    (wallPane, 2);
                        gridPane.getChildren().add(wallPane);
                    }
                }
            }
        }
    }


    private void addDoorVisual(Door door, boolean vertical) {
        Rectangle wallRect = vertical ? new Rectangle(5, CELL_SIZE) : new Rectangle(CELL_SIZE, 5);
        wallRect.setFill(Color.DARKSLATEGRAY);
        Rectangle doorRect = vertical ? new Rectangle(5, CELL_SIZE/3) : new Rectangle(CELL_SIZE/3, 5);
        doorRect.setFill(door.isLocked() ? Color.DARKRED : Color.SADDLEBROWN);
        doorRect.setMouseTransparent(false);
        door.setVisual(doorRect);

        StackPane wallPane = new StackPane(wallRect);
        StackPane doorPane = new StackPane(doorRect);

        if (vertical) {
            GridPane.setColumnIndex(wallPane, door.getX());
            GridPane.setRowIndex(wallPane, door.getY());
            GridPane.setColumnSpan(wallPane, 2);
        } else {
            GridPane.setColumnIndex(wallPane, door.getX());
            GridPane.setRowIndex(wallPane, door.getY());
            GridPane.setRowSpan(wallPane, 2);
        }

        if (vertical) {
            GridPane.setColumnIndex(doorPane, door.getX());
            GridPane.setRowIndex(doorPane, door.getY());
            GridPane.setColumnSpan(doorPane, 2);
        } else {
            GridPane.setColumnIndex(doorPane, door.getX());
            GridPane.setRowIndex(doorPane, door.getY());
            GridPane.setRowSpan(doorPane, 2);
        }
        gridPane.getChildren().add(wallPane);
        gridPane.getChildren().add(doorPane);
    }
}
