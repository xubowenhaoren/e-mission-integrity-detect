package edu.berkeley.eecs.emission.cordova.integrityDetect;

import edu.berkeley.eecs.emission.R;
import edu.berkeley.eecs.emission.cordova.usercache.UserCache;
import edu.berkeley.eecs.emission.cordova.usercache.UserCacheFactory;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.eecs.emission.cordova.unifiedlogger.Log;

public class IntegrityDetector extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor linearAccelerometer;
    private final String linearAccTAG = "linearAcceleration";

    ArrayList<Double> input = new ArrayList<Double>();

    private final double PI = 3.1415926535897932384626433832795;
    private final double FREQ = 1.0;
    // Unit: mm
    private final double threshold = 50;
    // Unit: s
    private final long timeDuration = 30;

    long startTime = 0;
    int bumpCount = 0;
    int resultCount = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        // Unit: second
        long tmpTimeDuration = (System.currentTimeMillis() - startTime) / 1000;
        if (tmpTimeDuration >= timeDuration) {
//            Log.d(this, linearAccTAG, "number of results is " + resultCount);
//            Log.d(this, linearAccTAG, "number of bumps is " + bumpCount);
            startTime = System.currentTimeMillis();
            bumpCount = 0;
            resultCount = 0;
            saveToDatabase(this, new SimpleMovementSensorEvent(sensorEvent.accuracy, bumpCount, threshold, timeDuration));
        }

        // Unit: convert m/s^2 to mm/s^2
        float x = sensorEvent.values[0] * 1000;
        float y = sensorEvent.values[1] * 1000;
        float z = sensorEvent.values[2] * 1000;

//        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//            Log.d(this, linearAccTAG, Float.toString(x) + ", " + Float.toString(y) + ", " + Float.toString(z));
//        }

        double v = (double) (x * x + y * y + z * z);
//        Log.d(this, linearAccTAG, "v is " + v);
        input.add(Math.sqrt(v));

        if (input.size() == 5) {
            Double averageAccel = calculateAverage(input);
//            Log.d(this, linearAccTAG, "average is " + averageAccel);

            Double result = accel2mms(averageAccel, FREQ);
            resultCount++;
//            Log.d(this, linearAccTAG, "result is " + result);

            if (result > threshold) {
                bumpCount++;
            }
            input.clear();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.d(this, linearAccTAG, "onAccuracyChanged called, accuracy: " + accuracy);
        }
    }

    public void saveToDatabase(Context context, SimpleMovementSensorEvent simpleMovementSensorEvent) {
        String savingTAG = "saveToDatabase";
        Log.d(this, savingTAG, "saveToDatabase called");
        UserCache uc = UserCacheFactory.getUserCache(context);
        uc.putSensorData(R.string.movement_sensor, simpleMovementSensorEvent);
    }

    @Override
    public void onDestroy() {
        String destoryTAG = "onDestroy";
        Log.d(this, destoryTAG, "onDestroy called");
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    private double calculateAverage(List<Double> list) {
        Double sum = 0.0;
        if (!list.isEmpty()) {
            for (Double n : list) {
                sum += n;
            }
            return sum / list.size();
        }
        return sum;
    }

    /*
        This calculation is based on a sinusoidal waveform,
        so the frequency would be representative of the physical movement of the accelerometer
        not the frequency of the sampling rate.
     */
    private double accel2mms(double accel, double freq) {
        return accel / (2 * PI * freq);
    }
}
