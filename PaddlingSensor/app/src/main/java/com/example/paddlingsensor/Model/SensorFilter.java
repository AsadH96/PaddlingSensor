package com.example.paddlingsensor.Model;

/**
 * Created by Asad Hussain.
 */

public class SensorFilter {
    private float filterFactor, filteredValueAccX, previousValueAccX, filteredValueAccY, previousValueAccY, filteredValueAccZ, previousValueAccZ,
            filteredValueGyrX, previousValueGyrX, filteredValueGyrY, previousValueGyrY, filteredValueGyrZ, previousValueGyrZ;

    public SensorFilter(float filterFactor, float value) {

        this.filterFactor = filterFactor;
        this.previousValueAccX = value;
        this.filteredValueAccX = value;
        this.previousValueAccY = value;
        this.filteredValueAccY = value;
        this.previousValueAccZ = value;
        this.filteredValueAccZ = value;

        this.previousValueGyrX = value;
        this.filteredValueGyrX = value;
        this.previousValueGyrY = value;
        this.filteredValueGyrY = value;
        this.previousValueGyrZ = value;
        this.filteredValueGyrZ = value;
    }

    public float filterDataAccX(float sensorValue) {
        filteredValueAccX = filterFactor * previousValueAccX + (1.0F - filterFactor) * sensorValue;
        previousValueAccX = filteredValueAccX;
        return filteredValueAccX;
    }

    public float filterDataAccY(float sensorValue) {
        filteredValueAccY = filterFactor * previousValueAccY + (1.0F - filterFactor) * sensorValue;
        previousValueAccY = filteredValueAccY;
        return filteredValueAccY;
    }

    public float filterDataAccZ(float sensorValue) {
        filteredValueAccZ = filterFactor * previousValueAccZ + (1.0F - filterFactor) * sensorValue;
        previousValueAccZ = filteredValueAccZ;
        return filteredValueAccZ;
    }

    public float filterDataGyrX(float sensorValue) {
        filteredValueGyrX = filterFactor * previousValueGyrX + (1.0F - filterFactor) * sensorValue;
        previousValueGyrX = filteredValueGyrX;
        return filteredValueGyrX;
    }

    public float filterDataGyrY(float sensorValue) {
        filteredValueGyrY = filterFactor * previousValueGyrY + (1.0F - filterFactor) * sensorValue;
        previousValueGyrY = filteredValueGyrY;
        return filteredValueGyrY;
    }

    public float filterDataGyrZ(float sensorValue) {
        filteredValueGyrZ = filterFactor * previousValueGyrZ + (1.0F - filterFactor) * sensorValue;
        previousValueGyrZ = filteredValueGyrZ;
        return filteredValueGyrZ;
    }

    public void setFilterFactor(float filterFactor) {
        this.filterFactor = filterFactor;
    }

    public float getFilterFactor() {
        return filterFactor;
    }
}
