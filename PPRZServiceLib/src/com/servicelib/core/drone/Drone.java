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
	
	private final Attitude attitude = new Attitude();
	
	private final Altitude altitude = new Altitude();
	
	private final Speed speed = new Speed();
	
	private final Heartbeat heartbeat = new Heartbeat();
	
	private final Battery battery = new Battery();
	
	private final Position position = new Position();
    
	public Drone(DroneClient client) {
		this.mClient = client;
	}
	
    boolean IS_CONNECTED = false;

    public State getState() {
        return state;
    }
    
    public double getAltitude() {
    	return this.altitude.getAltitude();
    }
    
    public double getTargetAltitude() {
    	return this.altitude.getTargetAltitude();
    }
    
    public double getRoll() {
    	return this.attitude.getRoll();
    }
    
    public double getPitch() {
    	return this.attitude.getPitch();
    }
    
    public double getYaw() {
    	return this.attitude.getYaw();
    }
    
    public double getGroundSpeed() {
    	return this.speed.getGroundSpeed();
    }
    
    public double getAirSpeed() {
    	return this.speed.getAirSpeed();
    }
    
    public double getClimbSpeed() {
    	return this.speed.getClimbSpeed();
    }
    
    public double getTargetSpeed() {
    	return this.speed.getTargetSpeed();
    }
    
    public byte getSysid() {
        return this.heartbeat.getSysid();
    }

    public byte getCompid() {
        return this.heartbeat.getCompid();
    }
    
    public int getBattVolt() {
    	return this.battery.getBattVolt();
    }
    
    public int getBattLevel() {
    	return this.battery.getBattLevel();
    }
    
    public int getBattCurrent() {
    	return this.battery.getBattCurrent();
    }
    
	public byte getSatVisible() {
		return this.position.getSatVisible();
	}
	
	public int getTimeStamp() {
		return this.position.getTimeStamp();
	}
	
	public int getLat() {
		return this.position.getLat();
	}
	
	public int getLon() {
		return this.position.getLon();
	}
	
	public int getAlt() {
		return this.position.getAlt();
	}
	
	public int getHdg() {
		return this.position.getHdg();
	}
    
    public boolean isConnected() {
    	return IS_CONNECTED;
    }
    
    public void setRollPitchYaw(double roll, double pitch, double yaw) {
    	this.attitude.setRollPitchYaw(roll, pitch, yaw);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.ATTITUDE_UPDATED);
    }
    
    public void setAltitudeGroundAndAirSpeeds(double altitude, double groundSpeed, double airSpeed, double climbSpeed) {
    	this.altitude.setAltitude(altitude);
    	this.speed.setGroundAndAirSpeeds(groundSpeed, airSpeed, climbSpeed);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.ALTITUDE_SPEED_UPDATED);
    }
    
    public void onHeartbeat(msg_heartbeat msg) {
        this.heartbeat.setSysid((byte) msg.sysid);
        this.heartbeat.setCompid((byte) msg.compid);
        this.heartbeat.setMavlinkVersion((byte)msg.mavlink_version);

        switch (this.heartbeat.heartbeatState) {
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

        this.heartbeat.heartbeatState = HeartbeatState.NORMAL_HEARTBEAT;
    }
    
    public void setBatteryState(int battVolt, int battLevel, int battCurrent) {
    	this.battery.setBatteryState(battVolt, battLevel, battCurrent);       
        mClient.onDroneEvent(DroneInterfaces.DroneEventsType.BATTERY_UPDATED);
    }
    
    public void setSatVisible(byte satVisible) {
    	this.position.setSatVisible(satVisible);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.SATELLITES_VISIBLE_UPDATED);
    }
    
    public void setLlaHdg(int lat, int lon, int alt, short hdg) {
    	this.position.setLlaHdg(lat, lon, alt, hdg);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.POSITION_UPDATED);
    }
}
