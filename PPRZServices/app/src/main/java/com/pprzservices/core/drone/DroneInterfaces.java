package com.pprzservices.core.drone;

public class DroneInterfaces {

    /**
     * Set of drone events used for broadcast identification.
     */
    public enum DroneEventsType {
        //Checking the vehicle link.
//        CHECKING_VEHICLE_LINK,

        //Heartbeat timeout event.
//        HEARTBEAT_TIMEOUT,

        // First heartbeat received event.
        HEARTBEAT_FIRST,

        // Restored heartbeat event.
//        HEARTBEAT_RESTORED,

        // Successful connection event.
        CONNECTED,

        // Connecting.
        CONNECTING,

        // Disconnected.
        DISCONNECTED,
        
        // Attitude updated
        ATTITUDE_UPDATED,
        
        // Altitude and speed updated
        ALTITUDE_SPEED_UPDATED,
        
        // Battery updated
        BATTERY_UPDATED,
        
        // Position updated
        POSITION_UPDATED,
        
        // Number of satellites updated
        SATELLITES_VISIBLE_UPDATED,

        // List of waypoints updated
        WAYPOINTS_UPDATED,

        // List of mission blocks updated
        MISSION_BLOCKS_UPDATED
    }

    public interface OnDroneListener {
        public void onDroneEvent(DroneEventsType event);
    }
}