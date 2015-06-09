package com.pprzservices.core.mavlink.waypoints.types;

import java.util.ArrayList;
import java.util.List;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_count;
import com.MAVLink.common.msg_mission_item;
import com.MAVLink.common.msg_mission_request;
import com.MAVLink.common.msg_mission_request_list;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.drone.DroneInterfaces;
import com.pprzservices.core.mavlink.waypoints.MissionManager;
import com.pprzservices.service.MavLinkService;

import android.os.Handler;
import android.util.Log;

/**
 * WaypointClient.java - Implements the MAVLink MissionLib waypoint protocol
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 *
 * It is currently assumed that the HOME waypoint is the first waypoint that is being sent.
 *
 */
public class WaypointClient extends MissionManager{
	private static final String TAG = MavLinkService.class.getSimpleName();

    private short wpCount = 0;

    private List<Waypoint> mWaypoints = new ArrayList<Waypoint>();

	public WaypointClient(DroneClient client, Handler handler) {
		super(client, handler);
	}

	public List<Waypoint> getWaypoints() {
		return mWaypoints;
	}

	@Override
	public void missionMsgHandler(MAVLinkMessage msg) {
    	switch (state) {
    		case STATE_IDLE: {
				break;
    		}

			case STATE_REQUEST_LIST: {
    			if (msg.msgid == msg_mission_count.MAVLINK_MSG_ID_MISSION_COUNT) {
					// Stop the timeout thread
					stopTimeoutThread(new TimeoutRequestTimer());
					nRetries = 0;

    				// Store the waypoint count
                    wpCount = ((msg_mission_count) msg).count;

                    // Clear the current list (if any waypoints are present)
					mWaypoints.clear();
                    
                    // Request the first waypoint
                    if (wpCount > 0) {
                    	requestItem((short)mWaypoints.size());
                    
                    	// Start the timeout thread
                    	startTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
                    }
                }
    			break;
    		}

    		case STATE_REQUEST_ITEM: {
    			if (msg.msgid == msg_mission_item.MAVLINK_MSG_ID_MISSION_ITEM) {
					// Stop the timeout thread
					stopTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
    				nRetries = 0;

    				// Add the received waypoint to the list of waypoints
    				msg_mission_item item = (msg_mission_item) msg;
					mWaypoints.add(new Waypoint(item.x, item.y, item.z, item.seq, item.target_system, item.target_component));
    				
    				if (mWaypoints.size() < wpCount) {
    					// Request next waypoint
    					requestItem((short)mWaypoints.size());

						// Start the timeout thread
						startTimeoutThread(new TimeoutRequestTimer((short)mWaypoints.size()));
                    } else {
                        // Set state to idle
                    	state = StateMachine.STATE_IDLE;
                        
                        // Send mission acknowledgement
                    	sendMissionAck();

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
    		
    		case STATE_WRITE_ITEM: {
    			
    			// TODO: Implement write WP to MAV
    			
    			break;
    		}
    		
    		default: 
    			break;
    	}
    }

	@Override
    public void requestList() {
    	// Only request list if the state of the state machine is STATE_IDLE
    	if (state != StateMachine.STATE_IDLE)
    		return;
    	
    	state = StateMachine.STATE_REQUEST_LIST;
    	sendRequestList();

		// Start the timeout thread
		startTimeoutThread(new TimeoutRequestTimer());
    }

	@Override
    public void sendRequestList() {
    	msg_mission_request_list msg = new msg_mission_request_list();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }

	@Override
    public void requestItem(short seq) {
    	state = StateMachine.STATE_REQUEST_ITEM;
    	sendRequestItem(seq);
    }

	@Override
    public void sendRequestItem(short seq) {
    	msg_mission_request msg = new msg_mission_request();
		msg.target_system = mClient.getDrone().getSysid();
		msg.target_component = mClient.getDrone().getCompid();
		msg.seq = seq;
		mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }

	@Override
	public void sendItem(short seq) {
	}
}