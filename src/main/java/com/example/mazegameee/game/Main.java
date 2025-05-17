package com.example.mazegameee.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Maze Game");
        runNewGame();
    }

    private void runNewGame() {
        GameUI ui = new GameUI();
        GameController ctl = ui.getController();

        ctl.setOnGameEnd(won -> Platform.runLater(() -> {
            // first, stop the old NPC loop:
            ui.stopNPCMovement();

            ButtonType playAgain = new ButtonType("Play Again", ButtonData.OK_DONE);
            ButtonType exitGame  = new ButtonType("Exit",       ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    won
                            ? "You win! Play again?"
                            : "You died! Play again?",
                    playAgain, exitGame
            );
            alert.setTitle(won ? "Victory" : "Game Over");
            alert.setHeaderText(null);

            alert.showAndWait().ifPresent(choice -> {
                if (choice == playAgain) {
                    runNewGame();       // start fresh
                } else {
                    Platform.exit();
                    System.exit(0);
                }
            });
        }));

        Scene scene = ui.getScene();
        scene.setOnKeyPressed(ui::handleKeyPress);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
