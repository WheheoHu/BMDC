package com.example.bmdc;

import androidx.appcompat.app.AppCompatActivity;

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

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleIndicateCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

public class OperationAc extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    public static final String KEY_DATA = "camera";
    private static final String TAG = "OPAC";
    private Vibrator vibrator;
    private static boolean isrecord = false;
    private static String ISO_Hex;
    private String Shutter_Angle_Hex;
    private String Aperture_Hex;
    private BleDevice bleDevice;
    private String[] ISO_DATA = {
            "ff080000010e030264000000",
            "ff080000010e03027d000000",
            "ff080000010e0302a0000000",
            "ff080000010e0302c8000000",
            "ff080000010e0302fa000000",
            "ff080000010e030240010000",
            "ff080000010e030290010000",
            "ff080000010e0302f4010000",
            "ff080000010e030280020000",
            "ff080000010e030220030000",
            "ff080000010e0302e8030000",
            "ff080000010e0302e2040000",
            "ff080000010e030240060000",
            "ff080000010e0302d0070000",
            "ff080000010e0302c4090000",
            "ff080000010e0302800c0000",
            "ff080000010e0302a00f0000",
            "ff080000010e030288130000",
            "ff080000010e030200190000",
            "ff080000010e0302401f0000",
            "ff080000010e030210270000",
            "ff080000010e030200320000",
            "ff080000010e0302803e0000",
            "ff080000010e0302204e0000",
            "ff080000010e030200640000"

    };
    private String[] ISO_Steps = {
            "100", "125", "160", "200",
            "250", "320", "400", "500",
            "640", "800", "1000", "1250",
            "1600", "2000", "2500", "3200",
            "4000", "5000", "6400", "8000", "10000",
            "12800", "16000", "20000", "25600", "25600"
    };
    private String[] Aperture_DATA = {
            "ff060000000380020000",
            "ff06000000038002cd00",
            "ff060000000380029a01",
            "ff060000000380026602",
            "ff060000000380023303",
            "ff060000000380020004",
            "ff06000000038002cd04",
            "ff060000000380029905",
            "ff060000000380026606",
            "ff060000000380023307",
            "ff060000000380020008"
    };
    private String[] Aperture_Steps = {
            "100",
            "90",
            "80",
            "70",
            "60",
            "50",
            "40",
            "30",
            "20",
            "10",
            "0"
    };

    HashMap<String, String> ISOHashMap = new HashMap<>();
    HashMap<String, String> ApertureHashMap = new HashMap<>();

    private SeekBar seekBar_ISO, seekBar_IRIS, seekBar_SHUTTER;
    private TextView textView_ISOValue, textView_IRISValue, textView_SHUTTERValue, textView_ShowISO, textView_ShowShutter, textView_ShowAperture;

    //    public BluetoothGattCharacteristic getCharacteristic() {
