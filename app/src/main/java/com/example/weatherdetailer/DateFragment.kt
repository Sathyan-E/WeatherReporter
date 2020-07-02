package com.example.weatherdetailer

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.util.*

class DateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.datefragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView=view.findViewById<TextView>(R.id.usrnmeDate)
        val calendarView=view.findViewById<CalendarView>(R.id.calenderView)
        calendarView?.setOnDateChangeListener{view,year,month,dayOfMonth ->
            val msg:String= dayOfMonth.toString() +"/"+(month+1)+"/"+year
            Toast.makeText(activity,""+msg,Toast.LENGTH_SHORT).show()
        }


        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val user: String? = sharedPreferences?.getString("name",null)
        nameTextView.text=user

    }


}