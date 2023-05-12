package com.smartrobot.temiwithaiotdevice;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.robotemi.sdk.Robot;
import com.robotemi.sdk.TtsRequest;
import com.robotemi.sdk.listeners.OnRobotReadyListener;
import com.robotemi.sdk.permission.Permission;
import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;
import com.smartrobot.temiwithaiotdevice.constant.BluetoothConstant;
import com.smartrobot.temiwithaiotdevice.constant.TemiConstant;
import com.smartrobot.temiwithaiotdevice.dialogfragment.AiotModeListDialogFragment;
import com.smartrobot.temiwithaiotdevice.fragment.ButtonBulbAiotFragment;
import com.smartrobot.temiwithaiotdevice.fragment.DoorBulbAiotFragment;
import com.smartrobot.temiwithaiotdevice.fragment.MaskBulbAiotFragment;
import com.smartrobot.temiwithaiotdevice.fragment.ThBulbAiotFragment;
import com.smartrobot.temiwithaiotdevice.listener.AppCloseListener;
import com.smartrobot.temiwithaiotdevice.listener.BluetoothOpenFunctionListener;
import com.smartrobot.temiwithaiotdevice.listener.BluetoothStartDisconnectListener;
import com.smartrobot.temiwithaiotdevice.listener.OnAiotModeChangeListener;
import com.smartrobot.temiwithaiotdevice.listener.OnAskBulbLightStatusListener;
import com.smartrobot.temiwithaiotdevice.listener.OnBulbLightListener;
import com.smartrobot.temiwithaiotdevice.listener.OnButtonAiotClickListener;
import com.smartrobot.temiwithaiotdevice.listener.OnDoorCloseOpenListener;
import com.smartrobot.temiwithaiotdevice.listener.OnGoogleTtsStatusListener;
import com.smartrobot.temiwithaiotdevice.listener.OnMaskDetectListener;
import com.smartrobot.temiwithaiotdevice.listener.OnTemperatureHumidityUncomfortListener;
import com.smartrobot.temiwithaiotdevice.listener.SettingChangeListener;
import com.smartrobot.temiwithaiotdevice.utilities.BluetoothScanConnectUtilities;
import com.smartrobot.temiwithaiotdevice.utilities.BulbLightAiotModeSharedpreferences;
import com.smartrobot.temiwithaiotdevice.utilities.CommonUtilities;
import com.smartrobot.temiwithaiotdevice.utilities.GoogleTtsUtilities;
import com.smartrobot.temiwithaiotdevice.utilities.SettingSharedpreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements
        BluetoothStartDisconnectListener,
        BluetoothOpenFunctionListener,
        OnButtonAiotClickListener,
        OnTemperatureHumidityUncomfortListener,
        OnMaskDetectListener,
        OnDoorCloseOpenListener,
        OnBulbLightListener,
        OnAskBulbLightStatusListener,
        OnAiotModeChangeListener,
        AppCloseListener,
        SettingChangeListener,
        OnGoogleTtsStatusListener,
        OnRobotReadyListener,
        Robot.TtsListener{

    private final static String TAG = "Debug_" +  MainActivity.class.getSimpleName();
    private Robot robot;

    //Fragment Parameter
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ButtonBulbAiotFragment buttonBulbAiotFragment;
    private MaskBulbAiotFragment maskBulbAiotFragment;
    private DoorBulbAiotFragment doorBulbAiotFragment;
    private ThBulbAiotFragment thBulbAiotFragment;
    private AiotModeListDialogFragment aiotModeListDialogFragment;

    //BLE Parameter
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;

    //List and Set for Store BLE Scan Result
    private ArrayList<BluetoothGatt> bluetoothGattArrayList = new ArrayList<>();
    private ArrayList<BluetoothScanConnectUtilities> bluetoothScanConnectUtilitiesArrayList = new ArrayList<>();

    //BLE Open Launcher Parameter
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private BluetoothScanConnectUtilities buttonBluetoothScanConnectUtilities;
    private BluetoothScanConnectUtilities lightBluetoothScanConnectUtilities;
    private BluetoothScanConnectUtilities mangnetBluetoothScanConnectUtilities;
    private BluetoothScanConnectUtilities pixettoBluetoothScanConnectUtilities;
    private BluetoothScanConnectUtilities temperatureBluetoothScanConnectUtilities;

    private ExecutorService bluetoothRemainConnectedService;
    private boolean isButtonBleScanOrNot = false;
    private boolean isLightBleScanOrNot = false;
    private boolean isPixettoBleScanOrNot = false;
    private boolean isMangnetBleScanOrNot = false;
    private boolean isTemperatureBleScanOrNot = false;

    private Button function_dialog_show_button;
    private ImageView bluetooth_connect_status_imageview;
    private ConstraintLayout initial_page_show_constraintlayout;

    private String currentAiotMode = AiotModeConstant.BUTTON_BULB_MODE;
    private String lastAiotMode = AiotModeConstant.BUTTON_BULB_MODE;

    private String lastMaskDetectRecord = AiotModeConstant.NO_MASK;
    private String lastThDetectRecord = AiotModeConstant.NO_MASK;
    private Timer bluetoothCheckConnectedTimer;

    //setting parameter
    private String temiTts = "temi語音";
    private float highTemperatureValue = 40.0f;
    private float lowTemperatureValue = 20.0f;
    private float highHumidityValue = 70.0f;
    private float lowHumidityValue = 20.0f;

    private String openLightButtonText = TemiConstant.TEMI_OPEN_LIGHT_BY_BUTTON;
    private String closeLightButtonText = TemiConstant.TEMI_CLOSE_LIGHT_BY_BUTTON;
    private String openLightMagnetText = TemiConstant.TEMI_OPEN_LIGHT_BY_MAGNET;
    private String closeLightMagnetText = TemiConstant.TEMI_CLOSE_LIGHT_BY_MAGNET;
    private String openLightPixettoText = TemiConstant.TEMI_OPEN_LIGHT_BY_PIXETTO;
    private String closeLightPixettoText = TemiConstant.TEMI_CLOSE_LIGHT_BY_PIXETTO;
    private String openLightThText = TemiConstant.TEMI_OPEN_LIGHT_BY_TH;
    private String closeLightThText = TemiConstant.TEMI_CLOSE_LIGHT_BY_TH;

    private GoogleTtsUtilities googleTtsUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        robot = Robot.getInstance();
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Log.d(TAG, "onActivityResult: Open BLE Check");
                    }else if (result.getResultCode() == Activity.RESULT_CANCELED){
                        Log.d(TAG, "onActivityResult: Open BLE Cancel");
                        finishAndRemoveTask();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        robot.addTtsListener(this);
        robot.addOnRobotReadyListener(this);

        initBleServices();

        function_dialog_show_button = findViewById(R.id.function_dialog_show_button);
        bluetooth_connect_status_imageview = findViewById(R.id.bluetooth_connect_status_imageview);
        initial_page_show_constraintlayout = findViewById(R.id.initial_page_show_constraintlayout);

        function_dialog_show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentManager == null){
                    fragmentManager = getSupportFragmentManager();
                }
                aiotModeListDialogFragment = new AiotModeListDialogFragment(MainActivity.this,
                        MainActivity.this,MainActivity.this,
                        MainActivity.this);
                aiotModeListDialogFragment.show(fragmentManager, AiotModeConstant.AIOT_DIALOG_FRAGMENT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        robot.removeTtsListener(this);
        robot.removeOnRobotReadyListener(this);
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
//        robot.removeTtsListener(this);
    }

    private void loadingPageShowOrNot(boolean showOrNot){
        if (showOrNot == true){
            initial_page_show_constraintlayout.setVisibility(View.VISIBLE);
            function_dialog_show_button.setVisibility(View.INVISIBLE);
            bluetooth_connect_status_imageview.setVisibility(View.INVISIBLE);
        }else if (showOrNot == false){
            initial_page_show_constraintlayout.setVisibility(View.INVISIBLE);
            function_dialog_show_button.setVisibility(View.VISIBLE);
            bluetooth_connect_status_imageview.setVisibility(View.VISIBLE);
        }

    }

    private void robotSpeakInitial(){
        loadingPageShowOrNot(true);
        robot.setVolume(0);
        if (googleTtsUtilities == null){
            googleTtsUtilities = new GoogleTtsUtilities(MainActivity.this, Locale.CHINA
                    , Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, MainActivity.this);
        }
        googleTtsUtilities.cancelTtsIntroduction();
        googleTtsUtilities.textSpeak(TemiConstant.FINISH_INITIAL);
    }

    private void robotSpeak(String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (temiTts.equals(TemiConstant.TEMI_TTS[0])){
                    robot.cancelAllTtsRequests();
                    robot.speak(TtsRequest.create(text,false, TtsRequest.Language.ZH_TW,false));
                }else if (temiTts.equals(TemiConstant.TEMI_TTS[1])){
                    if (googleTtsUtilities == null){
                        googleTtsUtilities = new GoogleTtsUtilities(MainActivity.this, Locale.TRADITIONAL_CHINESE
                                , Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, MainActivity.this);
                    }
                    googleTtsUtilities.cancelTtsIntroduction();
                    googleTtsUtilities.textSpeak(text);
                }
            }
        });
    }

    private void getSettingResult(){
        ArrayList<String[]> spinnerData = new SettingSharedpreferences(MainActivity.this).getSpinnerSettingData();
        int spinnerDataSize = spinnerData.size();
        for (int i = 0; i < spinnerDataSize; i++){
            String data = spinnerData.get(i)[0];
            switch (i){
                case 0:
                    temiTts = data;
                    break;

                case 1:
                    highTemperatureValue = Float.valueOf(data);
                    break;

                case 2:
                    lowTemperatureValue = Float.valueOf(data);
                    break;

                case 3:
                    highHumidityValue = Float.valueOf(data);
                    break;

                case 4:
                    lowHumidityValue = Float.valueOf(data);
                    break;
            }
        }

        ArrayList<String> edittextData = new SettingSharedpreferences(MainActivity.this).getEdittextSettingData();
        int edittextDataSize = edittextData.size();
        for (int i = 0; i < edittextDataSize; i++){
            String data = edittextData.get(i);
            switch (i){
                case 0:
                    openLightButtonText = data;
                    break;

                case 1:
                    closeLightButtonText = data;
                    break;

                case 2:
                    openLightMagnetText = data;
                    break;

                case 3:
                    closeLightMagnetText = data;
                    break;

                case 4:
                    openLightPixettoText = data;
                    break;

                case 5:
                    closeLightPixettoText = data;
                    break;

                case 6:
                    openLightThText = data;
                    break;

                case 7:
                    closeLightThText = data;
                    break;
            }
        }
        Log.d(TAG, "settingsInitial: current setting are " + temiTts
                + ", " + highTemperatureValue + ", " + lowTemperatureValue
                + ", " + highHumidityValue + ", " + lowHumidityValue
                + ", " + openLightButtonText + ", " + closeLightButtonText
                + ", " + openLightMagnetText + ", " + closeLightMagnetText
                + ", " + openLightPixettoText + ", " + closeLightPixettoText
                + ", " + openLightThText + ", " + closeLightThText);
    }

    //Get Bluetooth Services
    private void initBleServices(){
        if (bluetoothManager == null){
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (bluetoothAdapter == null){
            bluetoothAdapter = bluetoothManager.getAdapter();
            Log.d(TAG, "initBleServices: bluetooth adapter " + bluetoothAdapter.isEnabled());
        }

        if (bluetoothLeScanner == null){
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        checkPermission();
    }

    private void openBleFunction() {
        Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activityResultLauncher.launch(enableBluetoothIntent);
    }

    //確認藍牙權限
    private void checkPermission(){
        String[] permissions = new String[]{};
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
            permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION};
        }else {
            permissions = new String[]{
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION};
        }

        requestPermissions(permissions, BluetoothConstant.REQUEST_BLUETOOTH_FUNCTION_PERMISSION);
        Log.d(TAG, "checkPermission: permission check");
    }

    @CheckResult
    private boolean requestPermissionIfNeeded(Permission permission, int requestCode){

        if (robot.checkSelfPermission(permission) == Permission.GRANTED){
            //權限已經開啟
            //如果有進到if執行這一行，則執行完這一行後就會跳出requestPermissionIfNeeded()這個方法，並不會執行if{}外的程式
            return false;
        }

        //若權限沒有開啟，則請求開啟權限
        robot.requestPermissions(Collections.singletonList(permission),requestCode);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;
        Log.d(TAG, "onRequestPermissionsResult: allowed is " + allowed);
        switch (requestCode){
            case BluetoothConstant.REQUEST_BLUETOOTH_FUNCTION_PERMISSION:

                for (int res : grantResults){
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                    Log.d(TAG, "onRequestPermissionsResult: allowed is " + allowed);
                }

                //確認藍牙功能是否有開啟，沒有的話則開啟藍牙功能
                if ((bluetoothAdapter.isEnabled() == false) && (allowed == true)){
                    openBleFunction();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"藍牙準備開啟",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"藍牙已開啟",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;

            default:
                // if user denied then 'allowed' return false.
                allowed = false;
                break;
        }
    }

    @Override
    public void onGoogleTtsStatus(String status, String text) {
        Log.d(TAG, "onGoogleTtsFinish: Tts Content is " + text);
        switch(status){
            case TemiConstant.GOOGLE_TTS_FINISH:
                Log.d(TAG, "onGoogleTtsStatus: finish");
                switch (text){
                    case TemiConstant.FINISH_INITIAL:
                        robot.cancelAllTtsRequests();
                        robot.speak(TtsRequest.create(TemiConstant.FINISH_INITIAL,
                                false, TtsRequest.Language.ZH_TW,false));
                        break;
                }
                break;

            case TemiConstant.GOOGLE_TTS_START:
                Log.d(TAG, "onGoogleTtsStatus: start");
                break;

            case TemiConstant.GOOGLE_TTS_ERROR:
                Log.d(TAG, "onGoogleTtsStatus: error");
                googleTtsUtilities.reTtsIntroduction();
                break;
        }
    }

    @Override
    public void onTtsStatusChanged(@NotNull TtsRequest ttsRequest) {
        switch (ttsRequest.getStatus().toString()){
            case TemiConstant.TTS_COMPLETED:
                Log.d(TAG, "onTtsStatusChanged: TTS_COMPLETED, Speech is " + ttsRequest.getSpeech());
                robot.setVolume(3);
                loadingPageShowOrNot(false);
                break;

            case TemiConstant.TTS_STARTED:
                Log.d(TAG, "onTtsStatusChanged: TTS_STARTED, Speech is " + ttsRequest.getSpeech());
                break;

            case TemiConstant.TTS_ERROR:
                Log.d(TAG, "onTtsStatusChanged: TTS_ERROR");
                if (temiTts.equals(TemiConstant.TEMI_TTS[0])){
                    robotSpeak(ttsRequest.getSpeech());
                }else if (temiTts.equals(TemiConstant.TEMI_TTS[1])){
                    robot.speak(TtsRequest.create(ttsRequest.getStatus().toString(),
                            false, TtsRequest.Language.ZH_TW,false));
                }
                break;

            case TemiConstant.TTS_NOT_ALLOWED:
                Log.d(TAG, "onTtsStatusChanged: TTS_NOT_ALLOWED");
                if (temiTts.equals(TemiConstant.TEMI_TTS[0])){
                    robotSpeak(ttsRequest.getSpeech());
                }else if (temiTts.equals(TemiConstant.TEMI_TTS[1])){
                    robot.speak(TtsRequest.create(ttsRequest.getStatus().toString(),
                            false, TtsRequest.Language.ZH_TW,false));
                }
                break;
        }
    }

    @Override
    public void onRobotReady(boolean b) {
        if (b == true){
            new CommonUtilities(MainActivity.this).hideTemiToolbar();
            getSettingResult();
            robotSpeakInitial();

            bluetoothRemainConnectedService = Executors.newSingleThreadExecutor();
            bluetoothRemainConnectedService.submit(new Runnable() {
                @Override
                public void run() {
                    while (bluetoothRemainConnectedService.isShutdown() == false){
                        if (isButtonBleScanOrNot == false){
                            if (buttonBluetoothScanConnectUtilities == null){
                                isButtonBleScanOrNot = true;
                                buttonBluetoothScanConnectUtilities = new BluetoothScanConnectUtilities(MainActivity.this,
                                        MainActivity.this,
                                        BluetoothConstant.ARDUINO_BUTTON_BLE_NAME,
                                        BluetoothConstant.ARDUINO_BUTTON_BLE_ADDRESS
                                        ,bluetoothGattArrayList,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this);
                                buttonBluetoothScanConnectUtilities.startScanBleDevice();
                                bluetoothScanConnectUtilitiesArrayList.add(buttonBluetoothScanConnectUtilities);
                            }else if (buttonBluetoothScanConnectUtilities != null){
                                if (buttonBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == true){
                                    isButtonBleScanOrNot = false;
                                    Log.d(TAG, "bluetoothStartScan: button ble is connect");
                                }else if (buttonBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == false){
                                    isButtonBleScanOrNot = true;
                                    buttonBluetoothScanConnectUtilities.startScanBleDevice();
                                    bluetoothScanConnectUtilitiesArrayList.add(buttonBluetoothScanConnectUtilities);
                                }
                            }
                        }

                        if (isMangnetBleScanOrNot == false){
                            if (mangnetBluetoothScanConnectUtilities == null){
                                isMangnetBleScanOrNot = true;
                                mangnetBluetoothScanConnectUtilities = new BluetoothScanConnectUtilities(MainActivity.this,
                                        MainActivity.this,
                                        BluetoothConstant.ARDUINO_MAGNET_BLE_NAME,
                                        BluetoothConstant.ARDUINO_MAGNET_BLE_ADDRESS
                                        ,bluetoothGattArrayList,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this);
                                mangnetBluetoothScanConnectUtilities.startScanBleDevice();
                                bluetoothScanConnectUtilitiesArrayList.add(mangnetBluetoothScanConnectUtilities);
                            }else if (mangnetBluetoothScanConnectUtilities != null){
                                if (mangnetBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == true){
                                    isMangnetBleScanOrNot = false;
                                    Log.d(TAG, "bluetoothStartScan: button ble is connect");
                                }else if (mangnetBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == false){
                                    isMangnetBleScanOrNot = true;
                                    mangnetBluetoothScanConnectUtilities.startScanBleDevice();
                                    bluetoothScanConnectUtilitiesArrayList.add(mangnetBluetoothScanConnectUtilities);
                                }
                            }
                        }

                        if (isPixettoBleScanOrNot == false){
                            if (pixettoBluetoothScanConnectUtilities == null){
                                isPixettoBleScanOrNot = true;
                                pixettoBluetoothScanConnectUtilities = new BluetoothScanConnectUtilities(MainActivity.this,
                                        MainActivity.this,
                                        BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME,
                                        BluetoothConstant.ARDUINO_PIXETTO_BLE_ADDRESS
                                        ,bluetoothGattArrayList,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this);
                                pixettoBluetoothScanConnectUtilities.startScanBleDevice();
                                bluetoothScanConnectUtilitiesArrayList.add(pixettoBluetoothScanConnectUtilities);
                            }else if (pixettoBluetoothScanConnectUtilities != null){
                                if (pixettoBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == true){
                                    isPixettoBleScanOrNot = false;
                                    Log.d(TAG, "bluetoothStartScan: button ble is connect");
                                }else if (pixettoBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == false){
                                    isPixettoBleScanOrNot = true;
                                    pixettoBluetoothScanConnectUtilities.startScanBleDevice();
                                    bluetoothScanConnectUtilitiesArrayList.add(pixettoBluetoothScanConnectUtilities);
                                }
                            }
                        }

                        if (isLightBleScanOrNot == false){
                            if (lightBluetoothScanConnectUtilities == null){
                                isLightBleScanOrNot = true;
                                lightBluetoothScanConnectUtilities = new BluetoothScanConnectUtilities(MainActivity.this,
                                        MainActivity.this,
                                        BluetoothConstant.ARDUINO_LIGHT_BLE_NAME,
                                        BluetoothConstant.ARDUINO_LIGHT_BLE_ADDRESS,
                                        bluetoothGattArrayList,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this);
                                lightBluetoothScanConnectUtilities.startScanBleDevice();
                                bluetoothScanConnectUtilitiesArrayList.add(lightBluetoothScanConnectUtilities);
                            }else if (lightBluetoothScanConnectUtilities != null){
                                if (lightBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == true){
                                    isLightBleScanOrNot = false;
                                    Log.d(TAG, "bluetoothStartScan: light ble is connect");
                                }else if (lightBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == false){
                                    isLightBleScanOrNot = true;
                                    lightBluetoothScanConnectUtilities.startScanBleDevice();
                                    bluetoothScanConnectUtilitiesArrayList.add(lightBluetoothScanConnectUtilities);
                                }
                            }
                        }

                        if (isTemperatureBleScanOrNot == false){
                            if (temperatureBluetoothScanConnectUtilities == null){
                                isTemperatureBleScanOrNot = true;
                                temperatureBluetoothScanConnectUtilities = new BluetoothScanConnectUtilities(MainActivity.this,
                                        MainActivity.this,
                                        BluetoothConstant.TEMPERATURE_BLE_NAME,
                                        BluetoothConstant.TEMPERATURE_BLE_ADDRESS,
                                        bluetoothGattArrayList,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this,
                                        MainActivity.this);
                                temperatureBluetoothScanConnectUtilities.startScanBleDevice();
                                bluetoothScanConnectUtilitiesArrayList.add(temperatureBluetoothScanConnectUtilities);
                            }else if (temperatureBluetoothScanConnectUtilities != null){
                                if (temperatureBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == true){
                                    isTemperatureBleScanOrNot = false;
                                    Log.d(TAG, "bluetoothStartScan: temperature ble is connect");
                                }else if (temperatureBluetoothScanConnectUtilities.getBluetoothGattConnectResult() == false){
                                    isTemperatureBleScanOrNot = true;
                                    temperatureBluetoothScanConnectUtilities.startScanBleDevice();
                                    bluetoothScanConnectUtilitiesArrayList.add(temperatureBluetoothScanConnectUtilities);
                                }
                            }
                        }
                    }
                }
            });

            bluetoothCheckConnectedTimer = new Timer();
            bluetoothCheckConnectedTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<BluetoothDevice> deviceArrayList = new ArrayList<>();
                            List<String> deviceNameList = new ArrayList<>();
                            deviceArrayList = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
                            for (BluetoothDevice bluetoothDevice : deviceArrayList){
                                Log.d(TAG, "run: " + bluetoothDevice.getName());
                                deviceNameList.add(bluetoothDevice.getName().replace("\n","")
                                        .replace("\r",""));
                            }
                            switch (currentAiotMode){
                                case AiotModeConstant.BUTTON_BULB_MODE:
                                    if (deviceNameList.contains(BluetoothConstant.ARDUINO_BUTTON_BLE_NAME) &&
                                            deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(true);
                                    }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_BUTTON_BLE_NAME) ||
                                            !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(false);
                                    }
                                    break;

                                case AiotModeConstant.DOOR_BULB_MODE:
                                    if (deviceNameList.contains(BluetoothConstant.ARDUINO_MAGNET_BLE_NAME) &&
                                            deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(true);
                                    }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_MAGNET_BLE_NAME) ||
                                            !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(false);
                                    }
                                    break;

                                case AiotModeConstant.MASK_BULB_MODE:
                                    if (deviceNameList.contains(BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME) &&
                                            deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(true);
                                    }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME) ||
                                            !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(false);
                                    }
                                    break;

                                case AiotModeConstant.TEMPERATURE_BULB_MODE:
                                    if (deviceNameList.contains(BluetoothConstant.TEMPERATURE_BLE_NAME) &&
                                            deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(true);
                                    }else if (!deviceNameList.contains(BluetoothConstant.TEMPERATURE_BLE_NAME) ||
                                            !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                                        bluetoothConnectStatusImageSetting(false);
                                    }
                                    break;
                            }
                        }
                    });


                }
            },500,1000);
        }
    }

    private void bluetoothConnectStatusImageSetting(boolean connectOrNot){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (connectOrNot == true){
                    bluetooth_connect_status_imageview.setBackground(
                            ContextCompat.getDrawable(MainActivity.this,R.drawable.bluetooth_connect_picture));
                }else if (connectOrNot == false){
                    bluetooth_connect_status_imageview.setBackground(
                            ContextCompat.getDrawable(MainActivity.this,R.drawable.bluetooth_disconnect_picture));
                }
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(TAG, "onWindowFocusChanged: ");
        if (hasFocus == true){

            if (requestPermissionIfNeeded(Permission.SETTINGS, TemiConstant.REQUEST_CODE_SETTING)){
                return;
            }else {
                List<Fragment> fragments = getSupportFragmentManager().getFragments();
                boolean isFragmentAdded = false;
                for (Fragment fragment : fragments){
                    if (fragment instanceof ButtonBulbAiotFragment ||
                            fragment instanceof MaskBulbAiotFragment ||
                            fragment instanceof DoorBulbAiotFragment ||
                            fragment instanceof ThBulbAiotFragment){
                        isFragmentAdded = true;
                        break;
                    }
                }

                if (isFragmentAdded == false){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentManager = getSupportFragmentManager();
                            buttonBulbAiotFragment = new ButtonBulbAiotFragment(MainActivity.this, MainActivity.this);
                            fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.aiot_mode_image_show_container, buttonBulbAiotFragment);
                            fragmentTransaction.commit();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void bluetoothStartDisconnectListener() {
        isButtonBleScanOrNot = false;
        isLightBleScanOrNot = false;
        isMangnetBleScanOrNot = false;
        isPixettoBleScanOrNot = false;
        isTemperatureBleScanOrNot = false;
        if (bluetoothScanConnectUtilitiesArrayList.size() != 0 && bluetoothScanConnectUtilitiesArrayList != null){
            for (BluetoothScanConnectUtilities bluetoothScanConnectUtilities : bluetoothScanConnectUtilitiesArrayList){
                bluetoothScanConnectUtilities.disconnectBle();
            }
        }
        bluetoothScanConnectUtilitiesArrayList.clear();
        Log.d(TAG, "bluetoothStartDisconnectListener: " + bluetoothScanConnectUtilitiesArrayList);
    }

    @Override
    public void bluetoothOpenFunction() {
        openBleFunction();
    }

    @Override
    public void onBulbLight() {
        Log.d(TAG, "onBulbLight: light");
        switch (currentAiotMode){
            case AiotModeConstant.BUTTON_BULB_MODE:
                robotSpeak(openLightButtonText);
                if (buttonBulbAiotFragment != null){
                    buttonBulbAiotFragment.lightBulbOrNot(true);
                }
                break;

            case AiotModeConstant.DOOR_BULB_MODE:
                robotSpeak(openLightMagnetText);
                if (doorBulbAiotFragment != null){
                    doorBulbAiotFragment.lightBulbOrNot(true);
                }
                break;

            case AiotModeConstant.MASK_BULB_MODE:
                robotSpeak(openLightPixettoText);
                if (maskBulbAiotFragment != null){
                    maskBulbAiotFragment.lightBulbOrNot(true);
                }
                break;

            case AiotModeConstant.TEMPERATURE_BULB_MODE:
                robotSpeak(openLightThText);
                if (thBulbAiotFragment != null){
                    thBulbAiotFragment.lightBulbOrNot(true);
                }
                break;
        }

    }

    @Override
    public void onBulbUnLight() {
        Log.d(TAG, "onBulbUnLight: not light");
        switch (currentAiotMode){
            case AiotModeConstant.BUTTON_BULB_MODE:
                robotSpeak(closeLightButtonText);
                if (buttonBulbAiotFragment != null){
                    buttonBulbAiotFragment.lightBulbOrNot(false);
                }
                break;

            case AiotModeConstant.DOOR_BULB_MODE:
                robotSpeak(closeLightMagnetText);
                if (doorBulbAiotFragment != null){
                    doorBulbAiotFragment.lightBulbOrNot(false);
                }
                break;

            case AiotModeConstant.MASK_BULB_MODE:
                robotSpeak(closeLightPixettoText);
                if (maskBulbAiotFragment != null){
                    maskBulbAiotFragment.lightBulbOrNot(false);
                }
                break;

            case AiotModeConstant.TEMPERATURE_BULB_MODE:
                robotSpeak(closeLightThText);
                if (thBulbAiotFragment != null){
                    thBulbAiotFragment.lightBulbOrNot(false);
                }
                break;
        }

    }

    @Override
    public void onAskBulbLightStatus(boolean lightOrNot) {
        if (lightOrNot == true){
            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.UNLIGHT_BULB_DATA);
            }
        }else if (lightOrNot == false){
            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.LIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onButtonAiotPress() {
        Log.d(TAG, "onButtonAiotClick: press");
        if (currentAiotMode.equals(AiotModeConstant.BUTTON_BULB_MODE)){
            if (buttonBulbAiotFragment != null){
                buttonBulbAiotFragment.pressButtonOrNot(true);
            }
            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.ASK_BULB_STATUS_DATA1,
                        BluetoothConstant.ASK_BULB_STATUS_DATA2);
            }
        }
    }

    @Override
    public void onButtonAiotUnPress() {
        Log.d(TAG, "onButtonAiotClick: not press");
        if (currentAiotMode.equals(AiotModeConstant.BUTTON_BULB_MODE)){
            if (buttonBulbAiotFragment != null){
                buttonBulbAiotFragment.pressButtonOrNot(false);
            }
        }
    }

    @Override
    public void onThRecord(String record){
        lastThDetectRecord = record;
    }

    @Override
    public void onTemperatureHumidityValueComfort(float temperature, float humidity, float battery) {
        if (currentAiotMode == AiotModeConstant.TEMPERATURE_BULB_MODE){
            if (thBulbAiotFragment != null){
                thBulbAiotFragment.comfortOrNot(true);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.UNLIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onTemperatureHumidityValueUncomfort(float temperature, float humidity, float battery) {
        if (currentAiotMode == AiotModeConstant.TEMPERATURE_BULB_MODE){
            if (thBulbAiotFragment != null){
                thBulbAiotFragment.comfortOrNot(false);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.LIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onTemperatureHumidityValueShow(float temperature, float humidity, float battery){
        if (currentAiotMode == AiotModeConstant.TEMPERATURE_BULB_MODE){
            if (thBulbAiotFragment != null) {
                thBulbAiotFragment.setThValue(temperature, humidity, battery);
            }
        }
    }

    @Override
    public void onRecordMaskDetect(String record){
        lastMaskDetectRecord = record;
        Log.d(TAG, "onRecordMaskDetect: mask detected record is " + record);
    }

    @Override
    public void onWithMaskDetect() {
        if (currentAiotMode.equals(AiotModeConstant.MASK_BULB_MODE)){
            if (maskBulbAiotFragment != null){
                maskBulbAiotFragment.withMaskOrNot(true);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.LIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onNoMaskDetect() {
        if (currentAiotMode.equals(AiotModeConstant.MASK_BULB_MODE)){
            if (maskBulbAiotFragment != null){
                maskBulbAiotFragment.withMaskOrNot(false);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.UNLIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onDoorClose() {
        if (currentAiotMode.equals(AiotModeConstant.DOOR_BULB_MODE)){
            if (doorBulbAiotFragment != null){
                doorBulbAiotFragment.doorOpenOrNot(false);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.UNLIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onDoorOpen() {
        if (currentAiotMode.equals(AiotModeConstant.DOOR_BULB_MODE)){
            if (doorBulbAiotFragment != null){
                doorBulbAiotFragment.doorOpenOrNot(true);
            }

            if (lightBluetoothScanConnectUtilities != null){
                lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_BULB_STATUS_DATA,
                        BluetoothConstant.LIGHT_BULB_DATA);
            }
        }
    }

    @Override
    public void onAiotModeChange(String mode) {
        Log.d(TAG, "onAiotModeChange: mode is " + mode);
        if (lightBluetoothScanConnectUtilities != null){
            lightBluetoothScanConnectUtilities.sendBleByteData(BluetoothConstant.CHANGE_MODE_STATUS_DATA,
                    BluetoothConstant.UNLIGHT_BULB_DATA);
        }

        List<BluetoothDevice> deviceArrayList = new ArrayList<>();
        List<String> deviceNameList = new ArrayList<>();
        deviceArrayList = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        for (BluetoothDevice bluetoothDevice : deviceArrayList){
            Log.d(TAG, "run: " + bluetoothDevice.getName());
            deviceNameList.add(bluetoothDevice.getName().replace("\n","")
                    .replace("\r",""));
        }

        switch (mode){
            case AiotModeConstant.BUTTON_BULB_MODE:
                if (deviceNameList.contains(BluetoothConstant.ARDUINO_BUTTON_BLE_NAME) &&
                        deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(true);
                }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_BUTTON_BLE_NAME) ||
                        !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(false);
                }

                currentAiotMode = AiotModeConstant.BUTTON_BULB_MODE;
                lastAiotMode = AiotModeConstant.BUTTON_BULB_MODE;
                fragmentManager = getSupportFragmentManager();
                buttonBulbAiotFragment = new ButtonBulbAiotFragment(MainActivity.this, MainActivity.this);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.aiot_mode_image_show_container, buttonBulbAiotFragment);
                fragmentTransaction.commit();
                break;

            case AiotModeConstant.DOOR_BULB_MODE:
                if (deviceNameList.contains(BluetoothConstant.ARDUINO_MAGNET_BLE_NAME) &&
                        deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(true);
                }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_MAGNET_BLE_NAME) ||
                        !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(false);
                }

                currentAiotMode = AiotModeConstant.DOOR_BULB_MODE;
                lastAiotMode = AiotModeConstant.DOOR_BULB_MODE;
                fragmentManager = getSupportFragmentManager();
                doorBulbAiotFragment = new DoorBulbAiotFragment(MainActivity.this,MainActivity.this);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.aiot_mode_image_show_container,doorBulbAiotFragment);
                fragmentTransaction.commit();
                break;

            case AiotModeConstant.MASK_BULB_MODE:
                if (deviceNameList.contains(BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME) &&
                        deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(true);
                }else if (!deviceNameList.contains(BluetoothConstant.ARDUINO_PIXETTO_BLE_NAME) ||
                        !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(false);
                }

                currentAiotMode = AiotModeConstant.MASK_BULB_MODE;
                lastAiotMode = AiotModeConstant.MASK_BULB_MODE;
                fragmentManager = getSupportFragmentManager();
                maskBulbAiotFragment = new MaskBulbAiotFragment(MainActivity.this, MainActivity.this);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.aiot_mode_image_show_container,maskBulbAiotFragment);
                fragmentTransaction.commit();
                break;

            case AiotModeConstant.TEMPERATURE_BULB_MODE:
                if (deviceNameList.contains(BluetoothConstant.TEMPERATURE_BLE_NAME) &&
                        deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(true);
                }else if (!deviceNameList.contains(BluetoothConstant.TEMPERATURE_BLE_NAME) ||
                        !deviceNameList.contains(BluetoothConstant.ARDUINO_LIGHT_BLE_NAME)){
                    bluetoothConnectStatusImageSetting(false);
                }

                currentAiotMode = AiotModeConstant.TEMPERATURE_BULB_MODE;
                lastAiotMode = AiotModeConstant.TEMPERATURE_BULB_MODE;
                fragmentManager = getSupportFragmentManager();
                thBulbAiotFragment = new ThBulbAiotFragment(MainActivity.this,
                        MainActivity.this);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.aiot_mode_image_show_container,thBulbAiotFragment);
                fragmentTransaction.commit();
                temperatureBluetoothScanConnectUtilities.setTemperatureStatus(false);
                break;
        }
    }

    @Override
    public void appCloseCommand() {
        Log.d(TAG, "appcloseCommand: close app");
        isButtonBleScanOrNot = false;
        isLightBleScanOrNot = false;
        isMangnetBleScanOrNot = false;
        isPixettoBleScanOrNot = false;
        isTemperatureBleScanOrNot = false;

        while (bluetoothRemainConnectedService.isShutdown() == false){
            bluetoothRemainConnectedService.shutdownNow();
        }

        ArrayList<BluetoothScanConnectUtilities> arrayList = new ArrayList<>(bluetoothScanConnectUtilitiesArrayList);
        if (arrayList.size() != 0 && arrayList != null){
            for (BluetoothScanConnectUtilities bluetoothScanConnectUtilities : arrayList){
                Log.d(TAG, "appCloseCommand: " + bluetoothScanConnectUtilities.getBluetoothGattConnectResult());
                bluetoothScanConnectUtilities.disconnectBle();
            }
        }

        bluetoothScanConnectUtilitiesArrayList.clear();
        arrayList.clear();
        Log.d(TAG, "appcloseCommand: " + bluetoothScanConnectUtilitiesArrayList);
        Log.d(TAG, "appcloseCommand: " + arrayList);

        finishAndRemoveTask();
    }

    @Override
    public void settingChange() {
        getSettingResult();
    }
}