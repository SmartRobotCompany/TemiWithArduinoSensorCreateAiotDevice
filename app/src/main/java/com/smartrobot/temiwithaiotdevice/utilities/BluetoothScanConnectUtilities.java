package com.smartrobot.temiwithaiotdevice.utilities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;

import com.smartrobot.temiwithaiotdevice.MainActivity;
import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;
import com.smartrobot.temiwithaiotdevice.constant.BluetoothConstant;
import com.smartrobot.temiwithaiotdevice.listener.BluetoothOpenFunctionListener;
import com.smartrobot.temiwithaiotdevice.listener.OnAskBulbLightStatusListener;
import com.smartrobot.temiwithaiotdevice.listener.OnBulbLightListener;
import com.smartrobot.temiwithaiotdevice.listener.OnButtonAiotClickListener;
import com.smartrobot.temiwithaiotdevice.listener.OnDoorCloseOpenListener;
import com.smartrobot.temiwithaiotdevice.listener.OnMaskDetectListener;
import com.smartrobot.temiwithaiotdevice.listener.OnTemperatureHumidityUncomfortListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

public class BluetoothScanConnectUtilities{

    private final static String TAG = "Debug_" +  BluetoothScanConnectUtilities.class.getSimpleName();

    private Context context;
    private Activity activity;
    private String connectBleName;
    private String connectBleAddress;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;

    //Parameter of BLE to Send and Receive Data
    private BluetoothGattCharacteristic bluetoothGattCharacteristicRead;
    private BluetoothGattCharacteristic bluetoothGattCharacteristicWrite;
    private BluetoothGattDescriptor bluetoothGattDescriptorRead;
    private BluetoothGatt bluetoothGatt;

    //List and Set for Store BLE Scan Result
    private ArrayList<BluetoothDevice> bluetoothDeviceArrayList = new ArrayList<BluetoothDevice>();
    private HashSet<BluetoothDevice> bluetoothDeviceHashSet = new HashSet<BluetoothDevice>();
    private ArrayList<BluetoothGatt> bluetoothGattArrayList;

    //BLE Flag
    private boolean temiBleConnectState = false;
    private boolean bleScanState = false;

    //BLE Scan Time
    private int scanAmounts = 0;

    //Handler Parameter
    private Handler stopScanBleHandler = null;

    private BluetoothOpenFunctionListener bluetoothOpenFunctionListener;
    private OnButtonAiotClickListener onButtonAiotClickListener;
    private OnTemperatureHumidityUncomfortListener onTemperatureHumidityUncomfortListener;
    private OnMaskDetectListener onMaskDetectListener;
    private OnDoorCloseOpenListener onDoorCloseOpenListener;
    private OnBulbLightListener onBulbLightListener;
    private OnAskBulbLightStatusListener onAskBulbLightStatusListener;

    private boolean isTemperatureStatusSendOrNot = false;
    private float highTemperatureValue = 30.0f;
    private float lowTemperatureValue = 20.0f;
    private float highHumidityValue = 70.0f;
    private float lowHumidityValue = 20.0f;
    private AsyncTask scanAndConnectTask;

    public BluetoothScanConnectUtilities(Context context,
                                         Activity activity,
                                         String connectBleName,
                                         String connectBleAddress,
                                         ArrayList<BluetoothGatt> bluetoothGattArrayList,
                                         BluetoothOpenFunctionListener bluetoothOpenFunctionListener,
                                         OnButtonAiotClickListener onButtonAiotClickListener,
                                         OnTemperatureHumidityUncomfortListener onTemperatureHumidityUncomfortListener,
                                         OnMaskDetectListener onMaskDetectListener,
                                         OnDoorCloseOpenListener onDoorCloseOpenListener,
                                         OnBulbLightListener onBulbLightListener,
                                         OnAskBulbLightStatusListener onAskBulbLightStatusListener){
        this.context = context;
        this.activity = activity;
        this.connectBleName = connectBleName;
        this.connectBleAddress = connectBleAddress;
        this.bluetoothGattArrayList = bluetoothGattArrayList;
        this.bluetoothOpenFunctionListener = bluetoothOpenFunctionListener;
        this.onButtonAiotClickListener = onButtonAiotClickListener;
        this.onTemperatureHumidityUncomfortListener = onTemperatureHumidityUncomfortListener;
        this.onMaskDetectListener = onMaskDetectListener;
        this.onDoorCloseOpenListener = onDoorCloseOpenListener;
        this.onBulbLightListener = onBulbLightListener;
        this.onAskBulbLightStatusListener = onAskBulbLightStatusListener;
        initBleServices(this.context);
    }

