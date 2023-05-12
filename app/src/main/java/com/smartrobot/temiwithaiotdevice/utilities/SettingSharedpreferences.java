package com.smartrobot.temiwithaiotdevice.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.smartrobot.temiwithaiotdevice.constant.TemiConstant;

import java.util.ArrayList;

public class SettingSharedpreferences {

    Context context;

    public SettingSharedpreferences(Context context){
        this.context = context;
    }

    public void saveEdittextSettingData(String openLightButtonText,
                                        String closeLightButtonText,
                                        String openLightMagnetText,
                                        String closeLightMagnetText,
                                        String openLightPixettoText,
                                        String closeLightPixettoText,
                                        String openLightThText,
                                        String closeLightThText){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("openLightButtonText",openLightButtonText);
        editor.putString("closeLightButtonText",closeLightButtonText);
        editor.putString("openLightMagnetText",openLightMagnetText);
        editor.putString("closeLightMagnetText",closeLightMagnetText);
        editor.putString("openLightPixettoText",openLightPixettoText);
        editor.putString("closeLightPixettoText",closeLightPixettoText);
        editor.putString("openLightThText",openLightThText);
        editor.putString("closeLightThText",closeLightThText);
        editor.commit();
    }

    public void saveSpinnerSettingSata(String temiTts,
                                String temiTtsPosition,
                                String highTemperatureValue,
                                String highTemperatureValuePosition,
                                String lowTemperatureValue,
                                String lowTemperatureValuePosition,
                                String highHumidityValue,
                                String highHumidityValuePosition,
                                String lowHumidityValue,
                                String lowHumidityValuePosition){

        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("temiTts",temiTts);
        editor.putString("highTemperatureValue",highTemperatureValue);
        editor.putString("lowTemperatureValue",lowTemperatureValue);
        editor.putString("highHumidityValue",highHumidityValue);
        editor.putString("lowHumidityValue",lowHumidityValue);

        editor.putString("temiTtsPosition",temiTtsPosition);
        editor.putString("highTemperatureValuePosition",highTemperatureValuePosition);
        editor.putString("lowTemperatureValuePosition",lowTemperatureValuePosition);
        editor.putString("highHumidityValuePosition",highHumidityValuePosition);
        editor.putString("lowHumidityValuePosition",lowHumidityValuePosition);
        editor.commit();
    }

    public ArrayList<String> getEdittextSettingData(){
        String openLightButtonText = getStringSharedPreferencesData("openLightButtonText"
                , TemiConstant.TEMI_OPEN_LIGHT_BY_BUTTON);
        String closeLightButtonText = getStringSharedPreferencesData("closeLightButtonText",
                TemiConstant.TEMI_CLOSE_LIGHT_BY_BUTTON);
        String openLightMagnetText = getStringSharedPreferencesData("openLightMagnetText",
                TemiConstant.TEMI_OPEN_LIGHT_BY_MAGNET);
        String closeLightMagnetText = getStringSharedPreferencesData("closeLightMagnetText",
                TemiConstant.TEMI_CLOSE_LIGHT_BY_MAGNET);
        String openLightPixettoText = getStringSharedPreferencesData("openLightPixettoText",
                TemiConstant.TEMI_OPEN_LIGHT_BY_PIXETTO);
        String closeLightPixettoText = getStringSharedPreferencesData("closeLightPixettoText",
                TemiConstant.TEMI_CLOSE_LIGHT_BY_PIXETTO);
        String openLightThText = getStringSharedPreferencesData("openLightThText",
                TemiConstant.TEMI_OPEN_LIGHT_BY_TH);
        String closeLightThText = getStringSharedPreferencesData("closeLightThText",
                TemiConstant.TEMI_CLOSE_LIGHT_BY_TH);
        ArrayList<String> edittextSettingData = new ArrayList<>();
        edittextSettingData.add(openLightButtonText);
        edittextSettingData.add(closeLightButtonText);
        edittextSettingData.add(openLightMagnetText);
        edittextSettingData.add(closeLightMagnetText);
        edittextSettingData.add(openLightPixettoText);
        edittextSettingData.add(closeLightPixettoText);
        edittextSettingData.add(openLightThText);
        edittextSettingData.add(closeLightThText);
        return edittextSettingData;
    }

    public ArrayList<String[]> getSpinnerSettingData(){
        String[] temiTtsData = {getStringSharedPreferencesData("temiTts","temi語音")
                ,getStringSharedPreferencesData("temiTtsPosition","0")};

        String[] highTemperatureValue = {getStringSharedPreferencesData("highTemperatureValue","40")
                ,getStringSharedPreferencesData("highTemperatureValuePosition","8")};

        String[] lowTemperatureValue = {getStringSharedPreferencesData("lowTemperatureValue","20")
                ,getStringSharedPreferencesData("lowTemperatureValuePosition","4")};

        String[] highHumidityValue = {getStringSharedPreferencesData("highHumidityValue","70")
                ,getStringSharedPreferencesData("highHumidityValuePosition","7")};

        String[] lowHumidityValue = {getStringSharedPreferencesData("lowHumidityValue","20")
                ,getStringSharedPreferencesData("lowHumidityValuePosition","2")};

        ArrayList<String[]> spinnerSettingData = new ArrayList<>();
        spinnerSettingData.add(temiTtsData);
        spinnerSettingData.add(highTemperatureValue);
        spinnerSettingData.add(lowTemperatureValue);
        spinnerSettingData.add(highHumidityValue);
        spinnerSettingData.add(lowHumidityValue);

        return spinnerSettingData;
    }

    private String getStringSharedPreferencesData(String key, String defaultValue){
        String data = context.getSharedPreferences("Setting",Context.MODE_PRIVATE).getString(key,defaultValue);
        return data;
    }
}
