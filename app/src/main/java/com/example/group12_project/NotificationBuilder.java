package com.example.group12_project;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.group12_project.friendlist.FriendListActivity;

public class NotificationBuilder {
    private Context context;
    private String title, content;
    private String id;
    NotificationManager nManager;

    public NotificationBuilder(Context context, String textTitle, String textContent, String id) {
        this.id = this.context.getString(R.string.channel_id); // default_channel_id
        this.title = this.context.getString(R.string.channel_name); // Default Channel
        this.context = context;
        this.title = textTitle;
        this.content = textContent;
        this.id = id;
    }

    public void createNotification() {
        final int NOTIFY_ID = 0; // ID of notification
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (nManager == null) {
            nManager = (NotificationManager)this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            android.app.NotificationChannel mChannel = nManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(this.id, this.title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                nManager.createNotificationChannel(mChannel);
            }

            builder = new NotificationCompat.Builder(this.context, id);
            intent = new Intent(this.context, FriendListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
            builder.setContentTitle(this.title)                            // required
                    .setSmallIcon(R.drawable.notification)   // required
                    .setContentText(this.content) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(this.title);
        }

        else {
            builder = new NotificationCompat.Builder(this.context, this.id);
            intent = new Intent(this.context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this.context, 0, intent, 0);
            builder.setContentTitle(this.title)                            // required
                    .setSmallIcon(R.drawable.notification)   // required
                    .setContentText(this.content) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(this.title)
                    .setPriority(Notification.PRIORITY_HIGH);
        }

        Notification notification = builder.build();
        nManager.notify(NOTIFY_ID, notification);
    }
}