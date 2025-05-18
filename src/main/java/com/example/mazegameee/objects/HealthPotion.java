package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class HealthPotion extends Objects {
    private int healthPoints;

    public HealthPotion(int x, int y, int healthPoints) {
        super(x, y);              // place the potion in the chest
        this.healthPoints = healthPoints; // how much HP it gives
    }

    // how much this potion heals the hero
    public int getHealthPoints() {
        return healthPoints;
    }
}
