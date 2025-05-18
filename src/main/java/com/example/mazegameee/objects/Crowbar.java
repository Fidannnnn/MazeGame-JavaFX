package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class Crowbar extends Objects {

    public Crowbar(int x, int y) {
        super(x, y);  // place it in the chest like any other item
    }

    // brute force the lock open
    public static void breakLock(Lock lock) {
        lock.setLocked(false);
    }
}
