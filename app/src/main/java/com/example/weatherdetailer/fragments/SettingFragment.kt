package com.example.weatherdetailer.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.weatherdetailer.R
import com.example.weatherdetailer.localnotification.NotificationHelper


class SettingFragment : Fragment() {
    private lateinit var switch:Switch
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.settingsfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        switch=view.findViewById(R.id.unit_switch)
        val notificationHelper =NotificationHelper(context!!)



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationHelper.createNotificationChannel()
        }

/**
        val intent= Intent(context,MainActivity::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        createNotificationChannel()
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder= NotificationCompat.Builder(context!!,"10")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("This is the sample notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(alarmSound)


**/
        switch.setOnCheckedChangeListener { _, isChecked ->
            val m=if (isChecked) "farenheit" else "celsius"
            switch.text=m

           notificationHelper.creteNotification()
            update(m)
        }

    }

    private fun update(unit:String){
        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val editor= sharedPreferences!!.edit()
        editor.remove("unit")
        editor.putString("unit",unit)
        editor.apply()
    }


    private fun  createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name="Channel one"
            val descriptionText="Setting fragment notification"
            val impotance=NotificationManager.IMPORTANCE_DEFAULT
            val channel =  NotificationChannel("10",name,impotance).apply {
                description=descriptionText
            }

            val notificationManager:NotificationManager= context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }



}