package com.servicelib.core.drone;

import com.MAVLink.common.msg_heartbeat;
import com.model.Altitude;
import com.model.Attitude;
import com.model.Battery;
import com.model.Heartbeat;
import com.model.Position;
import com.model.Speed;
import com.model.State;
import com.model.Heartbeat.HeartbeatState;

public class Drone {

	private final State state = new State();
    
	private final DroneClient mClient;
	
	private final Attitude mAttitude = new Attitude();
	
	private final Altitude mAltitude = new Altitude();
	
	private final Speed mSpeed = new Speed();
	
	private final Heartbeat mHeartbeat = new Heartbeat();
	
	private final Battery mBattery = new Battery();
	
	private final Position mPosition = new Position();
    
	public Drone(DroneClient client) {
		this.mClient = client;
	}
	
    boolean IS_CONNECTED = false;

    public State getState() {
        return state;
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
    
    public short getBattVolt() {
    	return mBattery.getBattVolt();
    }
    
    public short getBattLevel() {
    	return mBattery.getBattLevel();
    }
    
    public short getBattCurrent() {
    	return mBattery.getBattCurrent();
    }
    
    public boolean isConnected() {
    	return IS_CONNECTED;
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
        mHeartbeat.setSysid((byte) msg.sysid);
        mHeartbeat.setCompid((byte) msg.compid);
        mHeartbeat.setMavlinkVersion((byte)msg.mavlink_version);

        switch (mHeartbeat.heartbeatState) {
            case FIRST_HEARTBEAT: {               
            	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.HEARTBEAT_FIRST);             
                break;
            }
            
            case LOST_HEARTBEAT:
                /* TODO: Handle lost heartbeat */
                break;
                
            default:
            	break;
        }

        mHeartbeat.heartbeatState = HeartbeatState.NORMAL_HEARTBEAT;
    }
    
    public void setBatteryState(short battVolt, short battLevel, short battCurrent) {
    	mBattery.setBatteryState(battVolt, battLevel, battCurrent);
    }
    
    public void setSatVisible(byte satVisible) {
    	mPosition.setSatVisible(satVisible);
    }
    
    public void setLlaHdg(int lat, int lon, int alt, short hdg) {
    	mPosition.setLlaHdg(lat, lon, alt, hdg);
    }
}
