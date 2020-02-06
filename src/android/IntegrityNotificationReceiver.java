package edu.berkeley.eecs.emission.cordova.integrityDetect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import edu.berkeley.eecs.emission.R;
import edu.berkeley.eecs.emission.cordova.unifiedlogger.Log;


public class IntegrityNotificationReceiver extends BroadcastReceiver {

    private static String TAG =  IntegrityNotificationReceiver.class.getSimpleName();
    private IntegrityDetector integrityDetector;
    private boolean notBeenCreated = true;

    public IntegrityNotificationReceiver() {
        // The automatically created receiver needs a default constructor
        android.util.Log.i(TAG, "noarg constructor called");
    }

    public IntegrityNotificationReceiver(Context context) {
        android.util.Log.i(TAG, "constructor called with arg " + context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(context, TAG, "onReceive called");
        String action = intent.getAction();
        Intent i = new Intent(context, IntegrityDetector.class);
        if (action.equals(context.getString(R.string.transition_exited_geofence))) {
            Log.d(context, TAG, "transition_exited_geofence received");
            context.startService(i);
        } else if (action.equals(context.getString(R.string.transition_stopped_moving))) {
            Log.d(context, TAG, "transition_stopped_moving received");
            context.stopService(i);
        }
    }
}