package com.example.mazegameee.LivingBeings;

import com.example.mazegameee.entities.LivingBeings;
import javafx.scene.layout.StackPane;

import java.util.Random;

public class Npc extends LivingBeings {

    private Random random = new Random();


    public Npc(int x, int y, int strength) {
        super(x, y, strength);
    }



    public void moveRandomly(int gridSize) {
        int dx = random.nextInt(3) - 1;
        int dy = random.nextInt(3) - 1;

        int newX = this.getX() + dx;
        int newY = this.getY() + dy;

        if (newX >= 0 && newX < gridSize && newY >= 0 && newY < gridSize) {
            this.x = newX;
            this.y = newY;
        }
    }

    private transient StackPane visual; // Don't serialize in future, UI only

    public void setVisual(StackPane visual) {
        this.visual = visual;
    }

    public StackPane getVisual() {
        return visual;
    }

}
