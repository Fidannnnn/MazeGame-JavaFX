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
    }

    @Override
    public void activate() {
        if (locked) {
            System.out.println(doorID + " is locked!");
        } else {
            System.out.println(doorID + " is now open!");
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
        return new ArrayList<Room>(){{add(room1);add(room2);}};
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
