package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.objects.Chest;
import com.example.mazegameee.objects.Crowbar;
import com.example.mazegameee.objects.HealthPotion;
import com.example.mazegameee.objects.Key;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.structures.Room;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.*;
import java.util.function.Consumer;


public class GameController {
    // grid where the whole game world lives
    private final Room[][] worldGrid;

    // all the enemies on the board
    private final List<Npc> npcs;

    // the one and only hero controlled by the player
    private final Hero hero;

    // UI elements
    private final GridPane gridPane;
    private final Label healthLabel;
    private final Label keysLabel;
    private final Label crowbarsLabel;
    private final Label enemyHealthLabel;

    // cell size for drawing stuff like doors, tiles, and icons
    private final int CELL_SIZE;

    // exit location in the grid
    private int exitRow;
    private int exitCol;

    // flags if the game has ended so we don’t trigger alerts multiple times
    private boolean gameOver = false;

    // this is a callback: it runs when the player either wins or dies
    private Consumer<Boolean> onGameEnd;

    // constructor — wires up the model and UI pieces together
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


    // updates the stat labels shown at the top of the game
    public void updateStats() {
        // update hero's current stats
        healthLabel.setText("Health: " + hero.getHealth());
        keysLabel.setText("Keys: " + hero.getNumOfKeys());
        crowbarsLabel.setText("Crowbars: " + hero.getNumOfCrowbars());

        // check if the hero is currently standing on an enemy — if yes, show enemy HP
        boolean enemyFound = false;
        for (Npc npc : npcs) {
            if (hero.getX() == npc.getX() && hero.getY() == npc.getY()) {
                enemyHealthLabel.setText("Enemy HP: " + npc.getHealth());
                enemyFound = true;
                break;
            }
        }

        // if no enemy is present in the hero's tile, set enemy HP to 0
        if (!enemyFound) {
            enemyHealthLabel.setText("Enemy HP: 0");
        }
    }

    // this lets the Main class hook into win/loss so it can show the restart prompt
    public void setOnGameEnd(Consumer<Boolean> onGameEnd) {
        this.onGameEnd = onGameEnd;
    }


    public boolean moveHero(int newRow, int newCol, int heroRow, int heroCol, ImageView heroImage, Pos ignoredAlignment) {
        // don’t let hero go off the grid
        if (newRow < 0 || newRow >= worldGrid.length
                || newCol < 0 || newCol >= worldGrid[0].length) {
            return false;
        }

        Room currentRoom = worldGrid[heroRow][heroCol];
        Room targetRoom  = worldGrid[newRow][newCol];

        // check if there's a door between current and target room AND it’s unlocked
        if (!currentRoom.isConnectedTo(targetRoom)) {
            System.out.println("No unlocked door between current room and target room.");
            return false;
        }

        // remove the hero image from the old cell
        gridPane.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null &&
                        GridPane.getRowIndex(node)    != null &&
                        GridPane.getColumnIndex(node) == heroCol &&
                        GridPane.getRowIndex(node)    == heroRow &&
                        node instanceof StackPane &&
                        ((StackPane) node).getChildren().contains(heroImage)
        );

        // update hero's internal position
        hero.setX(newCol);
        hero.setY(newRow);

        // win condition — if hero reaches the exit, trigger game win
        if (hero.getY() == exitRow && hero.getX() == exitCol) {
            if (!gameOver) {
                gameOver = true;
                if (onGameEnd != null) onGameEnd.accept(true);  // notify Main to handle restart dialog
            }
            return true;
        }

        // add hero back to the grid in new location
        StackPane newHeroPane = new StackPane(heroImage);
        newHeroPane.setPrefSize(CELL_SIZE, CELL_SIZE);
        newHeroPane.setAlignment(Pos.CENTER); // keep hero centered
        newHeroPane.setMouseTransparent(true);
        gridPane.add(newHeroPane, newCol, newRow);

        // check if hero is stuck with no resources and nowhere to go
        if (isCompletelyStuck()) {
            Alert stuckAlert = new Alert(Alert.AlertType.WARNING);
            stuckAlert.setTitle("Trapped!");
            stuckAlert.setHeaderText(null);
            stuckAlert.setContentText(
                    "You have no keys, no crowbars,\n" +
                            "no reachable chests, and no path to the exit.\n" +
                            "You’re completely trapped!"
            );
            stuckAlert.showAndWait();
        }

