package com.example.mazegameee.entities;

import com.example.mazegameee.structures.Room;

import java.util.ArrayList;

public class World {
    // so our world has living beings and structure
    // the structure is rooms but also doors???
    // every door has 2 rooms
    // lets make world a list of live beings and list of rooms

    private ArrayList<LivingBeings> livingBeings;
    private ArrayList<Room> rooms;

    public World() {
        this.livingBeings = new ArrayList<>();
        this.rooms = new ArrayList<>();
    }

    public ArrayList<LivingBeings> getLivingBeings() {
        return livingBeings;
    }

    public void addLivingBeings(LivingBeings livingBeing) {
        livingBeings.add(livingBeing);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void addRooms(Room room) {
        rooms.add(room);
    }

}
