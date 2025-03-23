package com.example.mazegameee.structures;

import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.StructuralElements;

import java.util.ArrayList;

public class Door extends StructuralElements implements Activable {
    private int doorID;
    private boolean locked;
    private Room room1, room2;

    public Door(int x, int y, int doorID, boolean locked, Room room1, Room room2) {
        super(x, y);
        this.doorID = doorID;
        this.locked = locked;
        this.room1 = room1;
        this.room2 = room2;

        // Add this door to both rooms
        if (room1 != null) room1.addDoor(this);
        if (room2 != null) room2.addDoor(this);
    }

    @Override
    public void activate() {
        if (locked) {
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
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public ArrayList<Room> getRooms() {
        return new ArrayList<Room>() {{
            add(room1);
            add(room2);
        }};
    }

    public Room getOtherRoom(Room current) {
        if (room1 != null && room1.equals(current)) return room2;
        if (room2 != null && room2.equals(current)) return room1;
        return null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}