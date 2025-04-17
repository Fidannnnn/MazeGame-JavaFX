package com.example.mazegameee.structures;

import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.entities.StructuralElements;

import java.util.ArrayList;

public class Room extends StructuralElements{
    private int roomID;
    private ArrayList<Objects> objects;
    private ArrayList<Door> doors;

    public Room(int x, int y, int roomID, ArrayList<Objects> objects, ArrayList<Door> doors) {
        super(x, y);
        this.roomID = roomID;
        this.objects = objects;
        this.doors = (doors != null) ? doors : new ArrayList<>();
    }



    public ArrayList<Objects> getObjects() {
        return objects;
    }

    public void addObject(Objects object) {
        if (this.objects == null) {
            this.objects = new ArrayList<>();
        }
        this.objects.add(object);
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public ArrayList<Door> getDoors() {
        return doors;
    }

    public void setDoors(ArrayList<Door> doors) {
        this.doors = doors;
    }

    public void addDoor(Door door) {
        if (!doors.contains(door)) {
            doors.add(door);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isConnectedTo(Room other) {
        for (Door door : doors) {
            if (door.getOtherRoom(this) != null && door.getOtherRoom(this).equals(other) && !door.isLocked()) {
                return true;
            }
        }
        return false;
    }
}
