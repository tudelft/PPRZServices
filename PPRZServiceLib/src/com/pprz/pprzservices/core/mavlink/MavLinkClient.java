package com.pprz.pprzservices.core.mavlink;

import android.content.Context;

import com.MAVLink.MAVLinkPacket;
import com.aidl.core.ConnectionParameter;
import com.pprz.pprzservices.core.mavlink.MavLinkStreams.*;
import com.pprz.pprzservices.core.mavlink.connection.MavLinkConnection;
import com.pprz.pprzservices.core.mavlink.connection.MavLinkConnectionListener;
import com.pprz.pprzservices.service.MavLinkServiceClient;

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

    private final MavlinkInputStream listener;

    private final MavLinkServiceClient mServiceClient;
    protected final Context context;

    private int packetSeqNumber = 0;
    private final ConnectionParameter connParams;

    public MavLinkClient(Context context, MavlinkInputStream listener,
                         ConnectionParameter connParams, MavLinkServiceClient serviceClient) {
        this.context = context;
        this.listener = listener;
        this.connParams = connParams;
        this.mServiceClient = serviceClient;
    }
    
    @Override
    public void onStartingConnection() {
        listener.notifyStartingConnection();
    }

    @Override
    public void onConnect() {
        listener.notifyConnected();
    }

    @Override
    public void onReceivePacket(final MAVLinkPacket packet) {
        listener.notifyReceivedData(packet);
    }

    @Override
    public void onDisconnect() {
        listener.notifyDisconnected();
        closeConnection();
    }

    @Override
    public void onComError(final String errMsg) {
        if (errMsg != null) {
            listener.onStreamError(errMsg);
        }
    }

    @Override
    public void openConnection() {
        if (this.connParams == null)
            return;

        if (mServiceClient.getConnectionStatus(this.connParams) == MavLinkConnection.MAVLINK_DISCONNECTED) {
        	mServiceClient.connectMavLink(this.connParams, this);
        }
    }

    @Override
    public void closeConnection() {
        if (this.connParams == null)
            return;

        if (mServiceClient.getConnectionStatus(this.connParams) == MavLinkConnection.MAVLINK_CONNECTED) {
        	mServiceClient.disconnectMavLink(this.connParams, this);
            listener.notifyDisconnected();
        }
    }

    @Override
    public void sendMavPacket(MAVLinkPacket pack) {
        if (this.connParams == null) {
            return;
        }

        pack.seq = packetSeqNumber;

        if(mServiceClient.sendData(this.connParams, pack)) {
            packetSeqNumber = (packetSeqNumber + 1) % (MAX_PACKET_SEQUENCE + 1);
        }
    }

    @Override
    public boolean isConnected() {
        return this.connParams != null
                && mServiceClient.getConnectionStatus(this.connParams) == MavLinkConnection.MAVLINK_CONNECTED;
    }

    public boolean isConnecting(){
        return this.connParams != null && mServiceClient.getConnectionStatus(this.connParams) == MavLinkConnection.MAVLINK_CONNECTING;
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