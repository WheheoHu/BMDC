package com.example.bmdc;


import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
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
    private String[] ProjectFrameRate_DATA = {


            "ff0e00000109020218001800001070080000",
            "ff0e00000109020219001900001070080000",
            "ff0e0000010902021e001e00001070080300",
            "ff0e0000010902021e001e00001070080000",
            "ff0e00000109020232003200001070080000",
            "ff0e0000010902023c003c00001070080300",
            "ff0e0000010902023c003c00001070080000"
    };
    private String[] ProjectFrameRate_Steps = {

            "24 fps",
            "25 fps",
            "29.97 fps",
            "30 fps",
            "50 fps",
            "59.94 fps",
            "60 fps"

    };
    private String[] Braw_DATA = {
            "ff0600000a00010203020000",
            "ff0600000a00010203030000",
            "ff0600000a00010203040000",
            "ff0600000a00010203050000",
            "ff0600000a00010203000000",
            "ff0600000a00010203010000",
    };
    private String[] ProRes_DATA = {
            "ff0600000a0001020200",
            "ff0600000a0001020201",
            "ff0600000a0001020202",
            "ff0600000a0001020203"
    };
    private String[] WB_Preset_DATA={
            "ff08000001020202e0150000",
            "ff08000001020202800c0000",
            "ff08000001020202a00f0000",
            "ff0800000102020294110000",
            "ff0800000102020264190000"
    };
    private HashMap<String, String> ProjectFrameRateHashmap = new HashMap<>();
     private Button button_DR_Video, button_DR_Extended_Video, button_DR_Film,
            button_3to1, button_5to1, button_8to1, button_12to1, button_q0, button_q5,
            button_HQ, button_422, button_LT, button_PXY,
            button_WB_Sun, button_WB_Lamp, button_WB_FLuorescent, button_WB_Shadow, button_WB_Cloudy, button_WB_Auto;
    private SeekBar seekBar_ProjectFrameRate;
    private TextView textView_framerate;
    ViewPager viewPager;
    ArrayList<CardView> settingViews;
    SettingAdapter mAdapter;
    CardView cardView_codec, cardView_WB;

    static private BleDevice bleDevice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        bleDevice = getIntent().getParcelableExtra(KEY_DATA);
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
        final BluetoothGattService service = gatt.getService(UUID.fromString("291D567A-6D75-11E6-8B77-86F30CA893D3"));
        final BluetoothGattCharacteristic Characteristic = service.getCharacteristic(UUID.fromString("B864E140-76A0-416A-BF30-5876504537D9"));
        assert bleDevice != null;
        Log.d(TAG, "onCreate: " + bleDevice.getName());
        button_DR_Video = findViewById(R.id.button_video);
        button_DR_Extended_Video = findViewById(R.id.button_extended_video);
        button_DR_Film = findViewById(R.id.button_film);
        button_3to1 = findViewById(R.id.button_CB_3);
        button_5to1 = findViewById(R.id.button_CB_5);
        button_8to1 = findViewById(R.id.button_CB_8);
        button_12to1 = findViewById(R.id.button_CB_12);
        button_q0 = findViewById(R.id.button_CQ_Q0);
        button_q5 = findViewById(R.id.button_CQ_Q5);

        button_HQ = findViewById(R.id.button_prores_HQ);
        button_422 = findViewById(R.id.button_prores_422);
        button_LT = findViewById(R.id.button_prores_LT);
        button_PXY = findViewById(R.id.button_prores_PXY);

        button_WB_Sun = findViewById(R.id.button_WB_Sun);
        button_WB_Lamp = findViewById(R.id.button_WB_Lamp);
        button_WB_FLuorescent = findViewById(R.id.button_WB_Fluorscent);
        button_WB_Shadow = findViewById(R.id.button_WB_Shadow);
        button_WB_Cloudy = findViewById(R.id.button_WB_CLoudy);
        button_WB_Auto = findViewById(R.id.button_WB_Auto);


        cardView_codec = findViewById(R.id.cardview_Codec);
        cardView_WB = findViewById(R.id.cardview_WB);
        viewPager = findViewById(R.id.viewpager_setting);
        settingViews = new ArrayList<>();
        settingViews.add(cardView_WB);
        settingViews.add(cardView_codec);

        mAdapter = new SettingAdapter(this, settingViews);
        viewPager.setAdapter(mAdapter);

        textView_framerate = findViewById(R.id.framerate);
        seekBar_ProjectFrameRate = findViewById(R.id.seekBar_PFR);
        seekBar_ProjectFrameRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_framerate.setText(ProjectFrameRate_Steps[progress]);
                vibrator.vibrate(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(ProjectFrameRate_DATA[seekBar.getProgress()]), new BleWriteCallback() {
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
            ProjectFrameRateHashmap.put(ProjectFrameRate_DATA[i], ProjectFrameRate_Steps[i]);
        }
        showDataFromCamera();

        setWBbuttons();
        setDynamicRangeButtons();
        setBRAWbutton();
        setProResButton();

    }

    private void setWBbuttons() {
        final Button[] WB_buttons={
                button_WB_Sun, button_WB_Lamp, button_WB_FLuorescent, button_WB_Shadow, button_WB_Cloudy
        };
        for(final Button button:WB_buttons){
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes( WB_Preset_DATA [Arrays.asList(WB_buttons).indexOf(button)]), new BleWriteCallback() {
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
        button_WB_Auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes( "ff04000001030002"), new BleWriteCallback() {
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

    class SettingAdapter extends PagerAdapter {
        Context context;
        ArrayList<CardView> settingList;

        SettingAdapter(Context context, ArrayList<CardView> settingList) {
            this.context = context;
            this.settingList = settingList;
        }

        @Override
        public int getCount() {
            return settingList.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public CardView instantiateItem(@NonNull ViewGroup container, int position) {
            CardView cardView = settingList.get(position);
            container.addView(cardView);

            return cardView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            CardView cardView = (CardView) object;
            container.removeView(cardView);
        }
    }


    private void setProResButton() {
        final Button[] Prores_buttons = {
                button_HQ, button_422, button_LT, button_PXY
        };
        for (final Button button : Prores_buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(ProRes_DATA[Arrays.asList(Prores_buttons).indexOf(button)]), new BleWriteCallback() {
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
        final Button[] Braw_buttons = {
                button_3to1,
                button_5to1,
                button_8to1,
                button_12to1,
                button_q0,
                button_q5
        };

        for (final Button button : Braw_buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    BleManager.getInstance().write(bleDevice, "291D567A-6D75-11E6-8B77-86F30CA893D3", "5DD3465F-1AEE-4299-8493-D2ECA2F8E1BB", HexUtil.hexStringToBytes(Braw_DATA[Arrays.asList(Braw_buttons).indexOf(button)]), new BleWriteCallback() {
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
        final Button[] Dynamic_range_Buttons = {
                button_DR_Video,
                button_DR_Extended_Video,
                button_DR_Film
        };
        for (final Button button : Dynamic_range_Buttons) {
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


