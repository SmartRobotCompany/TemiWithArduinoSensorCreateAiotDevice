package com.smartrobot.temiwithaiotdevice.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.smartrobot.temiwithaiotdevice.R;
import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;
import com.smartrobot.temiwithaiotdevice.utilities.BulbLightAiotModeSharedpreferences;

import org.jetbrains.annotations.NotNull;

public class ButtonBulbAiotFragment extends Fragment {
    private final static String TAG = "Debug_" +  ButtonBulbAiotFragment.class.getSimpleName();
    private Context context;
    private Activity activity;
    private ImageView button_buble_aiot_button_imageView;
    private ImageView button_buble_aiot_buble_imageView;

    public ButtonBulbAiotFragment(Context context,
                                  Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.button_bulb_aiot_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button_buble_aiot_button_imageView = view.findViewById(R.id.button_bulb_aiot_button_imageView);
        button_buble_aiot_buble_imageView = view.findViewById(R.id.button_bulb_aiot_bulb_imageView);
    }

    public void pressButtonOrNot(boolean pressOrNot){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: pressButton");
                if (pressOrNot == true){
                    button_buble_aiot_button_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.press_button));
                }else if (pressOrNot == false){
                    button_buble_aiot_button_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.unpress_button));
                }
            }
        });

    }

    public void lightBulbOrNot(boolean lightOrNot){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: lightBulb");
                if (lightOrNot == true){
                    new BulbLightAiotModeSharedpreferences(context).saveBulbLightAiotMode(AiotModeConstant.BUTTON_BULB_MODE);
                    button_buble_aiot_buble_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.light_bulb));
                }else if (lightOrNot == false){
                    button_buble_aiot_buble_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.unlight_bulb));
                }
            }
        });

    }
}