    private void initBleServices(Context context){
        if (bluetoothManager == null){
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (bluetoothAdapter == null){
            bluetoothAdapter = bluetoothManager.getAdapter();
            Log.d(TAG, "initBleServices: bluetooth adapter " + bluetoothAdapter.isEnabled());
        }

        if (bluetoothLeScanner == null){
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public void startScanBleDevice(){
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                if (bluetoothAdapter.isEnabled()){
                    bluetoothDeviceArrayList.clear();
                    bluetoothDeviceHashSet.clear();
                    if (bleScanCallback != null){
                        bluetoothLeScanner.startScan(bleScanCallback);
                        bleScanState = true;
                    }
                    Log.d(TAG, "startScanBleDevice: Start Scan BLE Around Temi, bleScanState is " + bleScanState);
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"Bluetooth Function doesn't open",
                                    Toast.LENGTH_SHORT).show();
                            bluetoothOpenFunctionListener.bluetoothOpenFunction();
                        }
                    });
                }
            }
        });

        stopScanBleHandler = new Handler(Looper.getMainLooper());
        stopScanBleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleScanState == true){
                    stopScanBleDevice();
                }
                Log.d(TAG, "startScanBleDevice: Stop BLE Scan, bleScanState is " + bleScanState);
            }
        },BluetoothConstant.CONTINUE_SCAN_TIME);
    }

    public void stopExecutor(){
        if (scanAndConnectTask != null){
            while (scanAndConnectTask.getStatus() == AsyncTask.Status.RUNNING){
                scanAndConnectTask.cancel(true);
                bleScanCallback = null;
                bleScanState = false;
            }
        }
    }

    private void stopScanBleDevice(){
        scanAndConnectTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                new CommonUtilities(activity).cancelHandler(stopScanBleHandler);
                if (bluetoothAdapter.isEnabled()){
                    bluetoothLeScanner.stopScan(bleScanCallback);
                    bleScanState = false;
                    Log.d(TAG, "stopScanBleDevice: Stop BLE Scan, bleScanState is " + bleScanState);

                    bluetoothDeviceHashSet.addAll(bluetoothDeviceArrayList);
                    bluetoothDeviceArrayList.clear();
                    bluetoothDeviceArrayList.addAll(bluetoothDeviceHashSet);
                    Log.d(TAG, "stopScanBleDevice: bluetoothDeviceArrayList elements are " + bluetoothDeviceArrayList);

                    if (bluetoothDeviceArrayList.size() != 0){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (BluetoothDevice bluetoothDevice : bluetoothDeviceArrayList){
                                    Log.d(TAG, "run: name is " + bluetoothDevice.getName().replace("\n","")
                                            .replace("\r",""));
                                    Log.d(TAG, "run: address is " + bluetoothDevice.getAddress());
                                    if (bluetoothDevice.getName().replace("\n","")
                                            .replace("\r","").equals(connectBleName)
                                            && bluetoothDevice.getAddress().equals(connectBleAddress)){
                                        connectBle(connectBleName,connectBleAddress);
                                        Log.d(TAG, "run: connect");
                                        break;
                                    }
                                }
//                                alertDialog = bluetoothSelectDialog(bluetoothDeviceArrayList);
                            }
                        });
                    }else if (bluetoothDeviceArrayList.size() == 0){
                        reStartBleScan();
                    }

                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,"temi的藍牙功能尚未開啟",
                                    Toast.LENGTH_SHORT).show();
                            bluetoothOpenFunctionListener.bluetoothOpenFunction();
                        }
                    });
                }
                return null;
            }
        };

        scanAndConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void reStartBleScan(){
        if (scanAmounts >= 2){
            Log.d(TAG, "reStartBleScan: scanAmounts is " + scanAmounts + ", too Much Time Scan");
            scanAmounts = 0;
            startScanBleDevice();
        }else if (scanAmounts < 2){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "reStartBleScan: Re-Scan BLE Device Again " + connectBleName);
                            startScanBleDevice();
                            scanAmounts++;
                        }
                    }, BluetoothConstant.RE_SCAN_BLE_TIME);
                }
            });
        }
    }

    private void connectBle(String bleDeviceName, String bleDeviceAddress){
        for (int i = 0; i < bluetoothDeviceArrayList.size(); i++){
            if (bluetoothDeviceArrayList.get(i).getName().replace("\n","")
                    .replace("\r","").equals(bleDeviceName)
                    && bluetoothDeviceArrayList.get(i).getAddress().equals(bleDeviceAddress)){
                BluetoothDevice currentBluetoothDevice = bluetoothDeviceArrayList.get(i);
                AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothGatt = currentBluetoothDevice
                                .connectGatt(context,false,bluetoothGattCallback);
                        Log.d(TAG, "connectBle: Connect with BLE " + bluetoothGatt.getDevice()
                                .getName().replace("\n","")
                                .replace("\r",""));
                    }
                });
                break;
            }
        }
    }

    public void disconnectBle(){
        if (temiBleConnectState == true){
            AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    bluetoothGattArrayList.remove(bluetoothGatt);
                    bluetoothGatt.disconnect();
                    temiBleConnectState = false;
                    Log.d(TAG, "disconnectBle: Disconnect BLE Device " + connectBleName);
                }
            });
        }else if (temiBleConnectState == false){
            Log.d(TAG, "disconnectBle: Temi doesn't Connect " + connectBleName + ", Can't do Disconnect");
        }
    }

    public void sendBleByteData(byte remainTaskData, byte disEnableTaskData){

        new BluetoothDataCommunication(activity).sendDataToBle(
                new BluetoothDataCommunication(activity).bytesArrayDataCreate(remainTaskData,disEnableTaskData),
                bluetoothGattCharacteristicWrite,bluetoothGatt);
    }

    public boolean getBluetoothGattConnectResult(){
        Log.d(TAG, "getBluetoothGattConnectResult: " + bluetoothGatt);
        if (bluetoothGatt == null){
            return false;
        }else {
            Log.d(TAG, "getBluetoothGattConnectResult: " + bluetoothGatt.connect());
            return bluetoothGatt.connect();
        }
    }

    private void initBLE(BluetoothGatt gatt){
        if (gatt == null){
            return;
        }
        for (BluetoothGattService bluetoothGattService : gatt.getServices()){
//            Log.d(TAG, "initBle Service UUID: " + bluetoothGattService.getUuid().toString());
            for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()){
//                Log.d(TAG, "initBle Characteristic UUID: " + bluetoothGattCharacteristic.getUuid().toString());
                for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()){
//                    Log.d(TAG, "initBle Descriptor UUID: " + bluetoothGattDescriptor.getUuid().toString());
                }
            }
        }
    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
