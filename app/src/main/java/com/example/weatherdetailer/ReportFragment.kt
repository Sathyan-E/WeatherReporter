package com.example.weatherdetailer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.adapter.ReportViewAdapter
import com.example.weatherdetailer.network.MonthlyResponse
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReportFragment : Fragment() {
    var unitType=""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var reportTextView:TextView
    private var responseList:MutableList<WeatherResponse> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapter: ReportViewAdapter? =null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{


        val view= inflater.inflate(R.layout.reportfragmentlayout,container,false)
        val nameTextView=view.findViewById<TextView>(R.id.usrnmeReport)
        val cityTextView=view.findViewById<TextView>(R.id.city)
        recyclerView=view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerAdapter= ReportViewAdapter(responseList,R.layout.reportlayoutitem)
        recyclerView.adapter=recyclerAdapter

        reportTextView = view.findViewById<TextView>(R.id.report)

        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!

        val user: String? =getData(sharedPreferences,"name")
        val city: String? = getData(sharedPreferences,"city")

        nameTextView.text=user
        cityTextView.text=city

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    fun loadData(){

        val unit =getData(sharedPreferences,"unit")
        val lat=getData(sharedPreferences,"lat")
        val lon=getData(sharedPreferences,"lon")
        if(unit=="celsius"){
            unitType="metric"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
        }

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)

        val reportCall = service.getForecast(lat.toString(),lon.toString(),"0458de72757b2f04185abd9a4b012488",unitType)

        reportCall.enqueue(object : Callback<MonthlyResponse> {
            override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {

                if (response!=null){
                    if (response.code()==200){
                        val weatherResponse=response.body()
                        val list=weatherResponse.list

                        responseList.addAll(list)
                        recyclerAdapter!!.notifyDataSetChanged()


                        var stringBuilder=StringBuilder()
                        var num:Int=0
                        for(i in list){
                            stringBuilder.append( weatherResponse.list[num].date.toString()+" - "+weatherResponse.list[num].weather[0].description+" - "+ weatherResponse.list[num].main!!.temp_min+" "+unit+"\n")
                            num++
                        }
                        //reportTextView!!.text=stringBuilder

                    }

                }else{
                    Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                reportTextView!!.text=t.message
            }

        })

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared?.getString(string,null)
    }

}