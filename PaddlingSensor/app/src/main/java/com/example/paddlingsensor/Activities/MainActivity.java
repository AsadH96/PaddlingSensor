package com.example.paddlingsensor.Activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.paddlingsensor.Model.PaddlingSensorModel;
import com.example.paddlingsensor.R;

import java.util.Locale;

/**
 * Created by Asad Hussain.
 */

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private Button instructionsButton;
    private Button settingsButton;

    private BluetoothAdapter bluetoothAdapter;

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String[] STORAGE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = (Button) findViewById(R.id.startButton);
        instructionsButton = (Button) findViewById(R.id.instructionsButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        verifyStoragePermissions();

        startButton.setOnClickListener(new StartHandler());
        instructionsButton.setOnClickListener(new InstructionHandler());
        settingsButton.setOnClickListener(new SettingsHandler());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setPreferences();
    }

    private void setPreferences() {

        PaddlingSensorModel model = PaddlingSensorModel.getInstance();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        // Sampling frequency
        String samplingFrequency = sharedPreferences.getString("sampling_frequency", "200");
        model.setSamplingFrequency(Integer.parseInt(samplingFrequency));

        String syncLevel1 = sharedPreferences.getString("sync_level_one", "50");
        model.setSyncLevelOne(Float.parseFloat(syncLevel1));

        String syncLevel2 = sharedPreferences.getString("sync_level_two", "100");
        model.setSyncLevelTwo(Float.parseFloat(syncLevel2));

        String currentLanguage = Locale.getDefault().getLanguage();
        String settingsLanguage = sharedPreferences.getString("language", "en");

        if (!currentLanguage.equals(new Locale(settingsLanguage).getLanguage())) {
            setLocale(settingsLanguage);
            recreate();
        }
    }

    public void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }

    private class StartHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, ConnectUserNodeActivity.class));
        }
    }

    private class InstructionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, InstructionsActivity.class));
        }
    }

    private class SettingsHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public boolean verifyStoragePermissions() {
        // Check write permission
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Permission not found, ask the user to give permission
            ActivityCompat.requestPermissions(
                    this,
                    STORAGE_PERMISSION,
                    STORAGE_PERMISSION_REQUEST_CODE
            );
        }

        return permission == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    protected void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
    }
}
