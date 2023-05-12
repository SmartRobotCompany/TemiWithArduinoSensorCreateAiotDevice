package com.smartrobot.temiwithaiotdevice.constant;

public interface TemiConstant {
    int REQUEST_CODE_SETTING = 201;

    String[] TEMI_TTS = new String[]{"temi語音","google語音"};
    String[] HIGH_TEMPERATURE_VALUE = new String[]{"0","5","10","15","20","25","30","35","40"};
    String[] LOW_TEMPERATURE_VALUE = new String[]{"0","5","10","15","20","25","30","35","40"};
    String[] HIGH_HUMIDITY_VALUE = new String[]{"0","10","20","30","40","50","60","70","80","90","100"};
    String[] LOW_HUMIDITY_VALUE = new String[]{"0","10","20","30","40","50","60","70","80","90","100"};


    String TEMI_OPEN_LIGHT_BY_BUTTON = "偵測到點擊按鈕，開燈";
    String TEMI_CLOSE_LIGHT_BY_BUTTON = "偵測到點擊按鈕，關燈";
    String TEMI_OPEN_LIGHT_BY_MAGNET = "偵測到門窗打開，開燈";
    String TEMI_CLOSE_LIGHT_BY_MAGNET = "偵測到門窗關閉，關燈";
    String TEMI_OPEN_LIGHT_BY_PIXETTO = "偵測到戴口罩，開燈";
    String TEMI_CLOSE_LIGHT_BY_PIXETTO = "偵測到沒戴口罩，關燈";
    String TEMI_OPEN_LIGHT_BY_TH = "偵測到溫溼度計值異常，開燈";
    String TEMI_CLOSE_LIGHT_BY_TH = "偵測到溫室溼度計值正常，關燈";
    String FINISH_INITIAL = "初始化完成";

    //Temi TTS Status
    String TTS_COMPLETED = "COMPLETED";
    String TTS_STARTED = "STARTED";
    String TTS_ERROR = "ERROR";
    String TTS_NOT_ALLOWED = "NOT_ALLOWED";

    //Google TTS Status
    String GOOGLE_TTS_FINISH = "google_tts_finish";
    String GOOGLE_TTS_START = "google_tts_start";
    String GOOGLE_TTS_ERROR = "google_tts_error";
    String GOOGLE_TTS_ID = "google_tts_id";
}
