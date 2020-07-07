package com.example.weatherdetailer

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var adapter: MyStateAdapter
    lateinit var viewPager2: ViewPager2
    lateinit var menu:Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val tablayout:TabLayout =findViewById(R.id.tabs)
        adapter= MyStateAdapter(this)
        viewPager2=findViewById(R.id.viewpager2)
        viewPager2.adapter=adapter

        save("unit","celsius")


        val num= arrayOf("Current","Date","Report","Preference")
        TabLayoutMediator(tablayout,viewPager2){tab, position ->
            tab.text=num[position]
        }.attach()

    }


    override fun onDestroy() {
        super.onDestroy()
        val  sharedPreferences=getSharedPreferences("weather", Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()

    }
    private  fun save(key:String,value:String){
        val  sharedPreferences=getSharedPreferences("weather",Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator:MenuInflater=menuInflater
       // inflator.inflate(R.menu.main_options,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

}