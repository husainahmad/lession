package com.example.trainingtwitter.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class TwitterAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ALARM");
		wl.acquire();
		Log.i("ALARM", "Alarm !!!!!!!!!!");
		//send broadcast to service 
		context.sendBroadcast(new Intent("TwitList_Alarm").putExtra("status", true));
		wl.release();
	}
	
	public void setAlarm(Context context) {
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, TwitterAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
	}
	
	public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, TwitterAlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
	
	

}
