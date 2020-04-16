/*******************************************************************************
 * Copyright (C) 2016 Maxim Integrated Products, Inc., All Rights Reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL MAXIM INTEGRATED BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * <p>
 * Except as contained in this notice, the name of Maxim Integrated
 * Products, Inc. shall not be used except as stated in the Maxim Integrated
 * Products, Inc. Branding Policy.
 * <p>
 * The mere transfer of this software does not imply any licenses
 * of trade secrets, proprietary technology, copyrights, patents,
 * trademarks, maskwork rights, or any other form of intellectual
 * property whatsoever. Maxim Integrated Products, Inc. retains all
 * ownership rights.
 * ******************************************************************************
 */
package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import graphing.LineGraphing;
import graphing.LinePointCollection;

public class SensorActivity extends Activity {
    /// define intent strings used the pass in data from other activities and services
    public static final String EXTRA_DEVICE = "extra_device";
    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";

    /// controls
    protected TextView mConnectionState;
    protected Button mBtnTest;
//    protected TextView mTextTempTop;
//    protected TextView mTextTempTopRaw;
//    protected TextView mTextTempBottom;
//    protected TextView mTextTempBottomRaw;
    protected TextView mTextLIS2DH_X;
    protected TextView mTextLIS2DH_Y;
    protected TextView mTextLIS2DH_Z;
    protected TextView mTextLIS2DHRaw;
//    protected TextView mTextPressure;
//    protected TextView mTextTemperature;
//    protected TextView mTextPressureRaw;
    protected TextView mTextHeartRate;
    protected TextView mTextHeartRateRaw;
    protected Button mBtnSubscribe;
    protected TextView mClickStartBtn;
    protected TextView mClickStartBtn2;
    protected TextView mAccelMissionStopped;
    protected TextView mECGMissionStopped;
    protected TextView mREMState;

    /// accelerometer
    protected LineGraphing lineGraphingAccelerometer;
    LinePointCollection linePointCollectionX;
    LinePointCollection linePointCollectionY;
    LinePointCollection linePointCollectionZ;

    /// pressure
//    protected LineGraphing lineGraphingPressure;
//    LinePointCollection linePointCollectionPressure;

    /// temperature 1
//    protected LineGraphing lineGraphingTemperature1;
//    LinePointCollection linePointCollectionTemperature1;

    /// temperature 2
//    protected LineGraphing lineGraphingTemperature2;
//    LinePointCollection linePointCollectionTemperature2;

    /// heartrate
    protected LineGraphing lineGraphingHeartrate;
    LinePointCollection linePointCollectionHeartrate;

    /// define variables of values that we care about
    private String mDeviceName;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private String mExportString;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothLeService mBluetoothLeService;
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    /// bool used to determine if we should enable the mission start button
    boolean isMissionButtonEnabled = true;

    /// define the UUIDs of the HSP BLE service and characteristics
    public final static String BLE_MAXIM_HSP_SERVICE = "5c6e40e8-3b7f-4286-a52f-daec46abe851"; // hsp service
    public final static String BLE_MAXIM_HSP_TEMPERATURE_TOP_CHARACTERISTIC = "3544531b-00c3-4342-9755-b56abe8e6c67"; // temp top
    public final static String BLE_MAXIM_HSP_TEMPERATURE_BOTTOM_CHARACTERISTIC = "3544531b-00c3-4342-9755-b56abe8e6a66"; // temp bottom
    public final static String BLE_MAXIM_HSP_ACCEL_CHARACTERISTIC = "e6c9da1a-8096-48bc-83a4-3fca383705af"; //accel
    public final static String BLE_MAXIM_HSP_PRESSURE_CHARACTERISTIC = "1d8a1932-da49-49ad-91d8-800832e7e940";  // pressure
    public final static String BLE_MAXIM_HSP_HEARTRATE_CHARACTERISTIC = "621a00e3-b093-46bf-aadc-abe4c648c569";  // heart rate
    public final static String BLE_MAXIM_HSP_COMMAND_CHARACTERISTIC = "36e55e37-6b5b-420b-9107-0d34a0e8675a"; // command

