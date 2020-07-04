package com.example.weatherdetailer

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.adapter.DateForecastAdapter
import com.example.weatherdetailer.adapter.DatePastDataAdapter
import com.example.weatherdetailer.network.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DateFragment : Fragment(),AdapterView.OnItemSelectedListener {
   lateinit var calendarView: CalendarView
    private lateinit var unitType:String
    private lateinit var weather:TextView
    var  m:String=""
    var selectedItem:String=""
    lateinit var sharedPreferences: SharedPreferences
    var firstTime:Boolean=true
    var future:Boolean=false
    var isDateChanged:Boolean=false
    private var currentTime:Long=0
    private var responseList:MutableList<WeatherResponse> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var dateAdapter: DateForecastAdapter?=null
    private var pastDataAdapter:DatePastDataAdapter?=null
    private var historyDataList:MutableList<Current> = ArrayList()
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
/**
        context?.let { Places.initialize(it,"AIzaSyD2BU6x8RqFCvHX4BnrIaI0f1ycabOcl2k") }
        var placesClient= context?.let { Places.createClient(it) }
**/
        recyclerView=view.findViewById(R.id.recycler)
        recyclerView.layoutManager=LinearLayoutManager(context)

        dateAdapter= DateForecastAdapter(responseList,R.layout.datefragment_forecast_item)
        pastDataAdapter= DatePastDataAdapter(historyDataList,R.layout.datefragment_pastdata_item)


       val nameTextView=view.findViewById<TextView>(R.id.usrnmeDate)
        calendarView=view.findViewById<CalendarView>(R.id.calenderView)
        val spinner:Spinner=view.findViewById(R.id.citySpinner)



        spinner.onItemSelectedListener=this
        ArrayAdapter.createFromResource(
                context!!,R.array.citylist,android.R.layout.simple_spinner_item).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter=arrayAdapter
        }


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
                recyclerView.adapter=dateAdapter
                Toast.makeText(activity,"Future",Toast.LENGTH_LONG).show()
            }
            if(unix<currentTime){
                Toast.makeText(activity,"Past"+unix,Toast.LENGTH_LONG).show()
                future=false
                recyclerView.adapter=pastDataAdapter

            }
            isDateChanged=true

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
            Toast.makeText(activity,"selected"+selectedItem,Toast.LENGTH_SHORT).show()
            if(isDateChanged==true){
                if(future==true){

                    forecast(selectedItem,m)
                }
                else{
                    loadPastData()
                }

            }else{
                val c:Date=Calendar.getInstance().time
                val format:SimpleDateFormat=SimpleDateFormat("yyyy-MM-dd")
                var day1=format.format(c)
                forecast(selectedItem,day1)


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
                        var array:ArrayList<WeatherResponse> = ArrayList()
                        for(i in list){
                            var dateString:String=weatherResponse.list[num].date.toString().substring(0,10)
                            if(day==dateString){
                                Toast.makeText(activity,"date checking works",Toast.LENGTH_SHORT).show()
                                stringBuilder.append( weatherResponse.list[num].date.toString()+" - "+weatherResponse.list[num].weather[0].description+" - "+ weatherResponse.list[num].main!!.temp_min+" "+unit+"\n")
                                array.add(weatherResponse.list[num])
                            }

                            num++
                        }
                        responseList.addAll(array)
                        dateAdapter!!.notifyDataSetChanged()
                        //Toast.makeText()
                       // weatherTextView!!.text=stringBuilder

                    }

                }else{
                    Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
              //  weatherTextView!!.text=t.message
            }

        })


    }

    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared.getString(string,null)
    }
    private  fun loadPastData(){
        val unit =getData(sharedPreferences,"unit")

        if(unit=="celsius"){
            unitType="metric"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
        }

        val lat:String="12.97"
        val lon:String="77.5946"
        val dt:String="1593697928"
        val appid:String="0458de72757b2f04185abd9a4b012488"

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)
        val reportCall = service.getPastData(lat,lon,dt,appid,unitType)

        reportCall.enqueue(object : Callback<PastResponse> {
            override fun onFailure(call: Call<PastResponse>?, t: Throwable?) {
                if (t != null) {
                   // weatherTextView.text=t.message
                    Toast.makeText(activity,""+t.message,Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<PastResponse>?, response: Response<PastResponse>?) {

                if (response!=null){
                    if (response.code()==200){
                        val pastResponse=response.body()
                        val array=pastResponse.hourly_update
                        var sbuilder=StringBuilder()
                       // sbuilder.append(pastResponse.current!!.temp.toString()+" "+pastResponse.current!!.weather[0].main+" "+pastResponse.current!!.weather[0].description)
                       // sbuilder.append(pastResponse.lat.toString())
                        //weatherTextView.text=sbuilder
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

}