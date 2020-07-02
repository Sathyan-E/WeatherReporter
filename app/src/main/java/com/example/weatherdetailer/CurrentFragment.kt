package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class CurrentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.currentfragmentlayout,container,false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
        val userTextView=view.findViewById<TextView>(R.id.userName)
        val cityTextView =view.findViewById<TextView>(R.id.cityname)
        val tempTextView = view.findViewById<TextView>(R.id.temp)
        val detailsTextView = view.findViewById<TextView>(R.id.weather)

        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val user: String? = sharedPreferences?.getString("name",null)
        val city: String? = sharedPreferences?.getString("city",null)
        userTextView.text=user
        cityTextView.text=city
        
        //Toast.makeText(activity,""+city,Toast.LENGTH_SHORT).show()

    }
}