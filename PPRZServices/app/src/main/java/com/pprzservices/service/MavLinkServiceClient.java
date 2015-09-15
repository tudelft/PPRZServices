package com.pprzservices.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageButton;

import com.MAVLink.MAVLinkPacket;
import com.aidllib.IEventListener;
import com.aidllib.IMavLinkServiceClient;
import com.aidllib.core.ConnectionParameter;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.aidllib.core.model.Altitude;
import com.aidllib.core.model.Attitude;
import com.aidllib.core.model.Battery;
import com.aidllib.core.model.Position;
import com.aidllib.core.model.Speed;
import com.aidllib.core.model.State;
import com.aidllib.core.model.Heartbeat;
import com.pprzservices.core.drone.Drone;
import com.pprzservices.core.drone.DroneClient;
import com.pprzservices.core.mavlink.connection.MavLinkConnection;
import com.pprzservices.core.mavlink.connection.MavLinkConnectionListener;
import com.pprzservices.core.mavlink.connection.MavLinkConnectionTypes;


import java.lang.ref.SoftReference;
import java.util.ArrayList;
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

    private SparseArray<DroneClient> mDroneClients = new SparseArray<>();

    public Handler handler = new Handler();

    private final int requestDelay = 2000; //milliseconds

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
	public Bundle getAttribute(String type, int sysId) throws RemoteException {
		Bundle carrier = new Bundle();

		final Drone drone = mDroneClients.get(sysId).getDrone();

		switch (type) {
			case "HEARTBEAT": {
				carrier.putParcelable(type, new Heartbeat(drone.getSysid(), drone.getCompid()));
				break;
			}
			
			case "ALTITUDE": {
				carrier.putParcelable(type, new Altitude(drone.getAltitude(), drone.getTargetAltitude(), drone.getAGL()));
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
                break;
			}

            case "STATE": {
                carrier.putParcelable(type, new State(drone.isArmed(),drone.isFlying()));
                break;
            }

			case "WAYPOINTS": {

                Log.d("wpTest1","ac"+String.valueOf(sysId)+" "+String.valueOf(mDroneClients.get(sysId).getDrone().getWaypoints().size()));

                carrier.putParcelableArrayList(type, (ArrayList<? extends Parcelable>) drone.getWaypoints());
				break;
			}

			case "CURRENT_BLOCK": {
			    carrier.putInt(type, drone.getCurrentBlock());
				break;
			}

            case "BLOCKS": {

                Log.d("blockTest1","ac"+String.valueOf(sysId)+" "+String.valueOf(mDroneClients.get(sysId).getDrone().getBlocks()));

                carrier.putStringArrayList(type, (ArrayList<String>) drone.getBlocks());
                break;
            }
			
			default:
				break;
		}
		
		return carrier;
	}

	@Override
	public void addEventListener(String id, IEventListener listener)
			throws RemoteException {
		Log.i(TAG, "Adding event listener...");

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

        /* Take the incoming connection parameters and put all port numbers in a single connection parameter package per port/drone. They will be sent to their droneclient. */
        // Get connection type
        final int connectionType = connParams.getConnectionType();

        switch (connectionType) {
            case MavLinkConnectionTypes.MAVLINK_CONNECTION_UDP: {
                // Get params bundle
                final Bundle paramsBundle = connParams.getParamsBundle();
                //Get a list of udp ports that should be connected to
                final ArrayList<Integer> udpServerPortList = paramsBundle.getIntegerArrayList("udp_port");
                //Get a list of the system ids that are used (same order as the udp port list)
                final ArrayList<Integer> sysIdList = paramsBundle.getIntegerArrayList("sysIds");

                //Loop over the list of udp ports, put them in a new connectionParameter package and create for every one of them a new droneClient.
                for (int i = 0; i < udpServerPortList.size(); i++) {
                    Bundle extraParams = new Bundle();
                    extraParams.putInt("udp_port", udpServerPortList.get(i));
                    connParams = new ConnectionParameter(connectionType, extraParams);
                    mDroneClients.put(sysIdList.get(i), new DroneClient(getService().getApplicationContext(), connParams, this));
                    mDroneClients.get(sysIdList.get(i)).connect(connParams);
                }

//                handler.post(initialRequester);
                handler.post(waypointsRequester);
                break;
            }

            case MavLinkConnectionTypes.MAVLINK_CONNECTION_BLUETOOTH: {
                mDroneClients.put(0,new DroneClient(getService().getApplicationContext(), connParams, this));
                mDroneClients.get(0).connect(connParams);
                break;
            }

            default: {
                return;
            }
        }
    }

	@Override
    public void disconnectDroneClient() throws RemoteException  {
        for(int i=0; i<mDroneClients.size(); i++) {
            mDroneClients.get(mDroneClients.keyAt(i)).destroy();
        }
        mDroneClients.clear();
    }

	@Override
	public void onEvent(String type, int sysId) throws RemoteException {
        if(sysId!=0) {
            for (IEventListener listener : mListeners.values()) {
                listener.onEvent(type, sysId);
            }
        }
	}

    @Override
    public void onCallback(Bundle carrier, int sysId) {
        carrier.setClassLoader(Waypoint.class.getClassLoader());
        switch (carrier.getString("TYPE")) {
            case "REQUEST_ALL_WP_LISTS": {
                handler.post(waypointsRequester);
                break;
            }

            case "REQUEST_WP_LIST": {
                mDroneClients.get(sysId).requestWpList();
                break;
            }

            case "WRITE_WP": {
                Waypoint waypoint = carrier.getParcelable("WP");
                mDroneClients.get(sysId).writeWp(waypoint.getLat(), waypoint.getLon(), waypoint.getAlt(), (short) waypoint.getSeq());
                break;
            }

            case "REQUEST_BLOCK_LIST": {
                handler.post(blocksRequester);
                break;
            }

            case "BLOCK_SELECTED": {
                mDroneClients.get(sysId).setCurrentBlock(carrier.getShort("SEQ"));
                break;
            }

            case "REQUEST_TAKE_OFF": {
                //Activate the selected block (take-off block)
                mDroneClients.get(sysId).setCurrentBlock(carrier.getShort("SEQ"));
                //Set launch mode to active
//                mDroneClients.get(sysId).commandLaunch(); //NOT USED BECAUSE FLIGHT PLAN SETS THE LAUNCH PARAMETER TO 1 AT TAKE-OFF BLOCK
                break;
            }
        }
    }


    int i = 0;
    private Runnable blocksRequester = new Runnable() {
        @Override
        public void run() {
            mDroneClients.get(mDroneClients.keyAt(i)).requestBlockList();
            i++;

            //Stop repeating if blocks have been requested for all aircraft
            if(i==mDroneClients.size()) {
                handler.removeCallbacks(this);
                i = 0;
                return;
            }

            //delay for the runnable
            handler.postDelayed(blocksRequester, requestDelay);
        }
    };

    int j = 0;
    private Runnable waypointsRequester = new Runnable() {
        @Override
        public void run() {
            mDroneClients.get(mDroneClients.keyAt(j)).requestWpList();
            j++;

            //Stop repeating if waypoints have been requested for all aircraft
            if(j==mDroneClients.size()) {
                handler.removeCallbacks(this);
                j = 0;
                return;
            }
            //delay for the runnable
            handler.postDelayed(waypointsRequester, requestDelay);
        }
    };

    private Runnable initialRequester = new Runnable() {
        @Override
        public void run() {
            if(i<mDroneClients.size()) {
                mDroneClients.get(mDroneClients.keyAt(i)).requestBlockList();
                i++;
            } else if(j<mDroneClients.size()) {
                mDroneClients.get(mDroneClients.keyAt(j)).requestWpList();
                j++;
            } else {
                handler.removeCallbacks(this);
                i = 0;
                j = 0;
                return;
            }
            //delay for the runnable
            handler.postDelayed(initialRequester, requestDelay);
        }
    };
}