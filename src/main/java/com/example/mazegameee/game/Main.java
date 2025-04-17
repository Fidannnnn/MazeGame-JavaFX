package com.example.mazegameee.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private GameUI gameUI;

    @Override
    public void start(Stage primaryStage) {
        try {
            GameUI gameUI = new GameUI();
            Scene scene = gameUI.getScene();

            primaryStage.setTitle("Maze Game");
            primaryStage.setScene(scene);
            primaryStage.show();

            scene.setOnKeyPressed(gameUI::handleKeyPress);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 will show real cause in terminal
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
