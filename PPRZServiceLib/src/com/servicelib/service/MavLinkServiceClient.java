package com.servicelib.service;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.MAVLink.MAVLinkPacket;
import com.aidl.IEventListener;
import com.aidl.IMavLinkServiceClient;
import com.aidl.core.ConnectionParameter;
import com.aidl.core.model.Altitude;
import com.aidl.core.model.Attitude;
import com.aidl.core.model.Battery;
import com.aidl.core.model.Position;
import com.aidl.core.model.Speed;
import com.aidl.core.model.Heartbeat;
import com.servicelib.core.drone.Drone;
import com.servicelib.core.drone.DroneClient;
import com.servicelib.core.mavlink.connection.MavLinkConnection;
import com.servicelib.core.mavlink.connection.MavLinkConnectionListener;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MavLinkServiceClient.java
 * @author lncsikkel
 *
 * Extending the IMavLinkServiceClient IBinder interface file allows us to pass
 * a reference of this instance to listeners.
 */
public class MavLinkServiceClient extends IMavLinkServiceClient.Stub {
	
	private static final String TAG = MavLinkServiceClient.class.getSimpleName();

    final ConcurrentHashMap<String, IEventListener> mListeners = new ConcurrentHashMap<>();

    private final SoftReference<MavLinkService> mServiceRef;

    private DroneClient mDroneClient;
    
    public MavLinkServiceClient(MavLinkService service) {
        mServiceRef = new SoftReference<>(service);
    }

    public MavLinkService getService() {
        MavLinkService service = mServiceRef.get();
        if (service == null) {
            /* TODO: Handle null exception. */
        }
        return service;
    }

    public boolean sendData(ConnectionParameter connParams, MAVLinkPacket packet) {
        final MavLinkConnection mavConnection = getService().mConnections.get(connParams);
        if (mavConnection == null) return false;

        if (mavConnection.getConnectionStatus() != MavLinkConnection.MAVLINK_DISCONNECTED) {
            mavConnection.sendMavPacket(packet);
            return true;
        }

        return false;
    }

    public int getConnectionStatus(ConnectionParameter connParams) {
        final MavLinkConnection mavConnection = getService().mConnections.get(connParams);
        if (mavConnection == null) {
            return MavLinkConnection.MAVLINK_DISCONNECTED;
        }

        return mavConnection.getConnectionStatus();
    }

    public void connectMavLink(ConnectionParameter connParams,
                               MavLinkConnectionListener listener) {
        getService().connectMavConnection(connParams, listener);
    }

    public void disconnectMavLink(ConnectionParameter connParams,
                                  MavLinkConnectionListener listener) {
        getService().disconnectMavConnection(connParams, listener);
    }

	@Override
	public Bundle getAttribute(String type) throws RemoteException {		
		Bundle carrier = new Bundle();
        
		final Drone drone = mDroneClient.getDrone();
		
		switch (type) {
			case "HEARTBEAT": {
				carrier.putParcelable(type, new Heartbeat(drone.getSysid(), drone.getCompid()));
				break;
			}
			
			case "ALTITUDE": {
				carrier.putParcelable(type, new Altitude(drone.getAltitude(), drone.getTargetAltitude()));
				break;
			}
			
			case "ATTITUDE": {
				carrier.putParcelable(type, new Attitude(drone.getRoll(), drone.getPitch(), drone.getYaw()));
				break;
			}
			
			case "SPEED": {
				carrier.putParcelable(type, new Speed(drone.getGroundSpeed(), drone.getAirSpeed(), drone.getClimbSpeed(), drone.getTargetSpeed()));
				break;
			}
			
			case "BATTERY": {
				carrier.putParcelable(type, new Battery(drone.getBattVolt(), drone.getBattLevel(), drone.getBattCurrent()));
				break;
			}
			
			case "POSITION": {
				carrier.putParcelable(type, new Position(drone.getSatVisible(), drone.getTimeStamp(), drone.getLat(), drone.getLon(), drone.getAlt(), drone.getHdg()));
			}
			
			default:
				break;
		}
		
		return carrier;
	}

	@Override
	public void addEventListener(String id, IEventListener listener)
			throws RemoteException {
		mListeners.put(id, listener);
	}

	@Override
	public void removeEventListener(String id) throws RemoteException {
		mListeners.remove(id);
	}
	
	@Override
    public void connectDroneClient(ConnectionParameter connParams) throws RemoteException  {
        if (connParams == null)
            return;

        mDroneClient = new DroneClient(getService().getApplicationContext(), connParams, this);   
        mDroneClient.connect(connParams);
    }

	@Override
    public void disconnectDroneClient() throws RemoteException  {
        if (mDroneClient == null)
            return;

        mDroneClient.disconnect();
    }

	@Override
	public void onEvent(String type) throws RemoteException {
		for(IEventListener listener : mListeners.values())
		{
			listener.onEvent(type);
		}
	}
}