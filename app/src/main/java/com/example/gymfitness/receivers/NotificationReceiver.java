package com.example.gymfitness.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import com.example.gymfitness.R;
import com.example.gymfitness.activities.LaunchActivity;
import com.example.gymfitness.data.DAO.NotificationDao;
import com.example.gymfitness.data.DAO.WorkoutLogDAO;
import com.example.gymfitness.data.database.FitnessDB;
import com.example.gymfitness.data.entities.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationReceiver extends BroadcastReceiver {
    private WorkoutLogDAO workoutLogDAO;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private NotificationDao notificationDao;
    @Override
    public void onReceive(Context context, Intent intent) {
        workoutLogDAO = FitnessDB.getInstance(context).workoutLogDAO();
        notificationDao = FitnessDB.getInstance(context).notificationDao();
        String action = intent.getAction();
        if ("com.example.gymfitness.ACTION_MORNING_REMINDER".equals(action)) {
            showMorningReminder(context);
        } else if ("com.example.gymfitness.ACTION_KCAL_REMINDER".equals(action)) {
            showKcalReminder(context);
        }
    }

    private void showMorningReminder(Context context) {

        sendNotification(context, "Workout Reminder", "It's time for your morning workout! Let's get started!", 1);
        if (notificationDao != null) {
            saveNotificationToDatabase("Morning Reminder", "It's time for your morning workout! Let's get started!", 1);
        } else {
            Log.e("NotificationReceiver", "notificationDao is null!");
        }
    }



    private void showKcalReminder(Context context) {
        executorService.execute(() -> {
            int totalKcal = getTotalKcalForToday(context);
            String content;
            if (totalKcal > 0) {
                content = "Total kcal today: " + totalKcal;
                sendNotification(context, "Workout Results", content, 2);
                if (notificationDao != null) {
                    saveNotificationToDatabase("Kcal Reminder", content, 2);
                } else {
                    Log.e("NotificationReceiver", "notificationDao is null!");
                }
            } else {
                content = "You haven't recorded any kcal today!";
                sendNotification(context, "Workout Reminder", content, 3);
                if (notificationDao != null) {
                    saveNotificationToDatabase("Workout Reminder", content, 3);
                } else {
                    Log.e("NotificationReceiver", "notificationDao is null!");
                }
            }
        });
    }

    private void saveNotificationToDatabase(String name, String content, int type) {
        Notification notification = new Notification(name, type, content, new Date());

        executorService.execute(() -> {
            if (notificationDao != null) {
                notificationDao.insertNotification(notification);
                Log.d("luu thong bao", "luu thanh cong roi: " + notification.toString());
            } else {
                Log.e("luu thong bao", "ngu is null!");
            }
        });
    }



    private int getTotalKcalForToday(Context context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        LocalDate currentDate = LocalDate.now();
        String dateString = currentDate.getYear() + "-" + String.format("%02d", currentDate.getMonthValue()) + "-" + String.format("%02d", currentDate.getDayOfMonth());
        try {
            Date selectedDate = dateFormat.parse(dateString);
            Integer totalKcal = workoutLogDAO.getTotalKcalByDate(selectedDate);
            return totalKcal != null ? totalKcal : 0;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private void sendNotification(Context context, String title, String content, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "daily_reminder_channel";

        // Tạo NotificationChannel nếu cần thiết
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
            if (channel == null) {
                channel = new NotificationChannel(channelId, "Daily Reminder", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Intent để mở ứng dụng khi người dùng nhấn vào thông báo
        Intent notificationIntent = new Intent(context, LaunchActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Xây dựng thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_off)
                .setContentTitle(title)
                .setContentText(content)
                .setColor(ContextCompat.getColor(context, R.color.purple))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Hiển thị thông báo
        notificationManager.notify(notificationId, builder.build());
    }


}