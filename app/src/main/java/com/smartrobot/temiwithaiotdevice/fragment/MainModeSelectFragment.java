package com.smartrobot.temiwithaiotdevice.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.smartrobot.temiwithaiotdevice.R;
import com.smartrobot.temiwithaiotdevice.constant.BluetoothConstant;
import com.smartrobot.temiwithaiotdevice.listener.BluetoothStartDisconnectListener;
import com.smartrobot.temiwithaiotdevice.listener.BluetoothStartScanListener;
import com.smartrobot.temiwithaiotdevice.listener.OnAiotModeChangeListener;


public class MainModeSelectFragment extends Fragment {
    private static final String TAG = "Debug_" + MainModeSelectFragment.class.getSimpleName();
    private Button function_dialog_show_button;
    private Context context;
    private BluetoothStartScanListener bluetoothStartScanListener;
    private BluetoothStartDisconnectListener bluetoothStartDisconnectListener;
    private OnAiotModeChangeListener onAiotModeChangeListener;

    public MainModeSelectFragment(Context context,
                                  OnAiotModeChangeListener onAiotModeChangeListener){
        this.context = context;
        this.onAiotModeChangeListener = onAiotModeChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_mode_select_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        function_dialog_show_button = view.findViewById(R.id.function_dialog_show_button);
        function_dialog_show_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
