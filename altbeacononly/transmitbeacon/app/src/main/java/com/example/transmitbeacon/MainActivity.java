package com.example.transmitbeacon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.content.Context;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText major;
    private TextView txtDevices;
    private ProgressBar pbar;
    private Button btnTransmit;
    private static boolean flagStart = false;
    public static final String BTN_ACTIVE = "Stop Transmitting";
    public static final String BTN_INACTIVE = "Start Transmitting";

    private List<BeaconTransmitter> beaconList = new ArrayList<>();
    private static int successCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txtDevices = (TextView) findViewById(R.id.cap3);
        pbar = (ProgressBar) findViewById(R.id.pbar);
        major = findViewById(R.id.major);
        btnTransmit = findViewById(R.id.btnStart);


        btnTransmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagStart) {
                    //stop
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnTransmit.setEnabled(false);

                            for (int i = 0; i < beaconList.size(); i++)
                                beaconList.get(i).stopAdvertising();
                            

                            flagStart = false;
                            setVisiblity(false);

                            btnTransmit.setEnabled(true);
                        }
                    });

                } else {
                    //start
                    flagStart = true;
                    setVisiblity(true);
                    startTransmit();
                }
            }
        });

        setVisiblity(false);
        int result = BeaconTransmitter.checkTransmissionSupported(this);
        if (result != BeaconTransmitter.SUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getString(R.string.app_name));
            alert.setMessage(R.string.msg_no_support);
            alert.setNeutralButton(R.string.str_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.this.finish();
                }
            });
            alert.show();
        }
    }

    private void setVisiblity(boolean flag) {
        if (flag) {
            txtDevices.setVisibility(View.VISIBLE);
            pbar.setVisibility(View.VISIBLE);
            btnTransmit.setText(BTN_ACTIVE);
            major.setEnabled(false);

        } else {
            txtDevices.setVisibility(View.INVISIBLE);
            pbar.setVisibility(View.INVISIBLE);
            btnTransmit.setText(BTN_INACTIVE);
            major.setEnabled(true);
        }
    }

    private void startTransmit() {
        beaconList.clear();
        successCount = 0;
        generateBeacon(generateUUID(), major.getText().toString(), successCount + "");
    }

    private void generateBeacon(String UUID, String major, String minor) {
        
        /*  transmiiting altbeacon*/
        Beacon beacon = new Beacon.Builder()
                .setId1(UUID)
                .setId2(major)
                .setId3(minor)
                .setManufacturer(0x0118)
                .setTxPower(-59)
                //.setRssi(-66)
                .setDataFields(Arrays.asList(new Long[]{6l, 7l}))
                .build();


        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        //beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        //beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        beaconList.add(beaconTransmitter);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        });

    }

    private String generateUUID() {
        return "aaaaaaaa-1111-1111-1111-aaaaaaaaaaaa";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < beaconList.size(); i++)
            beaconList.get(i).stopAdvertising();
    }

}