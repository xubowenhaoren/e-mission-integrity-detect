package edu.berkeley.eecs.emission.cordova.integrityDetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import edu.berkeley.eecs.emission.R;


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
        Intent i = new Intent(context, IntegrityDetector.class);
        if (action.equals(context.getString(R.string.transition_exited_geofence))) {
            context.startService(i);
        } else if (action.equals(context.getString(R.string.transition_stopped_moving))) {
            context.stopService(i);
        }
    }
}