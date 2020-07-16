package com.example.weatherdetailer.pushnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.weatherdetailer.MainActivity
import com.example.weatherdetailer.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageReceiver: FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
      if (p0.data.size>0){
          showNotification(p0.data.get("title").toString(),p0.data.get("message").toString())
      }
     if (p0.notification!=null){
         showNotification(p0.notification!!.title.toString(), p0.notification!!.body.toString())
     }
    }

    public fun showNotification(title:String,message:String){
        val intent=Intent(this,MainActivity::class.java)

        val channel_id="weather_app_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)
        val uri:Uri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext,channel_id).
                setSmallIcon(R.drawable.ic_launcher_background)
            .setSound(uri)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){
            builder=builder.setContent(getCustomDesign(title,message))
        }
        else{
            builder=builder.setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_launcher_background)
        }
        val notificationManager:NotificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel=NotificationChannel(channel_id,"Weather Detailer",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(uri,null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0,builder.build())



    }
    private fun getCustomDesign(title: String,message: String):RemoteViews{
        val remoteViews=RemoteViews(applicationContext.packageName, R.layout.notification_layout)
        remoteViews.setTextViewText(R.id.notification_title,title)
        remoteViews.setTextViewText(R.id.notification_body,message)
        remoteViews.setImageViewResource(R.id.notification_icon,R.drawable.ic_launcher_background)
        return remoteViews
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

    }
}