package com.smartrobot.temiwithaiotdevice.constant;

public interface BluetoothConstant {

    //BLE Permission request code
    int REQUEST_BLUETOOTH_FUNCTION_PERMISSION = 100;

    //BLE Name
    //temp
    String TEMPERATURE_BLE_NAME = "LYWSD03MMC";
    String ARDUINO_BUTTON_BLE_NAME = "BT_BUTTON";
    String ARDUINO_LIGHT_BLE_NAME = "BT_LIGHT";
    String ARDUINO_MAGNET_BLE_NAME = "BT_MAGNET";
    String ARDUINO_PIXETTO_BLE_NAME = "BT_PIXETTO";

    //BLE Address
    String TEMPERATURE_BLE_ADDRESS = "A4:C1:38:E3:CA:63";
    String ARDUINO_BUTTON_BLE_ADDRESS = "50:65:83:6E:11:9D";
    String ARDUINO_LIGHT_BLE_ADDRESS = "D0:B5:C2:9B:A5:30";
    String ARDUINO_MAGNET_BLE_ADDRESS = "88:C2:55:D2:0F:B8";
    String ARDUINO_PIXETTO_BLE_ADDRESS = "98:7B:F3:75:A6:28";

    //BLE UUID
    String TEMPERATURE_BLE_SERVICE_UUID = "ebe0ccb0-7a0a-4b0c-8a1a-6ff2997da3a6";
    String TEMPERATURE_BLE_READ_UUID = "ebe0ccc1-7a0a-4b0c-8a1a-6ff2997da3a6";

    String ARDUINO_BLE_SERVICE_UUID = "0000FFE0-0000-1000-8000-00805F9B34FB";
    String ARDUINO_BLE_READ_UUID = "0000FFE1-0000-1000-8000-00805F9B34FB";
    String ARDUINO_BLE_WRITE_UUID = "0000FFE1-0000-1000-8000-00805F9B34FB";

    String BLE_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    //Gatt Connect Status
    String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";//已連接到GATT服務器
    String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";//未連接GATT服務器
    String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";//未發現GATT服務
    String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";//接收到來自設備的數據，可通過讀取或操作獲得
    String EXTRA_DATA = "com.example.bluetooth.le.EXTRA_DATA"; //其他數據

    String CONNECTED_STATUS = "連線";
    String DISCONNECTED_STATUS = "未連線";

    //BLE Continue Scan Time
    int CONTINUE_SCAN_TIME = 8000;

    //Re Scan BLE Time After Scan Failed
    int RE_SCAN_BLE_TIME = 500;

    //Delay Time of Send Data After Connect With BLE
    int SEND_DATA_DELAY_TIME = 1500;

    //Send Data Head
    byte HEAD1 = (byte) 0xff;
    byte HEAD2 = (byte) 0x55;

    //Send Data Task Command Code
    byte CHANGE_BULB_STATUS_DATA = 0x00;
    byte CHANGE_MODE_STATUS_DATA = 0x03;
    byte UNLIGHT_BULB_DATA = 0x00;
    byte LIGHT_BULB_DATA = 0x01;

    byte ASK_BULB_STATUS_DATA1 = 0x01;
    byte ASK_BULB_STATUS_DATA2 = 0x00;
    byte RECEIVE_BULB_STATUS_DATA = 0x02;


}
