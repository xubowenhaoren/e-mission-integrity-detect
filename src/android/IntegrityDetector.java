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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;

import edu.berkeley.eecs.emission.cordova.unifiedlogger.Log;

public class IntegrityDetector extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor linearAccelerometer;
    private final String linearAccTAG = "linearAcceleration";

    // Time in milliseconds: 30s -> 30000 milliseconds
    private static final long timeThreshold = 30000;
    private static final long countThreshold = 3000;
    private long startTime;

    private LinkedList<float[]> rawValuesWindow;
    private ArrayList<float[]> rawValues;
    private ArrayList<float[]> valuesLowPass;

    private LinkedList<Float> xLowPass;
    private LinkedList<Float> yLowPass;
    private LinkedList<Float> zLowPass;

    private boolean isReady = false;
    private boolean isInitialized = false;

    private double xThreshold;
    private double yThreshold;
    private double zThreshold;

    private ArrayList<Long> timeStampsX;
    private ArrayList<Long> timeStampsY;
    private ArrayList<Long> timeStampsZ;

    private int numberOfRawValues = 0;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitude;
    private double longitude;

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

        startTime = System.currentTimeMillis();
        rawValuesWindow = new LinkedList<float[]>();
        rawValues = new ArrayList<float[]>();
        valuesLowPass = new ArrayList<float[]>();

        xLowPass = new LinkedList<Float>();
        yLowPass = new LinkedList<Float>();
        zLowPass = new LinkedList<Float>();

        timeStampsX = new ArrayList<Long>();
        timeStampsY = new ArrayList<Long>();
        timeStampsZ = new ArrayList<Long>();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!isInitialized) {
            if (rawValuesWindow.size() < 5) {
                rawValuesWindow.offer(sensorEvent.values.clone());
                rawValues.add(sensorEvent.values);
            } else if (rawValuesWindow.size() == 5 && !isReady) {
                float[] values = getLowPassValue();
                assignValues1(values);
                valuesLowPass.add(values);
                isReady = true;
            }

            if (isReady) {
                rawValuesWindow.poll();
                rawValuesWindow.offer(sensorEvent.values.clone());
                rawValues.add(sensorEvent.values);
                float[] values = getLowPassValue();
                assignValues1(values);
                valuesLowPass.add(values);
            }
            long currTime = System.currentTimeMillis() - startTime;
            if (currTime >= timeThreshold) {
                xThreshold = getAdaptiveThreshold(xLowPass);
                yThreshold = getAdaptiveThreshold(yLowPass);
                zThreshold = getAdaptiveThreshold(zLowPass);
                isInitialized = true;
                startTime = System.currentTimeMillis();
            }
        } else {
            rawValuesWindow.poll();
            rawValuesWindow.offer(sensorEvent.values.clone());
            numberOfRawValues++;
            float[] values = getLowPassValue();
            assignValues2(values);

            xThreshold = getAdaptiveThreshold(xLowPass);
            if (checkBump(values[0], xThreshold)) {
                timeStampsX.add(System.currentTimeMillis());
            }

            yThreshold = getAdaptiveThreshold(yLowPass);
            if (checkBump(values[1], yThreshold)) {
                timeStampsY.add(System.currentTimeMillis());
            }

            zThreshold = getAdaptiveThreshold(zLowPass);
            if (checkBump(values[2], zThreshold)) {
                timeStampsZ.add(System.currentTimeMillis());
            }

            if (numberOfRawValues >= countThreshold) {
                // If the last input is identified as bump,
                // then the time stamps added as bumps may be greater then the time stamp we get directly from sensor data.
                // save the output values  into the database
                if (Math.abs(latitude - 0.0) > 0.001 && Math.abs(longitude - 0.0) > 0.001) {
                    double[] resultX = new double[2];
                    double[] resultY = new double[2];
                    double[] resultZ = new double[2];

                    if (timeStampsX.size() != 0) {
                        resultX = getSTDandMean(timeStampsX);
                    }

                    if (timeStampsY.size() != 0) {
                        resultY = getSTDandMean(timeStampsY);
                    }

                    if (timeStampsZ.size() != 0) {
                        resultZ = getSTDandMean(timeStampsZ);
                    }


                    saveToDatabase(this, new SimpleMovementSensorEvent(resultX[0], resultX[1], timeStampsX, resultY[0], resultY[1], timeStampsY, resultZ[0], resultZ[1], timeStampsZ, latitude, longitude, sensorEvent.timestamp));
                    Log.d(this, linearAccTAG, "Saved into database");
                }
                
                renew();
            }
        }
    }

    private void renew() {

        startTime = System.currentTimeMillis();

        rawValuesWindow.clear();
        rawValues.clear();
        valuesLowPass.clear();

        xLowPass.clear();
        yLowPass.clear();
        zLowPass.clear();

        isReady = false;
        isInitialized = false;

        timeStampsX.clear();
        timeStampsY.clear();
        timeStampsZ.clear();
        
        numberOfRawValues = 0;
    }

    private boolean checkBump(float lowPassValue, double threshold) {
        return lowPassValue >= threshold;
    }

    private void assignValues1(float[] values) {
        xLowPass.offer(values[0]);
        yLowPass.offer(values[1]);
        zLowPass.offer(values[2]);
    }

    private void assignValues2(float[] values) {
        xLowPass.poll();
        xLowPass.offer(values[0]);

        yLowPass.poll();
        yLowPass.offer(values[1]);

        zLowPass.poll();
        zLowPass.offer(values[2]);
    }

    private static float mean(LinkedList<Float> values) {
        float sum = 0f;
        for (Float n : values) {
            sum += n;
        }
        return sum / values.size();
    }

    private static double stdv(LinkedList<Float> values, float mean) {
        float sum = 0f;
        for (Float n : values) {
            sum += Math.pow(n - mean, 2);
        }
        return Math.sqrt(sum / values.size());
    }

    private static double[] getSTDandMean(ArrayList<Long> timeStamps) {
        double sum = 0.0;
        for (Long l : timeStamps) {
            sum += l;
        }
        double mean = sum / timeStamps.size();

        double sum_std = 0.0;
        for (Long l : timeStamps) {
            sum_std += Math.pow(l - mean, 2);
        }
        double std = Math.sqrt(sum / timeStamps.size());

        return new double[]{mean, std};
    }

    private double getAdaptiveThreshold(LinkedList<Float> lowPassValues) {
        float mean = mean(lowPassValues);
        double stdv = stdv(lowPassValues, mean);
        return mean + 3 * stdv;
    }

    private float[] getLowPassValue() {
        float[] output = new float[3];
        float[] input0 = rawValuesWindow.get(0);
        float test[] = new float[3];
        try {
            test = rawValuesWindow.get(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        // System.out.println(test[0]);
        float[] input1 = rawValuesWindow.get(1);
        float[] input2 = rawValuesWindow.get(2);
        float[] input3 = rawValuesWindow.get(3);
        float[] input4 = rawValuesWindow.get(4);

        for (int i = 0; i < input0.length; i++) {
            output[i] = 0.1f * input0[i] + 0.2f * input1[i] + 0.5f * input2[i] + 0.2f * input3[i] + 0.1f * input4[i];
        }
        return output;
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
}