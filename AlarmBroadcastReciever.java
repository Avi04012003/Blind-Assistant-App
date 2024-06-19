package com.example.ech.alarm;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmBroadcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String lang = intent.getExtras().getString("Language");
        Intent mIntent = new Intent(context, Alarm_Notification.class);
        mIntent.putExtra("Language",lang);
        mIntent.setAction(Intent.ACTION_MAIN);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
























//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"ech_alarm");
//        mBuilder.setSmallIcon(R.mipmap.alarm);
//        mBuilder.setContentTitle("Alarm Ringing");
//        mBuilder.setContentIntent(pendingIntent);
//        mBuilder.setContentText("You have set this Alarm");
//        mBuilder.setAutoCancel(true);
//        mBuilder.setColor(context.getColor(R.color.design_default_color_secondary_variant));
//        long vibrate[]={100,50,100,50};
//        mBuilder.setVibrate(vibrate);
//        mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(123,mBuilder.build());

//        Intent alarmIntent = new Intent(context,Alarm_Notification.class);
//        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(alarmIntent);

//        Intent intent1 = new Intent("android.intent.category.LAUNCHER");
//        intent1.setClassName("com.example.ech", "com.example.ech.Alarm_Notification");
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);
    }
}