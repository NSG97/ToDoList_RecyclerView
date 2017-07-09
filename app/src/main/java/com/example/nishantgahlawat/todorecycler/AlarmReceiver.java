package com.example.nishantgahlawat.todorecycler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getBundleExtra(IntentConstraints.NotificationBundleExtra);

        ToDoItem toDoItem = (ToDoItem) bundle.getSerializable(IntentConstraints.NotificationToDoExtra);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(toDoItem.getTitle())
                .setAutoCancel(true)
                .setContentText(toDoItem.getDescription());

        Intent notifIntent = new Intent(context,ToDoDetails.class);
        notifIntent.putExtra(IntentConstraints.DetailsToDoExtra,toDoItem);
        notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,(int)toDoItem.getId(),notifIntent
                ,PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int)toDoItem.getId(),builder.build());
    }
}
