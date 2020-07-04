package com.example.weatherdetailer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyStateAdapter(activity: MainActivity):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment
        var name:String
        when(position){
            0 ->{
                fragment=CurrentFragment()
                name="Current"
            }
            1-> {
                fragment= DateFragment()
                name="Date"
            }

            2->{
                fragment=ReportFragment()
                name="Report"
            }
            3-> {
                fragment= SettingFragment()
                name="Preference"
            }
            else ->{
                fragment= CurrentFragment()
                name="Current"
            }
        }
        fragment.arguments= Bundle().apply {
            putString("obj",name)
        }

        return fragment
    }
}