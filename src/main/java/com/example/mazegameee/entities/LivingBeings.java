package com.example.mazegameee.entities;

import com.example.mazegameee.behaviours.Executable;

public class LivingBeings extends GameElements implements Executable {
    private int health = 100;
    private int strength;
    // add elements like crowbars, keys they might have
    public LivingBeings(int x, int y, int strength) {
        super(x,y);
        this.strength = strength;
    }

    public void attack(LivingBeings target) {
        target.takeDamage(this.strength);
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            die();
        }
    }

    public void die(){
        System.out.println("dead");
    };


    @Override
    public void execute() {

    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    // attack()
    // hero npc classes
}
