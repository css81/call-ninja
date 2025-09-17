package com.sschoi.callninja.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.sschoi.callninja.service.MyCallScreeningService;

/**
 * BroadcastReceiver that starts the Call Screening Service on device boot.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context, "Call Ninja started on boot", Toast.LENGTH_SHORT).show();

            // Start the Call Screening Service
            Intent serviceIntent = new Intent(context, MyCallScreeningService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
