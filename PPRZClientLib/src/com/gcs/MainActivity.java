package com.gcs;

import com.aidl.IEventListener;
import com.aidl.IMavLinkServiceClient;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	IMavLinkServiceClient mServiceClient;
	
	/**
     * Create service connection
     */
    ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceClient) {
			mServiceClient = IMavLinkServiceClient.Stub.asInterface(serviceClient);
			try {
				mServiceClient.addEventListener(TAG, listener);
			} catch (RemoteException e) {
				Log.e(TAG, "Failed to add listener", e);
			}
			Log.i(TAG, "Service connection established.");
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
	    			break;
    			}   			
	
	    		case "HEARTBEAT_FIRST": {
	    			break;
	    		}
	    			
	    		case "DISCONNECTED": {
	    			break;
	    		}
	    		
	    		case "ATTITUDE_UPDATED": {
	    			break;
	    		}
	    		
	    		case "ALTITUDE_SPEED_UPDATED": {
	    			break;
	    		}
	    		
	    		case "BATTERY_UPDATED": {
	    			
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
}
