package com.example.mazegameee.entities;

public class World {
    // basic x,y coordinates for anything in the game grid (hero, npc, chest, etc.)
    public int x;
    public int y;

    public World(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // basic getters/setters to update or fetch position
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
