package com.example.bmdc;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import java.util.UUID;

public class SettingAC extends AppCompatActivity {

    private static final String TAG = "SettingAC";
    public static final String KEY_DATA = "bledevices";
    private String[] DynamicRange_DATA={

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        BleDevice bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        BluetoothGattService service = gatt.getService(UUID.fromString("291D567A-6D75-11E6-8B77-86F30CA893D3"));
        final BluetoothGattCharacteristic Characteristic = service.getCharacteristic(UUID.fromString("B864E140-76A0-416A-BF30-5876504537D9"));
        assert bleDevice != null;
        Log.d(TAG, "onCreate: " + bleDevice.getName());
    }
}


