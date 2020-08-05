package com.example.weatherdetailer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

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

        val intent= Intent(context,MainActivity::class.java).apply {
            flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
        createNotificationChannel()
        var builder= NotificationCompat.Builder(context!!,"10")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Notification")
            .setContentText("This is the sample notification")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)



        switch.setOnCheckedChangeListener { _, isChecked ->
            val m=if (isChecked) "farenheit" else "celsius"
            switch.text=m

            with(NotificationManagerCompat.from(context!!)){
                notify(1,builder.build())
            }

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