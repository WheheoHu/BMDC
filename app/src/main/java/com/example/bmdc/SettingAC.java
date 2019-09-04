package com.example.bmdc;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.clj.fastble.data.BleDevice;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingAC extends AppCompatActivity {

    private static final String TAG = "SettingAC";
    public static final String KEY_DATA = "bledevices";
    private BleDevice bleDevice;

    /**
     * Touch listener to use for in-activity_setting UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        Log.d(TAG, "onCreate: " + bleDevice.getName());
    }
}


