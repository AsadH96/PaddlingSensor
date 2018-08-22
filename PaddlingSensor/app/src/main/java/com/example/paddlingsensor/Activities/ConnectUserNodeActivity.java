package com.example.paddlingsensor.Activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.paddlingsensor.Model.Nodes.PaddleNode;
import com.example.paddlingsensor.Model.PaddlingSensorModel;
import com.example.paddlingsensor.Model.RecyclerViewAdapter;
import com.example.paddlingsensor.R;

import java.util.ArrayList;

/**
 * Created by Asad Hussain.
 */

public class ConnectUserNodeActivity extends AppCompatActivity {

    private static final String TAG = "ConnectUserNodeActivity";
    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 102;

    private Button searchButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BluetoothDevice> foundDevices;
    private BluetoothAdapter btAdapter;
    private PaddlingSensorModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_user_node);
        model = PaddlingSensorModel.getInstance();

        foundDevices = new ArrayList<>();

        searchButton = (Button) findViewById(R.id.searchButtonUser);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewUser);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        recyclerAdapter = new RecyclerViewAdapter(foundDevices, "UserNodeConnector", model, this);
        recyclerView.setAdapter(recyclerAdapter);

        searchButton.setOnClickListener(new SearchListener());

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        verifyBluetoothPermissions();
    }

    /**
     * Handle click on searchButton
     */
    private class SearchListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            searchIMUSensors();
        }
    }

    /**
     * Callback for checking permissions
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                finish();
            }
        }
    }

    /**
     * Start discovering Bluetooth devices and add a BroadcastReceiver
     */
    private void searchIMUSensors() {

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }
        if (!btAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            btAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver, discoverDevicesIntent);
        }
    }

    /**
     * Add the found Bluetooth device to RecyclerView if it's an LPMS (from LP-Research) IMU-sensor
     *
     * @param device The discovered Bluetooth device
     */
    public void addSensorToList(BluetoothDevice device) {
        //Only add LPMS-sensors (IMU-sensors from LP-Research) to list
        if (device.getName().contains("LPMS") && !foundDevices.contains(device)) {
            Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
            foundDevices.add(device);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handle nearby discoverable Bluetooth devices in the BroadcastReceiver
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getName() != null) {
                    Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                    addSensorToList(device);
                }
            }
        }
    };

    /**
     * Check permissions to access Bluetooth
     */
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != 0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, BLUETOOTH_PERMISSION_REQUEST_CODE); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    /**
     * Check if Bluetooth is enabled, prompt user to enable if it's not enabled
     */
    private void verifyBluetoothPermissions() {
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLUETOOTH_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * User paddle node has been chosen, move to next activity to choose front paddle node
     */
    public void userNodeConnected() {
//        Intent intent = new Intent(ConnectUserNodeActivity.this, ConnectFrontNodeActivity.class);
//        intent.putExtra("model", model);
        unregisterReceiver(mBroadcastReceiver);
        startActivity(new Intent(ConnectUserNodeActivity.this, ConnectFrontNodeActivity.class));
        finish();
    }
}
