package com.model;

public class Heartbeat {

//    private static final long CONNECTION_TIMEOUT = 5000; //ms
//    private static final long HEARTBEAT_NORMAL_TIMEOUT = 5000; //ms
//    private static final long HEARTBEAT_LOST_TIMEOUT = 15000; //ms
//    private static final long HEARTBEAT_IMU_CALIBRATION_TIMEOUT = 35000; //ms

	/* TODO: Handle connection timeouts */
	
    public static final int INVALID_MAVLINK_VERSION = -1;

    public HeartbeatState heartbeatState = HeartbeatState.FIRST_HEARTBEAT;
    private byte sysid = 1;
    private byte compid = 1;

    /**
     * Stores the version of the mavlink protocol.
     */
    private byte mMavlinkVersion = INVALID_MAVLINK_VERSION;

    public enum HeartbeatState {
        FIRST_HEARTBEAT, LOST_HEARTBEAT, NORMAL_HEARTBEAT, IMU_CALIBRATION
    }

    public byte getSysid() {
        return sysid;
    }

    public byte getCompid() {
        return compid;
    }
   
    public byte getMavlinkVersion() {
        return mMavlinkVersion;
    }
    
    public void setSysid(byte sysid) {
    	this.sysid = sysid;
    }
    
    public void setCompid(byte compid) {
    	this.compid = compid;
    }
    
    public void setMavlinkVersion(byte mavlinkVersion) {
    	this.mMavlinkVersion = mavlinkVersion;
    }

    public boolean hasHeartbeat() {
        return heartbeatState != HeartbeatState.FIRST_HEARTBEAT;
    }

    public boolean isConnectionAlive() {
        return heartbeatState != HeartbeatState.LOST_HEARTBEAT;
    }
}