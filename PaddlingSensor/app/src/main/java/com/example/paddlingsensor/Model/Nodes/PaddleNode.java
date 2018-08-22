package com.example.paddlingsensor.Model.Nodes;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Asad Hussain.
 */

public class PaddleNode {
    private String macAddress;
    private BluetoothDevice bluetoothDevice;

    public PaddleNode(BluetoothDevice device) {
        this.bluetoothDevice = device;
    }

    public String getMacAddress() {
        return bluetoothDevice.getAddress();
    }

    public String getName() {
        return bluetoothDevice.getName();
    }
}
