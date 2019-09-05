package com.example.bmdc;


import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class SettingAC extends AppCompatActivity {

    private static final String TAG = "SettingAC";
    public static final String KEY_DATA = "bledevices";
    private Vibrator vibrator;
    private String[] DynamicRange_DATA = {
            "ff0500000107010201",
            "ff0500000107010202",
            "ff0500000107010200"
    };
    private  String[] ProjectFrameRate_DATA={


           "ff0e00000109020218001800001070080000",
           "ff0e00000109020219001900001070080000",
           "ff0e0000010902021e001e00001070080300",
           "ff0e0000010902021e001e00001070080000",
           "ff0e00000109020232003200001070080000",
           "ff0e0000010902023c003c00001070080300",
           "ff0e0000010902023c003c00001070080000"
    };
    private  String[] ProjectFrameRate_Steps={

            "24 fps",
            "25 fps",
            "29.97 fps",
            "30 fps",
            "50 fps",
            "59.94 fps",
            "60 fps"

    };
    private String[] Braw_DATA={
           " ff0600000a00010203020000",
           " ff0600000a00010203030000",
           " ff0600000a00010203040000",
           " ff0600000a00010203050000",
           " ff0600000a00010203000000",
           " ff0600000a00010203010000",
    };
    private String[] ProRes_DATA={
           " ff0600000a0001020200",
           " ff0600000a0001020201",
           " ff0600000a0001020202",
           " ff0600000a0001020203"
    };
    private HashMap<String,String> ProjectFrameRateHashmap=new HashMap<>();
    static private Button button_DR_Video, button_DR_Extended_Video, button_DR_Film,
                            button_3to1,button_5to1,button_8to1,button_12to1,button_q0,button_q5,
                            button_HQ,button_422,button_LT,button_PXY;
    private SeekBar seekBar_ProjectFrameRate;
    private TextView textView_framerate;
    static private BleDevice bleDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        final BluetoothGattService service = gatt.getService(UUID.fromString("291D567A-6D75-11E6-8B77-86F30CA893D3"));
        final BluetoothGattCharacteristic Characteristic = service.getCharacteristic(UUID.fromString("5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB"));
        assert bleDevice != null;
        Log.d(TAG, "onCreate: " + bleDevice.getName());
        button_DR_Video = findViewById(R.id.button_video);
        button_DR_Extended_Video = findViewById(R.id.button_extended_video);
        button_DR_Film = findViewById(R.id.button_film);
        button_3to1=findViewById(R.id.button_CB_3);
        button_5to1=findViewById(R.id.button_CB_5);
        button_8to1=findViewById(R.id.button_CB_8);
        button_12to1=findViewById(R.id.button_CB_12);
        button_q0=findViewById(R.id.button_CQ_Q0);
        button_q5=findViewById(R.id.button_CQ_Q5);

        button_HQ=findViewById(R.id.button_prores_HQ);
        button_422=findViewById(R.id.button_prores_422);
        button_LT=findViewById(R.id.button_prores_LT);
        button_PXY=findViewById(R.id.button_prores_PXY);

        textView_framerate=findViewById(R.id.framerate);
        seekBar_ProjectFrameRate=findViewById(R.id.seekBar_PFR);
        seekBar_ProjectFrameRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    textView_framerate.setText(ProjectFrameRate_Steps[progress]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(ProjectFrameRate_DATA[seekBar.getProgress() ]), new BleWriteCallback() {
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
                        vibrator.vibrate(50);
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
        for (int i = 0; i < ProjectFrameRate_DATA.length; i++) {
            ProjectFrameRateHashmap.put(ProjectFrameRate_DATA[i],ProjectFrameRate_Steps[i]);
        }
        showDataFromCamera();
        setDynamicRangeButtons();
        setBRAWbutton();
        setProresButton();

    }

    private void setProresButton() {
        final Button[] Prores_buttons={
                button_HQ,button_422,button_LT,button_PXY
        };
        for (final Button button:Prores_buttons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(Braw_DATA[Arrays.asList(Prores_buttons).indexOf(button)]), new BleWriteCallback() {
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
                            vibrator.vibrate(50);
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

    private void setBRAWbutton() {
        final Button[] Braw_buttons={
                button_3to1,
                button_5to1,
                button_8to1,
                button_12to1,
                button_q0,
                button_q5
        };
        for (final Button button:Braw_buttons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes("ff0c0000090202020000c60c00000000"+Braw_DATA[Arrays.asList(Braw_buttons).indexOf(button)]), new BleWriteCallback() {
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
                            vibrator.vibrate(50);
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

    private void showDataFromCamera() {

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
                            vibrator.vibrate(50);
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


