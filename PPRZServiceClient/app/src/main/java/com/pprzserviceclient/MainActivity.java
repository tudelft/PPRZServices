package com.pprzserviceclient;

import com.aidllib.IEventListener;
import com.aidllib.IMavLinkServiceClient;
import com.aidllib.core.ConnectionParameter;
import com.aidllib.core.mavlink.waypoints.Waypoint;
import com.aidllib.core.model.Altitude;
import com.aidllib.core.model.Attitude;
import com.aidllib.core.model.Heartbeat;
import com.aidllib.core.model.Position;
import com.aidllib.core.model.Speed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private final int delay = 500;

	private Handler handler;

	IMavLinkServiceClient mServiceClient;
	
	private Button connectButton;

	private Button wpButton;

	private boolean isConnected;

	private TextView serviceConn;
	
	private TextView altitude;
	
	private TextView targetAltitude;
	
	private TextView groundSpeed;
	
	private TextView airSpeed;
	
	private TextView climbSpeed;
	
	private TextView targetSpeed;
	
	private TextView roll;
	
	private TextView pitch;

	private TextView yaw;

	private TextView lat;

	private TextView lon;

	private TextView gpsAlt;

	private TextView wpCount;

	private List<String> blocks;

	private TextView blockCount;

//	private Spinner blockSpinner;
	
	/**
     * Create service connection
     */
    ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceClient) {
			mServiceClient = IMavLinkServiceClient.Stub.asInterface(serviceClient);

			if (mServiceClient == null)
				Log.d(TAG, "mServiceClient is null");

			try {
				mServiceClient.addEventListener(TAG, listener);

				Log.d(TAG, "Listener has been added");

			} catch (RemoteException e) {
				Log.e(TAG, "Failed to add listener", e);
			}
			serviceConn.setText(getString(R.string.service_connection) + " yes");
		}
		
        @Override
        public void onServiceDisconnected(ComponentName name) {
        	Log.i(TAG, "Service connection lost.");
        }
    };
    
    private IEventListener.Stub listener = new IEventListener.Stub() {
    	@Override
		public void onConnectionFailed() {
    		/* TODO: Handle connection failure */
    	}
    	
    	@Override
        public void onEvent(String type) {
    		switch(type) {
    			case "CONNECTED": {
    				Log.d(TAG, "Connected!");
    				isConnected = true;
    				updateConnectButton();  				
	    			break;
    			}   			
	
	    		case "HEARTBEAT_FIRST": {
	    			break;
	    		}
	    			
	    		case "DISCONNECTED": {
	    			Log.d(TAG, "Disconnected!");
	    			isConnected = false;
	    			updateConnectButton();	    			
	    			break;
	    		}
	    		
	    		case "ATTITUDE_UPDATED": {
	    			updateAttitude();
	    			break;
	    		}
	    		
	    		case "ALTITUDE_SPEED_UPDATED": {
	    			updateAltitude();
	    			updateSpeed();
	    			break;
	    		}

	    		case "POSITION_UPDATED": {
					updatePosition();
	    			break;
	    		}
	    		
	    		case "SATELLITES_VISIBLE_UPDATED": {
	    			break;
	    		}

				case "WAYPOINTS_UPDATED": {
                    Log.d(TAG, "Waypoints updated");
					updateWaypoints();
					break;
				}

				case "MISSION_BLOCKS_UPDATED": {
					updateBlocks();
					break;
				}

				case "CURRENT_BLOCK_UPDATED": {
//					updateMissionBlocksSpinnerSelection();
					break;
				}

	    		default:
	    			break;
    		}
    	}
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		handler = new Handler();
		
		connectButton = (Button) findViewById(R.id.connectButton);

		wpButton = (Button) findViewById(R.id.wpButton);

		isConnected = false;

		serviceConn = (TextView) findViewById(R.id.service_connection);

		altitude = (TextView) findViewById(R.id.altitude);
		
		targetAltitude = (TextView) findViewById(R.id.target_altitude);
		
		groundSpeed = (TextView) findViewById(R.id.ground_speed);
		
		airSpeed = (TextView) findViewById(R.id.air_speed);
		
		climbSpeed = (TextView) findViewById(R.id.climb_speed);
		
		targetSpeed = (TextView) findViewById(R.id.target_speed);
		
		roll = (TextView) findViewById(R.id.roll);
		
		pitch = (TextView) findViewById(R.id.pitch);
		
		yaw = (TextView) findViewById(R.id.yaw);

		lat = (TextView) findViewById(R.id.lat);

		lon = (TextView) findViewById(R.id.lon);

		gpsAlt = (TextView) findViewById(R.id.gps_alt);

		wpCount = (TextView) findViewById(R.id.waypoint_count);

		blockCount = (TextView) findViewById(R.id.block_count);

//		blockSpinner = (Spinner) findViewById(R.id.block_spinner);
//		blockSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();

		ComponentName componentName = new ComponentName("com.pprzservices", "com.pprzservices.service.MavLinkService");
        Intent intent = new Intent();
		intent.setComponent(componentName);
        if (!bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)) {

			// TODO: Handle service not binding problems

			Log.e(TAG, "The service could not be bound");
		} else {
			Log.d(TAG, "Service was bound");
		}
    }

	@Override
	public void onPause() {
		super.onPause();
		//Disconnect from service
		try{
			if (isConnected) {
				mServiceClient.disconnectDroneClient();
			}
		} catch(RemoteException e) {
			Log.e(TAG, "Failed to disconnect from service while closing application", e);
		}
	}
    
    @Override
    public void onDestroy() {
        super.onDestroy();;
    	try {
			mServiceClient.removeEventListener(TAG);
		} catch (RemoteException e) {
			// TODO Catch exception
		}
    	unbindService(serviceConnection);
    }
    
    private ConnectionParameter retrieveConnectionParameters() {
		
		/* TODO: Fetch connection type */
		
        final int connectionType = 0; // UDP connection
        
        /* TODO: Fetch server port */
        
        final int serverPort = 5000;
        
        Bundle extraParams = new Bundle();

        ConnectionParameter connParams;
        switch (connectionType) {
            case 0:
                extraParams.putInt("udp_port", serverPort);
                connParams = new ConnectionParameter(connectionType, extraParams);
                break;

			case 1:
				connParams = new ConnectionParameter(connectionType, extraParams);
				break;

            default:
            	connParams = null;
                break;
        }

        return connParams;
    }
	
	public void connectToDroneClient(){
        final ConnectionParameter connParams = retrieveConnectionParameters();
        if (!(connParams == null)) {
	        try {
	        	mServiceClient.connectDroneClient(connParams);
	        } catch (RemoteException e) {
	            /* TODO: Handle remote exception */
	        }
        }
        
        /* TODO: Update the text of the connect button */
        
    }
	
    public void onConnectButtonRequest(View view) {
    	if (!isConnected) {
			connectToDroneClient();
		} else {
			try {
				mServiceClient.disconnectDroneClient();
			} catch (RemoteException e) {
				// TODO: Handle exception
			}
		}
    }

	public void onWpButtonRequest(View view) {
		if (isConnected) {
			try {
				Bundle carrier = new Bundle();
				carrier.putString("TYPE", "WRITE_WP");
				Waypoint waypoint = new Waypoint((float)0., (float)0., (float)0., 3, (byte)0, (byte)0);
				carrier.putParcelable("WP", waypoint);
				mServiceClient.onCallback(carrier);
			} catch (RemoteException e) {
				// TODO: Handle exception
			}
		}
	}

