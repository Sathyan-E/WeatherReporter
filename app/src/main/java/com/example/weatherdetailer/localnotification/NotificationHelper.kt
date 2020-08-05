package com.example.weatherdetailer.localnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherdetailer.MainActivity
import com.example.weatherdetailer.R

class NotificationHelper(val context: Context) {


     fun  creteNotification(){
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           createNotificationChannel()
       }

        val intent= Intent(context, MainActivity::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        createNotificationChannel()
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder= NotificationCompat.Builder(context!!,"10")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("This is the sample notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(alarmSound)

        NotificationManagerCompat.from(context).notify(0,builder.build())


    }

     fun  createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name="Channel one"
            val descriptionText="Setting fragment notification"
            val impotance= NotificationManager.IMPORTANCE_DEFAULT
            val channel =  NotificationChannel("10",name,impotance).apply {
                description=descriptionText
            }

            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}