package com.example.henrikfogbunzel.appproject.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.henrikfogbunzel.appproject.ProfileActivity;
import com.example.henrikfogbunzel.appproject.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {

    //private GeofenceTransitionsIntentService mGeofenceTransitionsIntentService;
    //private GeofencingEvent mGeofencingEvent;

    protected static final String TAG = GeofenceTransitionsIntentService.class.getSimpleName();

    public GeofenceTransitionsIntentService() {
        super("No Name");
    }

    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    //onCreate ikke med fra dokumentationen, men https://stackoverflow.com/questions/30921004/my-geofencetransitionsintentservice-does-not-send-any-notifications-when-a-geofe
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "init GeofenceTransitionsIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {

            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
              //      geofencingEvent.getErrorCode());
            //Log.e(TAG, errorMessage);
            Log.e(TAG, "geofencing event error");
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geofenceTransitionDetails = getGeofenceTransitionDetails(
                    this,
                    geofenceTransition,
                    triggeringGeofences
            );
            // Send notification and log the transition details.
            sendNotification(geofenceTransitionDetails);
            Log.i(TAG, geofenceTransitionDetails);
            
        } else {
            // Log the error.
            Log.e(TAG, "geofence transistion invalid type");
        }
    }


    private String getGeofenceTransitionDetails(GeofenceTransitionsIntentService geofenceTransitionsIntentService, int geofenceTransition, List<Geofence> triggeringGeofences) {

        String geofenceTransitionString = getTransitionString(geofenceTransition);

        ArrayList<String> triggeringGeofencesIdsList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences) {
            triggeringGeofencesIdsList.add(geofence.getRequestId());
        }
        String triggeringGeofencesIdsString = TextUtils.join(", ",  triggeringGeofencesIdsList);

        return geofenceTransitionString + ": " + triggeringGeofencesIdsString;

    }

    private void sendNotification(String geofenceTransitionDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), ProfileActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(ProfileActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(geofenceTransitionDetails)
                .setContentText("Click notification to return to app")
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private String getTransitionString(int geofenceTransition) {
        switch (geofenceTransition){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return "transition - enter";
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return "transition - exit";
            default:
                return "unknown transition";
        }
    }
}
