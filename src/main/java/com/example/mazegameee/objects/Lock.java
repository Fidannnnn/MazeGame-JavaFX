package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class Lock extends Objects {
    private boolean locked;
    private int lockID;

    public Lock(int x, int y, int lockID) {
        super(x, y);       // it has the position of object it is on
        this.locked = true; // starts locked by default
        this.lockID = lockID; // can be used if we want to match specific keys later
    }

    // check if it's locked
    public boolean isLocked() {
        return locked;
    }

    // lock/unlock it
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    // optional ID in case we do custom key-lock matching
    public int getLockID() {
        return lockID;
    }

    public void setLockID(int lockID) {
        this.lockID = lockID;
    }
}
