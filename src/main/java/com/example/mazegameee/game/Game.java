package com.example.mazegameee.game;

import com.example.mazegameee.entities.World;
import com.example.mazegameee.structures.Room;
import com.example.mazegameee.structures.Door;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Game extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Initialize the game world
        World world = new World();

        // Create dummy rooms and doors (TEMP for testing)
        Room room1 = new Room(1, 1, 101, new ArrayList<>(), new ArrayList<>());
        Room room2 = new Room(2, 1, 102, new ArrayList<>(), new ArrayList<>());
        Door door = new Door(1, 2, 1, false, room1, room2);

        room1.getDoors().add(door);
        room2.getDoors().add(door);
        world.addRooms(room1);
        world.addRooms(room2);

        // Create the board UI
        Board board = new Board(world, 600, 400);

        // Set up the JavaFX scene
        StackPane root = new StackPane();
        root.getChildren().add(board);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
