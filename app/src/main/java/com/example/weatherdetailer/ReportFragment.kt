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
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.adapter.ReportViewAdapter
import com.example.weatherdetailer.network.MonthlyResponse
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class ReportFragment : Fragment() {
    private var unitType=""
    private var lastUsedUnit:String=""
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var reportTextView:TextView
    private var responseList:MutableList<WeatherResponse> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapter: ReportViewAdapter? =null
    private lateinit var progrssBar: ProgressBar
   private lateinit var screenshotView:LinearLayout
    private lateinit var shareButton: Button
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var cUnit=""
    private lateinit var autocompleteFragment:AutocompleteSupportFragment
    private  var selectedLat:String=""
    private  var selectedLon:String=""
    //val placesApi=PlacesAPI.Builder().apikey("AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k").build(activity)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        val view= inflater.inflate(R.layout.reportfragmentlayout,container,false)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val nameTextView=view.findViewById<TextView>(R.id.usrnmeReport)
        val cityTextView=view.findViewById<TextView>(R.id.city)
        recyclerView=view.findViewById(R.id.recyclerview)
        screenshotView=view.findViewById(R.id.report_sharing_layout)
        shareButton=view.findViewById(R.id.report_sharing_button)

        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerAdapter= ReportViewAdapter(responseList,cUnit)
        recyclerView.adapter=recyclerAdapter
        recyclerView.visibility=View.INVISIBLE

        reportTextView = view.findViewById(R.id.report)

        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity!!)


        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!


        val city: String? = getData(sharedPreferences,"city")

        Places.initialize(context!!,"AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k")
        var placesClient= Places.createClient(context!!)
        autocompleteFragment=childFragmentManager.findFragmentById(R.id.report_autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME))

        //nameTextView.text=user
        cityTextView.text=city

        progrssBar=view.findViewById(R.id.report_progressbar)

        shareButton.setOnClickListener {
            Toast.makeText(activity,"sharing the screenshot",Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PackageManager.PERMISSION_GRANTED)

            val bitmap= Bitmap.createBitmap(screenshotView.width,screenshotView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            screenshotView.draw(canvas)
            //ssImageView.setImageBitmap(bitmap)

            val  mainDirectoryname =
                File(context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ScreenShots")
            if (!mainDirectoryname.exists()){
                if (mainDirectoryname.mkdirs()){
                    Log.e("Create Directory", "Main Directory created: $mainDirectoryname")
                }
            }

            val name:String="screenshot"+ Calendar.getInstance().time.toString()+".jpg"
            val dir  = File(mainDirectoryname.absolutePath)
            if (!dir.exists()){
                dir.mkdirs()
            }
            val imagefile = File(mainDirectoryname.absolutePath,name)
            val outPutStream = FileOutputStream(imagefile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outPutStream)

            Toast.makeText(activity,"FIle saved in directory",Toast.LENGTH_SHORT).show()
            outPutStream.flush()
            outPutStream.close()
            shareScreenShot(imagefile)
            imagefile.delete()

        }

        autocompleteFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(activity,"LATLNG is"+p0.latLng,Toast.LENGTH_SHORT).show()
                selectedLat=p0.latLng!!.latitude.toString()
                selectedLon=p0.latLng!!.longitude.toString()
                loadData(selectedLat,selectedLon)
            }

            override fun onError(p0: Status) {

            }

        })

    }

    private fun shareScreenShot(imageFile:File){
        val fileuri: Uri = FileProvider.getUriForFile(context!!,"com.example.weatherdetailer.provider",imageFile)

        val intent= Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM,fileuri)
        startActivity(Intent.createChooser(intent,"Share Screenshot"))

    }

    private fun loadData(lat:String,lon:String){

        findUnit()

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)

        val reportCall = service.getForecast(lat,lon,"0458de72757b2f04185abd9a4b012488",unitType)

        reportCall.enqueue(object : Callback<MonthlyResponse> {
            override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {

                if (response!=null){
                    if (response.code()==200){
                        val weatherResponse=response.body()
                        responseList.clear()
                        val list=weatherResponse.list
                        responseList.addAll(list)
                        progrssBar.visibility=View.INVISIBLE
                        recyclerView.visibility=View.VISIBLE
                        recyclerAdapter!!.notifyDataSetChanged()

                    }else{
                        Toast.makeText(activity,"Error in Response",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                reportTextView.text=t.message
            }

        })

    }

    override fun onResume() {
        super.onResume()
        val unit =getData(sharedPreferences,"unit")
        val isConnected=isInternetConnected()
        if (isConnected){
            if(lastUsedUnit!=unit){
               if (selectedLat!=""){
                   loadData(selectedLat,selectedLon)
               }else{
                   Toast.makeText(activity,"Select place for report",Toast.LENGTH_SHORT).show()
               }

            }
            else{
                recyclerView.visibility=View.VISIBLE
            }

        }
        else{
            progrssBar.visibility=View.INVISIBLE
            Toast.makeText(activity,"No Internet Connection!",Toast.LENGTH_SHORT).show()
        }


    }
    private fun findUnit(){
        val unit =getData(sharedPreferences,"unit")

        if(unit=="celsius"){
            unitType="metric"
            cUnit="C"
            lastUsedUnit="celsius"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
            cUnit="F"
            lastUsedUnit="farenheit"
        }

    }
    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared.getString(string,null)
    }
    private fun isInternetConnected():Boolean{
        val cm= context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo?=cm.activeNetworkInfo
        val isConnected:Boolean=activeNetwork?.isConnectedOrConnecting==true
        return isConnected
    }

    override fun onPause() {
            super.onPause()
            recyclerView.visibility=View.INVISIBLE
    }
   /**
    private  fun getLastLocation():String{
        val name=""
        if(checkPermission()){
            if (isLocationEnabled()){
                fusedLocationClient .lastLocation.addOnCompleteListener{task ->
                    val location = task.result
                    if (location == null){
                        getNewLocation()
                    }else{
                       // save("lat",location.latitude.toString())
                        //save("lon",location.longitude.toString())
                        getCityName(location.latitude,location.longitude)
                        loadData(location.latitude.toString(),location.longitude.toString())
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
        fusedLocationClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )
    }

    private  val locationCallback = object  : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            val lastLocation =p0.lastLocation
           // save("lat",lastLocation.latitude.toString())
            //save("lat",lastLocation.longitude.toString())

            getCityName(lastLocation.latitude,lastLocation.longitude)
           loadData(lastLocation.latitude.toString(),lastLocation.longitude.toString())

        }
    }
    private fun checkPermission():Boolean{
        if (
            ActivityCompat.checkSelfPermission(context!!,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(context!!,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private  fun requestPermission(){
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),1
        )
    }

    private  fun isLocationEnabled():Boolean{
        val locationManager=activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun getCityName(lat:Double,long: Double) {
        var cityName=""
        val geoCoder= Geocoder(activity, Locale.getDefault())
        val addr=geoCoder.getFromLocation(lat,long,1)
        cityName=addr.get(0).locality
        save("city",cityName)
        // cityTextView.text=cityName
    }
    **/
    private  fun save(key:String,value:String){
        val  sharedPreferences=activity!!.getSharedPreferences("weather",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }



}