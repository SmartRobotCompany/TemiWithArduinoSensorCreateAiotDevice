package com.smartrobot.temiwithaiotdevice.utilities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.robotemi.sdk.Robot;

public class CommonUtilities {
    private final static String TAG = "Debug_" +  CommonUtilities.class.getSimpleName();

    private Activity activity;

    public CommonUtilities(Activity activity){
        this.activity = activity;
    }

    public void cancelHandler(Handler handler){
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
            Log.d(TAG, "cancelHandler: 取消" + handler.getClass().getSimpleName());
            handler = null;
        }
    }

    public void hideTemiToolbar() {
        try {
            ActivityInfo activityInfo = activity.getPackageManager()
                    .getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
            Robot.getInstance().onStart(activityInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
