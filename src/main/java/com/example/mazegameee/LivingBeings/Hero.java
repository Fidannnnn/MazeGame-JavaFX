package com.example.mazegameee.LivingBeings;

import com.example.mazegameee.entities.LivingBeings;

public class Hero extends LivingBeings {
    private int health;

    // inventory — keys and crowbars (both can be used to unlock stuff)
    private int numOfKeys;
    private int numOfCrowbars;

    // constructor that initializes everything — including keys/crowbars
    public Hero(int x, int y, int strength, int health, int startKeys, int startCrowbars) {
        super(x, y, strength);
        this.health         = health;
        this.numOfKeys      = startKeys;
        this.numOfCrowbars  = startCrowbars;
    }

    public int getNumOfKeys()      { return numOfKeys; }
    public int getNumOfCrowbars()  { return numOfCrowbars; }

    // healing logic — used when player picks up potions
    public void addHealth(int points) {
        this.health += points;
    }

    // used when picking up keys or spending them
    public void addNumOfKeys(int keys) {
        this.numOfKeys += keys;
    }

    public void addNumOfCrowbars(int crowbars) {
        this.numOfCrowbars += crowbars;
    }
}
