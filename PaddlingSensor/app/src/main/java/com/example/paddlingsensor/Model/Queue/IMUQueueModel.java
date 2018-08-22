package com.example.paddlingsensor.Model.Queue;

/**
 * Created by Asad Hussain.
 */

public class IMUQueueModel {
    private float accX, accY, accZ, gyrX, gyrY, gyrZ, eulerX;
    private long timestamp;

    public IMUQueueModel(/*float accX, float accY, float accZ, float gyrX, float gyrY, float gyrZ, */float eulerX, long t) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.gyrX = gyrX;
        this.gyrY = gyrY;
        this.gyrZ = gyrZ;
        this.eulerX = eulerX;
        this.timestamp = t;
    }

    public float getAccX() {
        return accX;
    }

    public float getAccY() {
        return accY;
    }

    public float getAccZ() {
        return accZ;
    }

    public float getGyrX() {
        return gyrX;
    }

    public float getGyrY() {
        return gyrY;
    }

    public float getGyrZ() {
        return gyrZ;
    }

    public float getEulerX() {
        return eulerX;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
