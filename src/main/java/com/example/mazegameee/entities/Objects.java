package com.example.mazegameee.entities;

// base class for all non-living items like keys, crowbars, potions, chests, etc.
// just like Structural Elements and LivingBeings, it's placed on the grid, so it extends World
public class Objects extends World {
    public Objects(int x, int y) {
        super(x, y);  // they still need a position on the grid
    }
}
