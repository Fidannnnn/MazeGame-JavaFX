package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.entities.MazeLayout;
import com.example.mazegameee.objects.Chest;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.structures.Room;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;
import java.util.*;

public class FillMaze {
    // reference to the main visual grid where we draw everything
    private final GridPane gridPane;

    // 2D array representing the rooms in our maze (logical structure)
    private final Room[][] worldGrid;

    // list of all the NPCs currently in the game
    private final List<Npc> npcs;

    // the player's character (there’s only one hero)
    private final Hero hero;

    // constant for how big each cell (room) appears on screen
    private final int CELL_SIZE;

    // constructor that connects everything together: visuals, logic, and game elements
    public FillMaze(GridPane gridPane, Room[][] worldGrid, List<Npc> npcs, Hero hero, int CELL_SIZE) {
        this.gridPane   = gridPane;
        this.worldGrid  = worldGrid;
        this.npcs       = npcs;
        this.hero       = hero;
        this.CELL_SIZE  = CELL_SIZE;
    }


    // creates all the rooms in the maze and picks a random floor texture for each
    public void drawRooms() {
        // we have two tile textures to randomly choose from
        String[] tileFiles = { "tilesand.png", "tilestone.png" };
        Random rnd = new Random();

        // go through every cell in the grid
        for (int row = 0; row < worldGrid.length; row++) {
            for (int col = 0; col < worldGrid[0].length; col++) {
                // first, create the room object and save it into our grid
                Room room = new Room(col, row, row * worldGrid.length + col, new ArrayList<>(), new ArrayList<>());
                worldGrid[row][col] = room;

                // randomly pick a texture file for the floor
                String path = tileFiles[rnd.nextInt(tileFiles.length)];

                // load the image from resources — crash if missing
                InputStream is = getClass().getClassLoader().getResourceAsStream(path);
                if (is == null) throw new IllegalStateException("Couldn’t load tile: " + path);

                // set image size and add to the grid visually
                Image tile = new Image(is);
                ImageView iv = new ImageView(tile);
                iv.setFitWidth(CELL_SIZE);
                iv.setFitHeight(CELL_SIZE);
                gridPane.add(iv, col, row);
            }
        }
    }


    // adds the visual gate image to the exit room
    public void markExit(Room exit) {
        // load the exit gate image and size it to fit the cell
        ImageView exitImage = new ImageView(new Image("opengate.png"));
        exitImage.setFitWidth(CELL_SIZE);
        exitImage.setFitHeight(CELL_SIZE);

        // wrap the image in a StackPane so we can position it easily
        StackPane exitPane = new StackPane(exitImage);
        exitPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        exitPane.setMouseTransparent(true); // so it doesn’t block clicks or key events
        exitPane.setAlignment(Pos.CENTER);

        // add the exit visual to the correct grid cell
        gridPane.add(exitPane, exit.getX(), exit.getY());
    }


    // puts the hero’s icon on the board at a given row and column
    public void addHeroVisual(int row, int col, ImageView heroImage) {
        // wrap the image in a StackPane to control layout and alignment
        StackPane heroPane = new StackPane(heroImage);
        heroPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        heroPane.setAlignment(Pos.CENTER); // center the hero in the cell

        // finally, drop it into the grid
        gridPane.add(heroPane, col, row);
    }


    // randomly places `count` NPCs on the board
    public void addNPCs(int count, int heroRow, int heroCol) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(worldGrid.length);
            int col = random.nextInt(worldGrid[0].length);

            // skip the hero’s starting spot
            if (row == heroRow && col == heroCol) continue;

            // skip the last cell
            if (row == 9 && col == 9) continue;

            // create and store the NPC
            Npc npc = new Npc(col, row, 50);
            npcs.add(npc);

            // set up the visual for the NPC
            Image npcIcon = new Image("npc.png");
            ImageView npcImage = new ImageView(npcIcon);
            npcImage.setFitWidth(CELL_SIZE / 3);
            npcImage.setFitHeight(CELL_SIZE / 3);

            StackPane npcPane = new StackPane(npcImage);
            npcPane.setAlignment(Pos.BOTTOM_CENTER); // stick the npc to the bottom
            npcPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            npcPane.setMouseTransparent(true); // don’t block mouse events
            npc.setVisual(npcPane);

