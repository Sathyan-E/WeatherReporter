package com.example.weatherdetailer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*

class FirstActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val userName=findViewById<EditText>(R.id.username)
        val nextButton=findViewById<Button>(R.id.button)

        nextButton.setOnClickListener {
            val usrName = userName.text.toString()
            if (userName.length()!=0){
                val  intent = Intent(this@FirstActivity,MainActivity::class.java).apply {
                   // city=getLastLocation()
                    save("name",usrName)
                    save("unit","celsius")
                }
                startActivity(intent)
            }else{
                Toast.makeText(this,"Enter Your Name",Toast.LENGTH_SHORT).show()
            }

        }
    }
    private  fun save(key:String,value:String){
        val  sharedPreferences=getSharedPreferences("weather",Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }
}