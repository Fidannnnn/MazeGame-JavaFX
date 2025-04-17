package com.example.mazegameee.objects;

import com.example.mazegameee.entities.Objects;

public class Lock extends Objects {
    private boolean locked;
    private int lockID;

    public Lock(int x, int y, int lockID) {
        super(x, y);
        this.locked = true;  // Default to locked
        this.lockID = lockID;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getLockID() {
        return lockID;
    }

    public void setLockID(int lockID) {
        this.lockID = lockID;
    }
}
