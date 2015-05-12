package com.pprzservices.core.mavlink.connection;

public class MavLinkConnectionTypes {
	/**
	 *  UDP MAVLink connection.
	 */
    public static final int MAVLINK_CONNECTION_UDP = 0;
    public static final int MAVLINK_CONNECTION_UDP_PORT = 5000;
    
	/** 
	 * Bluetooth MAVLink connection
	 */
    public static final int MAVLINK_CONNECTION_BLUETOOTH = 1;

    public static String getConnectionTypeLabel(int connectionType){
        switch(connectionType){
        	case MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP:
                return "udp";
            case MavLinkConnectionTypes.MAVLINK_CONNECTION_BLUETOOTH:
                return "bluetooth";
            default:
                return null;
        }
    }

    // Not instantiable
    private MavLinkConnectionTypes() {
    }
}