    /**
     * Subscribe to all of the HSP characteristics that we care about
     */
    private void subscribeCharacteristics() {
        for (List<BluetoothGattCharacteristic> chars : mGattCharacteristics) {
            for (BluetoothGattCharacteristic cha : chars) {
                if (cha.getService().getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_SERVICE)) == 0) {
//                    if (cha.getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_TEMPERATURE_TOP_CHARACTERISTIC)) == 0) {
//                        mBluetoothLeService.Subscribe(cha.getService().getUuid(), cha.getUuid());
//                    }
//                    if (cha.getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_TEMPERATURE_BOTTOM_CHARACTERISTIC)) == 0) {
//                        mBluetoothLeService.Subscribe(cha.getService().getUuid(), cha.getUuid());
//                    }
                    if (cha.getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_ACCEL_CHARACTERISTIC)) == 0) {
                        mBluetoothLeService.Subscribe(cha.getService().getUuid(), cha.getUuid());
                    }
                    if (cha.getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_HEARTRATE_CHARACTERISTIC)) == 0) {
                        mBluetoothLeService.Subscribe(cha.getService().getUuid(), cha.getUuid());
                    }
//                    if (cha.getUuid().compareTo(UUID.fromString(BLE_MAXIM_HSP_PRESSURE_CHARACTERISTIC)) == 0) {
//                        mBluetoothLeService.Subscribe(cha.getService().getUuid(), cha.getUuid());
//                    }
                }
            }
        }
    }

    /**
     * Called from the activity framework when the activity is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // bind the misc TextView fields
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mBtnTest = (Button) findViewById(R.id.btnTest);
//        mTextTempTop = (TextView) findViewById(R.id.textTempTop);
//        mTextTempTopRaw = (TextView) findViewById(R.id.textTempTopRaw);
//        mTextTempBottom = (TextView) findViewById(R.id.textTempBottom);
//        mTextTempBottomRaw = (TextView) findViewById(R.id.textTempBottomRaw);
        mTextLIS2DH_X = (TextView) findViewById(R.id.textLIS2DH_X);
        mTextLIS2DH_Y = (TextView) findViewById(R.id.textLIS2DH_Y);
        mTextLIS2DH_Z = (TextView) findViewById(R.id.textLIS2DH_Z);
        mTextLIS2DHRaw = (TextView) findViewById(R.id.textLIS2DHRaw);
//        mTextPressure = (TextView) findViewById(R.id.textPressure);
//        mTextTemperature = (TextView) findViewById(R.id.textTemperature);
//        mTextPressureRaw = (TextView) findViewById(R.id.textPressureRaw);
        mTextHeartRate = (TextView) findViewById(R.id.textHeartRate);
        mTextHeartRateRaw = (TextView) findViewById(R.id.textHeartRateRaw);
        mBtnSubscribe = (Button) findViewById(R.id.btnSubscribe);
        mClickStartBtn = (TextView) findViewById(R.id.clickStartBtn);
        mClickStartBtn2 = (TextView) findViewById(R.id.clickStartBtn2);
        mAccelMissionStopped = (TextView) findViewById(R.id.textView7);
        mECGMissionStopped = (TextView) findViewById(R.id.textView8);
        mREMState = (TextView) findViewById(R.id.remState);
        /// accelerometer
        lineGraphingAccelerometer = (LineGraphing) findViewById(R.id.lineGraphingAccelerometer);
//        /// pressure
//        lineGraphingPressure = (LineGraphing) findViewById(R.id.lineGraphingPressure);
//        /// temperature 1
//        lineGraphingTemperature1 = (LineGraphing) findViewById(R.id.lineGraphingTemperature1);
//        /// temperature 2
//        lineGraphingTemperature2 = (LineGraphing) findViewById(R.id.lineGraphingTemperature2);
        /// heartrate
        lineGraphingHeartrate = (LineGraphing) findViewById(R.id.lineGraphingHeartrate);

        mBtnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mBluetoothLeService.connect(mDeviceAddress);
            }
        });

        mClickStartBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                missionClick();
            }
        });

        final Intent intent = getIntent();
        final BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        mDeviceName = device.getName();
        mDeviceAddress = device.getAddress();
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);

        //getSupportActionBar().setTitle(mDeviceName);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // setup the accelerometer linegraph collections
        linePointCollectionX = new LinePointCollection(Color.RED, 20);
        linePointCollectionY = new LinePointCollection(Color.GREEN, 20);
        linePointCollectionZ = new LinePointCollection(Color.BLUE, 20);
        lineGraphingAccelerometer.addPointCollection(linePointCollectionX);
        lineGraphingAccelerometer.addPointCollection(linePointCollectionY);
        lineGraphingAccelerometer.addPointCollection(linePointCollectionZ);

//        // setup the pressure linegraph collections
//        linePointCollectionPressure = new LinePointCollection(Color.BLACK, 20);
//        lineGraphingPressure.addPointCollection(linePointCollectionPressure);
//
//        // setup the temperature 1 linegraph collections
//        linePointCollectionTemperature1 = new LinePointCollection(Color.BLACK, 20);
//        lineGraphingTemperature1.addPointCollection(linePointCollectionTemperature1);
//
//        // setup the temperature 2 linegraph collections
//        linePointCollectionTemperature1 = new LinePointCollection(Color.BLACK, 20);
//        lineGraphingTemperature1.addPointCollection(linePointCollectionTemperature1);
//
//        linePointCollectionTemperature2 = new LinePointCollection(Color.BLACK, 20);
//        lineGraphingTemperature2.addPointCollection(linePointCollectionTemperature2);

        linePointCollectionHeartrate = new LinePointCollection(Color.BLACK, 20);
        lineGraphingHeartrate.addPointCollection(linePointCollectionHeartrate);



    }

    /**
     * Called from the activity framework when the activity is paused
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    /**
     * Called from the activity framework when the activity is resuming
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }
    }

    /**
     * Define an intent filter of actions to respond to
     * @return Returns the intent filter that is created
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_CHARACTERISTIC_WRITE);
        return intentFilter;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName componentName, final IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(final ComponentName componentName) {
            mBluetoothLeService = null;
            //finish();
        }
    };

    /**
     * Handles various events fired by the Service.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                finish();   // get out of this activity and back to the parent
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                subscribeCharacteristics();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final String noData = getString(R.string.no_data);
                final String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_UUID_CHAR);
                final byte[] dataArr = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA_RAW);
                if (uuid != null) {
                    UpdateUI(UUID.fromString(uuid), dataArr);
                }
            } else if (BluetoothLeService.ACTION_CHARACTERISTIC_WRITE.equals(action)) {
                final String uuid = intent.getStringExtra(BluetoothLeService.EXTRA_UUID_CHAR);
                if (uuid.equalsIgnoreCase(BLE_MAXIM_HSP_COMMAND_CHARACTERISTIC)) {
                    EnableMissionButton(true);
                }
            }
        }
    };

    /**
     * Enable or disable the Start Mission Button based on HSP state variable
     * @param state Enabled or disabled status
     */
    private void EnableMissionButton(boolean state) {
        isMissionButtonEnabled = state;
        if (state) {
            mClickStartBtn2.setBackgroundColor(Color.BLACK);
        } else {
            mClickStartBtn2.setBackgroundColor(Color.LTGRAY);
        }
    }

    /**
     * Update the connection status
     * @param resourceId Resource id that is bound to the status test view field
     */
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int colourId;

                switch (resourceId) {
                    case R.string.connected:
                        colourId = android.R.color.holo_green_dark;
                        break;
                    case R.string.disconnected:
                        colourId = android.R.color.holo_red_dark;
                        break;
                    default:
                        colourId = android.R.color.black;
                        break;
                }

                mConnectionState.setText(resourceId);
                mConnectionState.setTextColor(getResources().getColor(colourId));
            }
        });
    }

    /**
     * Convert the temperature from celsius to fahrenheit
     * @param celsius temperature to convert
     * @return converted temperature in fahrenheit
     */
    private float ToFahrenheit(float celsius) {
        return celsius * 9 / 5 + 32;
    }

    /**
     * Convert the incoming byte data array into a temperature
     * @param data Data array containing two elements
     * @return temperature as a float in celcius
     */
    private float ToTemperature(byte[] data) {
        float value;
        value = (float) data[1] + (float) data[0] / 256.0f;
        return value;
    }

    /**
     * Convert the portion of the byte array into a float
     * @param data Array to convert, a part of this array is converted based on a offset
     * @param offset Offset into the array to extract the float
     * @return Returns the float that was converted
     */
    private float PartialArrayToFloat(byte[] data, int offset) {
        short value = (short) ((short) (data[offset + 1] << 8) + (short) data[offset]);
        return (float) value;
    }

    /**
     * Calculate the heart rate from the ECG RtoR device
     * @param data Incoming data to convert
     * @return Returns a float representing the heart rate
     */
    private float CalculateHeartRate(byte[] data) {
        float t = 8.0f;
        float value = 0;
        float RtoR = (float) ((int) data[1] << 8) + (float) data[0];
        float fmStr = (float) ((int) data[3] << 8) + (float) data[2];
        if (fmStr == 0.0f) t = 7.813f;
        if (RtoR > 0.0f) {
            value = 60000.0f / (RtoR * t);
        }
        return value;
    }

    /**
     * Update the UI for a given characteristic uuid using data
     * This will update all of the text view values for the given characteristic
     * It also will graph the incoming data
     * @param characteristic Characteristic to update with given data
     * @param data Data used to update the UI
     */
    private void UpdateUI(UUID characteristic, byte[] data) {
        float x = 0;
        float y = 0;
        float z = 0;
        float value = 0;
        String str = "";
        str = bytesToHex(data);
//        // temperature top
//        if (characteristic.compareTo(UUID.fromString(BLE_MAXIM_HSP_TEMPERATURE_TOP_CHARACTERISTIC)) == 0) {
//            mTextTempTopRaw.setText(str);
//            value = ToFahrenheit(ToTemperature(data));
//            mTextTempTop.setText(String.format("%.1f", value) + "F");
//            linePointCollectionTemperature1.addpoint(value);
//            lineGraphingTemperature1.plotPointCollection();
//        }
//        // temperature bottom
//        if (characteristic.compareTo(UUID.fromString(BLE_MAXIM_HSP_TEMPERATURE_BOTTOM_CHARACTERISTIC)) == 0) {
//            mTextTempBottomRaw.setText(str);
//            value = ToFahrenheit(ToTemperature(data));
//            mTextTempBottom.setText(String.format("%.1f", value) + "F");
//            linePointCollectionTemperature2.addpoint(value);
//            lineGraphingTemperature2.plotPointCollection();
//        }
        // acceleration
        if (characteristic.compareTo(UUID.fromString(BLE_MAXIM_HSP_ACCEL_CHARACTERISTIC)) == 0) {
            mTextLIS2DHRaw.setText(str);
            x = PartialArrayToFloat(data, 0);
            y = PartialArrayToFloat(data, 2);
            z = PartialArrayToFloat(data, 4);
            mTextLIS2DH_X.setText(String.format("X: %.1f", x));
            mTextLIS2DH_Y.setText(String.format("Y: %.1f", y));
            mTextLIS2DH_Z.setText(String.format("Z: %.1f", z));
            linePointCollectionX.addpoint(x);
            linePointCollectionY.addpoint(y);
            linePointCollectionZ.addpoint(z);
            lineGraphingAccelerometer.plotPointCollection();
            displayREMState(x, y, z);
        }
//        // pressure
//        if (characteristic.compareTo(UUID.fromString(BLE_MAXIM_HSP_PRESSURE_CHARACTERISTIC)) == 0) {
//            String pressureStr = "";
//            int n = 0;
//            int bits;
//            byte[] arr = {data[0], data[1], data[2], data[3]};
//            ByteBuffer bb = ByteBuffer.wrap(arr);
//            bb.order(ByteOrder.LITTLE_ENDIAN);
//            bits = bb.getInt();
//            float temperature = Float.intBitsToFloat(bits);
//            n = 4;
//            //bits = data[n] | data[n+1]<<8 | data[n+2]<<16 | data[n+3]<<24;
//            byte[] arr2 = {data[4], data[5], data[6], data[7]};
//            ByteBuffer bb2 = ByteBuffer.wrap(arr2);
//            bb2.order(ByteOrder.LITTLE_ENDIAN);
//            bits = bb2.getInt();
//            float pressure = Float.intBitsToFloat(bits);
//            mTextPressure.setText(String.format("%.1f", pressure) + "Pa");
//            linePointCollectionPressure.addpoint(pressure);
//            lineGraphingPressure.plotPointCollection();
//        }
        // heartrate
        if (characteristic.compareTo(UUID.fromString(BLE_MAXIM_HSP_HEARTRATE_CHARACTERISTIC)) == 0) {
            mTextHeartRateRaw.setText(str);
            value = CalculateHeartRate(data);
            mTextHeartRate.setText(String.format("%.1f", value));
            linePointCollectionHeartrate.addpoint(value);
            lineGraphingHeartrate.plotPointCollection();
        }
    }

    /// array of hex ascii values used for hex byte to string conversion
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Convert a series of bytes within a given array into a string
     * @param bytes Arbitrary array of bytes to convert
     * @return String is returned containing the bytes as hex ascii values
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     *  Demonstrates how to iterate through the supported GATT Services/Characteristics.
     * In this sample, we populate the data structure that is bound to the ExpandableListView
     * on the UI.
     * @param gattServices List of services to parse and display
     */
    private void displayGattServices(final List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        String uuid = null;
        final String unknownServiceString = getResources().getString(R.string.unknown_service);
        final String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        final List<Map<String, String>> gattServiceData = new ArrayList<>();
        final List<List<Map<String, String>>> gattCharacteristicData = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (final BluetoothGattService gattService : gattServices) {
            final Map<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, GattAttributeLookupTable.getAttributeName(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            final List<Map<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            final List<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                final Map<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, GattAttributeLookupTable.getAttributeName(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }

            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    /**
     * Given a TextView, output a string plus units concatenated
     * @param textView View to set
     * @param value String representing the value
     * @param units String representing the units
     */
    private void SetSensorText(TextView textView, String value, String units) {
        textView.setText(value + units);
    }

    /**
     * Create the menu for the options
     * @param menu the menu to set the options for
     * @return true if created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    /**
     * Called when the user selects a menu item
     * @param item Selected menu item
     * @return returns true if the selection was handled
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                mBluetoothLeService.disconnect();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mission button clicked
     * @param v The handle of the view clicked
     */
    public void onClick(View v) {
        missionClick();
    }

    /**
     * Start or stop the mission
     */
    private void missionClick() {
        byte[] data = new byte[1];
        //if (isMissionButtonEnabled == false) return;

        if (mClickStartBtn2.getText().toString().equalsIgnoreCase("Stop Mission") == true) {
            // stop
            data[0] = 0x00;
            mBluetoothLeService.writeCharacteristic(UUID.fromString(BLE_MAXIM_HSP_SERVICE), UUID.fromString(BLE_MAXIM_HSP_COMMAND_CHARACTERISTIC), data);
            mClickStartBtn2.setText("Start Mission");
            EnableMissionButton(false);
        } else {
            // start
            data[0] = 0x01;
            mBluetoothLeService.writeCharacteristic(UUID.fromString(BLE_MAXIM_HSP_SERVICE), UUID.fromString(BLE_MAXIM_HSP_COMMAND_CHARACTERISTIC), data);
            mClickStartBtn2.setText("Stop Mission");
            EnableMissionButton(false);
        }
    }

    private void displayREMState(double x_accel, double y_accel, double z_accel) {
        int heartrate = 60;
        double var = 2.0886 - (0.1648 * x_accel) + (0.0749 * y_accel) - (0.0303 * z_accel) - (0.0140 * heartrate);
        if (1./(1+ Math.exp(-1*var)) < 0.5) {
            mREMState.setText("IN REM");
        }
        else {
            mREMState.setText("NOT IN REM");
        }

    }
}
