package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.objects.Chest;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;

public class KeyHandler {
    private final Hero hero;
    private int heroRow, heroCol;
    private final ImageView heroImage;
    private final GameController controller;
    private final int cellSize;

    public KeyHandler(Hero hero, int heroRow, int heroCol, ImageView heroImage, GameController controller, int cellSize) {
        this.hero       = hero;
        this.heroRow    = heroRow;
        this.heroCol    = heroCol;
        this.heroImage  = heroImage;
        this.controller = controller;
        this.cellSize   = cellSize;
    }

    public void handle(KeyEvent event) {
        int dR = 0, dC = 0;        // movement direction (row/col offset)
        boolean moveKey = false;  // did the player press a movement key?

        switch (event.getCode()) {
            case W, UP    -> { dR = -1; moveKey = true; }  // go up
            case S, DOWN  -> { dR =  1; moveKey = true; }  // go down
            case A, LEFT  -> { dC = -1; moveKey = true; }  // go left
            case D, RIGHT -> { dC =  1; moveKey = true; }  // go right

            // E → open chest at your feet (if one exists)
            case E -> {
                Chest chest = controller.getChestAt(heroCol, heroRow);
                if (chest != null) {
                    controller.tryOpenChest(chest, heroRow, heroCol);
                }
                return;
            }

            // F → attack NPC in same room
            case F -> {
                Npc target = controller.getNpcAt(hero.getX(), hero.getY());
                if (target != null) {
                    hero.attack(target);
                    controller.updateStats();
                    if (target.getHealth() <= 0) {
                        controller.removeNpc(target);
                    }
                }
                return;
            }

            default -> {
                // ignore everything else
                return;
            }
        }

        if (moveKey) {
            // 1️⃣ First, try unlocking the door in that direction (if needed)
            controller.tryUnlockDoorInDirection(heroRow, heroCol, dR, dC);

            // 2️⃣ Then try to walk through the door (if it's now open)
            int newRow = heroRow + dR;
            int newCol = heroCol + dC;
            if (controller.moveHero(newRow, newCol, heroRow, heroCol, heroImage, Pos.CENTER)) {
                heroRow = newRow;
                heroCol = newCol;
                controller.updateStats();  // update health, keys, etc.
            }
        }
    }

    public int getHeroRow() { return heroRow; }
    public int getHeroCol() { return heroCol; }
}
