package com.example.bmdc;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.Arrays;
import java.util.UUID;

public class SettingAC extends AppCompatActivity {

    private static final String TAG = "SettingAC";
    public static final String KEY_DATA = "bledevices";
    private String[] DynamicRange_DATA = {
            "ff0500000107010201",
            "ff0500000107010202",
            "ff0500000107010200"
    };
    static private Button button_DR_Video, button_DR_Extended_Video, button_DR_Film;

    static private BleDevice bleDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        final BluetoothGattService service = gatt.getService(UUID.fromString("291D567A-6D75-11E6-8B77-86F30CA893D3"));
        final BluetoothGattCharacteristic Characteristic = service.getCharacteristic(UUID.fromString("5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB"));
        assert bleDevice != null;
        Log.d(TAG, "onCreate: " + bleDevice.getName());
        button_DR_Video = findViewById(R.id.button_video);
        button_DR_Extended_Video = findViewById(R.id.button_extended_video);
        button_DR_Film = findViewById(R.id.button_film);



        setDynamicRangeButtons();
    }

    private void setDynamicRangeButtons() {
        final Button[] Dynamic_range_Buttons={
                button_DR_Video,
                button_DR_Extended_Video,
                button_DR_Film
        };
        for (final Button button:Dynamic_range_Buttons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(DynamicRange_DATA[Arrays.asList(Dynamic_range_Buttons).indexOf(button)]), new BleWriteCallback() {
                        @Override
                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: write success, current: " + current
                                            + " total: " + total
                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                }
                            });
                        }

                        @Override
                        public void onWriteFailure(final BleException exception) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "run: " + exception.toString());
                                }
                            });

                        }
                    });
                }
            });
        }

    }
}


