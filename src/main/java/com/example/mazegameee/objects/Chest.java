package com.example.mazegameee.objects;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.Objects;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.*;

public class Chest extends Objects implements Activable {
    private boolean locked;
    private List<Objects> items; // Items inside the chest

    public Chest(int x, int y, boolean locked) {
        super(x, y);
        this.locked = locked;
        this.items = new ArrayList<>(); // Initialize before calling populateChest
        populateChest();
    }

    private void populateChest() {
        Random random = new Random();

        int numKeys = random.nextInt(3); // 0, 1, or 2 keys
        int numCrowbars = random.nextInt(3); // 0, 1, or 2 crowbars
        int healthPoints = random.nextInt(25); // 0 to 24 health points

        // Add keys
        for (int i = 0; i < numKeys; i++) {
            items.add(new Key(x,y));
        }

        // Add crowbars
        for (int i = 0; i < numCrowbars; i++) {
            items.add(new Crowbar(x,y));
        }

        // Add health potion if healthPoints > 0
        if (healthPoints > 0) {
            items.add(new HealthPotion(x,y,healthPoints));
        }

        // Ensure the chest has at least one item
        if (items.isEmpty()) {
            items.add(new Crowbar(x,y)); // Default item if empty
        }
    }

    public boolean unlock(Hero hero) {
        if (locked) {
            if (hero.getNumOfKeys() > 0) {
                hero.addNumOfKeys(-1);
                locked = false;
                System.out.println("You used a key! The chest is now unlocked.");
            } else if (hero.getNumOfCrowbars() > 0) {
                hero.addNumOfCrowbars(-1);
                locked = false;
                System.out.println("You used a crowbar! The chest is now unlocked.");
            } else {
                System.out.println("You need a key or crowbar to open this chest!");
                return false;
            }
        } else {
            System.out.println("Chest is already unlocked!");
        }
        return true;
    }


    public List<Objects> getItems() {
        return items;
    }

    @Override
    public void activate() {
        if (locked) {
            System.out.println("chest is locked!");
        } else {
            // add what a chest might have
            System.out.println("chest is now open!");
        }
    }
}
