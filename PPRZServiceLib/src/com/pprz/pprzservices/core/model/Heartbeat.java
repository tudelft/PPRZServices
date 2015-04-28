package com.pprz.pprzservices.core.model;

import com.MAVLink.common.msg_heartbeat;
import com.pprz.pprzservices.core.drone.DroneClient;
import com.pprz.pprzservices.core.drone.DroneInterfaces;

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

    /**
     * @return the version of the mavlink protocol.
     */
    public byte getMavlinkVersion() {
        return mMavlinkVersion;
    }

    public void onHeartbeat(msg_heartbeat msg, DroneClient client) {
        sysid = (byte) msg.sysid;
        compid = (byte) msg.compid;
        mMavlinkVersion = msg.mavlink_version;

        switch (heartbeatState) {
            case FIRST_HEARTBEAT: {               
                client.onDroneEvent(DroneInterfaces.DroneEventsType.HEARTBEAT_FIRST);             
                break;
            }
            
            case LOST_HEARTBEAT:
                /* TODO: Handle lost heartbeat */
                break;
                
            default:
            	break;
        }

        heartbeatState = HeartbeatState.NORMAL_HEARTBEAT;
    }

    public boolean hasHeartbeat() {
        return heartbeatState != HeartbeatState.FIRST_HEARTBEAT;
    }

    public boolean isConnectionAlive() {
        return heartbeatState != HeartbeatState.LOST_HEARTBEAT;
    }
}