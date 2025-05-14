package com.example.mazegameee.LivingBeings;

import com.example.mazegameee.entities.LivingBeings;

public class Hero extends LivingBeings {
    private int strength;
    private int health;
    private int numOfKeys = 5;
    private int numOfCrowbars = 0;

    public Hero(int x, int y, int strength, int health, int startKeys, int startCrowbars) {
        super(x, y, strength);
        this.health = health;
        this.numOfKeys      = startKeys;
        this.numOfCrowbars  = startCrowbars;
    }

    public int getStrength() {
        return strength;
    }

    public int getNumOfKeys() {
        return numOfKeys;
    }

    public int getNumOfCrowbars() {
        return numOfCrowbars;
    }

//    public Hero(int x, int y, int strength, int health) {
//        super(x, y, strength);
//        this.health = health;
//    }

    public void addHealth(int points) {
        this.health += points;
    }
    public void addNumOfKeys(int keys) {
        this.numOfKeys += keys;
    }
    public void addNumOfCrowbars(int crowbars) {
        this.numOfCrowbars += crowbars;
    }
}
