package com.pprz.pprzservices.core.model;

public class Altitude {
    private double altitude = 0.;
    private double targetAltitude = 0.;
    
    public double getAltitude()
    {
        return altitude;
    }

    public double getTargetAltitude()
    {
        return targetAltitude;
    }

    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }

    public void setTargetAltitude(double targetAltitude)
    {
        this.targetAltitude = targetAltitude;
    }

}
