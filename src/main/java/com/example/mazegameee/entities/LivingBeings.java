package com.example.mazegameee.entities;

import com.example.mazegameee.behaviours.Executable;

public class LivingBeings extends GameElements implements Executable {
    private int health = 100;
    private int strength;
    // add elements like crowbars, keys they might have
    public LivingBeings(int strength, int x, int y) {
        super(x,y);
        this.strength = strength;
    }

    @Override
    public void execute() {

    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
