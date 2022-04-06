package com.example.emitterbeacon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;


import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSetParameters;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";
    private ProgressBar pbar;
    private Button btnTransmit;
    private static boolean flagStart = false;
    public static final String BTN_ACTIVE = "Stop Transmitting";
    public static final String BTN_INACTIVE = "Start Transmitting";

    private List<BeaconTransmitter> beaconList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        pbar = findViewById(R.id.pbar);
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
            pbar.setVisibility(View.VISIBLE);
            btnTransmit.setText(BTN_ACTIVE);
        } else {
            pbar.setVisibility(View.INVISIBLE);
            btnTransmit.setText(BTN_INACTIVE);
        }
    }

    private void startTransmit() {
        beaconList.clear();
        generateBeaconURL(generateURL());
    }

    //FOR EDDYSTONE URL
    private String generateURL() {
        return "http://mariem12345678";
    } //maximum length

    private void generateBeaconURL(String URL){
        try {
            byte[] urlBytes = UrlBeaconUrlCompressor.compress(URL);
            System.out.println(URL.length()-7);
            Identifier encodedUrlIdentifier = Identifier.fromBytes(urlBytes, 0, urlBytes.length, false);
            ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
            identifiers.add(encodedUrlIdentifier);

            Beacon beacon = new Beacon.Builder()
                    .setBluetoothName("Mariem")
                    .setIdentifiers(identifiers)
                    .setManufacturer(0x0188)
                    .setTxPower(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .build();

            BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v");

            BeaconTransmitter beaconTransmitter = new BeaconTransmitter(
                getApplicationContext(), beaconParser);
            beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
            beaconList.add(beaconTransmitter);
            beaconTransmitter.startAdvertising(beacon);

        } catch (MalformedURLException e) {
            Log.d(TAG, "That URL cannot be parsed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < beaconList.size(); i++)
            beaconList.get(i).stopAdvertising();
    }
}