//            Log.d(TAG, "1bleScanCallback: onScanResult, Add BLE Device Name is " + result.getDevice().getName());
            if (result.getDevice().getName() != null && bleScanState == true){
                Log.d(TAG, "1bleScanCallback: onScanResult, Add BLE Device Name is " + result.getDevice().getName());
                Log.d(TAG, "1bleScanCallback: onScanResult, Add BLE Device is " + result.getDevice().getAddress());

                //一發現掃描到要連接的Ble裝置，立刻停止Ble掃描功能，節省運作時間
//                Log.d(TAG, "onScanResult: will name " + willConnectBleName);

//                String a = result.getDevice().getName();
//                for (int i = 0; i < a.length(); i++){
//                    Log.d(TAG, "onScanResult: String " + a.charAt(i));
//                }

                if (result.getDevice().getName().replace("\n","")
                        .replace("\r","").equals(connectBleName) &&
                        result.getDevice().getAddress().equals(connectBleAddress)){

                    bluetoothDeviceArrayList.add(result.getDevice());
                    Log.d(TAG, "bleScanCallback: onScanResult, Add BLE Device is " + result.getDevice());
                    Log.d(TAG, "bleScanCallback: onScanResult, Add BLE Device Name is " + result.getDevice().getName());
                    bleScanState = false;
                    stopScanBleDevice();
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "bleScanCallback: onScanFailed, Error Code is " + errorCode);
            BluetoothClientRelease.releaseAllScanClient();
            if (bluetoothAdapter.isEnabled() == true){
                bluetoothAdapter.disable();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bluetoothAdapter.enable();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                reStartBleScan();
                            }
                        },1000);
                    }
                },500);
            }
        }
    };

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            Log.d(TAG, "bluetoothGattCallback: onConnectionStateChange, status is  " + status);
            Log.d(TAG, "bluetoothGattCallback: onConnectionStateChange, newState is  " + newState);

            if (status == 133 || status == 8 || status == 22){
                reStartBleScan();
            }else{
                if (newState == BluetoothProfile.STATE_CONNECTED){
//                    alertDialog.cancel();
                    if (bluetoothGatt != null){
                        bluetoothGatt.discoverServices();
                    }

                }else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                    temiBleConnectState = false;

                    if (bluetoothGatt != null){
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                    }

                    Log.d(TAG, "bluetoothGattCallback: onConnectionStateChange, " +
                            "bluetoothGattCallback is close and null");
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                if (gatt.getDevice().getName().replace("\n","").replace("\r","")
                        .equals(BluetoothConstant.TEMPERATURE_BLE_NAME)){
                    temiBleConnectState = true;
                    initBLE(gatt);
                    BluetoothGattService bluetoothGattServiceRead;
                    bluetoothGattServiceRead = bluetoothGatt
                            .getService(UUID.fromString(BluetoothConstant.TEMPERATURE_BLE_SERVICE_UUID));
                    if (bluetoothGattServiceRead != null) {
                        bluetoothGattCharacteristicRead = bluetoothGattServiceRead
                                .getCharacteristic(UUID.fromString(BluetoothConstant.TEMPERATURE_BLE_READ_UUID));
                        bluetoothGattDescriptorRead = bluetoothGattCharacteristicRead
                                .getDescriptor(UUID.fromString(BluetoothConstant.BLE_DESCRIPTOR_UUID));

                        Log.d(TAG, "onServicesDiscovered bluetoothGattServiceRead: " + bluetoothGattServiceRead);
//                    Log.d(TAG, "onServicesDiscovered bluetoothGattServiceWrite: " + bluetoothGattServiceWrite);

                        //Ble接收資料設定
                        bluetoothGattDescriptorRead.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGattCharacteristicRead.addDescriptor(bluetoothGattDescriptorRead);
                        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicRead, true);
                        bluetoothGatt.writeDescriptor(bluetoothGattDescriptorRead);
                        Log.d(TAG, "bluetoothGattCallback: onServicesDiscovered, " + connectBleName + " is connected success");
                        bluetoothGattArrayList.add(bluetoothGatt);
                    }

                }else {
                    temiBleConnectState = true;
                    initBLE(gatt);
                    BluetoothGattService bluetoothGattServiceRead;
                    BluetoothGattService bluetoothGattServiceWrite;
                    bluetoothGattServiceRead = bluetoothGatt
                            .getService(UUID.fromString(BluetoothConstant.ARDUINO_BLE_SERVICE_UUID));
                    bluetoothGattServiceWrite = bluetoothGatt
                            .getService(UUID.fromString(BluetoothConstant.ARDUINO_BLE_SERVICE_UUID));

                    if (bluetoothGattServiceRead != null) {
                        bluetoothGattCharacteristicRead = bluetoothGattServiceRead
                                .getCharacteristic(UUID.fromString(BluetoothConstant.ARDUINO_BLE_READ_UUID));
                        bluetoothGattDescriptorRead = bluetoothGattCharacteristicRead
                                .getDescriptor(UUID.fromString(BluetoothConstant.BLE_DESCRIPTOR_UUID));

                        Log.d(TAG, "onServicesDiscovered bluetoothGattServiceRead: " + bluetoothGattServiceRead);
                        Log.d(TAG, "onServicesDiscovered bluetoothGattServiceWrite: " + bluetoothGattServiceWrite);

                        //Ble接收資料設定
                        bluetoothGattDescriptorRead.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        bluetoothGattCharacteristicRead.addDescriptor(bluetoothGattDescriptorRead);
                        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicRead, true);
                        bluetoothGatt.writeDescriptor(bluetoothGattDescriptorRead);
                    }

                    if (bluetoothGattServiceWrite != null){
                        bluetoothGattCharacteristicWrite = bluetoothGattServiceWrite
                                .getCharacteristic(UUID.fromString(BluetoothConstant.ARDUINO_BLE_WRITE_UUID));

                        Log.d(TAG, "onServicesDiscovered bluetoothGattCharacteristicWrite: " + bluetoothGattCharacteristicWrite);
                        Log.d(TAG, "bluetoothGattCallback: onServicesDiscovered, " + connectBleName + " is connected success");
                        bluetoothGattArrayList.add(bluetoothGatt);
                    }
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.d(TAG, "bluetoothGattCallback: onCharacteristicWrite, Send Data Success");
            }else{
                Log.d(TAG, "bluetoothGattCallback: onCharacteristicWrite, Send Data Fail");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "bluetoothGattCallback: onCharacteristicChanged, Temi Receive Data, It is " +
                    Arrays.toString(characteristic.getValue()));
            Log.d(TAG, "bluetoothGattCallback: onCharacteristicChanged, Data Length is "
                    + characteristic.getValue().length);
            Log.d(TAG, "onCharacteristicChanged: gatt name is " + gatt.getDevice().getName());
            switch (gatt.getDevice().getName().replace("\n","").replace("\r","")){
                case BluetoothConstant.ARDUINO_BUTTON_BLE_NAME:
                    if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.LIGHT_BULB_DATA),characteristic.getValue())){
                        onButtonAiotClickListener.onButtonAiotPress();
                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        onButtonAiotClickListener.onButtonAiotUnPress();
                    }
                    break;

                case BluetoothConstant.ARDUINO_LIGHT_BLE_NAME:
                    if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.LIGHT_BULB_DATA),characteristic.getValue())){
                        onBulbLightListener.onBulbLight();
                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        onBulbLightListener.onBulbUnLight();
                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.RECEIVE_BULB_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        onAskBulbLightStatusListener.onAskBulbLightStatus(false);

                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.RECEIVE_BULB_STATUS_DATA,BluetoothConstant.LIGHT_BULB_DATA),characteristic.getValue())){
                        onAskBulbLightStatusListener.onAskBulbLightStatus(true);

                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_MODE_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        Log.d(TAG, "onCharacteristicChanged: CHANGE_MODE_STATUS_DATA, UNLIGHT_BULB_DATA ");
                    }
                    break;

                case BluetoothConstant.ARDUINO_MAGNET_BLE_NAME:
                    if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.LIGHT_BULB_DATA),characteristic.getValue())){
                        onDoorCloseOpenListener.onDoorOpen();
                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        onDoorCloseOpenListener.onDoorClose();
                    }
                    break;

                case BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME:
                    if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.LIGHT_BULB_DATA),characteristic.getValue())){
                        onMaskDetectListener.onRecordMaskDetect(AiotModeConstant.WITH_MASK);
                        onMaskDetectListener.onWithMaskDetect();
                    }else if (Arrays.equals(new BluetoothDataCommunication(activity).bytesArrayDataCreate(
                            BluetoothConstant.CHANGE_BULB_STATUS_DATA,BluetoothConstant.UNLIGHT_BULB_DATA),characteristic.getValue())){
                        onMaskDetectListener.onRecordMaskDetect(AiotModeConstant.NO_MASK);
                        onMaskDetectListener.onNoMaskDetect();
                    }
                    break;

                case BluetoothConstant.TEMPERATURE_BLE_NAME:
                    byte data[] = characteristic.getValue();
                    short temp_data = (short) ((data[1] << 8) | (data[0] & 0xff));
                    short vol_data = (short) ((data[4] << 8) | (data[3] & 0xff));
                    float temperature = temp_data * 0.01f;
                    float humidity = data[2];
                    float vol = vol_data * 0.001f;

                    ArrayList<String[]> spinnerData = new SettingSharedpreferences(context).getSpinnerSettingData();
                    int spinnerDataSize = spinnerData.size();
                    for (int i = 1; i < spinnerDataSize; i++){
                        String thData = spinnerData.get(i)[0];
                        switch (i){
                            case 1:
                                highTemperatureValue = Float.valueOf(thData);
                                break;

                            case 2:
                                lowTemperatureValue = Float.valueOf(thData);
                                break;

                            case 3:
                                highHumidityValue = Float.valueOf(thData);
                                break;

                            case 4:
                                lowHumidityValue = Float.valueOf(thData);
                                break;
                        }
                    }

                    Log.d(TAG, "onCharacteristicChanged: hT: " + highTemperatureValue);
                    Log.d(TAG, "onCharacteristicChanged: lT: " + lowTemperatureValue);
                    Log.d(TAG, "onCharacteristicChanged: hH: " + highHumidityValue);
                    Log.d(TAG, "onCharacteristicChanged: lH: " + lowHumidityValue);

                    onTemperatureHumidityUncomfortListener.onTemperatureHumidityValueShow(temperature, humidity, vol);
                    if (temperature >= highTemperatureValue ||
                            temperature <= lowTemperatureValue ||
                            humidity >= highHumidityValue || humidity <= lowHumidityValue){
                        if (isTemperatureStatusSendOrNot == false){
                            Log.d(TAG, "onCharacteristicChanged: uncomfort");
                            onTemperatureHumidityUncomfortListener.onThRecord(AiotModeConstant.TH_UNCOMFORT);
                            onTemperatureHumidityUncomfortListener.onTemperatureHumidityValueUncomfort(temperature, humidity, vol);
                            isTemperatureStatusSendOrNot = true;
                        }
                    }else {
                        if (isTemperatureStatusSendOrNot == true){
                            Log.d(TAG, "onCharacteristicChanged: comfort");
                            onTemperatureHumidityUncomfortListener.onThRecord(AiotModeConstant.TH_COMFORT);
                            onTemperatureHumidityUncomfortListener.onTemperatureHumidityValueComfort(temperature,humidity, vol);
                            isTemperatureStatusSendOrNot = false;
                        }
                    }

                    Log.d(TAG, "onCharacteristicChanged: temperature is " + temperature);
                    Log.d(TAG, "onCharacteristicChanged: humidity is " + humidity);
                    Log.d(TAG, "onCharacteristicChanged: voltage is " + vol);
                    break;
            }
        }
    };

    public void setTemperatureStatus(boolean status){
        isTemperatureStatusSendOrNot = status;
    }
}
