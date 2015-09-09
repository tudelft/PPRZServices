package com.pprzservices.core.mavlink.mission.types;

import android.os.Handler;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_command_long;
import com.MAVLink.enums.MAV_CMD;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.mavlink.mission.MissionManager;

public class CommandClient extends MissionManager {
    private static final String TAG = WaypointClient.class.getSimpleName();

    public CommandClient(DroneClient client, Handler handler) {
        super(client, handler);
    }

    @Override
    public void missionMsgHandler(MAVLinkMessage msg) {
        switch (state) {
            case STATE_IDLE: {
                break;
            }
        }
    }

    @Override
    public void requestList() {
    }

    @Override
    public void sendRequestList() {
    }

    @Override
    public void requestItem(short seq) {
    }

    @Override
    public void sendRequestItem(short seq) {
    }

    @Override
    public void sendItem(short seq) {
    }

    //Send a command to launch the aircraft
    public void launch() {
        msg_command_long msg = new msg_command_long();

        //Takeoff command
        msg.command = MAV_CMD.MAV_CMD_NAV_TAKEOFF;
        msg.target_system = mClient.getDrone().getSysid();
        mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }

    //Send a command to kill the throttle of the aircraft
    public void kill() {
        //TODO: Send a kill message
    }

    //Send a command to resurrect the aircraft
    public void resurrect() {
        //TODO: Send a resurrect message
    }
}
