package com.smartrobot.temiwithaiotdevice.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassifierEvent;
import android.view.textclassifier.TextSelection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.smartrobot.temiwithaiotdevice.R;
import com.smartrobot.temiwithaiotdevice.constant.TemiConstant;
import com.smartrobot.temiwithaiotdevice.listener.SettingChangeListener;
import com.smartrobot.temiwithaiotdevice.utilities.SettingSharedpreferences;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SettingPageDialogFragment extends DialogFragment {

    private static final String TAG = "Debug_" + SettingPageDialogFragment.class.getSimpleName();

    private Spinner tts_mode_spinner;
    private Spinner temperature_high_value_spinner;
    private Spinner temperature_low_value_spinner;
    private Spinner humidity_high_value_spinner;
    private Spinner humidity_low_value_spinner;
    private Button setting_apply_button;
    private Button setting_cancel_button;
    private ScrollView setting_scrollview;
    private EditText open_light_button_edittext;
    private EditText close_light_button_edittext;
    private EditText open_light_magnet_edittext;
    private EditText close_light_magnet_edittext;
    private EditText open_light_pixetto_edittext;
    private EditText close_light_pixetto_edittext;
    private EditText open_light_th_edittext;
    private EditText close_light_th_edittext;
    private EditText[] editTexts;
    private Context context;
    private SettingChangeListener settingChangeListener;

    public SettingPageDialogFragment(Context context, SettingChangeListener settingChangeListener){
        this.context = context;
        this.settingChangeListener = settingChangeListener;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        getDialog().setCancelable(false);
        return inflater.inflate(R.layout.setting_page_dialog_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tts_mode_spinner = view.findViewById(R.id.tts_mode_spinner);
        temperature_high_value_spinner = view.findViewById(R.id.temperature_high_value_spinner);
        temperature_low_value_spinner = view.findViewById(R.id.temperature_low_value_spinner);
        humidity_high_value_spinner = view.findViewById(R.id.humidity_high_value_spinner);
        humidity_low_value_spinner = view.findViewById(R.id.humidity_low_value_spinner);

        setting_apply_button = view.findViewById(R.id.setting_apply_button);
        setting_cancel_button = view.findViewById(R.id.setting_cancel_button);

        setting_scrollview = view.findViewById(R.id.setting_scrollview);
        setting_scrollview.setScrollBarFadeDuration(0);

        open_light_button_edittext = view.findViewById(R.id.open_light_button_edittext);
        close_light_button_edittext = view.findViewById(R.id.close_light_button_edittext);
        open_light_magnet_edittext = view.findViewById(R.id.open_light_magnet_edittext);
        close_light_magnet_edittext = view.findViewById(R.id.close_light_magnet_edittext);
        open_light_pixetto_edittext = view.findViewById(R.id.open_light_pixetto_edittext);
        close_light_pixetto_edittext = view.findViewById(R.id.close_light_pixetto_edittext);
        open_light_th_edittext = view.findViewById(R.id.open_light_th_edittext);
        close_light_th_edittext = view.findViewById(R.id.close_light_th_edittext);

        editTexts = new EditText[]{open_light_button_edittext,
                close_light_button_edittext,
                open_light_magnet_edittext,
                close_light_magnet_edittext,
                open_light_pixetto_edittext,
                close_light_pixetto_edittext,
                open_light_th_edittext,
                close_light_th_edittext};

        spinnerDataSetting(tts_mode_spinner,new ArrayList<>(Arrays.asList(TemiConstant.TEMI_TTS)));
        spinnerDataSetting(temperature_high_value_spinner,new ArrayList<>(Arrays.asList(TemiConstant.HIGH_TEMPERATURE_VALUE)));
        spinnerDataSetting(temperature_low_value_spinner,new ArrayList<>(Arrays.asList(TemiConstant.LOW_TEMPERATURE_VALUE)));
        spinnerDataSetting(humidity_high_value_spinner,new ArrayList<>(Arrays.asList(TemiConstant.HIGH_HUMIDITY_VALUE)));
        spinnerDataSetting(humidity_low_value_spinner,new ArrayList<>(Arrays.asList(TemiConstant.LOW_HUMIDITY_VALUE)));

        spinnerInitial();
        edittextInitial();

        setting_apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (areAllEdittextFilled(editTexts) == true){

                    new SettingSharedpreferences(getActivity()).saveSpinnerSettingSata(
                            tts_mode_spinner.getSelectedItem().toString(),
                            String.valueOf(tts_mode_spinner.getSelectedItemPosition()),
                            temperature_high_value_spinner.getSelectedItem().toString(),
                            String.valueOf(temperature_high_value_spinner.getSelectedItemPosition()),
                            temperature_low_value_spinner.getSelectedItem().toString(),
                            String.valueOf(temperature_low_value_spinner.getSelectedItemPosition()),
                            humidity_high_value_spinner.getSelectedItem().toString(),
                            String.valueOf(humidity_high_value_spinner.getSelectedItemPosition()),
                            humidity_low_value_spinner.getSelectedItem().toString(),
                            String.valueOf(humidity_low_value_spinner.getSelectedItemPosition()));

                    new SettingSharedpreferences(getActivity()).saveEdittextSettingData(
                            open_light_button_edittext.getText().toString(),
                            close_light_button_edittext.getText().toString(),
                            open_light_magnet_edittext.getText().toString(),
                            close_light_magnet_edittext.getText().toString(),
                            open_light_pixetto_edittext.getText().toString(),
                            close_light_pixetto_edittext.getText().toString(),
                            open_light_th_edittext.getText().toString(),
                            close_light_th_edittext.getText().toString());

                    settingChangeListener.settingChange();

                    dismiss();
                }else {
                    Toast.makeText(context,"有欄位未填寫，無法儲存設定",Toast.LENGTH_SHORT).show();
                }
            }
        });

        setting_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private static boolean areAllEdittextFilled(EditText[] editTexts){
        for (EditText editText : editTexts){
            if (TextUtils.isEmpty(editText.getText())){
                return false;
            }
        }
        return true;
    }

    private void edittextTouchSetting(EditText editText){
        registerForContextMenu(editText);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }



    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "onDismiss: setting dialog is dismiss");
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void spinnerDataSetting(Spinner spinner, ArrayList<String> data){
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),R.layout.setting_page_spinner_textsize,data);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                open_light_button_edittext.clearFocus();
                close_light_button_edittext.clearFocus();
                open_light_magnet_edittext.clearFocus();
                close_light_magnet_edittext.clearFocus();
                open_light_pixetto_edittext.clearFocus();
                close_light_pixetto_edittext.clearFocus();
                open_light_th_edittext.clearFocus();
                close_light_th_edittext.clearFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });
    }

    private void edittextInitial(){
        ArrayList<String> edittextData = new SettingSharedpreferences(getActivity()).getEdittextSettingData();
        int size = edittextData.size();
        for (int i = 0; i < size; i++){
            String text = edittextData.get(i);
            switch (i){
                case 0:
                    open_light_button_edittext.setText(text);
                    edittextTouchSetting(open_light_button_edittext);
                    break;

                case 1:
                    close_light_button_edittext.setText(text);
                    edittextTouchSetting(close_light_button_edittext);
                    break;

                case 2:
                    open_light_magnet_edittext.setText(text);
                    edittextTouchSetting(open_light_magnet_edittext);
                    break;

                case 3:
                    close_light_magnet_edittext.setText(text);
                    edittextTouchSetting(close_light_magnet_edittext);
                    break;

                case 4:
                    open_light_pixetto_edittext.setText(text);
                    edittextTouchSetting(open_light_pixetto_edittext);
                    break;

                case 5:
                    close_light_pixetto_edittext.setText(text);
                    edittextTouchSetting(close_light_pixetto_edittext);
                    break;

                case 6:
                    open_light_th_edittext.setText(text);
                    edittextTouchSetting(open_light_th_edittext);
                    break;

                case 7:
                    close_light_th_edittext.setText(text);
                    edittextTouchSetting(close_light_th_edittext);
                    break;
            }
        }
    }

    private void spinnerInitial(){
        ArrayList<String[]> spinnerData = new SettingSharedpreferences(getActivity()).getSpinnerSettingData();
        int size = spinnerData.size();
        for (int i = 0; i < size; i++){
            String data = spinnerData.get(i)[0];
            int position = Integer.parseInt(spinnerData.get(i)[1]);
            switch (i){
                case 0:
                    tts_mode_spinner.setSelection(position);
                    break;

                case 1:
                    temperature_high_value_spinner.setSelection(position);
                    break;

                case 2:
                    temperature_low_value_spinner.setSelection(position);
                    break;

                case 3:
                    humidity_high_value_spinner.setSelection(position);
                    break;

                case 4:
                    humidity_low_value_spinner.setSelection(position);
                    break;
            }
        }
    }
}
