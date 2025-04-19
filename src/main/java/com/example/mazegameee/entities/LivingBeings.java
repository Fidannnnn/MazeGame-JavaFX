package com.example.mazegameee.entities;

import com.example.mazegameee.behaviours.Executable;

public class LivingBeings extends World implements Executable {
    private int health = 100;
    private int strength;
    // add elements like crowbars, keys they might have
    public LivingBeings(int x, int y, int strength) {
        super(x,y);
        this.strength = strength;
    }

    public void attack(LivingBeings target) {
        int damage = (int) (this.strength * 0.1);
        System.out.println(this.getClass().getSimpleName() + " attacks " +
                target.getClass().getSimpleName() + " for " + damage + " damage!");
        target.takeDamage(damage);
    }


    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();
        }
    }

    public void die() {
        System.out.println(this.getClass().getSimpleName() + " died!");
    }


    @Override
    public void execute(LivingBeings target) {
        if (target.getX() == this.x && target.getY() == this.y) {
            attack(target);  // NPC attacks hero
        }
    }


    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }
    // attack()
    // hero npc classes
}
