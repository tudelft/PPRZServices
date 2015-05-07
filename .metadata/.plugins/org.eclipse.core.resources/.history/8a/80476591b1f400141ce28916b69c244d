package com.model;

public class Battery {
    /* TODO: Replace with true capacity */
    private static final double BATTERY_CAPACITY = 0;
    
    private short battVolt = -1; // in millivolts
    private short battLevel = -1;
    private short battCurrent = -1;

    public short getBattVolt() {
        return battVolt;
    }

    public short getBattLevel() {
        return battLevel;
    }

    public short getBattCurrent() {
        return battCurrent;
    }

    public double getBattDischarge() {
        return (1-battLevel/100.0)*BATTERY_CAPACITY;
    }

    public void setBatteryState(short battVolt, short battLevel, short battCurrent) {
        if (this.battVolt != battVolt | this.battLevel != battLevel 
        		| this.battCurrent != battCurrent) {
            this.battVolt = battVolt;
            this.battLevel = battLevel;
            this.battCurrent = battCurrent;
        }
    }

}
