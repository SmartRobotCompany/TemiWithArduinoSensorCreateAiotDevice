package com.smartrobot.temiwithaiotdevice.utilities;

import android.bluetooth.BluetoothGatt;

import java.lang.reflect.Field;

public class CancelBluetoothDeviceBusyStatus {

    public static boolean isDeviceBusy(BluetoothGatt bluetoothGatt) throws NoSuchFieldException, IllegalAccessException {
        Field privateField = BluetoothGatt.class.getDeclaredField("mDeviceBusy");
        privateField.setAccessible(true);
        boolean state = (boolean) privateField.get(bluetoothGatt);
        return state;
    }

    public static void nonDeviceBusySetting(BluetoothGatt bluetoothGatt) throws NoSuchFieldException, IllegalAccessException {
        Field privateField = BluetoothGatt.class.getDeclaredField("mDeviceBusy");
        privateField.setAccessible(true);
        privateField.set(bluetoothGatt,false);
    }
}
