package com.example.mazegameee.structures;

import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.entities.StructuralElements;

import java.util.ArrayList;

public class Room extends StructuralElements {
    private int roomID;
    private ArrayList<Objects> objects; // stuff like chests in the room
    private ArrayList<Door> doors;      // doors connected to this room

    public Room(int x, int y, int roomID, ArrayList<Objects> objects, ArrayList<Door> doors) {
        super(x, y);
        this.roomID = roomID;
        this.objects = objects;
        this.doors = (doors != null) ? doors : new ArrayList<>();
    }

    // get the stuff placed in this room
    public ArrayList<Objects> getObjects() {
        return objects;
    }

    // add a new item to this room (like a chest)
    public void addObject(Objects object) {
        if (this.objects == null) {
            this.objects = new ArrayList<>();
        }
        this.objects.add(object);
    }

    // room ID is optional but can help with debugging or labeling
    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    // get all doors this room is connected to
    public ArrayList<Door> getDoors() {
        return doors;
    }

    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    // make sure we don’t add the same door twice
    public void addDoor(Door door) {
        if (!doors.contains(door)) {
            doors.add(door);
        }
    }

    // x and y just for easy access
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // check if this room can reach another through an *unlocked* door
    public boolean isConnectedTo(Room other) {
        for (Door door : doors) {
            if (door.getOtherRoom(this) != null &&
                    door.getOtherRoom(this).equals(other) &&
                    !door.isLocked()) {
                return true;
            }
        }
        return false;
    }
}
