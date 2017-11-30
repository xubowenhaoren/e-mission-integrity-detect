package edu.berkeley.eecs.emission.cordova.integrityDetect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import edu.berkeley.eecs.emission.cordova.unifiedlogger.Log;

public class SimpleMovementSensorEvent {

    public String getDataType() {
        return dataType;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getTimeStamp() {
        return timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }

    private static String TAG =  SimpleMovementSensorEvent.class.getSimpleName();
    private static final float NS2S = 1.0f / 1000000000.0f;
    // unit m/s^2 for acceleration
    // unit radians/second for gyroscope
    private float x;
    private float y;
    private float z;
    private int accuracy;
    // unit second
    private float timestamp;
    // dataType should be either Acceleration or AngularSpeed
    // Please refer to IntegrityDetector for more details.
    private String dataType;

    public SimpleMovementSensorEvent() {
    }

    public SimpleMovementSensorEvent(SensorEvent sensorEvent) {

        Log.d(TAG, "SimpleMovementSensorEvent created");

        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];

        accuracy = sensorEvent.accuracy;
        timestamp = sensorEvent.timestamp * NS2S;

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            dataType = "Acceleration";
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            dataType = "AngularSpeed";
        }
    }
}
