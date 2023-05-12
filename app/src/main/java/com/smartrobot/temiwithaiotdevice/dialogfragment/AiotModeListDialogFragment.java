package com.smartrobot.temiwithaiotdevice.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.smartrobot.temiwithaiotdevice.R;
import com.smartrobot.temiwithaiotdevice.constant.AiotModeConstant;
import com.smartrobot.temiwithaiotdevice.listener.AppCloseListener;
import com.smartrobot.temiwithaiotdevice.listener.OnAiotModeChangeListener;
import com.smartrobot.temiwithaiotdevice.listener.SettingChangeListener;

import org.jetbrains.annotations.NotNull;

public class AiotModeListDialogFragment extends DialogFragment {
    private static final String TAG = "Debug_" + AiotModeListDialogFragment.class.getSimpleName();
    private Context context;
    private OnAiotModeChangeListener onAiotModeChangeListener;
    private AppCloseListener appCloseListener;
    private SettingChangeListener settingChangeListener;

    private Button setting_button;
    private Button button_aiot_button;
    private Button door_aiot_button;
    private Button mask_aiot_button;
    private Button th_aiot_button;
    private Button close_app_button;


    public AiotModeListDialogFragment(Context context,
                                      OnAiotModeChangeListener onAiotModeChangeListener,
                                      AppCloseListener appCloseListener,
                                      SettingChangeListener settingChangeListener){
        this.context = context;
        this.onAiotModeChangeListener = onAiotModeChangeListener;
        this.appCloseListener = appCloseListener;
        this.settingChangeListener = settingChangeListener;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aiot_mode_list_dialog_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setting_button = view.findViewById(R.id.setting_button);
        button_aiot_button = view.findViewById(R.id.button_aiot_button);
        door_aiot_button = view.findViewById(R.id.door_aiot_button);
        mask_aiot_button = view.findViewById(R.id.mask_aiot_button);
        th_aiot_button = view.findViewById(R.id.th_aiot_button);
        close_app_button = view.findViewById(R.id.close_app_button);

        setting_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SettingPageDialogFragment settingPageDialogFragment = new SettingPageDialogFragment(context, (SettingChangeListener) context);
                settingPageDialogFragment.show(fragmentManager,"setting_page");
                dismiss();
            }
        });

        button_aiot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAiotModeChangeListener.onAiotModeChange(AiotModeConstant.BUTTON_BULB_MODE);
                dismiss();
            }
        });

        door_aiot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAiotModeChangeListener.onAiotModeChange(AiotModeConstant.DOOR_BULB_MODE);
                dismiss();
            }
        });

        mask_aiot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAiotModeChangeListener.onAiotModeChange(AiotModeConstant.MASK_BULB_MODE);
                dismiss();
            }
        });

        th_aiot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAiotModeChangeListener.onAiotModeChange(AiotModeConstant.TEMPERATURE_BULB_MODE);
                dismiss();
            }
        });

        close_app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appCloseListener.appCloseCommand();
            }
        });

    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
