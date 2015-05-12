package com.pprzservices.core.mavlink;

import com.MAVLink.MAVLinkPacket;

public class MavLinkStreams {

    public interface MavLinkOutputStream {
        void sendMavPacket(MAVLinkPacket pack);

        boolean isConnected();

        void toggleConnectionState();

        void openConnection();

        void closeConnection();

    }

    public interface MavlinkInputStream {
        public void notifyStartingConnection();

        public void notifyConnected();

        public void notifyDisconnected();

        public void notifyReceivedData(MAVLinkPacket packet);

        public void onStreamError(String errorMsg);
    }
}
