package com.model;

public class Speed {
    private double groundSpeed = (double) 0.;
    private double airSpeed = (double) 0.;
    private double climbSpeed = (double) 0.;
    private double targetSpeed = (double) 0.;

    public double getGroundSpeed() {
        return groundSpeed;
    }

    public double getAirSpeed() {
        return airSpeed;
    }
    
    public double getClimbSpeed() {
        return climbSpeed;
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public void setGroundAndAirSpeeds(double groundSpeed, double airSpeed, double climbSpeed) {
        this.groundSpeed = groundSpeed;
        this.airSpeed = airSpeed;
        this.climbSpeed = climbSpeed;
    }
    
    public void setTargetSpeed(double targetSpeed) {
    	this.targetSpeed = targetSpeed;
    }

}