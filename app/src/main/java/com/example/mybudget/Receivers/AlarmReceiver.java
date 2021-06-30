package com.example.mybudget.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.mybudget.Database.DatabaseViewModel;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent intent2 = new Intent(context, AlarmReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent2, 0);
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, 5000, 6000, alarmIntent);
            }
        }
        else {
            context.sendBroadcast(new Intent("MONTH_PASSED"));
        }
    }
}
