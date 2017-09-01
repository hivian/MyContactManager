package com.example.hivian.my_contact_manager;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hivian on 9/1/17.
 */

public class SmsNotificationService extends IntentService {
    public SmsNotificationService() {
        super("SmsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String contentTitle = (String) intent.getExtras().get("ContentTitle");
        String contentMessage = (String) intent.getExtras().get("ContentMessage");

        Log.d("HERE", "HERE");

        DBHandler db = new DBHandler(this);
        Contact contact = db.getContactByPhone(contentTitle);

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification n  = new Notification.Builder(this)
                .setContentTitle(contact != null ? contact.getName() : contentTitle)
                .setContentText(contentMessage)
                .setSmallIcon(R.drawable.ic_message_white_24dp)
                .setLargeIcon(contact.getImage() != null ? DbBitmapUtility.getImage(contact.getImage()) : null)
                .setTicker("toto")
                .setContentIntent(pIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Destroyed", "D");
    }
}
