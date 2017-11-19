package edu.berkeley.eecs.emission.cordova.integrityDetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class IntegrityNotificationReceiver extends BroadcastReceiver {

    private static String TAG =  IntegrityNotificationReceiver.class.getSimpleName();
    private IntegrityDetector integrityDetector;
    private boolean notBeenCreated = true;

    public IntegrityNotificationReceiver() {
        // The automatically created receiver needs a default constructor
        android.util.Log.i(TAG, "noarg constructor called");
    }

    public IntegrityNotificationReceiver(Context context) {
        android.util.Log.i(TAG, "constructor called with arg "+context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("local.transition.exited_geofence")){
            if (notBeenCreated) {
                integrityDetector = new IntegrityDetector();
            } else {
                integrityDetector.startService(intent);
            }
        } else if (action.equals("local.transition.stopped_moving")) {
                integrityDetector.stopSelf();
        }

    }
}