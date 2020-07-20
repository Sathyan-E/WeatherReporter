package com.example.weatherdetailer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.adapter.DateForecastAdapter
import com.example.weatherdetailer.adapter.DatePastDataAdapter
import com.example.weatherdetailer.adapter.OnPlaceClickListener
import com.example.weatherdetailer.adapter.PlacesPredictionAdapter
import com.example.weatherdetailer.network.*
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DateFragment : Fragment(),OnPlaceClickListener {
    private lateinit var calendarView: CalendarView
    private lateinit var unitType:String
    var  m:String=""
    var selectedLat:String=""
    var selectedlon:String=""
    var selectedDate:String=""
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var   selectedCity:String
    var isfuture:Boolean=false
    var isPast:Boolean=false
    var isDateChanged:Boolean=false
    var isPlaceSelected:Boolean=false
    private var currentTime:Long=0
    private var responseList:MutableList<WeatherResponse> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var dateAdapter: DateForecastAdapter?=null
    private var pastDataAdapter:DatePastDataAdapter?=null
    private var historyDataList:MutableList<Current> = ArrayList()
    private lateinit var autocompleteSupportFragment:AutocompleteSupportFragment
    lateinit var currentDataTextview:TextView
    private var lastUpdate:Int=-1
    private var lastunitPreference:String=""
    private lateinit var shareButton: Button
    private lateinit var sharingLayout: LinearLayout
    public var currentUnit=""
    private lateinit var placeEditText: EditText
    private lateinit var placeRecyclerView: RecyclerView
    private lateinit var placeAdapter:PlacesPredictionAdapter
    private lateinit var placesClient: PlacesClient
    private var placeList:ArrayList<AutocompletePrediction> = ArrayList()
    private var selectedPlace=""
    private lateinit var noPlaceFoundTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.datefragmentlayout,container,false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentDataTextview=view.findViewById(R.id.data_type)
        shareButton=view.findViewById(R.id.datefragment_share_button)
        sharingLayout=view.findViewById(R.id.layout_sharing)
        shareButton.isEnabled=false
        placeEditText=view.findViewById(R.id.find_place_editview)
        placeRecyclerView=view.findViewById(R.id.place_recycler_datefragment)
        noPlaceFoundTextView=view.findViewById(R.id.no_places_found)

        placeRecyclerView.layoutManager=LinearLayoutManager(context)
        placeAdapter= PlacesPredictionAdapter(placeList,this)
        placeRecyclerView.adapter=placeAdapter

        shareButton.setOnClickListener {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                PackageManager.PERMISSION_GRANTED)

            val bitmap= Bitmap.createBitmap(sharingLayout.width,sharingLayout.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            sharingLayout.draw(canvas)
            //ssImageView.setImageBitmap(bitmap)
            val  mainDirectoryname =
                File(context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"ScreenShots")
            if (!mainDirectoryname.exists()){
                if (mainDirectoryname.mkdirs()){
                    Log.e("Create Directory", "Main Directory created: $mainDirectoryname")
                }
            }
            val name:String="screenshot"+ Calendar.getInstance().time.toString()+".jpg"
            val dir =File(mainDirectoryname.absolutePath)
            if (!dir.exists()){
                dir.mkdirs()
            }
            val imagefile = File(mainDirectoryname.absolutePath,name)
            val outPutStream = FileOutputStream(imagefile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outPutStream)
            outPutStream.flush()
            outPutStream.close()
            shareScreenShot(imagefile)
            imagefile.delete()
        }

        if (!Places.isInitialized()){
            Places.initialize(context!!,"AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k")
        }

        placesClient=Places.createClient(context!!)

        placeEditText.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                placeRecyclerView.visibility=View.VISIBLE
                val token=AutocompleteSessionToken.newInstance()
                val bounds=RectangularBounds.newInstance(LatLng(0.0,0.0), LatLng(0.0,0.0))
                val request=FindAutocompletePredictionsRequest.builder().
                        setSessionToken(token)
                    .setTypeFilter(TypeFilter.CITIES)
                    .setLocationBias(bounds)
                    .setQuery(p0.toString())
                    .build()
                placesClient.findAutocompletePredictions(request).addOnSuccessListener { findAutocompletePredictionsResponse ->
                    placeList.clear()
                    for(prediction:AutocompletePrediction in findAutocompletePredictionsResponse.autocompletePredictions){
                        placeList.add(prediction)
                    }
                    if (placeList.isEmpty()){
                        noPlaceFoundTextView.visibility=View.VISIBLE
                    }else{
                        noPlaceFoundTextView.visibility=View.GONE
                        placeAdapter.notifyDataSetChanged()
                    }

                }.addOnFailureListener { exception ->
                    Toast.makeText(activity,""+exception.message,Toast.LENGTH_SHORT).show()
                }
            }

        })

        recyclerView=view.findViewById(R.id.recycler)
        recyclerView.layoutManager=LinearLayoutManager(context)


        dateAdapter= DateForecastAdapter(responseList,currentUnit)
        pastDataAdapter= DatePastDataAdapter(historyDataList,currentUnit)
        calendarView=view.findViewById(R.id.calenderView)

        val date=calendarView.date
        val min=date-432000000
        val max=date+432000000

        calendarView.minDate=min
        calendarView.maxDate=max
        calendarView.setOnDateChangeListener{ view, year, month, dayOfMonth ->

            m= "$year-"
            m += if(month<10){
                "0"+(month+1).toString()+"-"
                // m+= "0$month+1"
            }else{
                "$month-"
            }
            m += if (dayOfMonth<10){
                "0$dayOfMonth"
            }else{
                "$dayOfMonth"
            }

            val l=LocalDate.parse(m, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val unix=l.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            currentTime=System.currentTimeMillis()
            isDateChanged=true
            selectedDate=(unix/1000).toString()
            if(unix>currentTime){
                isfuture=true
                isPast=false
                recyclerView.adapter=dateAdapter
                if (isPlaceSelected){
                    loadForecast(selectedLat,selectedlon,m)
                }else{

                    Toast.makeText(activity,"Select the place",Toast.LENGTH_SHORT).show()
                }
            }
            else if (unix<currentTime){

                isfuture=false
                isPast=true
                recyclerView.adapter=pastDataAdapter
                if (isPlaceSelected){
                    loadPastData(selectedLat,selectedlon,selectedDate)
                }
                else{

                    Toast.makeText(activity,"Select the place",Toast.LENGTH_SHORT).show()
                }
            }
        }
        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!
    }

    private fun shareScreenShot(imageFile:File){
        val fileuri: Uri = FileProvider.getUriForFile(context!!,"com.example.weatherdetailer.provider",imageFile)
        val intent= Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM,fileuri)
        startActivity(Intent.createChooser(intent,"Share Screenshot"))
    }

    override fun onResume() {
        super.onResume()
        val unitPreference=getData(sharedPreferences,"unit")
        if(unitPreference!=lastunitPreference){
            when(lastUpdate){
                0 -> loadPastData(selectedLat,selectedlon,selectedDate)
                1 -> loadCurrentData(selectedLat,selectedlon)
                2->loadForecast(selectedLat,selectedlon,m)
            }
        }
    }


    private  fun loadForecast(lat: String,lng:String, day:String){
        lastUpdate=2
        val date= "Date :$m Location: $selectedPlace"
        currentDataTextview.text=date

        findUnit()
        dateAdapter!!.setUnit(currentUnit)

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)

        val reportCall = service.getForecast(lat,lng,"0458de72757b2f04185abd9a4b012488",unitType)

        reportCall.enqueue(object : Callback<MonthlyResponse> {
            override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {

                if (response!=null){
                    if (response.code()==200){
                        val weatherResponse=response.body()
                        val list=weatherResponse.list

                        responseList.clear()
                        var num=0
                        val array:ArrayList<WeatherResponse> = ArrayList()
                        for(i in list){
                            val dateString:String=weatherResponse.list[num].date.toString().substring(0,10)
                            if(day==dateString){
                                //Toast.makeText(activity,"date checking works",Toast.LENGTH_SHORT).show()
                                array.add(weatherResponse.list[num])
                            }
                            num++
                        }
                        responseList.addAll(array)
                        dateAdapter!!.notifyDataSetChanged()

                    }

                }else{
                    Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                Toast.makeText(activity,""+t.message,Toast.LENGTH_SHORT).show()
            }

        })


    }

    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared.getString(string,null)
    }
    private  fun loadPastData(lat:String,lon:String,dt:String){
        lastUpdate=0
        val date= "Date :$m Location: $selectedPlace"
        currentDataTextview.text=date

        findUnit()
        pastDataAdapter!!.setUnit(currentUnit)

        val appid="0458de72757b2f04185abd9a4b012488"

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)
        val reportCall = service.getPastData(lat,lon,dt,appid,unitType)

        reportCall.enqueue(object : Callback<PastResponse> {
            override fun onFailure(call: Call<PastResponse>?, t: Throwable?) {
                if (t != null) {

                    Toast.makeText(activity,""+t.message,Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<PastResponse>?, response: Response<PastResponse>?) {

                if (response!=null){
                    if (response.code()==200){

                        val pastResponse=response.body()
                        val array=pastResponse.hourly_update
                        historyDataList.clear()
                        historyDataList.addAll(array)
                        pastDataAdapter!!.notifyDataSetChanged()
                    }
                    else{
                        Toast.makeText(activity,"Error Response Code",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(activity,"Null Response",Toast.LENGTH_SHORT).show()
                }

            }

        })

    }
    private fun loadCurrentData(lat:String,lng:String){
        lastUpdate=1
        val date="Date :$m Location: $selectedPlace"
        currentDataTextview.text= date

        findUnit()
        dateAdapter!!.setUnit(currentUnit)
       

        val retrofit=Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(WeatherService::class.java)

        val call = service.getCurrentWeatherData(lat,lng,"0458de72757b2f04185abd9a4b012488",unitType)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>?) {
                if (response!=null){
                    if (response.code() == 200){
                        val weatherResponse=response.body()
                       responseList.clear()
                        responseList.add(weatherResponse)
                        dateAdapter!!.notifyDataSetChanged()
                    }
                }
                else{
                    Toast.makeText(activity,"response is null",Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable) {
                currentDataTextview.text=t.message
            }
        })


    }
    private fun findUnit(){
        val unit =getData(sharedPreferences,"unit")
        lastunitPreference=unit!!
        if(unit=="celsius"){
            unitType="metric"
            currentUnit="C"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
            currentUnit="F"
        }

    }

    override fun onItemClick(place: AutocompletePrediction, pos: Int) {
        isPlaceSelected=true
        val placeField= listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG)
        val placeRequest=FetchPlaceRequest.newInstance(place.placeId,placeField)
        placesClient.fetchPlace(placeRequest).addOnSuccessListener {fetchPlaceResponse: FetchPlaceResponse? ->
            placeEditText.setText(place.getFullText(null))
            placeRecyclerView.visibility=View.GONE
            val placeDetail=fetchPlaceResponse!!.place
            selectedPlace=placeDetail.name.toString()
            selectedLat=placeDetail.latLng!!.latitude.toString()
            selectedlon=placeDetail.latLng!!.longitude.toString()
            if(!isDateChanged){
                recyclerView.adapter=dateAdapter
                loadCurrentData(selectedLat,selectedlon)
            }else if(isDateChanged){
                if(isPast){
                    loadPastData(selectedLat,selectedlon,selectedDate)
                }else if(isfuture){
                    loadForecast(selectedLat,selectedlon,m)
                }
            }


        }

    }

}