package com.pprz.pprzservices.core.model;

public class Battery {
    /* TODO: Replace with true capacity */
    private static final double BATTERY_CAPACITY = 0;
    
    private double battVolt = (double) -1.;
    private double battRemain = (double) -1.;
    private double battCurrent = (double) -1.;

    public double getBattVolt() {
        return battVolt;
    }

    public double getBattRemain() {
        return battRemain;
    }

    public double getBattCurrent() {
        return battCurrent;
    }

    public Double getBattDischarge() {
        return (1-battRemain/100.0)*BATTERY_CAPACITY;
    }

    public void setBatteryState(double battVolt, double battRemain, double battCurrent) {
        if (this.battVolt != battVolt | this.battRemain != battRemain
                | this.battCurrent != battCurrent) {
            this.battVolt = battVolt;
            this.battRemain = battRemain;
            this.battCurrent = battCurrent;
        }
    }

}
