package com.example.mazegameee.entities;

import com.example.mazegameee.behaviours.Executable;

// base class for all living beings (Hero & NPC) handles position, health, strength, and combat logic
// just like Structural Elements and Objects, it's placed on the grid, so it extends World
public class LivingBeings extends World implements Executable {
    private int health = 100;
    private int strength;

    public LivingBeings(int x, int y, int strength) {
        super(x, y);
        this.strength = strength;
    }

    // basic attack: deals 10% of strength as damage
    public void attack(LivingBeings target) {
        int damage = (int) (this.strength * 0.1);
        System.out.println(this.getClass().getSimpleName() + " attacks " +
                target.getClass().getSimpleName() + " for " + damage + " damage!");
        target.takeDamage(damage);
    }

    // subtract damage from health, and check for death
    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();
        }
    }

    // default death behavior (can be overridden if needed)
    public void die() {
        System.out.println(this.getClass().getSimpleName() + " died!");
    }

    // Executable interface method — NPCs use this to attack the Hero
    @Override
    public void execute(LivingBeings target) {
        if (target.getX() == this.x && target.getY() == this.y) {
            attack(target);
        }
    }

    // getters/setters
    public int getX() { return x; }
    public int getY() { return y; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    // subclasses: Hero and NPC
}
