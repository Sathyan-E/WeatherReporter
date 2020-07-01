package com.example.weatherdetailer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat

class FirstActivity : AppCompatActivity() {

    private var PERMISSION_ID=1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val userName=findViewById<EditText>(R.id.username)
        val nextButton=findViewById<Button>(R.id.button)

        nextButton.setOnClickListener {
            val usrName = userName.text.toString()

            val  intent = Intent(this@FirstActivity,MainActivity::class.java).apply {
                putExtra("name",usrName)
            }
            startActivity(intent)
        }
    }

    private fun checkPermission():Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID)
    }
    private  fun requestPermission(){
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)
        if(shouldProvideRationale){
            Log.i("TAG","Displaying permission rationale to provide additional contaxt.")

        }
    }
}