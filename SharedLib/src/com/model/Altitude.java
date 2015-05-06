package com.model;

public class Altitude {
    private double altitude = 0.;
    private double targetAltitude = 0.;
    private double AGL = 0.;
    
    public double getAltitude()
    {
        return altitude;
    }

    public double getTargetAltitude()
    {
        return targetAltitude;
    }
    
    public double getAGL()
    {
        return AGL;
    }

    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }

    public void setTargetAltitude(double targetAltitude)
    {
        this.targetAltitude = targetAltitude;
    }
    
    public void setAGL(double AGL)
    {
        this.AGL = AGL;
    }
}
