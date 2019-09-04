package com.example.bmdc;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.example.bmdc.adapter.DeviceAdapter;
import com.example.bmdc.comm.ObserverManager;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class LoginAC extends AppCompatActivity implements View.OnClickListener {
    private Button button_scan;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private DeviceAdapter mDeviceAdapter;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);



    }

    private void initView() {
        button_scan = findViewById(R.id.button_scan);
        button_scan.setText(R.string.start_scan);
        button_scan.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);

        mDeviceAdapter = new DeviceAdapter(this);
        mDeviceAdapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisConnect(BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                }
            }

            @Override
            public void onDetail(BleDevice bleDevice) {
//todo finishdetail
                if (BleManager.getInstance().isConnected(bleDevice)){
                    Intent intent=new Intent(LoginAC.this, OperationAC.class);
                    intent.putExtra(OperationAC.KEY_DATA,bleDevice);
                    startActivity(intent);
                }
            }
        });
ListView listView_devices= findViewById(R.id.list_device);
listView_devices.setAdapter(mDeviceAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_scan) {
            if (button_scan.getText().equals(getString(R.string.start_scan))) {
                checkPermissions();
            } else if (button_scan.getText().equals(R.string.stop_scan)) {
                BleManager.getInstance().cancelScan();
            }
        }
    }


    private void checkPermissions() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }
    }

    private void onPermissionGranted(String permission) {
        if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.notifyTitle)
                        .setMessage(R.string.gpsNotifyMsg)
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                        .setPositiveButton(R.string.setting,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                        startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                                    }
                                })

                        .setCancelable(false)
                        .show();
            } else {
                setScanRule();
                startScan();
            }
        }
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> list) {
                button_scan.setText(R.string.start_scan);

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanStarted(boolean b) {
                button_scan.setText(R.string.stop_scan);
                mDeviceAdapter.clearScanDevice();
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setScanRule() {

        String[] unis = "291D567A-6D75-11E6-8B77-86F30CA893D3".split(",");
        UUID[] serviceUuids = null;
        if (unis.length > 0) {
            serviceUuids = new UUID[unis.length];
            for (int i = 0; i < unis.length; i++) {
                String name = unis[i];
                String[] components = name.split("-");
                if (components.length != 5) {
                    serviceUuids[i] = null;
                } else {
                    serviceUuids[i] = UUID.fromString(unis[i]);
                }
            }
        }

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)
                .build();

        BleManager.getInstance().initScanRule(scanRuleConfig);
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                setScanRule();
                startScan();
            }
        }
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException e) {
                button_scan.setText(R.string.start_scan);
                progressDialog.dismiss();
                Toast.makeText(LoginAC.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                progressDialog.dismiss();
                mDeviceAdapter.addDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt bluetoothGatt, int i) {
                progressDialog.dismiss();

                mDeviceAdapter.removeDevice(bleDevice);
                mDeviceAdapter.notifyDataSetChanged();

                if (isActiveDisConnected) {
                    Toast.makeText(LoginAC.this, getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginAC.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }
            }
        });
    }

}