//	public void onBlockButtonRequest(View view) {
//		if (isConnected) {
//			try {
//				mServiceClient.requestMissionBlockList();
//			} catch (RemoteException e) {
//				// TODO: Handle exception
//			}
//		}
//	}

	/**
     * This runnable object is created such that the update is performed
     * by the UI handler. This is a design requirement posed by the Android SDK
     */
	private void updateConnectButton() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					if (isConnected) {
						connectButton.setText(R.string.disconnect_button);
					} else {
						connectButton.setText(R.string.connect_button);
					}
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the connect button", t);
				}
			}
		});
	}
	
	/**
	 * This runnable object is created to update the altitude
	 */
	private void updateAltitude() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Altitude mAltitude = getAttribute("ALTITUDE");					
					altitude.setText(getString(R.string.altitude) + " " + String.format("%.2f", mAltitude.getAltitude()));
					targetAltitude.setText(getString(R.string.target_altitude) + " " + String.format("%.2f", mAltitude.getTargetAltitude()));
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the altitude", t);
				}
			}
		});
	}
	
	/**
	 * This runnable object is created to update the ground and airspeeds
	 */
	private void updateSpeed() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Speed mSpeed = getAttribute("SPEED");					
					groundSpeed.setText(getString(R.string.ground_speed) + " " + String.format("%.2f", mSpeed.getGroundSpeed()));
					airSpeed.setText(getString(R.string.air_speed) + " " + String.format("%.2f", mSpeed.getAirspeed()));
					climbSpeed.setText(getString(R.string.climb_speed) + " " + String.format("%.2f", mSpeed.getClimbSpeed()));
					targetSpeed.setText(getString(R.string.target_speed) + " " + String.format("%.2f", mSpeed.getTargetSpeed()));
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the speed", t);
				}
			}
		});
	}
	
	/**
	 * This runnable object is created to update the attitude
	 */
	private void updateAttitude() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Attitude mAttitude = getAttribute("ATTITUDE");					
					roll.setText(getString(R.string.roll) + " " + String.format("%.2f", (mAttitude.getRoll() * 180.0 / Math.PI)));
					pitch.setText(getString(R.string.pitch) + " " + String.format("%.2f", (mAttitude.getPitch() * 180.0 / Math.PI)));
					yaw.setText(getString(R.string.yaw) + " " + String.format("%.2f", (mAttitude.getYaw() * 180.0 / Math.PI)));
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the attitude", t);
				}
			}
		});
	}

	private void updatePosition() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Position mPosition = getAttribute("POSITION");
					lat.setText(getString(R.string.lat) + " " + String.format("%d", mPosition.getLat()));
					lon.setText(getString(R.string.lon) + " " + String.format("%d", mPosition.getLon()));
					gpsAlt.setText(getString(R.string.gps_alt) + " " + String.format("%d", mPosition.getAlt()));
				} catch (Throwable t) {
					Log.e(TAG, "Error while updating the position", t);
				}
			}
		});
	}

	private void updateWaypoints() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle carrier = mServiceClient.getAttribute("WAYPOINTS");
					carrier.setClassLoader(Waypoint.class.getClassLoader());
					List<Waypoint> waypoints = carrier.getParcelableArrayList("WAYPOINTS");
					wpCount.setText(getString(R.string.waypoint_count) + " " + String.format("%d", waypoints.size()));
				} catch (RemoteException e) {
					// TODO: Handle exception
				}
			}
		});
	}

	private void updateBlocks() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				try {
					Bundle carrier = mServiceClient.getAttribute("BLOCKS");
					blocks = carrier.getStringArrayList("BLOCKS");
					blockCount.setText(getString(R.string.block_count) + " " + String.format("%d", blocks.size()));
//					updateMissionBlocksSpinner();
				} catch (RemoteException e) {
					// TODO: Handle exception
				}
			}
		});
	}

	public <T extends Parcelable> T getAttribute(String type) {
        if (type == null)
            return null;

        T attribute = null;
        Bundle carrier = null;
        try {
            carrier = mServiceClient.getAttribute(type);   
        } catch (RemoteException e) {
            /* TODO: Handle remote exception */
        }

        if (carrier != null) {
            ClassLoader classLoader = getAttributeClassLoader(type);
            if (classLoader != null) {
                carrier.setClassLoader(classLoader);
                attribute = carrier.getParcelable(type);
            }
        }

        return attribute == null ? this.<T>getAttributeDefaultValue(type) : attribute;
    }
	
	@SuppressWarnings("unchecked")
	private <T extends Parcelable> T getAttributeDefaultValue(String type) {
        switch (type) {
            case "HEARTBEAT":
                return (T) new Heartbeat();
                
            case "ALTITUDE":
            	return (T) new Altitude();
            	
            case "SPEED":
            	return (T) new Speed();
            	
            case "ATTITUDE":
            	return (T) new Attitude();

			case "POSITION":
				return (T) new Position();
                
            default:
            	return null;
        }
	}
	
	private ClassLoader getAttributeClassLoader(String type) {
		switch (type) {
			case "HEARTBEAT":
				return Heartbeat.class.getClassLoader();
				
			case "ALTITUDE":
				return Altitude.class.getClassLoader();
				
			case "SPEED":
				return Speed.class.getClassLoader();
				
			case "ATTITUDE":
				return Attitude.class.getClassLoader();

			case "POSITION":
				return Position.class.getClassLoader();

			default:
				return null;
		}
	 }



//	private void updateMissionBlocksSpinner() {
//		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, blocks);
//		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		blockSpinner.setAdapter(spinnerArrayAdapter);
//	}
//
//	private void updateMissionBlocksSpinnerSelection() {
//		if (blocks.size() > 0) {
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						Log.d(TAG, "Block: " + mServiceClient.getCurrentBlock());
//						blockSpinner.setSelection(mServiceClient.getCurrentBlock());
//					} catch (RemoteException e) {
//						// TODO: Handle exception
//					}
//				}
//			}, delay);
//		}
//	}

//	@Override
//	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//		try {
//			mServiceClient.onBlockSelected(position);
//		} catch (RemoteException e) {
//			// TODO: Handle exception
//		}
//	}
//
//	@Override
//	public void onNothingSelected(AdapterView<?> parent) {
//	}
}

