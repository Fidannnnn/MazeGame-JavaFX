package com.example.mazegameee.game;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.LivingBeings.Npc;
import com.example.mazegameee.objects.Chest;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyHandler {
    private final Hero hero;
    private int heroRow;
    private int heroCol;
    private final ImageView heroImage;
    private final GameController controller;
    private final int cellSize;

    public KeyHandler(Hero hero, int heroRow, int heroCol, ImageView heroImage, GameController controller, int cellSize) {
        this.hero = hero;
        this.heroRow = heroRow;
        this.heroCol = heroCol;
        this.heroImage = heroImage;
        this.controller = controller;
        this.cellSize = cellSize;
    }

    public void handle(KeyEvent event, boolean isHeroOnRight) {
        switch (event.getCode()) {
            case UP, DOWN -> {
                int newRow = heroRow + (event.getCode() == KeyCode.UP ? -1 : 1);
                Pos alignment = isHeroOnRight ? Pos.BOTTOM_RIGHT : Pos.BOTTOM_LEFT;
                if (controller.moveHero(newRow, heroCol, heroRow, heroCol, heroImage, alignment)) {
                    heroRow = newRow;
                }
            }
            case LEFT -> {
                int newCol = heroCol - 1;
                if (controller.moveHero(heroRow, newCol, heroRow, heroCol, heroImage, Pos.BOTTOM_LEFT)) {
                    heroCol = newCol;
                }
            }
            case RIGHT -> {
                int newCol = heroCol + 1;
                if (controller.moveHero(heroRow, newCol, heroRow, heroCol, heroImage, Pos.BOTTOM_RIGHT)) {
                    heroCol = newCol;
                }
            }
            case W -> controller.tryUnlockDoorInDirection(heroRow, heroCol, -1, 0);
            case A -> controller.tryUnlockDoorInDirection(heroRow, heroCol, 0, -1);
            case S -> controller.tryUnlockDoorInDirection(heroRow, heroCol, 1, 0);
            case D -> controller.tryUnlockDoorInDirection(heroRow, heroCol, 0, 1);
            case E -> {
                Chest chest = controller.getChestAt(heroCol, heroRow);
                if (chest != null) {
                    controller.tryOpenChest(chest, heroRow, heroCol);
                }
            }
            case F -> {
                Npc target = controller.getNpcAt(hero.getX(), hero.getY());
                if (target != null) {
                    hero.attack(target);
                    controller.updateStats();
                    if (target.getHealth() <= 0) {
                        controller.removeNpc(target);
                    }
                }

            }

        }
    }

    public int getHeroRow() {
        return heroRow;
    }

    public int getHeroCol() {
        return heroCol;
    }
}
