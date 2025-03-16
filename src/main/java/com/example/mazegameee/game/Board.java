package com.example.mazegameee.game;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.mazegameee.structures.Room;
import com.example.mazegameee.structures.Door;
import com.example.mazegameee.entities.LivingBeings;
import com.example.mazegameee.entities.World;

public class Board extends Canvas {
    private World world;

    public Board(World world, int width, int height) {
        super(width, height);
        this.world = world;
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Draw rooms
        gc.setFill(Color.GRAY);
        for (Room room : world.getRooms()) {
            gc.fillRect(room.getX() * 50, room.getY() * 50, 50, 50);
        }

        // Draw doors
        gc.setFill(Color.BROWN);
        for (Room room : world.getRooms()) {
            for (Door door : room.getDoors()) {
                gc.fillRect(door.getX() * 50, door.getY() * 50, 10, 50);
            }
        }

        // Draw living beings
        gc.setFill(Color.RED);
        for (LivingBeings being : world.getLivingBeings()) {
            gc.fillOval(being.getX() * 50, being.getY() * 50, 20, 20);
        }
    }
}
