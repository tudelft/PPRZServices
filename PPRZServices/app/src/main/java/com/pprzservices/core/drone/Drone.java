package com.pprzservices.core.drone;

import com.MAVLink.common.msg_heartbeat;
import com.sharedlib.model.Altitude;
import com.sharedlib.model.Attitude;
import com.sharedlib.model.Battery;
import com.sharedlib.model.Heartbeat;
import com.sharedlib.model.Position;
import com.sharedlib.model.Speed;
import com.sharedlib.model.State;
import com.sharedlib.model.Heartbeat.HeartbeatState;

public class Drone {

    private int mTime = 0; // milliseconds

    boolean IS_CONNECTED = false;

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

    public int getTime() { return mTime; }

    public boolean isConnected() {
        return IS_CONNECTED;
    }

    public State getState() {
        return state;
    }
    
    public double getAltitude() {
    	return mAltitude.getAltitude();
    }
    
    public double getTargetAltitude() {
    	return mAltitude.getTargetAltitude();
    }

    public double getAGL() { return mAltitude.getAGL(); }
    
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
    
    public byte getSysid() {
        return mHeartbeat.getSysid();
    }

    public byte getCompid() {
        return mHeartbeat.getCompid();
    }
    
    public int getBattVolt() {
    	return mBattery.getBattVolt();
    }
    
    public int getBattLevel() {
    	return mBattery.getBattLevel();
    }
    
    public int getBattCurrent() {
    	return mBattery.getBattCurrent();
    }
    
	public byte getSatVisible() {
		return mPosition.getSatVisible();
	}
	
	public int getTimeStamp() {
		return mPosition.getTimeStamp();
	}
	
	public int getLat() {
		return mPosition.getLat();
	}
	
	public int getLon() {
		return mPosition.getLon();
	}
	
	public int getAlt() {
		return mPosition.getAlt();
	}
	
	public int getHdg() {
		return mPosition.getHdg();
	}

    public void setTime(int time) {
        mTime = time;
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
    
    public void setBatteryState(int battVolt, int battLevel, int battCurrent) {
    	mBattery.setBatteryState(battVolt, battLevel, battCurrent);
        mClient.onDroneEvent(DroneInterfaces.DroneEventsType.BATTERY_UPDATED);
    }
    
    public void setSatVisible(byte satVisible) {
    	mPosition.setSatVisible(satVisible);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.SATELLITES_VISIBLE_UPDATED);
    }
    
    public void setLlaHdg(int lat, int lon, int alt, int AGL, short hdg) {
        mAltitude.setAGL(AGL/1000.);
    	mPosition.setLlaHdg(lat, lon, alt, hdg);
    	mClient.onDroneEvent(DroneInterfaces.DroneEventsType.POSITION_UPDATED);
    }
}
