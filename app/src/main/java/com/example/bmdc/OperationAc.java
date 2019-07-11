package com.example.bmdc;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

public class OperationAc extends AppCompatActivity {
    public static final String KEY_DATA = "camera";
    private static final String TAG = "OPAC";


    private static boolean isrecord = false;

    private BleDevice bleDevice;
    private BluetoothGattServer bluetoothGattServer;
    private BluetoothGattCharacteristic characteristic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operation);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        if (bleDevice == null) {
            finish();
        }
        Log.d(TAG, "onCreate: " + bleDevice.getName());

        Button button_rec = findViewById(R.id.button_record);
        button_rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isrecord) {
                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes("ff0800000a01010000000000"), new BleWriteCallback() {
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

                    isrecord = false;
                } else {

                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes("ff0800000a01010002000000"), new BleWriteCallback() {
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

                    isrecord = true;

                }
            }
        });

        TextView textView_camera_name = findViewById(R.id.textView_Camera_name);
        textView_camera_name.setText(bleDevice.getName());

    }
}
