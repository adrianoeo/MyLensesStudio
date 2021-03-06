package com.aeo.mylenses.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylenses.R;
import com.aeo.mylenses.dao.AlarmDAO;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmDAO.getInstance(context).cancelAlarm();

        // Clean Notification in action bar
        NotificationManager manager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(R.string.app_name);

    }

}
