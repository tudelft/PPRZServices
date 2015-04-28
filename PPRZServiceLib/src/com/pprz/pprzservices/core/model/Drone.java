package com.pprz.pprzservices.core.model;

import com.MAVLink.common.msg_heartbeat;
import com.pprz.pprzservices.core.drone.DroneClient;
import com.pprz.pprzservices.core.drone.DroneInterfaces;

public class Drone {

	private final State state = new State();
    
	private final DroneClient mClient;
	
	private final Attitude mAttitude = new Attitude();
	
	private final Altitude mAltitude = new Altitude();
	
	private final Speed mSpeed = new Speed();
	
	private final Heartbeat mHeartbeat = new Heartbeat();
    
	public Drone(DroneClient client) {
		this.mClient = client;
	}
	
    boolean IS_CONNECTED = false;

    public State getState() {
        return state;
    }

    public void setRollPitchYaw(double roll, double pitch, double yaw) {
    	mAttitude.setRollPitchYaw(roll, pitch, yaw);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.ATTITUDE_UPDATED);
    }
    
    public void setAltitudeGroundAndAirSpeeds(double altitude, double groundSpeed, double airSpeed, double climbSpeed) {
    	mAltitude.setAltitude(altitude);
    	mSpeed.setGroundAndAirSpeeds(groundSpeed, airSpeed, climbSpeed);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.ALTITUDE_SPEED_UPDATED);
    }
    
    public void onHeartbeat(msg_heartbeat msg) {
        mHeartbeat.onHeartbeat(msg, mClient);
    }
    
    public double getAltitude() {
    	return mAltitude.getAltitude();
    }
    
    public double getTargetAltitude() {
    	return mAltitude.getTargetAltitude();
    }
    
    public double getRoll() {
    	return mAttitude.getRoll();
    }
    
    public double getPitch() {
    	return mAttitude.getPitch();
    }
    
    public double getYaw() {
    	return mAttitude.getYaw();
    }
    
    public double getGroundSpeed() {
    	return mSpeed.getGroundSpeed();
    }
    
    public double getAirSpeed() {
    	return mSpeed.getAirSpeed();
    }
    
    public double getClimbSpeed() {
    	return mSpeed.getClimbSpeed();
    }
    
    public double getTargetSpeed() {
    	return mSpeed.getTargetSpeed();
    }
    
    public boolean isConnected() {
    	return IS_CONNECTED;
    }
}
