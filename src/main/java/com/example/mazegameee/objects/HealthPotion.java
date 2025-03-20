package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class HealthPotion extends Objects {
    private int healthPoints;
    public HealthPotion(int x, int y, int healthPoints) {
        super(x, y);
        this.healthPoints = healthPoints;
    }

    public int getHealthPoints() {
        return healthPoints;
    }
}
