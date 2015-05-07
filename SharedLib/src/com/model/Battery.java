package com.model;

public class Battery {
    /* TODO: Replace with true capacity */
    private static final double BATTERY_CAPACITY = 0;
    
    private int battVolt = -1; // in millivolts
    private int battLevel = -1;
    private int battCurrent = -1;

    public int getBattVolt() {
        return battVolt;
    }

    public int getBattLevel() {
        return battLevel;
    }

    public int getBattCurrent() {
        return battCurrent;
    }

    public double getBattDischarge() {
        return (1-battLevel/100.0)*BATTERY_CAPACITY;
    }

    public void setBatteryState(int battVolt, int battLevel, int battCurrent) {
        if (this.battVolt != battVolt | this.battLevel != battLevel 
        		| this.battCurrent != battCurrent) {
            this.battVolt = battVolt;
            this.battLevel = battLevel;
            this.battCurrent = battCurrent;
        }
    }

}
