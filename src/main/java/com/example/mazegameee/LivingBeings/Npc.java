package com.example.mazegameee.LivingBeings;

import com.example.mazegameee.entities.LivingBeings;
import javafx.scene.layout.StackPane;

import java.util.Random;

public class Npc extends LivingBeings {

    // used to randomize movement
    private Random random = new Random();

    public Npc(int x, int y, int strength) {
        super(x, y, strength); // inherit position and strength
    }

    // Random movement inside the grid
    public void moveRandomly(int gridSize) {
        int dx = random.nextInt(3) - 1; // -1, 0, or 1
        int dy = random.nextInt(3) - 1;

        int newX = this.getX() + dx;
        int newY = this.getY() + dy;

        // only move if the new position is inside the grid
        if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
            this.x = newX;
            this.y = newY;
        }
    }

    // this is the NPC’s visual (the thing that actually shows up on the screen)
    // we store it inside the NPC itself so we can move/update it easily
    // it’s marked `transient` cuz visuals shouldn’t be saved if we ever serialize the NPC
    // (they only exist while the game is running)
    private transient StackPane visual;

    public void setVisual(StackPane visual) {
        this.visual = visual;
    }

    public StackPane getVisual() {
        return visual;
    }

}
