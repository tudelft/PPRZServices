package com.servicelib.core.mavlink.connection;

import com.MAVLink.MAVLinkPacket;

/**
 * Provides updates about the mavlink connection.
 */
public interface MavLinkConnectionListener {

    /**
     * Called when a connection is being established.
     */
    public void onStartingConnection();

    /**
     * Called when the mavlink connection is established.
     */
    public void onConnect();

    /**
     * Called when data is received via the mavlink connection.
     *
     * @param packet Received data
     */
    public void onReceivePacket(MAVLinkPacket packet);

    /**
     * Called when the mavlink connection is disconnected.
     */
    public void onDisconnect();

    /**
     * Provides information about communication error.
     *
     * @param errMsg Error information
     */
    public void onComError(String errMsg);

}
