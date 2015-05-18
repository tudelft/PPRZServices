package com.pprzservices.core.mavlink.connection.types;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.pprzservices.core.mavlink.connection.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides support for MAVLink connection via Bluetooth.
 * @author Lodewijk Sikkel <l.n.c.sikkel@tudelft.nl>
 */
public class BluetoothConnection extends MavLinkConnection {

    private static final String TAG = BluetoothConnection.class.getSimpleName();

    private BluetoothManager mBluetoothManager;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothLeScanner mBluetoothScanner;

    private ScanSettings mScanSettings;

    private List<ScanFilter> mScanFilters;

    private String mBluetoothDeviceAddress;

    private BluetoothGatt mBluetoothGatt;

    public final static String ACTION_GATT_CONNECTED = "com.example.bluetooth.le.ACTION_GATT_CONNECTED";

    public final static String ACTION_GATT_DISCONNECTED = "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";

    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";

    public final static String ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    Context mContext;

    Handler mHandler;

    public BluetoothConnection(Context context) {
        // The context is passed as this is not an activity class
        mContext = context;

        // The handler grabs hold of the looper of the current service thread
        mHandler = new Handler();

        mBluetoothManager = (BluetoothManager) mContext.getSystemService(context.BLUETOOTH_SERVICE);

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Verify whether Bluetooth has been enabled on the device
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            // TODO: Handle the case when bluetooth is not enabled
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mBluetoothScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            mScanFilters = new ArrayList<ScanFilter>();
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT < 21) {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    } else {
                        mBluetoothScanner.stopScan(mScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            } else {
                mBluetoothScanner.startScan(mScanCallback);
            }
        } else {
            if (Build.VERSION.SDK_INT < 21) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            } else {
                mBluetoothScanner.stopScan(mScanCallback);
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Connect to: " + device.toString());
                    connectToBTDevice(device);
                }
            });
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "Scan result: " + String.valueOf(callbackType) + " " + result.toString());
            BluetoothDevice btDevice = result.getDevice();
            connectToBTDevice(btDevice);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i(TAG, "Scan result: " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan failed: " + errorCode);
        }
    };

    public void connectToBTDevice(BluetoothDevice device) {
        if (mBluetoothGatt == null) {
            mBluetoothGatt = device.connectGatt(mContext, false, mGattCallback);

            // Stop scanning after the first device has been detected
            scanLeDevice(false);// will stop after first device detection
        }
    }

    // GATT callback functions
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch(newState) {
                case BluetoothProfile.STATE_CONNECTED: {
                    Log.i(TAG, "Connected to GATT server.");
                    mBluetoothGatt.discoverServices();
                }

                case BluetoothProfile.STATE_DISCONNECTED: {
                    Log.i(TAG, "Disconnected from GATT server.");
                }

                default:
                    // TODO: Handle the default case
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // TODO: Handle onServiceDiscovered
        }

        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            // TODO: Handle read characteristic
        }
    };

    @Override
    public final void closeConnection() throws IOException {
        // Close the GATT connection to the device
        if (mBluetoothGatt == null) {
            return;
        }

        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public final void openConnection(MavLinkConnectionListener listener) throws IOException {

        // Start scanning for devices
        scanLeDevice(true);

        // TODO: Handle retries

        onConnectionOpened(listener);
    }

    @Override
    public final void sendBuffer(byte[] buffer) throws IOException {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public final int readDataBlock(byte[] readData) throws IOException {

        // return packet length
        return 0;
    }

    @Override
    public final int getConnectionType() {
        return MavLinkConnectionTypes.MAVLINK_CONNECTION_BLUETOOTH;
    }
}
