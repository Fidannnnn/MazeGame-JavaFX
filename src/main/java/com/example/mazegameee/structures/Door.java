package com.example.mazegameee.structures;

import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.StructuralElements;
import com.example.mazegameee.objects.Lock;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Door extends StructuralElements implements Activable {
    private int doorID;
    private Lock lock;
    private Room room1, room2;
    private Rectangle visual;

    public Door(int x, int y, int doorID, boolean locked, Room room1, Room room2) {
        super(x, y);
        this.doorID = doorID;
        this.lock = new Lock(x, y, doorID); // Create a lock with same ID and position
        this.lock.setLocked(locked);       // Set initial lock state
        this.room1 = room1;
        this.room2 = room2;

        if (room1 != null) room1.addDoor(this);
        if (room2 != null) room2.addDoor(this);
    }

    @Override
    public void activate() {
        if (lock.isLocked()) {
            System.out.println("Door " + doorID + " is locked!");
        } else {
            System.out.println("Door " + doorID + " is now open!");
        }
    }

    public int getDoorID() {
        return doorID;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
        if (lock != null) lock.setLockID(doorID);
    }

    public boolean isLocked() {
        return lock != null && lock.isLocked();
    }

    public void setLocked(boolean locked) {
        if (lock != null) lock.setLocked(locked);
    }

    public Lock getLock() {
        return lock;
    }

    public ArrayList<Room> getRooms() {
        return new ArrayList<>() {{
            add(room1);
            add(room2);
        }};
    }

    public Room getOtherRoom(Room current) {
        if (room1 != null && room1.equals(current)) return room2;
        if (room2 != null && room2.equals(current)) return room1;
        return null;
    }

    public void setVisual(Rectangle visual) {
        this.visual = visual;
    }

    public void updateVisual() {
        if (visual != null) {
            visual.setFill(isLocked() ? Color.DARKRED : Color.SADDLEBROWN);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
