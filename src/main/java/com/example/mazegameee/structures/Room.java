package com.example.mazegameee.structures;

import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.Objects;
import com.example.mazegameee.entities.StructuralElements;

import java.util.ArrayList;

public class Room extends StructuralElements implements Activable {
    private int roomID;
    private ArrayList<Objects> objects;
    private ArrayList<Door> doors;
    private boolean locked;

    public Room(int x, int y, int roomID, ArrayList<Objects> objects, ArrayList<Door> doors) {
        super(x, y);
        this.roomID = roomID;
        this.objects = objects;
        this.doors = doors;
    }

    @Override
    public void activate() {
        if (locked) {
            System.out.println(roomID + " is locked!");
        } else {
            System.out.println(roomID + " is now open!");
        }
    }

    public ArrayList<Objects> getObjects() {
        return objects;
    }

    public void addGameElements(ArrayList<Objects> objects) {
        this.objects = objects;
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

    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
