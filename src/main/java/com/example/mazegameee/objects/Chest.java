package com.example.mazegameee.objects;

import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.Objects;

public class Chest extends Objects implements Activable {
    private boolean locked;

    public Chest(int x, int y) {
        super(x, y);
    }

    @Override
    public void activate() {
        if (locked) {
            System.out.println("chest is locked!");
        } else {
            // add what a chest might have
            System.out.println("chest is now open!");
        }
    }
}
