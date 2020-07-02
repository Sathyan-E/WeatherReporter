package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ReportFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.reportfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView=view.findViewById<TextView>(R.id.usrnmeReport)
        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val user: String? = sharedPreferences?.getString("name",null)
        nameTextView.text=user
    }
}