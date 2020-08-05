package com.example.weatherdetailer.fragments

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



}