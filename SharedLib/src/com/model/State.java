package com.model;

public class State {
    private boolean armed = false;
    private boolean isFlying = false;

    public boolean isArmed() {
        return armed;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setIsFlying(boolean newState) {
        if (this.isFlying != newState) {
            this.isFlying = newState;
        }
    }

    public void setArmed(boolean newState) {
        if (this.armed != newState) {
            this.armed = newState;
        }
    }
}