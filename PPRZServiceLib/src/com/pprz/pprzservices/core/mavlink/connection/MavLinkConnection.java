package com.pprz.pprzservices.core.mavlink.connection;

import android.util.Log;

import com.MAVLink.MAVLinkPacket;
import com.MAVLink.Parser;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base for mavlink connection implementations.
 */
public abstract class MavLinkConnection {

    private static final String TAG = MavLinkConnection.class.getSimpleName();

    /*
     * MavLink connection states
     */
    public static final int MAVLINK_DISCONNECTED = 0;
    public static final int MAVLINK_CONNECTING = 1;
    public static final int MAVLINK_CONNECTED = 2;

    /**
     * Size of the buffer used to read messages from the mavlink connection.
     */
    private static final int READ_BUFFER_SIZE = 4096;

    /**
     * List which blocks thread when no data is available
     */
    private final LinkedBlockingQueue<byte[]> mPacketsToSend = new LinkedBlockingQueue<>();

    private final AtomicInteger mConnectionStatus = new AtomicInteger(MAVLINK_DISCONNECTED);
    
    /**
     * Connecting thread
     */
    private class ConnectingClass implements Runnable {
        MavLinkConnectionListener listener;

        public ConnectingClass(MavLinkConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            // Open the connection
            try {
                openConnection(listener);
            } catch (IOException e) {
                // Ignore errors while shutting down
                if (mConnectionStatus.get() != MAVLINK_DISCONNECTED) {
                    reportComError(e.getMessage());
                }
                disconnect(listener);
            }
        }
    }

    /**
     * MAVLink message client
     */
    private class ClientClass implements Runnable {
        MavLinkConnectionListener listener;

        public ClientClass(MavLinkConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            Thread sendingThread = null;

            try {
                reportConnect(listener);

                sendingThread = new Thread(new SendingClass(listener), "SendingThread");
                sendingThread.start();

                final Parser parser = new Parser();
                parser.stats.mavlinkResetStats();

                final byte[] readBuffer = new byte[READ_BUFFER_SIZE];

                while (mConnectionStatus.get() == MAVLINK_CONNECTED) {
                    Log.d(TAG, "Waiting for data...");
                    int bufferSize = readDataBlock(readBuffer);
                    handleData(parser, bufferSize, readBuffer, listener);
                }
            } catch (IOException e) {
                if (mConnectionStatus.get() != MAVLINK_DISCONNECTED) {
                    reportComError(e.getMessage());
                }
            } finally {
                if (sendingThread != null && sendingThread.isAlive()) {
                    sendingThread.interrupt();
                }

                disconnect(listener);
            }
        }

        private void handleData(Parser parser,
                                int bufferSize,
                                byte[] buffer,
                                MavLinkConnectionListener listener) {
            if (bufferSize < 1) {
                return;
            }

            for (int i = 0; i < bufferSize; i++) {
                MAVLinkPacket receivedPacket = parser.mavlink_parse_char(buffer[i] & 0x00ff);
                if (receivedPacket != null) {
                    reportReceivedPacket(receivedPacket, listener);
                }
            }
        }
    }

    /**
     * Blocks until there's packet(s) to send, then dispatch them.
     */
    private class SendingClass implements Runnable {
        MavLinkConnectionListener listener;

        public SendingClass(MavLinkConnectionListener listener) {
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                while (mConnectionStatus.get() == MAVLINK_CONNECTED) {
                    byte[] buffer = mPacketsToSend.take();

                    try {
                        sendBuffer(buffer);
                    } catch (IOException e) {
                        reportComError(e.getMessage());
                    }
                }
            } catch (InterruptedException e) {
            } finally {
                disconnect(listener);
            }
        }
    };

    private Thread mConnectThread;
    private Thread mTaskThread;

    /**
     * Establish a mavlink connection. If the connection is successful, it will
     * be reported through the MavLinkConnectionListener interfaces.
     */
    public void connect(MavLinkConnectionListener listener) {
        if (mConnectionStatus.compareAndSet(MAVLINK_DISCONNECTED, MAVLINK_CONNECTING)) {
            mConnectThread = new Thread(new ConnectingClass(listener), "ConnectingThread");
            Log.d(TAG, "Created 'Connecting' thread");
            mConnectThread.start();
            reportConnecting(listener);
        }
        
        
    }

    protected void onConnectionOpened(MavLinkConnectionListener listener) {
        if (mConnectionStatus.compareAndSet(MAVLINK_CONNECTING, MAVLINK_CONNECTED)) {
            mTaskThread = new Thread(new ClientClass(listener), "ClientThread");
            Log.d(TAG, "Created 'Client' thread");
            mTaskThread.start();
        }
    }

    protected void onConnectionFailed(String errMsg, MavLinkConnectionListener listener) {
        reportComError(errMsg);
        disconnect(listener);
    }

    /**
     * Disconnect a mavlink connection. If the operation is successful, it will
     * be reported through the MavLinkConnectionListener interfaces.
     */
    public void disconnect(MavLinkConnectionListener listener) {
        if (mConnectionStatus.get() == MAVLINK_DISCONNECTED || (mConnectThread == null && mTaskThread == null)) {
            return;
        }

        try {
            mConnectionStatus.set(MAVLINK_DISCONNECTED);
            if (mConnectThread != null && mConnectThread.isAlive() && !mConnectThread.isInterrupted()) {
                mConnectThread.interrupt();
            }

            if (mTaskThread != null && mTaskThread.isAlive() && !mTaskThread.isInterrupted()) {
                mTaskThread.interrupt();
            }

            closeConnection();
            reportDisconnect(listener);
        } catch (IOException e) {
            reportComError(e.getMessage());
        }
    }

    public int getConnectionStatus() {
        return mConnectionStatus.get();
    }

    public void sendMavPacket(MAVLinkPacket packet) {
        final byte[] packetData = packet.encodePacket();
        if (!mPacketsToSend.offer(packetData)) {
            /* TODO: Handle queue overflowing. */
        }
    }

    protected void reportComError(String errMsg) {
        /* TODO: Handle reportComError. */
    	
    	Log.d(TAG, "Could not open connection!");
    }

    protected void reportConnecting(MavLinkConnectionListener listener) {
        listener.onStartingConnection();
    }

    protected void reportConnect(MavLinkConnectionListener listener) {
        listener.onConnect();
    }

    protected void reportDisconnect(MavLinkConnectionListener listener) {
        listener.onDisconnect();
    }

    private void reportReceivedPacket(MAVLinkPacket packet, MavLinkConnectionListener listener) {
        listener.onReceivePacket(packet);
    }

    protected abstract void openConnection(MavLinkConnectionListener listener) throws IOException;

    protected abstract int readDataBlock(byte[] buffer) throws IOException;

    protected abstract void sendBuffer(byte[] buffer) throws IOException;

    protected abstract void closeConnection() throws IOException;

    public abstract int getConnectionType();

}