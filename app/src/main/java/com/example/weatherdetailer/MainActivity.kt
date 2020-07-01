package com.example.weatherdetailer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar);
        val name:String? =intent.getStringExtra("name")

        val adapter=ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(CurrentFragment(),"Current")
        adapter.addFragment(DateFragment(),"Date")
        adapter.addFragment(ReportFragment(),"Report")
        adapter.addFragment(SettingFragment(),"Settings")
        viewPager.adapter=adapter
        tabs.setupWithViewPager(viewPager)

    }
}