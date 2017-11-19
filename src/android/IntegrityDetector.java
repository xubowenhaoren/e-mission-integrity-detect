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

public class IntegrityDetector extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor linearAccelerometer;
    Sensor gyroscope;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_NORMAL, new Handler());

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL, new Handler());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION || sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            notifyEvent(this, sensorEvent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void notifyEvent(Context context, SensorEvent sensorEvent) {
        SimpleMovementSensorEvent simpleMovementSensorEvent = new SimpleMovementSensorEvent(sensorEvent);
        UserCache uc = UserCacheFactory.getUserCache(context);
        uc.putSensorData(R.string.key_usercache_movement_sensor, simpleMovementSensorEvent);
    }
}
