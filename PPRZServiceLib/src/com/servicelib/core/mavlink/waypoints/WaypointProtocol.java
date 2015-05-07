package com.servicelib.core.mavlink.waypoints;

import java.util.ArrayList;
import java.util.List;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.servicelib.core.drone.Drone;
import com.servicelib.core.drone.DroneClient;

import android.os.Handler;

/**
 * WaypointProtocol.java - Implements the MissionLib waypoint protocol 
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 * 
 */
public class WaypointProtocol {
	/** 
	 * State machine
	 */
	enum StateMachine { 
		STATE_IDLE, STATE_REQUEST_LIST, STATE_REQUEST_WP, STATE_WRITE_COUNT, STATE_WRITE_WP
	}
	
	private StateMachine state = StateMachine.STATE_IDLE;
	
	// Timeout 
	private static final long TIMEOUT = 15000; //ms
	
	// Number of retries
    private static final int RETRIES = 3;
    
    private int nRetries = 0;
    
    // Number of waypoints receiver from the MAV
    private short count = 0;
    
    private List<Waypoint> waypoints = new ArrayList<Waypoint>();
    
    DroneClient mClient; 
    
    Handler mHandler;
    
    public WaypointProtocol(DroneClient client, Handler handler) {
    	this.mClient = client;
    	this.mHandler = handler;
    }
    
    /**
     * Waypoint message handler
     */
    public void waypointMsgHandler(MAVLinkMessage msg) {
    	switch (state) {
    		case STATE_IDLE: {   			
    			break;
    		}
    		
    		case STATE_REQUEST_LIST: {
    			if (msg.msgid == msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT) {
    				// Store the number of waypoints
                    count = ((msg_mission_count) msg).count;
                    
                    // Clear the current list (if any waypoints are present)
                    waypoints.clear();
                    
                    // Request the first waypoint
                    requestWp((short)waypoints.size());
                    
                    state = StateMachine.STATE_REQUEST_WP;
                }
    			break;
    		}
    		
    		case STATE_REQUEST_WP: {
    			
    			break;
    		}
    		
    		case STATE_WRITE_COUNT: {
    			
    			// TODO: Implement write list to MAV
    			
    			break;
    		}
    		
    		case STATE_WRITE_WP: {
    			
    			// TODO: Implement write WP to MAV
    			
    			break;
    		}
    		
    		default: 
    			break;
    	}
    }
    
    /** 
     * Request list of waypoints
     */
    public void requestList() {
    	// Only request list if the state of the state machine is STATE_IDLE
    	if (state != StateMachine.STATE_IDLE)
    		return;
    	
    	state = StateMachine.STATE_REQUEST_LIST;
    	sendRequestList();
    }
    
    /**
     * Send request for a list of waypoints 
     */
    public void sendRequestList() {
    	msg_mission_request_list msg = new msg_mission_request_list();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }
    
    /** 
     * Request waypoint
     */
    public void requestWp(short seq) {
    	
    }
    
    /**
     * Send request for a waypoints 
     */
    public void sendRequestWp(short seq) {
    	msg_mission_request msg = new msg_mission_request();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		msg.seq = seq;
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }
    
    /**
     * Post timeout runnable
     */
    private final Runnable timeoutRequest = new Runnable() {
        @Override
        public void run() {
//            if (processTimeOut(++retryTracker))
//                watchdog.postDelayed(this, TIMEOUT);
        }
    };
}