            // add it to the board
            gridPane.add(npcPane, col, row);
            placed++;
        }
    }


    // randomly places `count` chests on the board
    public void addChest(int count, int heroRow, int heroCol) {
        Random random = new Random();
        int placed = 0;

        while (placed < count) {
            int row = random.nextInt(worldGrid.length);
            int col = random.nextInt(worldGrid[0].length);

            // skip the hero’s position
            if (row == heroRow && col == heroCol) continue;

            // skip the last cell
            if (row == 9 && col == 9) continue;

            // don’t place chests on top of NPCs
            boolean npcThere = npcs.stream().anyMatch(n -> n.getX() == col && n.getY() == row);
            if (npcThere) continue;

            // create a new locked chest and drop it into the model
            Chest chest = new Chest(col, row, true);
            worldGrid[row][col].getObjects().add(chest);

            // load chest image
            Image chestIcon = new Image("chest.png");
            ImageView chestImage = new ImageView(chestIcon);
            chestImage.setFitWidth(CELL_SIZE / 3);
            chestImage.setFitHeight(CELL_SIZE / 3);

            // visually place the chest in the grid
            StackPane chestPane = new StackPane(chestImage);
            chestPane.setId("chest"); // useful for removal later
            chestPane.setPrefSize(CELL_SIZE, CELL_SIZE);
            chestPane.setAlignment(Pos.TOP_RIGHT); // float it in the corner
            chestPane.setMouseTransparent(true); // just visual, no interaction

            gridPane.add(chestPane, col, row);
            placed++;
        }
    }


    // connects rooms with doors or walls based on CSV layout
    public void addDoorsAndWalls(MazeLayout layout) {
        int rows = worldGrid.length;
        int cols = worldGrid[0].length;
        int id = 1;
        Random rnd = new Random();

        // go through every room in the grid
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Room here = worldGrid[r][c];

                // ─── EAST side between (r,c) and (r,c+1)
                if (c < cols - 1) {
                    Room there = worldGrid[r][c + 1];
                    if (layout.hasEastOpening(r, c)) {
                        // if there's a connection to the right, place a door (maybe locked)
                        boolean locked = rnd.nextBoolean();
                        Door door = new Door(c, r, id++, locked, here, there);
                        here.getDoors().add(door);
                        there.getDoors().add(door);
                        addDoorWallVisual(door, true);  // vertical door
                    } else {
                        // otherwise draw a wall on the right edge
                        Rectangle wall = new Rectangle(5, CELL_SIZE);
                        wall.setFill(Color.DARKSLATEGRAY);
                        StackPane wallPane = new StackPane(wall);
                        GridPane.setColumnIndex(wallPane, c);
                        GridPane.setRowIndex(wallPane, r);
                        GridPane.setColumnSpan(wallPane, 2);  // spans into next column
                        gridPane.getChildren().add(wallPane);
                    }
                }

                // ─── SOUTH side between (r,c) and (r+1,c)
                if (r < rows - 1) {
                    Room there = worldGrid[r + 1][c];
                    if (layout.hasSouthOpening(r, c)) {
                        // connect room below with door (maybe locked)
                        boolean locked = rnd.nextBoolean();
                        Door door = new Door(c, r, id++, locked, here, there);
                        here.getDoors().add(door);
                        there.getDoors().add(door);
                        addDoorWallVisual(door, false);  // horizontal door
                    } else {
                        // otherwise draw a wall below the room
                        Rectangle wall = new Rectangle(CELL_SIZE, 5);
                        wall.setFill(Color.DARKSLATEGRAY);
                        StackPane wallPane = new StackPane(wall);
                        GridPane.setColumnIndex(wallPane, c);
                        GridPane.setRowIndex(wallPane, r);
                        GridPane.setRowSpan(wallPane, 2);  // spans into next row
                        gridPane.getChildren().add(wallPane);
                    }
                }
            }
        }
    }


    // draws the visual for the door and its wall based on direction
    private void addDoorWallVisual(Door door, boolean vertical) {
        // base wall visual (gray line) stretches fully
        Rectangle wallRect = vertical
                ? new Rectangle(5, CELL_SIZE)
                : new Rectangle(CELL_SIZE, 5);
        wallRect.setFill(Color.DARKSLATEGRAY);

        // actual door visual (shorter colored line)
        Rectangle doorRect = vertical
                ? new Rectangle(5, CELL_SIZE / 3)
                : new Rectangle(CELL_SIZE / 3, 5);
        doorRect.setFill(door.isLocked() ? Color.DARKRED : Color.SADDLEBROWN);
        doorRect.setMouseTransparent(false);  // allow mouse interaction (optional)
        door.setVisual(doorRect);             // save so we can update color later

        // wrap visuals in panes to position them on the grid
        StackPane wallPane = new StackPane(wallRect);
        StackPane doorPane = new StackPane(doorRect);

        // position both on the same cell (the edge between rooms)
        GridPane.setColumnIndex(wallPane, door.getX());
        GridPane.setRowIndex(wallPane, door.getY());
        GridPane.setColumnIndex(doorPane, door.getX());
        GridPane.setRowIndex(doorPane, door.getY());

        // depending on door direction, stretch horizontally or vertically
        if (vertical) {
            GridPane.setColumnSpan(wallPane, 2);
            GridPane.setColumnSpan(doorPane, 2);
        } else {
            GridPane.setRowSpan(wallPane, 2);
            GridPane.setRowSpan(doorPane, 2);
        }

        // actually add them to the board
        gridPane.getChildren().add(wallPane);
        gridPane.getChildren().add(doorPane);
    }

}
