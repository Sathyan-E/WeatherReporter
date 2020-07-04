package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

class SettingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.settingsfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView=view.findViewById<TextView>(R.id.usrnmeSettings)
        val changeButton = view.findViewById<Button>(R.id.changeButton)

        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)

        val user: String? = sharedPreferences?.getString("name",null)

        nameTextView.text=user
        var unit: String? = sharedPreferences?.getString("unit",null)
        changeButton.text=unit
        changeButton.setOnClickListener{
            unit= sharedPreferences?.getString("unit",null)
            if (unit=="celsius"){
                var editor= sharedPreferences!!.edit()
                editor.remove("unit")
                editor.putString("unit","farenheit")
                editor.commit()
                changeButton.text="Farenheit"

            }
            else if (unit=="farenheit"){
                var editor=sharedPreferences!!.edit()
                editor.remove("unit")
                editor.putString("unit","celsius")
                editor.commit()
                changeButton.text="Celsius"
            }

        }


    }


}