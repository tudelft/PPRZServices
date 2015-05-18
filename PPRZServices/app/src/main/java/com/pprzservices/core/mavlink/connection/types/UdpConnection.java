package com.pprzservices.core.mavlink.connection.types;

import android.util.Log;

import com.pprzservices.core.mavlink.connection.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Provides support for MAVLink connection via UDP.
 */
public class UdpConnection extends MavLinkConnection {

    private static final String TAG = UdpConnection.class.getSimpleName();

    private DatagramSocket socket;
    private int serverPort;

    private int hostPort;
    private InetAddress hostAdd;

    public UdpConnection(int serverPort) {
        this.serverPort = serverPort;
    }

    private void getUdpStream() throws IOException {
        socket = new DatagramSocket(serverPort);
        socket.setBroadcast(true); 
        socket.setReuseAddress(true);
    }

    @Override
    public final void closeConnection() throws IOException {
        if (socket != null)
            socket.close();
    }

    @Override
    public final void openConnection(MavLinkConnectionListener listener) throws IOException {
        getUdpStream();
        onConnectionOpened(listener);
    }

    @Override
    public final void sendBuffer(byte[] buffer) throws IOException {
        try {
            /* Data transmission is possible if and only if a packet has first been received */
            if (hostAdd != null) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, hostAdd, hostPort);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final int readDataBlock(byte[] readData) throws IOException {
        DatagramPacket packet = new DatagramPacket(readData, readData.length);
        socket.receive(packet); /* This method blocks the thread until a datagram is received */
        hostAdd = packet.getAddress();
        hostPort = packet.getPort();
        return packet.getLength();
    }

    @Override
    public final int getConnectionType() {
        return MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP;
    }
}
