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

    private var PERMISSION_ID=1000
    lateinit var fusedLocationClient : FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    var city:String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        val userName=findViewById<EditText>(R.id.username)
        val nextButton=findViewById<Button>(R.id.button)
        val locateButton =findViewById<Button>(R.id.locationButton)

        nextButton.isEnabled=false
        nextButton.isClickable=false
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        locateButton.setOnClickListener {
            city=getLastLocation()
            //Toast.makeText(this,""+city,Toast.LENGTH_SHORT).show()
            nextButton.isEnabled=true
            nextButton.isClickable=true
        }


        nextButton.setOnClickListener {
            val usrName = userName.text.toString()

            val  intent = Intent(this@FirstActivity,MainActivity::class.java).apply {
                save("name",usrName)
                save("unit","celsius")

            }
            startActivity(intent)
        }
    }

    private  fun getLastLocation():String{
        var name:String=""
        if(checkPermission()){
            if (isLocationEnabled()){
                fusedLocationClient .lastLocation.addOnCompleteListener{task ->
                    var location = task.result
                    if (location == null){
                        getNewLocation()
                    }else{
                        save("lat",location.latitude.toString())
                        save("lon",location.longitude.toString())
                        getCityName(location.latitude,location.longitude)
                    }
                }

            }else{
                Toast.makeText(this,"Please Enable Your Location Service!",Toast.LENGTH_SHORT).show()
            }

        }else{
            requestPermission()
        }
        return name
    }

    private fun checkPermission():Boolean{
        if (
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ){
            return true
        }
        return false
    }

    private  fun requestPermission(){
        ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    private  fun isLocationEnabled():Boolean{
        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID){
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Debug","You have the permission")
            }
        }
    }

    private  fun getNewLocation(){
        locationRequest= LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient!!.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private  val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation =p0.lastLocation
            save("lat",lastLocation.latitude.toString())
            save("lon",lastLocation.longitude.toString())
            getCityName(lastLocation.latitude,lastLocation.longitude)

        }
    }

    private fun getCityName(lat:Double,long: Double) {
        var cityName=""
        var geoCoder=Geocoder(this, Locale.getDefault())
        var addr=geoCoder.getFromLocation(lat,long,1)
        cityName=addr.get(0).locality
        Toast.makeText(applicationContext,"your city :"+cityName,Toast.LENGTH_SHORT).show()
        save("city",cityName)



    }
    private  fun save(key:String,value:String){
        val  sharedPreferences=getSharedPreferences("weather",Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.commit()

    }
}