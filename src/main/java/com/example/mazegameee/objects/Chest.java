package com.example.mazegameee.objects;

import com.example.mazegameee.LivingBeings.Hero;
import com.example.mazegameee.behaviours.Activable;
import com.example.mazegameee.entities.Objects;

import java.util.*;

public class Chest extends Objects implements Activable {
    private Lock lock;
    private List<Objects> items;

    public Chest(int x, int y, boolean locked) {
        super(x, y);

        // every chest has its own lock with a random ID
        this.lock = new Lock(x, y, new Random().nextInt(10000));
        this.lock.setLocked(locked);

        // we fill the chest with random items right away
        this.items = new ArrayList<>();
        populateChest();
    }

    private void populateChest() {
        Random random = new Random();

        int numKeys      = random.nextInt(3);     // 0 to 2 keys
        int numCrowbars  = random.nextInt(3);     // 0 to 2 crowbars
        int healthPoints = random.nextInt(25);    // 0 to 24 hp

        // put in keys
        for (int i = 0; i < numKeys; i++) {
            items.add(new Key(x, y));
        }

        // then crowbars
        for (int i = 0; i < numCrowbars; i++) {
            items.add(new Crowbar(x, y));
        }

        // and maybe some health
        if (healthPoints > 0) {
            items.add(new HealthPotion(x, y, healthPoints));
        }

        // just to be safe: chest should never be empty
        if (items.isEmpty()) {
            items.add(new Crowbar(x, y));
        }
    }

    // tries to open the chest using a key or crowbar
    public boolean unlock(Hero hero) {
        if (lock.isLocked()) {
            if (hero.getNumOfKeys() > 0) {
                hero.addNumOfKeys(-1);
                Key.openLock(lock);
                System.out.println("You used a key! The chest is now unlocked.");
            } else if (hero.getNumOfCrowbars() > 0) {
                hero.addNumOfCrowbars(-1);
                Crowbar.breakLock(lock);
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

    // returns the list of items inside the chest
    public List<Objects> getItems() {
        return items;
    }

    // just used for generic activation calls
    @Override
    public void activate() {
        if (lock.isLocked()) {
            System.out.println("Chest is locked!");
        } else {
            System.out.println("Chest is now open!");
        }
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }
}
