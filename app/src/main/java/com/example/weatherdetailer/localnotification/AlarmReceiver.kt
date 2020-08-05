package com.example.weatherdetailer.localnotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver:BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val nHelper=NotificationHelper(p0!!)
        nHelper.creteNotification()

    }
}