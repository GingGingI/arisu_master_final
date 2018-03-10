package com.chkk.arisong_arisu.Fragment;


import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.chkk.arisong_arisu.GPSInfo;
import com.chkk.arisong_arisu.R;
import com.chkk.arisong_arisu.saveData;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    TextView[] textViews;
    saveData data;
    Typeface regular;
    Switch API,push;
    SeekBar textSize;
    Spinner spinner;
    GPSInfo gpsInfo;
    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        // Inflate the layout for this fragment
        data = new saveData(getActivity());
        data.open();

        Cursor all_cursor = data.AllRows();
        all_cursor.moveToFirst();

        if(all_cursor.getCount()==0)
            dbset();

        textViews = new TextView[10];

        API = v.findViewById(R.id.APISwitch);
        push = v.findViewById(R.id.pushSwitch);

        /*TextViews FindViewByID*/
        textViews[0] = v.findViewById(R.id.pushText);
        textViews[1] = v.findViewById(R.id.under_pushText);
        textViews[2] = v.findViewById(R.id.APIText);
        textViews[3] = v.findViewById(R.id.under_APIText);
        textViews[4] = v.findViewById(R.id.textSize);
        textViews[5] = v.findViewById(R.id.under_textSize);
        textViews[6] = v.findViewById(R.id.aroundSize);
        textViews[7] = v.findViewById(R.id.under_aroundSize);
        textViews[8] = v.findViewById(R.id.version);
        textViews[9] = v.findViewById(R.id.under_version);

        gpsInfo = new GPSInfo(getActivity());
        gpsInfo.stopUsingGPS();
        textSize = v.findViewById(R.id.textSizeSeekBar);
        textSize.setMax(10);
        if (data.ChkDB()) {
            textSize.setProgress(all_cursor.getInt(all_cursor.getColumnIndex("TEXTSIZE")));
        }
        API.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    data.updatetextAPI("1",0);
                else
                    data.updatetextAPI("1",1);
            }
        });
        push.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    data.updatetextpush("1",0);
                else
                    data.updatetextpush("1",1);
            }
        });
        textSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int CHprogress;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                for(int i = 0; i < 10; i = i+2){
                    textViews[i].setTextSize(progress+14);
                }
                for(int i = 1; i < 10; i = i+2){
                    textViews[i].setTextSize(progress+6);
                }
                CHprogress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                data.updatetextSize("1",CHprogress);
            }
        });
        spinner = v.findViewById(R.id.around);
        Log.i(" 확인","확인");
        regular = Typeface.createFromAsset(getActivity().getAssets(), "NanumBarunGothicBold.ttf");
        try{

            Field staticField = Typeface.class.getDeclaredField("MONOSPACE");
            staticField.setAccessible(true);
            staticField.set(null,regular);
            Log.i("폰트적용성공","성공");

        }catch (Exception e){
            Log.i("dsdsdsdsd",e.toString());
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String textColor = "#5ac2e0";
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor(textColor));
                data.updatearound("1",i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return v;
    }

    public void dbset(){
        data.insert(5,0,1,0);
    }

}