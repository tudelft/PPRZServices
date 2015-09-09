package com.pprzservices.core.drone;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.aidllib.core.ConnectionParameter;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.pprzservices.core.drone.DroneInterfaces.DroneEventsType;
import com.pprzservices.core.drone.DroneInterfaces.OnDroneListener;
import com.pprzservices.core.mavlink.MavLinkClient;
import com.pprzservices.core.mavlink.MavLinkMsgHandler;
import com.pprzservices.core.mavlink.MavLinkStreams;
import com.pprzservices.service.MavLinkServiceClient;

import java.util.List;

/**
 * DroneClient.java
 * @author lncsikkel
 * 
 * This class handles all incomming notification coming from the MavLinkInput stream as well as 
 * implements the listening methods for update the Drone UI.
 */
public class DroneClient implements MavLinkStreams.MavlinkInputStream, OnDroneListener {

    private static final String TAG = DroneClient.class.getSimpleName();

    private final MavLinkClient mMavLinkClient;
    private final Drone mDrone;
    private final MavLinkMsgHandler mMavLinkMsgHandler;
    private final ConnectionParameter mConnParams;
    private final MavLinkServiceClient mServiceClient;

    public DroneClient(Context context, ConnectionParameter connParams, MavLinkServiceClient serviceClient) {
        mConnParams = connParams;
        mDrone = new Drone(this);

        mMavLinkMsgHandler = new MavLinkMsgHandler(this, new Handler(context.getMainLooper()));

        mServiceClient = serviceClient;

        mMavLinkClient = new MavLinkClient(context, this, connParams, mServiceClient);
    }

    public void destroy() {
        disconnect();
    }

    public void connect(ConnectionParameter connParams) {
        if (!mMavLinkClient.isConnected()) {
            mMavLinkClient.openConnection();

            requestWpList();
            requestBlockList();
        }
    }

    public void disconnect() {
    	mMavLinkClient.toggleConnectionState();
    }

    public Drone getDrone() {
        return mDrone;
    }

    public MavLinkClient getMavLinkClient() {
        return mMavLinkClient;
    }

    public boolean isConnected() {
        return mDrone.isConnected();
    }

    public ConnectionParameter getConnectionParameter() {
        return mConnParams;
    }

    @Override
    public void notifyStartingConnection() {
        onDroneEvent(DroneInterfaces.DroneEventsType.CONNECTING);
    }

    @Override
    public void notifyConnected() {
        onDroneEvent(DroneInterfaces.DroneEventsType.CONNECTED);
    }

    @Override
    public void notifyDisconnected() {
        onDroneEvent(DroneInterfaces.DroneEventsType.DISCONNECTED);
    }

    @Override
    public void notifyReceivedData(MAVLinkPacket packet) {
        MAVLinkMessage receivedMsg = packet.unpack();

        // Handle the MAVLink message
        mMavLinkMsgHandler.receiveData(receivedMsg);
    }

    @Override
    public void onStreamError(String errorMsg) {
        /* TODO: Handle onStreamError. */
    }

	@Override
	public void onDroneEvent(DroneEventsType event) {
		try {
			mServiceClient.onEvent(event.toString(), mDrone.getSysid());
        } catch (RemoteException e) {
        	/* TODO: Handle remote exception */
        }
	}

    // Request list of waypoints
    public void requestWpList() {
        mMavLinkMsgHandler.getWaypointClient().requestList();
    }

    // Write waypoint
    public void writeWp(float lat, float lon, float alt, short seq) {
        mMavLinkMsgHandler.getWaypointClient().sendItem(lat, lon, alt, seq);
    }

    //Enable launch mode
    public void commandLaunch() {
        mMavLinkMsgHandler.getCommandClient().launch();
    }

    // Request list of blocks
    public void requestBlockList() {
        mMavLinkMsgHandler.getBlockClient().requestList();
    }

    public void setCurrentBlock(short seq) {
        mMavLinkMsgHandler.getBlockClient().sendItem(seq);
    }
}