package com.pprzservices.core.mavlink.mission;

import android.os.Handler;
import android.util.Log;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.common.msg_mission_ack;
import com.MAVLink.enums.MAV_MISSION_RESULT;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.service.MavLinkService;

/**
 * MissionManager.java - Abstract class implementing common mission manager methods
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 */
public abstract class MissionManager {
    private static final String TAG = MavLinkService.class.getSimpleName();

    // Timeout
    protected static final long TIMEOUT = 1000; //ms

    // Number of retries
    protected static final int RETRIES = 3;

    // Create a thread-safe retry counter
    protected int nRetries = 0;

    protected DroneClient mClient;

    // Pass the handler to the service thread
    private Handler mHandler;

    // State machine
    protected enum StateMachine {
        STATE_IDLE, STATE_REQUEST_LIST, STATE_REQUEST_ITEM, STATE_WRITE_COUNT, STATE_WRITE_ITEM
    }

    protected StateMachine state = StateMachine.STATE_IDLE;

    // Constructor
    public MissionManager(DroneClient client, Handler handler) {
        mClient = client;
        mHandler = handler;
    }

    // Timeout runnable according to Handler documentation, which handles retries or timeouts
    protected class TimeoutRequestTimer implements Runnable {
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

//                        Log.d(TAG, "Retrying to request list of waypoints... (" + nRetries + ")");

                        // Request the list of waypoints
                        sendRequestList();

                        // Start the timeout thread
                        startTimeoutThread(new TimeoutRequestTimer());

                        break;
                    }

                    case STATE_REQUEST_ITEM: {

//                        Log.d(TAG, "Retrying to request waypoint... (" + nRetries + ")");

                        // Request next waypoint
                        requestItem(mSeq);

                        // Start the timeout thread
                        startTimeoutThread(new TimeoutRequestTimer(mSeq));

                        break;
                    }
                }
            } else {
                state = StateMachine.STATE_IDLE;
                nRetries = 0;
            }
        }
    }

    // Start timeout thread
    protected void startTimeoutThread(TimeoutRequestTimer timeoutRequestTimer) {
        mHandler.postDelayed(timeoutRequestTimer, TIMEOUT);
    }

    // Stop timeout thread
    protected void stopTimeoutThread(TimeoutRequestTimer timeoutRequestTimer) {
        mHandler.removeCallbacks(timeoutRequestTimer);
    }

    protected abstract void requestList();

    protected abstract void sendRequestList();

    protected abstract void requestItem(short seq);

    protected abstract void sendRequestItem(short seq);

    protected abstract void sendItem(short seq);

    // Send mission acknowledgement to MAV
    protected void sendMissionAck() {
        msg_mission_ack msg = new msg_mission_ack();
        msg.target_system = mClient.getDrone().getSysid();
        msg.target_component = mClient.getDrone().getCompid();
        msg.type = MAV_MISSION_RESULT.MAV_MISSION_ACCEPTED;
        mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }

    // Abstract method that handles incoming MAVLink mission messages
    protected abstract void missionMsgHandler(MAVLinkMessage msg);
}
