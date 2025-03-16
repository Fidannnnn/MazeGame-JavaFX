package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class Crowbar extends Objects {
    public Crowbar(int x, int y) {
        super(x, y);
    }

    public void breakLock(Lock lock){
        lock.setLocked(false);
    }
    // make it so it breaks only doors
}
