package com.smartrobot.temiwithaiotdevice.listener;

public interface OnMaskDetectListener {
    void onRecordMaskDetect(String record);
    void onWithMaskDetect();
    void onNoMaskDetect();
}
