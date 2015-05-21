package com.pprzservices.core.mavlink.waypoints;

import java.util.ArrayList;
import java.util.List;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.drone.DroneInterfaces;
import com.pprzservices.service.MavLinkService;

import android.os.Handler;
import android.util.Log;

/**
 * WaypointProtocol.java - Implements the MissionLib waypoint protocol 
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 * 
 */
public class WaypointProtocol {
	
	private static final String TAG = MavLinkService.class.getSimpleName();
	
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

	// Create a thread-safe retry counter
	private int nRetries = 0;
    
    // Number of waypoints receiver from the MAV
    private short count = 0;

    private List<Waypoint> mWaypoints = new ArrayList<Waypoint>();
    
    DroneClient mClient; 

	// Pass the service handler
    Handler mHandler;
    
    public WaypointProtocol(DroneClient client, Handler handler) {
    	this.mClient = client;

    	this.mHandler = handler;
    }

	public List<Waypoint> getWaypoints() {
		return mWaypoints;
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
					// Stop the timeout thread
					stopTimeoutThread(new TimeoutRequestTimer());
					nRetries = 0;

    				// Store the number of waypoints
                    count = ((msg_mission_count) msg).count;

					Log.d(TAG, "Mission count: " + count);

                    // Clear the current list (if any waypoints are present)
					mWaypoints.clear();
                    
                    // Request the first waypoint
                    if (count > 0) {
                    	requestWp((short)mWaypoints.size());
                    
                    	// Start the timeout thread
                    	startTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
                    }
                }
    			break;
    		}
    		
    		case STATE_REQUEST_WP: {
    			if (msg.msgid == msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM) {
					// Stop the timeout thread
					stopTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
    				nRetries = 0;

    				// Add the received waypoint to the list of waypoints
    				msg_mission_item item = (msg_mission_item) msg;
					mWaypoints.add(new Waypoint(item.x, item.y, item.z, item.seq, item.target_system, item.target_component));
    				
    				if (mWaypoints.size() < count) {
    					// Request next waypoint
    					requestWp((short)mWaypoints.size());

						// Start the timeout thread
						startTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
                    } else {
                        // Set state to idle
                    	state = StateMachine.STATE_IDLE;
                        
                        // Send acknowledgement
                    	sendAck();

						// Notify the drone client that the list of waypoints is updated
						mClient.onDroneEvent(DroneInterfaces.DroneEventsType.WAYPOINTS_UPDATED);

						// TODO: Currently changing a single waypoints implies reloading the entire list

                    }
    			}
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
    public void requestWpList() {
    	// Only request list if the state of the state machine is STATE_IDLE
    	if (state != StateMachine.STATE_IDLE)
    		return;
    	
    	state = StateMachine.STATE_REQUEST_LIST;
    	sendRequestWpList();

		// Start the timeout thread
		startTimeoutThread(new TimeoutRequestTimer());
    }
    
    /**
     * Send request for a list of waypoints 
     */
    public void sendRequestWpList() {
    	msg_mission_request_list msg = new msg_mission_request_list();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }
    
    /** 
     * Request waypoint
     */
    public void requestWp(short seq) {
    	state = StateMachine.STATE_REQUEST_WP;
    	sendRequestWp(seq);
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
     * Timeout runnable according to Handler documentation, which handles retries or timeouts
     */
	private class TimeoutRequestTimer implements Runnable {
		private short mSeq;

		public TimeoutRequestTimer() {mSeq = 0;}

		public TimeoutRequestTimer(short seq) {
			mSeq = seq;
		}

		@Override
		public void run() {
			if (nRetries++ < RETRIES) {
				switch(state) {
					case STATE_REQUEST_LIST: {

						Log.d(TAG, "Retrying to request list of waypoints... (" + nRetries + ")");

						// Request the list of waypoints
						sendRequestWpList();

						// Start the timeout thread
						startTimeoutThread(new TimeoutRequestTimer());

						break;
					}

					case STATE_REQUEST_WP: {

						Log.d(TAG, "Retrying to request waypoint... (" + nRetries + ")");

						// Request next waypoint
						requestWp((short) mWaypoints.size());

						// Start the timeout thread
						startTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));

						break;
					}
				}
			} else {
				state = StateMachine.STATE_IDLE;
				nRetries = 0;
			}
		}
	}

    /**
     * Start timeout thread
     */
    public void startTimeoutThread(TimeoutRequestTimer timeoutRequestTimer) {
    	mHandler.postDelayed(timeoutRequestTimer, TIMEOUT);
    }
    
    /**
     * Stop timeout thread
     */
    public void stopTimeoutThread(TimeoutRequestTimer timeoutRequestTimer) {
    	mHandler.removeCallbacks(timeoutRequestTimer);
    }
    
    /**
     * Send acknowledgement
     */
    public void sendAck() {
    	msg_mission_ack msg = new msg_mission_ack();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		msg.type = MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED;
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }
}