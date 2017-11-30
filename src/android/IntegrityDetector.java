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
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import edu.berkeley.eecs.emission.cordova.unifiedlogger.Log;

public class IntegrityDetector extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor linearAccelerometer;
    Sensor gyroscope;
    private final String linearAccTAG = "linearAcceleration";
    private final String gyroTAG = "gyroscope";
    private final String startTAG = "onStartCommand";
    private final String savingTAG = "saveToDatabase";
    private final String destoryTAG = "onDestroy";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(this, startTAG, "onStartCommand called");    

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); 
        sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.d(this, linearAccTAG, Float.toString(x) + ", " + Float.toString(y) + ", " + Float.toString(z));    
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d(this, gyroTAG, Float.toString(x) + ", " + Float.toString(y) + ", " + Float.toString(z));    
        }      

        saveToDatabase(this, sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.d(this, linearAccTAG, "onAccuracyChanged called, accuracy: " + accuracy);    
        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            Log.d(this, gyroTAG, "onAccuracyChanged called, accuracy: " + accuracy);    
        }
    }

    public void saveToDatabase(Context context, SensorEvent sensorEvent) {
        Log.d(this, savingTAG, "saveToDatabase called");    
        SimpleMovementSensorEvent simpleMovementSensorEvent = new SimpleMovementSensorEvent(sensorEvent);
        UserCache uc = UserCacheFactory.getUserCache(context);
        uc.putSensorData(R.string.movement_sensor, simpleMovementSensorEvent);
    }

    @Override
    public void onDestroy() {
        Log.d(this, destoryTAG, "onDestroy called");    
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
