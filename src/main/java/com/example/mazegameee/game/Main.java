package com.example.mazegameee.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int GRID_SIZE = 10; // 10x10 maze
    private GameUI gameUI;

    @Override
    public void start(Stage primaryStage) {
        gameUI = new GameUI(GRID_SIZE);
        Scene scene = gameUI.getScene(); // ✅ use full layout with stats panel

        primaryStage.setTitle("Maze Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> gameUI.handleKeyPress(event));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
