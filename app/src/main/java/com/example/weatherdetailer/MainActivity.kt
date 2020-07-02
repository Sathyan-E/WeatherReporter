package com.example.weatherdetailer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

      //  setSupportActionBar(toolbar);

        val viewPager: ViewPager= findViewById(R.id.viewPager)
        val tablayout:TabLayout =findViewById(R.id.tabs)

        val adapter=ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(CurrentFragment(),"Current")
        adapter.addFragment(DateFragment(),"Date")
        adapter.addFragment(ReportFragment(),"Report")
        adapter.addFragment(SettingFragment(),"Settings")
        viewPager.adapter=adapter
        tablayout.setupWithViewPager(viewPager)

        val name:String? =intent.getStringExtra("name")
        val city:String? =intent.getStringExtra("city")



    }
}