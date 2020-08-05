package com.example.weatherdetailer.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.weatherdetailer.MainActivity
import com.example.weatherdetailer.fragments.CurrentFragment
import com.example.weatherdetailer.fragments.DateFragment
import com.example.weatherdetailer.fragments.ReportFragment
import com.example.weatherdetailer.fragments.SettingFragment

class MyStateAdapter(activity: MainActivity):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        var fragment:Fragment
        var name:String
        when(position){
            0 ->{
                fragment= CurrentFragment()
                name="Current"
            }
            1-> {
                fragment= DateFragment()
                name="Date"
            }

            2->{
                fragment= ReportFragment()
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