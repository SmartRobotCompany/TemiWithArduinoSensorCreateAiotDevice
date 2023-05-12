package com.smartrobot.temiwithaiotdevice.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.smartrobot.temiwithaiotdevice.R;
import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;
import com.smartrobot.temiwithaiotdevice.utilities.BulbLightAiotModeSharedpreferences;

import org.jetbrains.annotations.NotNull;

public class ThBulbAiotFragment extends Fragment {
    private final static String TAG = "Debug_" +  ThBulbAiotFragment.class.getSimpleName();
    private Context context;
    private Activity activity;
    private ImageView th_bulb_aiot_th_imageView;
    private ImageView th_bulb_aiot_bulb_imageView;
    private TextView temperature_value_textview;
    private TextView humidity_value_textview;
    private TextView battery_value_textview;

    public ThBulbAiotFragment(Context context,
                              Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.th_bulb_aiot_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        th_bulb_aiot_th_imageView = view.findViewById(R.id.th_bulb_aiot_th_imageView);
        th_bulb_aiot_bulb_imageView = view.findViewById(R.id.th_bulb_aiot_bulb_imageView);
        temperature_value_textview = view.findViewById(R.id.temperature_value_textview);
        humidity_value_textview = view.findViewById(R.id.humidity_value_textview);
        battery_value_textview = view.findViewById(R.id.battery_value_textview);
    }

    public void comfortOrNot(boolean comfortable){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: pressButton");
                if (comfortable == true){
                    th_bulb_aiot_th_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.comfort_th));
                }else if (comfortable == false){
                    th_bulb_aiot_th_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.uncomfort_th));
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
                    new BulbLightAiotModeSharedpreferences(context).saveBulbLightAiotMode(AiotModeConstant.TEMPERATURE_BULB_MODE);
                    th_bulb_aiot_bulb_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.light_bulb));
                }else if (lightOrNot == false){
                    th_bulb_aiot_bulb_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.unlight_bulb));
                }
            }
        });
    }

    public void setThValue(float tValue, float hValue, float bValue){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                temperature_value_textview.setText(String.format("%.2f",tValue));
                humidity_value_textview.setText(String.format("%.2f",hValue));
                battery_value_textview.setText(String.format("%.2f",bValue));
            }
        });

    }
}
