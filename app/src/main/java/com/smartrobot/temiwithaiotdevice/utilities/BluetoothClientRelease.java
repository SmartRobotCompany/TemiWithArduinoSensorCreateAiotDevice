package com.smartrobot.temiwithaiotdevice.utilities;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BluetoothClientRelease {

    private final static String TAG = "Debug_" + BluetoothClientRelease.class.getSimpleName();

    //Release All register of mClient
    public static boolean releaseAllScanClient(){
        Log.d(TAG, "releaseAllScanClient: Release mClient");
        try {
            Object mIBluetoothManager = getIBluetoothManager(BluetoothAdapter.getDefaultAdapter());
            if (mIBluetoothManager == null) return false;
            Object iGatt = getIBluetoothGatt(mIBluetoothManager);
            if (iGatt == null) return false;

            Method unregisterClient = getDeclaredMethod(iGatt, "unregisterClient", int.class);
            Method stopScan;
            int type;
            try{
                type = 0;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class, boolean.class);
            }catch (Exception e){
                type = 1;
                stopScan = getDeclaredMethod(iGatt, "stopScan", int.class);
            }

            for (int mClientIf = 0; mClientIf <= 40; mClientIf++){
                if (type == 0){
                    try {
                        stopScan.invoke(iGatt, mClientIf, false);
                    }catch (Exception ignored){
                    }
                }

                if (type == 1){
                    try{
                        stopScan.invoke(iGatt, mClientIf);
                    }catch (Exception ignored){
                    }
                }

                try{
                    unregisterClient.invoke(iGatt, mClientIf);
                }catch (Exception ignored){
                }
            }

            stopScan.setAccessible(false);
            unregisterClient.setAccessible(false);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object getIBluetoothGatt(Object mIBluetoothManager) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method getBluetoothGatt = getDeclaredMethod(mIBluetoothManager, "getBluetoothGatt");
        Object object = new Object();
        object = getBluetoothGatt.invoke(mIBluetoothManager);
        return object;
    }

    public static Object getIBluetoothManager(BluetoothAdapter adapter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method getBluetoothManager = getDeclaredMethod(BluetoothAdapter.class, "getBluetoothManager");
        return getBluetoothManager.invoke(adapter);
    }

    public static Field getDeclaredField(Class<?> clazz, String name) throws NoSuchFieldException {
        Field declaredField = clazz.getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = clazz.getDeclaredMethod(name,parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }

    public static Field getDeclaredField(Object obj, String name) throws NoSuchFieldException {
        Field declaredField = obj.getClass().getDeclaredField(name);
        declaredField.setAccessible(true);
        return declaredField;
    }

    public static Method getDeclaredMethod(Object obj, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(name, parameterTypes);
        declaredMethod.setAccessible(true);
        return declaredMethod;
    }
}