        return true;
    }



    // stores the exit room’s row and column so we can check for victory later
    public void setExitCoordinates(int row, int col) {
        this.exitRow = row;
        this.exitCol = col;
    }

    // checks if there's a door in the given direction, and tries to unlock it if it’s locked
    public void tryUnlockDoorInDirection(int heroRow, int heroCol, int rowOffset, int colOffset) {
        int newRow = heroRow + rowOffset;
        int newCol = heroCol + colOffset;

        // make sure we’re not going out of bounds
        if (newRow < 0 || newRow >= worldGrid.length
                || newCol < 0 || newCol >= worldGrid[0].length) {
            return;
        }

        Room currentRoom  = worldGrid[heroRow][heroCol];
        Room adjacentRoom = worldGrid[newRow][newCol];

        for (Door door : currentRoom.getDoors()) {
            if (!door.getRooms().contains(adjacentRoom)) {
                continue;
            }

            // found the door between current and next room
            if (door.isLocked()) {
                if (hero.getNumOfKeys() > 0) {
                    // use key to unlock
                    hero.addNumOfKeys(-1);
                    Key.openLock(door.getLock());
                    door.updateVisual();
                    updateStats();
                    System.out.println("Unlocked Door " + door.getDoorID() + " with a key!");

                    // check if the player is now completely stuck
                    if (isCompletelyStuck()) {
                        new Alert(Alert.AlertType.WARNING,
                                "You have no keys, no crowbars,\n" +
                                        "no chests reachable,\n" +
                                        "and no path to the exit.\n" +
                                        "You’re completely trapped!")
                                .showAndWait();
                    }
                } else if (hero.getNumOfCrowbars() > 0) {
                    // smash open with a crowbar
                    hero.addNumOfCrowbars(-1);
                    Crowbar.breakLock(door.getLock());
                    door.updateVisual();
                    updateStats();
                    System.out.println("Smashed Door with a crowbar!");
                } else {
                    // no tools left
                    System.out.println("Door is locked. You need a key or a crowbar!.");
                }
            } else {
                // door already open
                System.out.println("Door " + door.getDoorID() + " is already open.");
            }

            // we handled the door, no need to keep checking
            return;
        }

        // no door in that direction at all
        System.out.println("No door in that direction.");
    }


    // opens the chest the hero is standing on (if they can)
    public void tryOpenChest(Chest chest, int heroRow, int heroCol) {
        // make sure the hero is on the same cell as the chest
        if (chest.getX() != heroCol || chest.getY() != heroRow) {
            System.out.println("You must stand on the chest to open it.");
            return;
        }

        // try to unlock the chest using a key or a crowbar
        if (!chest.unlock(hero)) {
            // unlock() already printed the failure message (no tool), so we skip
            return;
        }

        chest.activate(); // just prints that the chest opened

        // give the items to the hero
        List<Objects> items = chest.getItems();
        for (Objects item : items) {
            if (item instanceof Key) {
                hero.addNumOfKeys(1);
            } else if (item instanceof Crowbar) {
                hero.addNumOfCrowbars(1);
            } else if (item instanceof HealthPotion hp) {
                hero.addHealth(hp.getHealthPoints());
            }
        }

        // remove the chest from the model (the logical grid)
        worldGrid[heroRow][heroCol].getObjects().remove(chest);

        // remove the chest image from the screen
        gridPane.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) != null &&
                        GridPane.getRowIndex(node) != null &&
                        GridPane.getColumnIndex(node) == heroCol &&
                        GridPane.getRowIndex(node) == heroRow &&
                        node instanceof StackPane &&
                        "chest".equals(((StackPane) node).getId())
        );

        // update labels like health, keys, crowbars
        updateStats();
        System.out.println("Chest items added to the hero!");

        // 🚨 warn the player if there’s no way forward
        if (isCompletelyStuck()) {
            new Alert(Alert.AlertType.WARNING,
                    "You have no keys, no crowbars,\n" +
                            "no reachable chests, and no path to the exit.\n" +
                            "You’re completely trapped!")
                    .showAndWait();
        }
    }





    // finds the chest at the given (col, row) if there's one there
    public Chest getChestAt(int col, int row) {
        Room currentRoom = worldGrid[row][col];

        // go through all objects in this room
        for (Objects obj : currentRoom.getObjects()) {
            // if it’s a Chest and it’s exactly at the given spot, return it
            if (obj instanceof Chest chest && chest.getX() == col && chest.getY() == row) {
                return chest;
            }
        }

        // no chest found
        return null;
    }

    // finds an NPC at the given x,y (if any)
    public Npc getNpcAt(int x, int y) {
        for (Npc npc : npcs) {
            // simple coordinate check
            if (npc.getX() == x && npc.getY() == y) {
                return npc;
            }
        }
        return null;
    }

    // removes an NPC from the game — both from visuals and logic
    public void removeNpc(Npc npc) {
        gridPane.getChildren().remove(npc.getVisual()); // remove the image from the grid
        npcs.remove(npc);                               // remove it from the list of active NPCs
    }


    public void moveNPCs() {
        // if the hero is already dead, don’t let NPCs move
        if (hero.getHealth() <= 0) return;

        for (Npc npc : npcs) {
            int oldX = npc.getX();
            int oldY = npc.getY();

            // if the NPC is already standing on the hero, let it attack and skip movement
            if (npc.getX() == hero.getX() && npc.getY() == hero.getY()) {
                npc.execute(hero); // deal damage
                continue;
            }

            // remove the NPC’s current image before moving
            gridPane.getChildren().remove(npc.getVisual());

            // move randomly
            npc.moveRandomly(worldGrid.length);

            // if it accidentally moves onto the exit, undo it
            if (npc.getX() == CELL_SIZE - 1 && npc.getY() == CELL_SIZE - 1) {
                npc.setX(oldX);
                npc.setY(oldY);
            }

            // update its alignment depending on move direction (for bottom-left / right visuals)
            StackPane pane = (StackPane) npc.getVisual();
            if (npc.getX() < oldX) {
                pane.setAlignment(Pos.BOTTOM_LEFT);  // moved left
            } else if (npc.getX() > oldX) {
                pane.setAlignment(Pos.BOTTOM_RIGHT); // moved right
            }

            // recheck if it's now on the hero and attack again if so
            npc.execute(hero);

            // clamp hero’s HP at 0 if it goes negative
            if (hero.getHealth() < 0) hero.setHealth(-1);

            checkHeroDeath(); // show game over if needed
            updateStats();    // refresh labels

            // redraw the npc in its new spot
            gridPane.add(pane, npc.getX(), npc.getY());
        }

        // final stats update just in case
        updateStats();
    }

    // checks if the hero is dead, and if so, triggers the game over flow
    private void checkHeroDeath() {
        if (hero.getHealth() <= 0 && !gameOver) {
            gameOver = true;  // make sure this only happens once
            if (onGameEnd != null) onGameEnd.accept(false); // false → lost
        }
    }

    /**
     * Randomly locks around `lockProb` fraction of doors.
     * Just picks a random set of doors and locks them visually + logically.
     */
    public void randomLockAllDoors(double lockProb) {
        Set<Door> uniqueDoors = new HashSet<>();

        // collect each door only once (since each is shared by 2 rooms)
        for (Room[] row : worldGrid) {
            for (Room room : row) {
                uniqueDoors.addAll(room.getDoors());
            }
        }

        Random rnd = new Random();
        for (Door d : uniqueDoors) {
            d.setLocked(rnd.nextDouble() < lockProb); // lock it based on prob
            d.updateVisual(); // make sure it shows up red if locked
        }
    }


    // this just checks if there's *any* path from start (0,0) to the exit
    // by only walking through *unlocked* doors.
    // doesn’t care about resources like keys or crowbars.
    public boolean isSolvable() {
        Room start = worldGrid[0][0];  // always start from top-left
        Room goal  = worldGrid[exitRow][exitCol];  // target is the exit

        Queue<Room> q = new LinkedList<>();  // standard BFS queue
        Set<Room> seen = new HashSet<>();    // to avoid re-visiting

        q.add(start);
        seen.add(start);

        while (!q.isEmpty()) {
            Room cur = q.poll();  // grab next room

            // found the exit — no need to continue
            if (cur == goal) return true;

            // check all neighbors connected via unlocked doors
            for (Door door : cur.getDoors()) {
                if (door.isLocked()) continue; // skip locked paths

                Room nbr = door.getOtherRoom(cur);
                if (nbr != null && seen.add(nbr)) { // add only if not visited
                    q.add(nbr);
                }
            }
        }

        // if we finish BFS and never found the goal, it's not solvable
        return false;
    }


    // this returns all the chests that the hero could actually *reach*,
    // considering their current number of keys and crowbars.
    // (so basically a smart BFS that spends resources when needed.)
    public Set<Chest> getReachableChests() {
        // each state tracks: current room, keys left, crowbars left
        record State(Room room, int keys, int crows) {}

        Room start = worldGrid[hero.getY()][hero.getX()]; // start from hero’s current position

        Queue<State> q = new LinkedList<>();
        Set<State> seen = new HashSet<>();   // we don’t want to revisit same state (room + resources)
        Set<Chest> found = new HashSet<>();  // result: chests we know we can reach

        q.add(new State(start, hero.getNumOfKeys(), hero.getNumOfCrowbars()));
        seen.add(new State(start, hero.getNumOfKeys(), hero.getNumOfCrowbars()));

        while (!q.isEmpty()) {
            State cur = q.poll();

            // if there’s a chest in this room, add it to the result
            for (Objects obj : worldGrid[cur.room.getY()][cur.room.getX()].getObjects()) {
                if (obj instanceof Chest chest) {
                    found.add(chest);
                }
            }

            // look at all neighbors through doors
            for (Door door : cur.room.getDoors()) {
                Room nbr = door.getOtherRoom(cur.room);
                if (nbr == null) continue;

                // if the door is already open, we can pass freely
                if (!door.isLocked()) {
                    State nxt = new State(nbr, cur.keys, cur.crows);
                    if (seen.add(nxt)) q.add(nxt);
                } else {
                    // try opening it with a key
                    if (cur.keys > 0) {
                        State nxt = new State(nbr, cur.keys - 1, cur.crows);
                        if (seen.add(nxt)) q.add(nxt);
                    }
                    // or with a crowbar
                    if (cur.crows > 0) {
                        State nxt = new State(nbr, cur.keys, cur.crows - 1);
                        if (seen.add(nxt)) q.add(nxt);
                    }
                }
            }
        }

        return found; // this is the list of all reachable chests
    }


    // checks if there is *any* possible way to reach the exit room,
    // considering hero’s current number of keys and crowbars.
    // basically a resource-aware BFS.
    public boolean isSolvableWithResources() {
        // each state is like a snapshot: where we are, and how much gear we have
        record State(Room room, int keys, int crows) {}

        Room start = worldGrid[hero.getY()][hero.getX()];
        Room goal  = worldGrid[exitRow][exitCol];

        Queue<State> queue = new LinkedList<>();
        Set<State> seen = new HashSet<>();  // avoid revisiting same room+inventory combo

        // start from the hero’s position with current keys and crowbars
        queue.add(new State(start, hero.getNumOfKeys(), hero.getNumOfCrowbars()));
        seen.add(new State(start, hero.getNumOfKeys(), hero.getNumOfCrowbars()));

        while (!queue.isEmpty()) {
            State cur = queue.poll();

            // found the goal — we’re done!
            if (cur.room == goal) return true;

            // try moving to all neighbors
            for (Door door : cur.room.getDoors()) {
                Room nbr = door.getOtherRoom(cur.room);
                if (nbr == null) continue;

                // if the door is already open, just go through
                if (!door.isLocked()) {
                    State next = new State(nbr, cur.keys, cur.crows);
                    if (seen.add(next)) queue.add(next);
                }
                // try using a key to unlock it
                else if (cur.keys > 0) {
                    State next = new State(nbr, cur.keys - 1, cur.crows);
                    if (seen.add(next)) queue.add(next);
                }
                // try using a crowbar instead
                else if (cur.crows > 0) {
                    State next = new State(nbr, cur.keys, cur.crows - 1);
                    if (seen.add(next)) queue.add(next);
                }
            }
        }

        // if we finish the queue without hitting the exit, it’s not solvable
        return false;
    }


    // checks if the hero is completely done
    // no tools, no reachable chests, and no path to the exit = you’re stuck.
    public boolean isCompletelyStuck() {
        // check if we still have any keys or crowbars
        if (hero.getNumOfKeys() > 0 || hero.getNumOfCrowbars() > 0) {
            return false; // still got options
        }

        // check if there's *any* chest the hero can reach
        if (!getReachableChests().isEmpty()) {
            return false; // maybe we’ll find goodies in a chest
        }

        // check if there's a clean open path to the exit (no tools needed)
        return !isSolvable(); // if not solvable with just open doors, we’re toast
    }


}






