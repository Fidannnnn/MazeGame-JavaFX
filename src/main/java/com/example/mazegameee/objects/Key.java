package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class Key extends Objects {
    public Key(int x, int y) {
        super(x, y);
    }
    public static void openLock(Lock lock){
        lock.setLocked(false);
    }
}
