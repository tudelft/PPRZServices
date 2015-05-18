package com.pprzservices.core.mavlink;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_battery_status;
import com.MAVLink.common.msg_global_position_int;
import com.MAVLink.common.msg_gps_status;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_vfr_hud;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.MAV_STATE;
import com.pprzservices.core.drone.Drone;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.mavlink.waypoints.WaypointProtocol;

public class MavLinkMsgHandler {

    private final String TAG = MavLinkMsgHandler.class.getSimpleName();

    private DroneClient mDroneClient;

    //private WaypointProtocol waypointProtocol;

    // TODO: Fix waypoint protocol error

    public MavLinkMsgHandler(DroneClient droneClient) {
        mDroneClient = droneClient;

        //waypointProtocol = new WaypointProtocol(mDroneClient);
    }

    public void receiveData(MAVLinkMessage msg)
    {
        // Handle any message that might contain mission data
        //waypointProtocol.waypointMsgHandler(msg);

        switch (msg.msgid) {
	        case msg_attitude.MAVLINK_MSG_ID_ATTITUDE: {
	            msg_attitude msg_att = (msg_attitude) msg;
                mDroneClient.getDrone().setRollPitchYaw(msg_att.roll, msg_att.pitch, msg_att.yaw);
	            break;
	        }
	        
	        case msg_vfr_hud.MAVLINK_MSG_ID_VFR_HUD: {
	            msg_vfr_hud msg_vfr_hud = (msg_vfr_hud) msg;
                mDroneClient.getDrone().setAltitudeGroundAndAirSpeeds(msg_vfr_hud.alt, msg_vfr_hud.groundspeed, msg_vfr_hud.airspeed, msg_vfr_hud.climb);
	            break;
	        }
        
            case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT: {
                msg_heartbeat msg_heart = (msg_heartbeat) msg;
                checkIfFlying(msg_heart);
                processState(msg_heart);
                mDroneClient.getDrone().onHeartbeat(msg_heart);
                break;
            }
            
            case msg_battery_status.MAVLINK_MSG_ID_BATTERY_STATUS: {
            	msg_battery_status msg_batt = (msg_battery_status) msg;
            	
            	// for now only use the first entry of the voltages array
                mDroneClient.getDrone().setBatteryState(msg_batt.voltages[0], msg_batt.battery_remaining, msg_batt.current_battery);
            	break;
            }
            
            case msg_gps_status.MAVLINK_MSG_ID_GPS_STATUS: {
            	msg_gps_status msg_gps = (msg_gps_status) msg;
            	
            	// for now ignore all variables except the number of satellites
                mDroneClient.getDrone().setSatVisible(msg_gps.satellites_visible);
            	break;
            }
            
            case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT: {
            	msg_global_position_int msg_pos = (msg_global_position_int) msg;
            
            	// for now ignore the relative altitude and ground speeds
                mDroneClient.getDrone().setLlaHdg(msg_pos.lat, msg_pos.lon, msg_pos.alt, msg_pos.hdg);
            	break;
            }
            
            default:
                break;
        }
    }

    private void checkIfFlying(msg_heartbeat msg_heart) {
        final byte systemStatus = msg_heart.system_status;
        final boolean wasFlying = mDroneClient.getDrone().getState().isFlying();

        final boolean isFlying = systemStatus == MAV_STATE.MAV_STATE_ACTIVE
                || (wasFlying
                && (systemStatus == MAV_STATE.MAV_STATE_CRITICAL || systemStatus == MAV_STATE.MAV_STATE_EMERGENCY));

        mDroneClient.getDrone().getState().setIsFlying(isFlying);
    }

    public void processState(msg_heartbeat msg_heart) {
        checkArmState(msg_heart);
    }

    private void checkArmState(msg_heartbeat msg_heart) {
        mDroneClient.getDrone().getState().setArmed(
                (msg_heart.base_mode & (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED) == (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED);
    }

//    public void requestWpList() {
//        waypointProtocol.requestList();
//    }
}
