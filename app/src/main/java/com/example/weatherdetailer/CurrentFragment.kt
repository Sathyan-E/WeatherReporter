package com.example.weatherdetailer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CurrentFragment : Fragment(){
    private var unitType=""
    lateinit var detailsTextView:TextView
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
  //  private lateinit var swipeRefreshLayout: SwipeRefreshLayout
   // private lateinit var ssImageView: ImageView
    private lateinit var shareButton:Button



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.currentfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Instantiating all the views,objects
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
        shareButton=view.findViewById(R.id.share_button)
        detailsTextView=view.findViewById(R.id.problem_details_textview)
       // ssImageView=view.findViewById(R.id.screeenshot_imageview)
        //making cardview invisible it will visible when we completes the fetching the data process
        cardView.visibility=View.INVISIBLE
       //fusedlocationclient instantiation
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity!!)


        //sharedpreference intantiating
        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!
        //attching onclick listner for share button to share the screenshot
        shareButton.isEnabled=false


        shareButton.setOnClickListener {
           Toast.makeText(activity,"Button CLicked",Toast.LENGTH_SHORT).show()
           //checking permission to w=read and write external storage
            ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),PackageManager.PERMISSION_GRANTED)
            //converting cardview into bitmap
            val bitmap=Bitmap.createBitmap(cardView.width,cardView.height,Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            cardView.draw(canvas)
            //getting directory file
            val  mainDirectoryname=File(context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ScreenShots")

                //if the directory is not there then create it.
            if (!mainDirectoryname.exists()){
                if (mainDirectoryname.mkdirs()){
                    Log.e("Create Directory", "Main Directory created: $mainDirectoryname")
                }
            }
            //creating file name
            val name:String="screenshot"+ Calendar.getInstance().time.toString()+".jpg"
            val dir=File(mainDirectoryname.absolutePath)
            if (!dir.exists()){
                dir.mkdirs()
            }

            val imagefile= File(mainDirectoryname.absolutePath,name)
            //outputstream intantiation
            val outPutStream= FileOutputStream(imagefile)
           //compressing the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outPutStream)

            Toast.makeText(activity,"File saved in directory",Toast.LENGTH_SHORT).show()
            outPutStream.flush()
            outPutStream.close()
            //sharing the stored screenshot
            shareScreenShot(imagefile)
            imagefile.delete()

        }



    }
    private fun shareScreenShot(imageFile:File){
       //fetching the image uri
        val fileuri:Uri=
            FileProvider.getUriForFile(context!!,"com.example.weatherdetailer.provider",imageFile)
        //intamtiation of intent
        val intent=Intent()
        //setting action fo intent
        intent.action = Intent.ACTION_SEND
        //setting type for the intent
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM,fileuri)
       //once all setup done,starting the activity to show options for sahring apps
        startActivity(Intent.createChooser(intent,"Share Screenshot"))

    }
    private fun findWeather(lat:String,lon:String){
       //fetching the unit preferenc form sharedpreference
        val unit:String? =getData(sharedPreferences,"unit")
        //intantiating retrofit
        val retrofit=Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(WeatherService::class.java)
        var u=""
        //setting unit parameter fot the api call based on the user preference
        if (unit=="celsius"){
            unitType="metric"
            u=" C"
        }else if(unit=="farenheit"){
            unitType="imperial"
            u=" F"
        }
        //api call
        val call = service.getCurrentWeatherData(lat,lon,"0458de72757b2f04185abd9a4b012488",unitType)
        //response
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>?) {
                if (response!=null){
                    if (response.code() == 200){
                        val weatherResponse=response.body()
                        //setting the fetched data into cardview
                        cardView.visibility=View.VISIBLE
                        progressBar.visibility=View.INVISIBLE
                        shareButton.isEnabled=true
                        presentCityName.text=weatherResponse.name
                        presentCityDescription.text=weatherResponse.weather[0].description
                        val temp:String=weatherResponse.main!!.temp.toString()+" "+u
                        presentCitytemp.text= temp
                        val fTemp:String=weatherResponse.main!!.feels_like.toString()+" "+u
                        presentCityFeelsLikeTemp.text=fTemp
                        val wind:String=weatherResponse.wind!!.speed.toString()+" m/h"
                        presentCityWind.text=wind
                        val humidity:String=weatherResponse.main!!.humudity.toString()+"%"
                        presentCityHumdity.text=humidity
                        val pressure:String=weatherResponse.main!!.pressure.toString()+" hPa"
                        presentCityPressure.text=pressure
                        val iconId:String=weatherResponse.weather[0].icon.toString()
                        Picasso.get().load("http://openweathermap.org/img/wn/$iconId@2x.png").into(presentClimateStateImage)
                        val utc:Long=weatherResponse.dt
                        val date= Date(utc*1000L)
                        val sdf= SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
                        val currrentday:String= sdf.format(date)
                        presentDate.text=currrentday

                    }
                }
                else{
                    Toast.makeText(activity,"response is null",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable) {
                detailsTextView.text=t.message
            }
        })
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }
    fun refresh(){
        val isConnected=isInternetConnected()
        if (isConnected){
            getLastLocation()
        }else{
            progressBar.visibility=View.INVISIBLE
            cardView.visibility=View.INVISIBLE
            Toast.makeText(activity,"Turn On Internet Connection!",Toast.LENGTH_SHORT).show()
        }

    }
    //method for getting data from shared preference
    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared.getString(string,null)

    }

    //method for checking the internet connection
    private fun isInternetConnected():Boolean{
        val cm= context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork=cm.activeNetworkInfo
        val isConnected:Boolean=activeNetwork?.isConnectedOrConnecting==true
        return isConnected
    }
    //method for checking permission for location
    private fun checkPermission():Boolean{
        if (
                ActivityCompat.checkSelfPermission(context!!,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context!!,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }
    //requesting permission for location
    private  fun requestPermission(){
        ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID
        )
    }
    //method for checking the location service enabled or not
    private  fun isLocationEnabled():Boolean{
        val locationManager=activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    //method for getting the current location from lastknown location
    private  fun getLastLocation():String{
        val name=""
        if(checkPermission()){
            if (isLocationEnabled()){
                fusedLocationClient .lastLocation.addOnCompleteListener{task ->
                    val location = task.result
                    if (location == null){
                        getNewLocation()
                    }else{
                        //finding weather from lat and lon
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

    //getting location by calling location request
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
        fusedLocationClient.requestLocationUpdates(
                locationRequest,locationCallback, Looper.myLooper()
        )
    }
        //location call back method for location request
    private  val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation =p0.lastLocation
           findWeather(lastLocation.latitude.toString(),lastLocation.longitude.toString())

        }
    }

    override fun onPause() {
        super.onPause()
        cardView.visibility=View.INVISIBLE
    }



}