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

public class MaskBulbAiotFragment extends Fragment {
    private final static String TAG = "Debug_" +  MaskBulbAiotFragment.class.getSimpleName();
    private Context context;
    private Activity activity;
    private ImageView mask_bulb_aiot_mask_imageView;
    private ImageView mask_bulb_aiot_bulb_imageView;

    public MaskBulbAiotFragment(Context context,
                                Activity activity){
        this.context = context;
        this.activity = activity;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mask_bulb_aiot_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mask_bulb_aiot_mask_imageView = view.findViewById(R.id.mask_bulb_aiot_mask_imageView);
        mask_bulb_aiot_bulb_imageView = view.findViewById(R.id.mask_bulb_aiot_bulb_imageView);
    }

    public void withMaskOrNot(boolean withOrNot){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: pressButton");
                if (withOrNot == true){
                    mask_bulb_aiot_mask_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.with_mask));
                }else if (withOrNot == false){
                    mask_bulb_aiot_mask_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.no_mask));
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
                    new BulbLightAiotModeSharedpreferences(context).saveBulbLightAiotMode(AiotModeConstant.MASK_BULB_MODE);
                    mask_bulb_aiot_bulb_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.light_bulb));
                }else if (lightOrNot == false){
                    mask_bulb_aiot_bulb_imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.unlight_bulb));
                }
            }
        });

    }
}
