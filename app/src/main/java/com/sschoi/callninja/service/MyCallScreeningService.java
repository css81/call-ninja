package com.sschoi.callninja.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.CallScreeningService;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.sschoi.callninja.R;
import com.sschoi.callninja.data.db.BlockLogDBHelper;

/**
 * Call Screening Service to block calls not in the contact list or favorites.
 */
@SuppressLint("NewApi")
public class MyCallScreeningService extends CallScreeningService {

    private static final String CHANNEL_ID = "call_ninja_channel";

    private BlockLogDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new BlockLogDBHelper(this);
        createNotificationChannel();
        startForegroundServiceWithNotification();
    }

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String incomingNumber = callDetails.getHandle().getSchemeSpecificPart();

        if (shouldBlockCall(incomingNumber)) {
            // Block the call
            CallResponse response = new CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build();

            dbHelper.insertLog(incomingNumber);
            Toast.makeText(this, "Blocked call from: " + incomingNumber, Toast.LENGTH_SHORT).show();

            respondToCall(callDetails, response);
        }
    }

    private boolean shouldBlockCall(String phoneNumber) {
        // TODO: Implement logic to allow only contacts/favorites
        // For now, block all calls
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Call Ninja Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Foreground service for Call Ninja");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startForegroundServiceWithNotification() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Call Ninja Running")
                .setContentText("Blocking calls not in contacts/favorites")
                .setSmallIcon(R.drawable.ic_call_block)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();

        startForeground(1, notification);
    }
}
