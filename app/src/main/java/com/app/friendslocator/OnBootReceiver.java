package com.app.friendslocator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
           ContextCompat.startForegroundService(context,new Intent(context, MyService.class));
       //    context.startService(new Intent(context, MyService.class));
       }
    }
}
