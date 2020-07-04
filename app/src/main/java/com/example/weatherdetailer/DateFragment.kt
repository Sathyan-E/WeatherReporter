package com.example.weatherdetailer

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.weatherdetailer.network.MonthlyResponse
import com.example.weatherdetailer.network.WeatherService
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.datefragmentlayout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DateFragment : Fragment(),AdapterView.OnItemSelectedListener {
   lateinit var calendarView: CalendarView
    private lateinit var unitType:String
    private lateinit var weather:TextView
    var  m:String=""
    var selectedItem:String=""
    lateinit var sharedPreferences: SharedPreferences
    var firstTime:Boolean=true
    var future:Boolean=false
    private var currentTime:Long=0
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

        context?.let { Places.initialize(it,"AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k") }
        var placesClient= context?.let { Places.createClient(it) }


       val nameTextView=view.findViewById<TextView>(R.id.usrnmeDate)
        calendarView=view.findViewById<CalendarView>(R.id.calenderView)
        val spinner:Spinner=view.findViewById(R.id.citySpinner)
        weather=view.findViewById(R.id.weatherTextView)

        val autocompleteFragment= fragmentManager?.findFragmentById(R.id.autoCOmpleteFragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.NAME,Place.Field.LAT_LNG))

        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(activity,"place"+p0.name,Toast.LENGTH_SHORT).show()
            }

            override fun onError(p0: Status) {
                Log.i("Error","An error occured: $p0")
            }


        })


        /**
        spinner.onItemSelectedListener=this
        ArrayAdapter.createFromResource(
                context!!,R.array.citylist,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=arrayAdapter
        }
        **/

        val date=calendarView.date
        val min=date-432000000
        val max=date+432000000
        currentTime=System.currentTimeMillis()
        Toast.makeText(activity,""+currentTime,Toast.LENGTH_LONG).show()


        calendarView.minDate=min
        calendarView.maxDate=max

        calendarView.setOnDateChangeListener{ view, year, month, dayOfMonth ->

            m= "$year-"
            if(month<10){
                m+="0"+(month+1).toString()+"-"
                // m+= "0$month+1"
            }else{
                m+= "$month-"
            }
            if (dayOfMonth<10){
                m+="0$dayOfMonth"
            }else{
                m+="$dayOfMonth"
            }

            val l=LocalDate.parse(m, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val unix=l.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
          //  Toast.makeText(activity,"Future"+unix,Toast.LENGTH_LONG).show()
            if(unix>currentTime){
                future=true
                Toast.makeText(activity,"Future",Toast.LENGTH_LONG).show()
            }
            if(unix<currentTime){
                Toast.makeText(activity,"Past"+unix,Toast.LENGTH_LONG).show()
            }

            // val msg:String= year.toString()+"-"+(month+1)+"-"+dayOfMonth
            //Toast.makeText(activity,""+m,Toast.LENGTH_LONG).show()

        }



        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!
        val user: String? = getData(sharedPreferences,"name")
        nameTextView.text=user

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        if (p0!!.getItemAtPosition(p2).toString()!="None"){
           // Toast.makeText(activity,""+p0!!.getItemAtPosition(p2).toString(),Toast.LENGTH_SHORT).show()
            selectedItem=p0.getItemAtPosition(p2).toString()
            if(future==true){
                forecast(p0.getItemAtPosition(p2).toString(),m)
            }

        }

    }

    override fun onResume() {
        super.onResume()
        //Toast.makeText(activity,""+date.toString(),Toast.LENGTH_LONG).show()

    }


    private  fun forecast(name:String, day:String){

        val unit =getData(sharedPreferences,"unit")

        if(unit=="celsius"){
            unitType="metric"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
        }


        if (day==""){
            val c:Date=Calendar.getInstance().time
            val format:SimpleDateFormat=SimpleDateFormat("yyyy-MM-dd")
             var day1=format.format(c)
            Toast.makeText(activity,""+day1,Toast.LENGTH_SHORT).show()

            val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
            val service = reportRetofit.create(WeatherService::class.java)

            val reportCall = service.getForecast(name,"0458de72757b2f04185abd9a4b012488",unitType)

            reportCall.enqueue(object : Callback<MonthlyResponse> {
                override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {

                    if (response!=null){
                        if (response.code()==200){
                            val weatherResponse=response.body()
                            val list=weatherResponse.list

                            var stringBuilder=StringBuilder()
                            var num:Int=0
                            for(i in list){
                                var dateString:String=weatherResponse.list[num].date.toString().substring(0,10)
                                if(day1==dateString){
                                    Toast.makeText(activity,"date checking works",Toast.LENGTH_SHORT).show()
                                    stringBuilder.append( weatherResponse.list[num].date.toString()+" - "+weatherResponse.list[num].weather[0].description+" - "+ weatherResponse.list[num].main!!.temp_min+" "+unit+"\n")
                                }

                                num++
                            }
                            //Toast.makeText()
                            weatherTextView!!.text=stringBuilder

                        }

                    }else{
                        Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                    weatherTextView!!.text=t.message
                }

            })

        }
        else{
            val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
            val service = reportRetofit.create(WeatherService::class.java)

            val reportCall = service.getForecast(name,"0458de72757b2f04185abd9a4b012488",unitType)

            reportCall.enqueue(object : Callback<MonthlyResponse> {
                override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {

                    if (response!=null){
                        if (response.code()==200){
                            val weatherResponse=response.body()
                            val list=weatherResponse.list

                            var stringBuilder=StringBuilder()
                            var num:Int=0
                            for(i in list){
                                var dateString:String=weatherResponse.list[num].date.toString().substring(0,10)
                                if(day==dateString){
                                    Toast.makeText(activity,"date checking works",Toast.LENGTH_SHORT).show()
                                    stringBuilder.append( weatherResponse.list[num].date.toString()+" - "+weatherResponse.list[num].weather[0].description+" - "+ weatherResponse.list[num].main!!.temp_min+" "+unit+"\n")
                                }

                                num++
                            }
                            //Toast.makeText()
                            weatherTextView!!.text=stringBuilder

                        }

                    }else{
                        Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                    weatherTextView!!.text=t.message
                }

            })

        }



    }

    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared.getString(string,null)
    }

}