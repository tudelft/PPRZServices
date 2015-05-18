package com.pprzservices.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.aidllib.core.ConnectionParameter;
import com.pprzservices.core.mavlink.connection.MavLinkConnection;
import com.pprzservices.core.mavlink.connection.MavLinkConnectionListener;
import com.pprzservices.core.mavlink.connection.MavLinkConnectionTypes;
import com.pprzservices.core.mavlink.connection.types.BluetoothConnection;
import com.pprzservices.core.mavlink.connection.types.UdpConnection;

import java.util.concurrent.ConcurrentHashMap;

public class MavLinkService extends Service {

    private static final String TAG = MavLinkService.class.getSimpleName();

    Context mContext;

    private MavLinkServiceClient mMavLinkServiceClient;

    /**
     * Store MAVLink connection per connection type, e.g. UDP, Bluetooth...
     * as they all necessarily implement MavLinkConnection
     */
    final ConcurrentHashMap<ConnectionParameter, MavLinkConnection> mConnections = new ConcurrentHashMap<>();

    @Override
    public void onCreate()
    {
        super.onCreate();

        mContext = getApplicationContext();

        // Create a new service api object
        mMavLinkServiceClient = new MavLinkServiceClient(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        /* Called when calling startService(Intent) explicitly */

        return startId;
    }

    @Override
    public void onDestroy()
    {
        /* TODO: Handle onDestroy. */
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMavLinkServiceClient;
    }

    void connectMavConnection(ConnectionParameter connParams,
                              MavLinkConnectionListener listener) {
        MavLinkConnection conn = mConnections.get(connParams);
        if (conn == null) {

            // Get connection type
            final int connectionType = connParams.getConnectionType();

            // Get params bundle
            final Bundle paramsBundle = connParams.getParamsBundle();

            switch (connectionType) {

                case MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP: {
                    final int udpServerPort = paramsBundle.getInt("udp_port",
                    		MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP_PORT);
                    conn = new UdpConnection(udpServerPort);
                    break;
                }
                
                case MavLinkConnectionTypes.MAVLINK_CONNECTION_BLUETOOTH: {
                    conn = new BluetoothConnection(mContext);
                    break;
                }
                default: {
                    return;
                }
            }

            mConnections.put(connParams, conn);
        }

        if (conn.getConnectionStatus() == MavLinkConnection.MAVLINK_DISCONNECTED) {
            conn.connect(listener);
        }
    }

    void disconnectMavConnection(ConnectionParameter connParams,
                                 MavLinkConnectionListener listener) {
        final MavLinkConnection conn = mConnections.get(connParams);
        if (conn == null)
            return;

        conn.disconnect(listener);
    }
}
