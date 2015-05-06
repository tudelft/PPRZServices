package com.servicelib.core.mavlink;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_attitude;
import com.MAVLink.common.msg_heartbeat;
import com.MAVLink.common.msg_vfr_hud;
import com.MAVLink.enums.MAV_MODE_FLAG;
import com.MAVLink.enums.MAV_STATE;
import com.servicelib.core.drone.Drone;

public class MavLinkMsgHandler {

    private final String TAG = MavLinkMsgHandler.class.getSimpleName();

    private Drone drone;

    public MavLinkMsgHandler(Drone drone) {
        this.drone = drone;
    }

    public void receiveData(MAVLinkMessage msg)
    {
        switch (msg.msgid) {
	        case msg_attitude.MAVLINK_MSG_ID_ATTITUDE: {
	            msg_attitude msg_att = (msg_attitude) msg;
	            drone.setRollPitchYaw(msg_att.roll * 180.0 / Math.PI, msg_att.pitch * 180.0 / Math.PI, msg_att.yaw * 180.0 / Math.PI);
	            break;
	        }
	        
	        case msg_vfr_hud.MAVLINK_MSG_ID_VFR_HUD: {
	            msg_vfr_hud msg_vfr_hud = (msg_vfr_hud) msg;
	            drone.setAltitudeGroundAndAirSpeeds(msg_vfr_hud.alt, msg_vfr_hud.groundspeed, msg_vfr_hud.airspeed, msg_vfr_hud.climb);
	            break;
	        }
        
            case msg_heartbeat.MAVLINK_MSG_ID_HEARTBEAT: {
                msg_heartbeat msg_heart = (msg_heartbeat) msg;
                checkIfFlying(msg_heart);
                processState(msg_heart);
                drone.onHeartbeat(msg_heart);
                break;
            }
            
            // TODO: Add additional messages
            
            default:
                break;
        }
    }

    private void checkIfFlying(msg_heartbeat msg_heart) {
        final byte systemStatus = msg_heart.system_status;
        final boolean wasFlying = drone.getState().isFlying();

        final boolean isFlying = systemStatus == MAV_STATE.MAV_STATE_ACTIVE
                || (wasFlying
                && (systemStatus == MAV_STATE.MAV_STATE_CRITICAL || systemStatus == MAV_STATE.MAV_STATE_EMERGENCY));

        drone.getState().setIsFlying(isFlying);
    }

    public void processState(msg_heartbeat msg_heart) {
        checkArmState(msg_heart);
    }

    private void checkArmState(msg_heartbeat msg_heart) {
        drone.getState().setArmed(
                (msg_heart.base_mode & (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED) == (byte) MAV_MODE_FLAG.MAV_MODE_FLAG_SAFETY_ARMED);
    }
}
