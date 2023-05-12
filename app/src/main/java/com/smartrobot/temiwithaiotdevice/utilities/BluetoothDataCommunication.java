package com.smartrobot.temiwithaiotdevice.utilities;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.widget.Toast;

import com.smartrobot.temiwithaiotdevice.constant.BluetoothConstant;

import java.util.Arrays;

public class BluetoothDataCommunication {

    private final static String TAG = "Debug_" +  BluetoothDataCommunication.class.getSimpleName();
    private Activity activity;
    byte[] data = null;

    public BluetoothDataCommunication(Activity activity){
        this.activity = activity;
    }

    public byte[] bytesArrayDataCreate(byte remainTaskData, byte enOrDisableTaskData){
        data = new byte[]{BluetoothConstant.HEAD1, BluetoothConstant.HEAD2,
                remainTaskData, enOrDisableTaskData, BluetoothConstant.HEAD2, BluetoothConstant.HEAD1};

        return data;
    }

    public void sendDataToBle(byte[] data,
                              BluetoothGattCharacteristic bluetoothGattCharacteristicWrite,
                              BluetoothGatt connectedBluetoothGatt){
        if (connectedBluetoothGatt != null){
            if (connectedBluetoothGatt.connect() == true){
                if (bluetoothGattCharacteristicWrite != null){
                    try {
                        while (CancelBluetoothDeviceBusyStatus.isDeviceBusy(connectedBluetoothGatt)){
                            CancelBluetoothDeviceBusyStatus.nonDeviceBusySetting(connectedBluetoothGatt);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    bluetoothGattCharacteristicWrite.setValue(data);
                }

                if (connectedBluetoothGatt != null && bluetoothGattCharacteristicWrite != null){
                    boolean dataSendResult = connectedBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristicWrite);
                    Log.d(TAG, "sendDataToBle: Sent Data is " + Arrays.toString(data));
                    Log.d(TAG, "sendDataToBle: Send Data Result is " + dataSendResult);
                }
            }else{
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity,"sendDataToBle: BLE not Connect",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}
