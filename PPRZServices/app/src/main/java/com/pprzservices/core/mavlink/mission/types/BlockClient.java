package com.pprzservices.core.mavlink.mission.types;

import android.os.Handler;
import android.util.Log;

import com.MAVLink.Messages.MAVLinkMessage;
import com.MAVLink.paparazzi.msg_block_count;
import com.MAVLink.paparazzi.msg_block_item;
import com.MAVLink.paparazzi.msg_block_request;
import com.MAVLink.paparazzi.msg_block_request_list;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.drone.DroneInterfaces;
import com.pprzservices.core.mavlink.mission.MissionManager;
import com.pprzservices.service.MavLinkService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BlockClient.java - Implements the block interface default to Paparazzi
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 */
public class BlockClient extends MissionManager {
    private static final String TAG = MavLinkService.class.getSimpleName();

    private short blockCount = 0;

    private List<String> mBlocks = new ArrayList<String>();

    private int mCurrentBlock = 0;

    public BlockClient(DroneClient client, Handler handler) {
        super(client, handler);
    }

    public List<String> getBlocks() {return mBlocks; }

    public int getCurrentBlock() { return mCurrentBlock; }

    @Override
    public void missionMsgHandler(MAVLinkMessage msg) {
        switch (state) {
            case STATE_IDLE: {
                if (msg.msgid == msg_block_item.MAVLINK_MSG_ID_BLOCK_ITEM) {
                    mCurrentBlock = ((msg_block_item) msg).seq;

                    mClient.onDroneEvent(DroneInterfaces.DroneEventsType.CURRENT_BLOCK_UPDATED);

                    // Send acknowledgement
                    sendMissionAck();
                }
                break;
            }

            case STATE_REQUEST_LIST: {
                if (msg.msgid == msg_block_count.MAVLINK_MSG_ID_BLOCK_COUNT) {
                    // Stop the timeout thread
                    stopTimeoutThread(new TimeoutRequestTimer());
                    nRetries = 0;

                    // Store the number of mission blocks
                    blockCount = ((msg_block_count) msg).count;

                    // Clear the current list
                    mBlocks.clear();

                    // Request the first mission block
                    if (blockCount > 0) {
                        requestItem((short) mBlocks.size());

                        // Start the timeout thread
                        startTimeoutThread(new TimeoutRequestTimer((short) mBlocks.size()));
                    }
                }
                break;
            }

            case STATE_REQUEST_ITEM: {
                if (msg.msgid == msg_block_item.MAVLINK_MSG_ID_BLOCK_ITEM) {
                    // Stop the timeout thread
                    stopTimeoutThread(new TimeoutRequestTimer((short) mBlocks.size()));
                    nRetries = 0;

                    // Add the received block to the list of blocks
                    msg_block_item block_item = (msg_block_item) msg;
                    mBlocks.add(new String(Arrays.copyOf(block_item.name, block_item.len)));

                    if (mBlocks.size() < blockCount) {
                        // Request next block
                        requestItem((short) mBlocks.size());

                        // Start the timeout thread
                        startTimeoutThread(new TimeoutRequestTimer((short) mBlocks.size()));
                    } else {
                        // Set state to idle
                        state = StateMachine.STATE_IDLE;

                        // Send acknowledgement
                        sendMissionAck();

                        // Notify the drone client that the list of blocks is updated
                        mClient.onDroneEvent(DroneInterfaces.DroneEventsType.MISSION_BLOCKS_UPDATED);
                    }
                }
                break;
            }
        }
    }

    /**
     * Request list of blocks
     */
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
        msg_block_request_list msg = new msg_block_request_list();
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
        msg_block_request msg = new msg_block_request();
        msg.target_system = mClient.getDrone().getSysid();
        msg.target_component = mClient.getDrone().getCompid();
        msg.seq = seq;
        mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }

    @Override
    public void sendItem(short seq) {
        msg_block_item msg = new msg_block_item();
        msg.target_system = mClient.getDrone().getSysid();
        msg.target_component = mClient.getDrone().getCompid();
        msg.seq = seq;
        mClient.getMavLinkClient().sendMavPacket(msg.pack());
    }
}
