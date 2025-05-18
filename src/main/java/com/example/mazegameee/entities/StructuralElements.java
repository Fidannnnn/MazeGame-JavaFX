package com.example.mazegameee.entities;

// this is the base class for structural things like Doors or Rooms
// just like Objects and LivingBeings, it's placed on the grid, so it extends World
public class StructuralElements extends World {
    public StructuralElements(int x, int y) {
        super(x, y);  // inherits x and y from World
    }
}
