package com.servicelib.core.drone;

import android.content.Context;
import android.os.RemoteException;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Messages.MAVLinkMessage;
import com.aidl.core.ConnectionParameter;
import com.servicelib.core.drone.DroneInterfaces;
import com.servicelib.core.drone.DroneInterfaces.DroneEventsType;
import com.servicelib.core.drone.DroneInterfaces.OnDroneListener;
import com.servicelib.core.mavlink.MavLinkClient;
import com.servicelib.core.mavlink.MavLinkMsgHandler;
import com.servicelib.core.mavlink.MavLinkStreams;
import com.servicelib.service.MavLinkServiceClient;

/**
 * DroneClient.java
 * @author lncsikkel
 * 
 * This class handles all incomming notification coming from the MavLinkInput stream as well as 
 * implements the listening methods for update the Drone UI.
 */
public class DroneClient implements MavLinkStreams.MavlinkInputStream, OnDroneListener {

    private static final String TAG = DroneClient.class.getSimpleName();

    protected final Context context;
    private final MavLinkClient mavLinkClient;
    private final Drone drone;
    private final MavLinkMsgHandler mavLinkMsgHandler;
    private final ConnectionParameter connParams;
    private final MavLinkServiceClient mServiceClient;

    public DroneClient(Context context, ConnectionParameter connParams, MavLinkServiceClient serviceClient) {
        this.context = context;
        this.connParams = connParams;
        this.drone = new Drone(this);
        this.mavLinkMsgHandler = new MavLinkMsgHandler(this.drone);
        this.mServiceClient = serviceClient;
        
        mavLinkClient = new MavLinkClient(context, this, connParams, mServiceClient);
    }

    public void destroy() {
        disconnect();
    }

    public void connect(ConnectionParameter connParams) {
        if (!mavLinkClient.isConnected()) {
        	mavLinkClient.openConnection();
        }
    }

    public void disconnect() {
    	mavLinkClient.toggleConnectionState();
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
        this.mavLinkMsgHandler.receiveData(receivedMsg);
    }

    @Override
    public void onStreamError(String errorMsg) {
        /* TODO: Handle onStreamError. */
    }

    public Drone getDrone() {
        return drone;
    }

    public boolean isConnected() {
        return drone.isConnected();
    }

    public ConnectionParameter getConnectionParameter() {
        return connParams;
    }

	@Override
	public void onDroneEvent(DroneEventsType event) {
		try {
			mServiceClient.onEvent(event.toString());
        } catch (RemoteException e) {
        	/* TODO: Handle remote exception */
        }
	}
}