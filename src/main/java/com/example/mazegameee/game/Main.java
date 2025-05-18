package com.example.mazegameee.game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Maze Game");
        runNewGame();  // launch the game for the first time
    }

    // 🔁 This handles starting a new game (and restarting after win/death)
    private void runNewGame() {
        GameUI ui = new GameUI();                     // set up visuals & gameplay
        GameController ctl = ui.getController();      // grab the controller

        // hook into the controller’s end-game logic (win/lose)
        ctl.setOnGameEnd(won -> Platform.runLater(() -> {
            ui.stopNPCMovement();  // 🛑 stop enemy movement loop

            // build the dialog asking player if they want to play again
            ButtonType playAgain = new ButtonType("Play Again", ButtonData.OK_DONE);
            ButtonType exitGame  = new ButtonType("Exit",       ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    won ? "You win! Play again?" : "You died! Play again?",
                    playAgain, exitGame
            );
            alert.setTitle(won ? "Victory" : "Game Over");
            alert.setHeaderText(null);

            // handle what they choose
            alert.showAndWait().ifPresent(choice -> {
                if (choice == playAgain) {
                    runNewGame();  // reload everything from scratch
                } else {
                    Platform.exit();     // clean close
                    System.exit(0);      // just in case
                }
            });
        }));

        // Set scene and key handling
        Scene scene = ui.getScene();
        scene.setOnKeyPressed(ui::handleKeyPress);
        primaryStage.setScene(scene);
        primaryStage.show();  // finally show the game window
    }

    public static void main(String[] args) {
        launch(args);  // classic JavaFX launcher
    }
}
