package com.smartrobot.temiwithaiotdevice.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;

public class BulbLightAiotModeSharedpreferences {

    Context context;

    public BulbLightAiotModeSharedpreferences(Context context){
        this.context = context;
    }

    public void saveBulbLightAiotMode(String mode){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Bulb_Light_Aiot_Mode",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Aiot_Mode",mode);
        editor.commit();
    }

    public String getLastBulbLightAiotMode(){
        String data = context.getSharedPreferences("Bulb_Light_Aiot_Mode",Context.MODE_PRIVATE).getString("Aiot_Mode",
                AiotModeConstant.BUTTON_BULB_MODE);
        return  data;
    }
}
