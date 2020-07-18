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
import android.text.Editable
import android.text.TextWatcher
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
import com.example.weatherdetailer.adapter.OnPlaceClickListener
import com.example.weatherdetailer.adapter.PlacesPredictionAdapter
import com.example.weatherdetailer.adapter.ReportViewAdapter
import com.example.weatherdetailer.network.MonthlyResponse
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
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
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class ReportFragment : Fragment(),OnPlaceClickListener   {
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
    private lateinit var cityTextView:TextView
    //val placesApi=PlacesAPI.Builder().apikey("AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k").build(activity)
    private lateinit var search:EditText
    private var placeList:ArrayList<AutocompletePrediction> = ArrayList()
    private lateinit var placesRecyclerView: RecyclerView
    private lateinit var placeAdapter: PlacesPredictionAdapter
    private lateinit var placesClient: PlacesClient
    private lateinit var listener:OnPlaceClickListener

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
        cityTextView=view.findViewById<TextView>(R.id.city)
        recyclerView=view.findViewById(R.id.recyclerview)
        screenshotView=view.findViewById(R.id.report_sharing_layout)
        shareButton=view.findViewById(R.id.report_sharing_button)
        search=view.findViewById(R.id.search)
        placesRecyclerView=view.findViewById(R.id.places_list_recyclervew)

        placesRecyclerView.layoutManager=LinearLayoutManager(context)
        placeAdapter= PlacesPredictionAdapter(placeList,listener)

        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerAdapter= ReportViewAdapter(responseList,cUnit)
        recyclerView.adapter=recyclerAdapter
        recyclerView.visibility=View.INVISIBLE

        reportTextView = view.findViewById(R.id.report)

        fusedLocationClient= LocationServices.getFusedLocationProviderClient(activity!!)


        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!


       // val city: String? = getData(sharedPreferences,"city")
        if (!Places.isInitialized()){
            Places.initialize(context!!,"AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k")
        }


        placesClient= Places.createClient(context!!)
        autocompleteFragment=childFragmentManager.findFragmentById(R.id.report_autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME))

        //nameTextView.text=user
       // cityTextView.text=city

        progrssBar=view.findViewById(R.id.report_progressbar)
       // progrssBar.visibility=View.INVISIBLE

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
                progrssBar.visibility=View.VISIBLE
                cityTextView.text=p0.name
                loadData(selectedLat,selectedLon)
            }

            override fun onError(p0: Status) {

            }

        })

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val token= AutocompleteSessionToken.newInstance()
                val bound= RectangularBounds.newInstance(
                    LatLng(0.0,0.0), LatLng(0.0,0.0))
                val request= FindAutocompletePredictionsRequest.builder()
                    .setLocationBias(bound)
                    .setTypeFilter(TypeFilter.CITIES)
                    .setSessionToken(token)
                    .setQuery(search.text.toString()).build()

                placesClient.findAutocompletePredictions(request).addOnSuccessListener { findAutocompletePredictionsResponse ->
                    //sBuilder= StringBuilder()
                    placeList.clear()
                    for(prediction:AutocompletePrediction in findAutocompletePredictionsResponse.autocompletePredictions){
                       placeList.add(prediction)
                        //sBuilder.append(" ").append(prediction.getFullText(null)).toString()+"\n"
                        Toast.makeText(activity,"Place ID is"+prediction.getFullText(null),Toast.LENGTH_SHORT).show()
                    }
                    placeAdapter.notifyDataSetChanged()


                }.addOnFailureListener{
                    Toast.makeText(activity,"add failure listener",Toast.LENGTH_SHORT).show()
                }



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
        refreshReport()
    }
   public fun refreshReport(){
        val unit =getData(sharedPreferences,"unit")
        val isConnected=isInternetConnected()
        if ( lastUsedUnit!=unit){
            //     progrssBar.visibility=View.VISIBLE
            if(isConnected){
                if (selectedLat!=""){
                    loadData(selectedLat,selectedLon)
                }else{
                    progrssBar.visibility=View.INVISIBLE
                    Toast.makeText(activity,"Select place for report",Toast.LENGTH_SHORT).show()
                }

            }
            else{
                progrssBar.visibility=View.INVISIBLE
                Toast.makeText(activity,"No Internet Connection!",Toast.LENGTH_SHORT).show()
            }

        }
        else{
            recyclerView.visibility=View.VISIBLE

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

    private  fun save(key:String,value:String){
        val  sharedPreferences=activity!!.getSharedPreferences("weather",Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()

    }

    override fun onItemClick(place: AutocompletePrediction, pos: Int) {
      //  result.text=place.placeId
        val placeField= listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG)
        val request= FetchPlaceRequest.newInstance(place.placeId,placeField)
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            val place=response.place
            Toast.makeText(activity,"Place name"+place.latLng,Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception: Exception ->
            Toast.makeText(activity,"Place not found: ${exception.message}",Toast.LENGTH_SHORT).show()

        }
    }

}