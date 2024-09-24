package com.example.gymfitness.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.gymfitness.receivers.NotificationReceiver;

import java.util.Calendar;

public class ScheduleNotification {

    public static void setMorningReminder(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent morningIntent = new Intent(context, NotificationReceiver.class);
        morningIntent.setAction("com.example.gymfitness.ACTION_MORNING_REMINDER");
        PendingIntent morningPendingIntent = PendingIntent.getBroadcast(context, 0, morningIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //7h moi sang thong bao
        Calendar morningCalendar = Calendar.getInstance();
        morningCalendar.set(Calendar.HOUR_OF_DAY, 15);
        morningCalendar.set(Calendar.MINUTE, 36);
        morningCalendar.set(Calendar.SECOND, 0);


        if (morningCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            morningCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, morningCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, morningPendingIntent);
        }


    }
    public static void setDailyReminders(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent kcalIntent = new Intent(context, NotificationReceiver.class);
        kcalIntent.setAction("com.example.gymfitness.ACTION_KCAL_REMINDER");
        PendingIntent kcalPendingIntent = PendingIntent.getBroadcast(context, 2, kcalIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar kcalCalendar = Calendar.getInstance();
        kcalCalendar.set(Calendar.HOUR_OF_DAY, 15);
        kcalCalendar.set(Calendar.MINUTE, 35);
        kcalCalendar.set(Calendar.SECOND, 0);


        if (kcalCalendar.getTimeInMillis() < System.currentTimeMillis()) {
            kcalCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, kcalCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, kcalPendingIntent);
        }
    }
}