package com.pprzservices.core.mavlink;

import android.content.Context;

import com.MAVLink.MAVLinkPacket;
import com.aidllib.core.ConnectionParameter;
import com.pprzservices.core.mavlink.MavLinkStreams.*;
import com.pprzservices.core.mavlink.connection.MavLinkConnection;
import com.pprzservices.core.mavlink.connection.MavLinkConnectionListener;
import com.pprzservices.service.MavLinkServiceClient;

/** 
 * MavLinkClient.java
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 *
 * This class handles all incoming events from the MAVLink connection and calls the 
 * appropriate handler in the MavLinkInputStream listener. It is a convenience class
 * for receiving and sending through, as well as connecting to, a MAVLink data stream.
 */
public class MavLinkClient implements MavLinkConnectionListener, MavLinkOutputStream {

//    private final static String TAG = MavLinkClient.class.getSimpleName();

    /**
     * Maximum possible sequence number for a MAVLink packet.
     */
    private static final int MAX_PACKET_SEQUENCE = 255;

    private final MavlinkInputStream mListener; // extend this parameter to allow multiple connections

    private final MavLinkServiceClient mServiceClient;
//    protected final Context context;

    private int packetSeqNumber = 0;
    private final ConnectionParameter mConnParams;

    public MavLinkClient(Context context, MavlinkInputStream listener,
                         ConnectionParameter connParams, MavLinkServiceClient serviceClient) {
//        this.context = context;
        mListener = listener;
        mConnParams = connParams;
        mServiceClient = serviceClient;
    }
    
    @Override
    public void onStartingConnection() {
        mListener.notifyStartingConnection();
    }

    @Override
    public void onConnect() {
        mListener.notifyConnected();
    }

    @Override
    public void onReceivePacket(final MAVLinkPacket packet) {
        mListener.notifyReceivedData(packet);
    }

    @Override
    public void onDisconnect() {
        mListener.notifyDisconnected();
        closeConnection();
    }

    @Override
    public void onComError(final String errMsg) {
        if (errMsg != null) {
            mListener.onStreamError(errMsg);
        }
    }

    @Override
    public void openConnection() {
        if (this.mConnParams == null)
            return;

        if (mServiceClient.getConnectionStatus(this.mConnParams) == MavLinkConnection.MAVLINK_DISCONNECTED) {
        	mServiceClient.connectMavLink(this.mConnParams, this);
        }
    }

    @Override
    public void closeConnection() {
        if (this.mConnParams == null)
            return;

        if (mServiceClient.getConnectionStatus(this.mConnParams) == MavLinkConnection.MAVLINK_CONNECTED) {
        	mServiceClient.disconnectMavLink(this.mConnParams, this);
        }
    }

    @Override
    public void sendMavPacket(MAVLinkPacket pack) {
        if (this.mConnParams == null) {
            return;
        }

        pack.seq = packetSeqNumber;

        if(mServiceClient.sendData(this.mConnParams, pack)) {
            packetSeqNumber = (packetSeqNumber + 1) % (MAX_PACKET_SEQUENCE + 1);
        }
    }

    @Override
    public boolean isConnected() {
        return this.mConnParams != null
                && mServiceClient.getConnectionStatus(this.mConnParams) == MavLinkConnection.MAVLINK_CONNECTED;
    }

    public boolean isConnecting(){
        return this.mConnParams != null && mServiceClient.getConnectionStatus(this.mConnParams) == MavLinkConnection.MAVLINK_CONNECTING;
    }

    /**
     * This function is necessarily called to toggle the connection status
     */    
    @Override
    public void toggleConnectionState() {
        if (isConnected()) {
            closeConnection();
        } else {
            openConnection();
        }
    }
}