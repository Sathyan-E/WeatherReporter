package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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


        switch.setOnCheckedChangeListener { _, isChecked ->
            val m=if (isChecked) "farenheit" else "celsius"
            switch.text=m
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