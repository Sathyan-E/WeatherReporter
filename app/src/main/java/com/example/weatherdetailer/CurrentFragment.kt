package com.example.weatherdetailer

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.opengl.Visibility
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import com.google.android.gms.location.*
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class CurrentFragment : Fragment() {
    var unitType=""
    lateinit var detailsTextView:TextView
    lateinit var cityTextView:TextView
    lateinit var sharedPreferences:SharedPreferences
    private val PERMISSION_ID=1000
    lateinit var fusedLocationClient : FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private lateinit var presentCityName:TextView
    private lateinit var presentCityDescription:TextView
    private lateinit var presentCitytemp:TextView
    private lateinit var presentCityFeelsLikeTemp:TextView
    private lateinit var presentCityWind:TextView
    private lateinit var presentCityHumdity:TextView
    private lateinit var presentCityPressure:TextView
    private lateinit var presentDate:TextView
    private lateinit var presentClimateStateImage:ImageView
    private lateinit var cardView:CardView
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.currentfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
       // val userTextView=view.findViewById<TextView>(R.id.userName)
       //cityTextView =view.findViewById<TextView>(R.id.cityname)

        //detailsTextView = view.findViewById(R.id.weather)
        presentCityName=view.findViewById(R.id.current_city_name)
        presentCityDescription=view.findViewById(R.id.current_climat_description)
        presentClimateStateImage=view.findViewById(R.id.current_climate_image)
        presentCitytemp=view.findViewById(R.id.current_city_temp)
        presentCityFeelsLikeTemp=view.findViewById(R.id.current_city_feelslike_temp)
        presentCityWind=view.findViewById(R.id.current_city_wind)
        presentCityHumdity=view.findViewById(R.id.current_city_humidity)
        presentCityPressure=view.findViewById(R.id.current_city_pressure)
        presentDate=view.findViewById(R.id.current_date)
        cardView=view.findViewById(R.id.cardview)
        progressBar=view.findViewById(R.id.current_progress_bar)


        cardView.visibility=View.INVISIBLE
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity!!)


        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!
        val user: String? = sharedPreferences?.getString("name",null)
       // userTextView.text=user


    }
    private fun findWeather(lat:String,lon:String){
        val unit:String? =getData(sharedPreferences,"unit")

        val retrofit=Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(WeatherService::class.java)
        var u:String=""
        if (unit=="celsius"){
            unitType="metric"
            u=" C"
        }else if(unit=="farenheit"){
            unitType="imperial"
            u=" F"
        }

        val call = service.getCurrentWeatherData(lat,lon,"0458de72757b2f04185abd9a4b012488",unitType)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>?) {
                if (response!=null){
                    if (response.code() == 200){
                        val weatherResponse=response.body()
                        cardView.visibility=View.VISIBLE
                        val stringBuilder="Country :"+weatherResponse.sys!!.country+"\n"+
                                "Temperature: "+weatherResponse.main!!.temp+"\n"+
                                "Temperature(Min): "+weatherResponse.main!!.temp_min+unit+"\n"+
                                "Temperature(Max): "+weatherResponse.main!!.temp_max+unit+"\n"+
                                weatherResponse.weather!![0].description+"\n"
                        "Humidity: "+weatherResponse.main!!.humudity+"\n"+
                                "Pressure: "+weatherResponse.main!!.pressure


                        presentCityName.text=weatherResponse.name
                        presentCityDescription.text=weatherResponse.weather[0].description
                        var temp:String=weatherResponse.main!!.temp.toString()+" "+u
                        presentCitytemp.text= temp
                        var fTemp:String=weatherResponse.main!!.feels_like.toString()+" "+u
                        presentCityFeelsLikeTemp.text=fTemp
                        var wind:String=weatherResponse.wind!!.speed.toString()+" m/h"
                        presentCityWind.text=wind
                        var humidity:String=weatherResponse.main!!.humudity.toString()+"%"
                        presentCityHumdity.text=humidity
                        var pressure:String=weatherResponse.main!!.pressure.toString()+" hPa"
                        presentCityPressure.text=pressure
                        var iconId:String=weatherResponse.weather[0].icon.toString()
                        Picasso.get().load("http://openweathermap.org/img/wn/$iconId@2x.png").into(presentClimateStateImage)
                       // detailsTextView!!.text=stringBuilder
                        var utc:Long=weatherResponse.dt
                        var date:Date= Date(utc*1000L)
                        var sdf:SimpleDateFormat= SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
                        var cal:Calendar= Calendar.getInstance()
                        var tz:TimeZone=cal.timeZone
                        var currrentday:String= sdf.format(date)
                        presentDate.text=currrentday

                    }
                }
                else{
                    Toast.makeText(activity,"response is null",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable) {
                detailsTextView!!.text=t.message
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val isConnected=isInternetConnected()
        if (isConnected){
            getLastLocation()
        }else{
            progressBar.visibility=View.INVISIBLE
            //cardView.visibility=View.INVISIBLE
            Toast.makeText(activity,"Turn On Internet Connection!",Toast.LENGTH_SHORT).show()
        }

    }
    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared?.getString(string,null)

    }
    private fun isInternetConnected():Boolean{
        val cm= context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork:NetworkInfo?=cm.activeNetworkInfo
        val isConnected:Boolean=activeNetwork?.isConnectedOrConnecting==true
        return isConnected
    }
    private fun checkPermission():Boolean{
        if (
                ActivityCompat.checkSelfPermission(context!!,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context!!,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private  fun requestPermission(){
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }

    private  fun isLocationEnabled():Boolean{
        var locationManager=activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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
                        findWeather(location.latitude.toString(),location.longitude.toString())
                    }
                }

            }else{
                Toast.makeText(activity,"Please Enable Your Location Service!",Toast.LENGTH_SHORT).show()
            }

        }else{
            requestPermission()
        }
        return name
    }
    private  fun getNewLocation(){
        locationRequest= LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2
        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity,"Problem in getting location permission",Toast.LENGTH_SHORT).show()
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
            save("lat",lastLocation.longitude.toString())

            getCityName(lastLocation.latitude,lastLocation.longitude)
            findWeather(lastLocation.latitude.toString(),lastLocation.longitude.toString())

        }
    }
    private fun getCityName(lat:Double,long: Double) {
        var cityName=""
        var geoCoder= Geocoder(activity, Locale.getDefault())
        var addr=geoCoder.getFromLocation(lat,long,1)
        cityName=addr.get(0).locality
        save("city",cityName)
       // cityTextView.text=cityName
    }
    private  fun save(key:String,value:String){
        val  sharedPreferences=activity!!.getSharedPreferences("weather",Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }

    override fun onPause() {
        super.onPause()
        cardView.visibility=View.INVISIBLE
    }








}