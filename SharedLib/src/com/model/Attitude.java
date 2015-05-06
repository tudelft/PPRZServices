package com.model;

public class Attitude {
    private double roll = (double) 0.;
    private double pitch = (double)  0.;
    private double yaw = (double) 0.;

    /**
     * The Euler angles
     * Units: radians
     */
    
    public double getRoll() {
        return roll;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setRollPitchYaw(double roll, double pitch, double yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }	
}