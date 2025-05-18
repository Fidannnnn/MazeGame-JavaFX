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
        int dR = 0, dC = 0;
        boolean moveKey = false;

        switch (event.getCode()) {
            case W, UP    -> { dR = -1; moveKey = true; }
            case S, DOWN  -> { dR =  1; moveKey = true; }
            case A, LEFT  -> { dC = -1; moveKey = true; }
            case D, RIGHT -> { dC =  1; moveKey = true; }

            // Open chest
            case E -> {
                Chest chest = controller.getChestAt(heroCol, heroRow);
                if (chest != null) {
                    controller.tryOpenChest(chest, heroRow, heroCol);
                }
                return;
            }
            // Attack NPC
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
                // ignore other keys
                return;
            }
        }

        if (moveKey) {
            // 1) Try to unlock the door (if locked)
            controller.tryUnlockDoorInDirection(heroRow, heroCol, dR, dC);

            // 2) Then attempt to move through it
            int newRow = heroRow + dR;
            int newCol = heroCol + dC;
            if (controller.moveHero(newRow, newCol,
                    heroRow, heroCol,
                    heroImage, Pos.CENTER)) {
                heroRow = newRow;
                heroCol = newCol;
                controller.updateStats();
            }
        }
    }

    public int getHeroRow() { return heroRow; }
    public int getHeroCol() { return heroCol; }
}
