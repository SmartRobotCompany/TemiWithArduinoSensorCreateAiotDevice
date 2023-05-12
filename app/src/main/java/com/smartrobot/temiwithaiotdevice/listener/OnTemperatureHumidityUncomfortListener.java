package com.smartrobot.temiwithaiotdevice.listener;

public interface OnTemperatureHumidityUncomfortListener {
    void onThRecord(String record);
    void onTemperatureHumidityValueUncomfort(float temperature, float humidity, float battery);
    void onTemperatureHumidityValueComfort(float temperature, float humidity, float battery);
    void onTemperatureHumidityValueShow(float temperature, float humidity, float battery);
}