//        return characteristic;
//    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {


        if (seekBar == seekBar_IRIS) {
            // vibrator.vibrate(20);
            textView_IRISValue.setText("IRIS: " + (100 - seekBar_IRIS.getProgress()) + " %");

        } else if (seekBar == seekBar_SHUTTER) {
            textView_SHUTTERValue.setText("SHUTTER: " + seekBar_SHUTTER.getProgress());
        } else if (seekBar == seekBar_ISO) {

            textView_ISOValue.setText("ISO: " + ISOHashMap.get(ISO_DATA[(int) ((float) seekBar_ISO.getProgress() * (ISO_DATA.length - 1) / 100)]));

            // Log.d(TAG, "onProgressChanged: testnum: "+ISO_DATA.length);

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


        if (seekBar == seekBar_ISO) {
            vibrator.vibrate(50);
            BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(ISO_DATA[(int) ((float) seekBar_ISO.getProgress() * (ISO_DATA.length - 1) / 100)]), new BleWriteCallback() {
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
        } else if (seekBar == seekBar_IRIS) {
            vibrator.vibrate(50);
            BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(Aperture_DATA[(int) ((float) seekBar_IRIS.getProgress() * (Aperture_DATA.length - 1) / 100)]), new BleWriteCallback() {
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
                public void onWriteFailure(final BleException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: " + e.toString());
                        }
                    });
                }
            });
        } else if (seekBar == seekBar_SHUTTER) {
            Log.d(TAG, "onStopTrackingTouch: mm");
        }
    }

    public void getDataFormCamera() {

        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        BluetoothGattService service = gatt.getService(UUID.fromString("291D567A-6D75-11E6-8B77-86F30CA893D3"));
        final BluetoothGattCharacteristic Characteristic = service.getCharacteristic(UUID.fromString("B864E140-76A0-416A-BF30-5876504537D9"));
        BleManager.getInstance().indicate(
                bleDevice,
                "291D567A-6D75-11E6-8B77-86F30CA893D3",
                "B864E140-76A0-416A-BF30-5876504537D9",
                new BleIndicateCallback() {
                    @Override
                    public void onIndicateSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: indicate success");
                            }
                        });
                    }

                    @Override
                    public void onIndicateFailure(final BleException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, e.toString());
                            }
                        });
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] bytes) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                boolean isochange = false;
                                boolean aperturechange = false;
                                boolean shutterchange = false;
                                String HexValue = HexUtil.formatHexString(Characteristic.getValue());
                                Log.d(TAG, "onCharacteristicChanged: " + HexValue);
                                if (Pattern.matches(".*ff080000010e.*", HexValue)) {
                                    ISO_Hex = HexValue;
                                    isochange = true;
                                    Log.d(TAG, "onCharacteristicChanged: ISOHex: " + ISO_Hex);
                                } else if (Pattern.matches(".*ff0600000002.*", HexValue)) {
                                    Aperture_Hex = HexValue;
                                    aperturechange = true;
                                    Log.d(TAG, "onCharacteristicChanged: ApertureHex: " + Aperture_Hex);
                                } else if (Pattern.matches(".*ff080000010b.*", HexValue)) {
                                    Shutter_Angle_Hex = HexValue;
                                    Log.d(TAG, "onCharacteristicChanged: ShutterHex: " + Shutter_Angle_Hex);
                                }
                                if (isochange) {
                                    for (String ISOdata : ISO_DATA) {
                                        if (ISOdata.equals(ISO_Hex)) {
                                            String isodata = ISOHashMap.get(ISOdata);
                                            textView_ShowISO.setText(isodata);
                                            textView_ISOValue.setText("ISO: " + isodata);
                                            seekBar_ISO.setProgress(Arrays.asList(ISO_Steps).indexOf(isodata) * 100 / 24);
                                        }
                                    }
                                    isochange = false;
                                } else if (aperturechange) {
                                    for (String Ap_data : Aperture_DATA) {
                                        if (Ap_data.equals(Aperture_Hex)) {
                                            String aperturedata = ApertureHashMap.get(Ap_data);
                                            textView_ShowAperture.setText(aperturedata);
                                            textView_IRISValue.setText("IRIS: " + aperturedata);
                                            seekBar_IRIS.setProgress(Arrays.asList(Aperture_Steps).indexOf(aperturedata) * 100 / Aperture_DATA.length);
                                        }
                                    }
                                    aperturechange = false;
                                }

                            }
                        });


                    }
                }

        );


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_operation);

        seekBar_IRIS = findViewById(R.id.seekbar_IRIS);
        seekBar_IRIS.setOnSeekBarChangeListener(this);
        seekBar_ISO = findViewById(R.id.seekbar_ISO);
        seekBar_ISO.setOnSeekBarChangeListener(this);
        seekBar_SHUTTER = findViewById(R.id.seekbar_SHUTTER);
        seekBar_SHUTTER.setOnSeekBarChangeListener(this);

        textView_IRISValue = findViewById(R.id.textView_IRISValue);
        textView_ISOValue = findViewById(R.id.textView_ISOValue);
        textView_SHUTTERValue = findViewById(R.id.textView_SHUTTERValue);
        textView_ShowAperture = findViewById(R.id.textView_Show_Aperture);
        textView_ShowISO = findViewById(R.id.textView_Show_ISO);
        textView_ShowShutter = findViewById(R.id.textView_Show_Shutter);

        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //Init HashMap
        for (int i = 0; i < ISO_DATA.length; i++) {
            ISOHashMap.put(ISO_DATA[i], ISO_Steps[i]);
        }
        for (int i = 0; i < Aperture_DATA.length; i++) {
            ApertureHashMap.put(Aperture_DATA[i], Aperture_Steps[i]);
        }


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
        getDataFormCamera();


    }

}
