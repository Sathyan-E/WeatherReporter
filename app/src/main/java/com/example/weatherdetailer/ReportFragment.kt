package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        return inflater.inflate(R.layout.reportfragmentlayout,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView=view.findViewById<TextView>(R.id.usrnmeReport)
        val cityTextView=view.findViewById<TextView>(R.id.city)
        val reportTextView = view.findViewById<TextView>(R.id.report)
        //reportTextView.movementMethod(ScrollingMovementMethod())

        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val user: String? = sharedPreferences?.getString("name",null)
        val city: String? = sharedPreferences?.getString("city",null)
        val lat:String? = sharedPreferences?.getString("lat",null)
        val lon:String? = sharedPreferences?.getString("lon",null)
        val unit:String? = sharedPreferences?.getString("unit",null)

        if(unit=="censius"){
            unitType="metric"
        }
        else if(unit=="farenheit"){
            unitType="imperial"
        }

        nameTextView.text=user
        cityTextView.text=city

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)

        val reportCall = service.getMonthlyReport(lat.toString(),lon.toString(),"0458de72757b2f04185abd9a4b012488",unitType)

        reportCall.enqueue(object : Callback<MonthlyResponse> {
            override fun onResponse(call: Call<MonthlyResponse>?, response: Response<MonthlyResponse>?) {
                //TODO("Not yet implemented")
                if (response!=null){
                    if (response.code()==200){
                        val weatherResponse=response.body()
                        val list=weatherResponse.list
                        Toast.makeText(activity,"The length is "+list.size,Toast.LENGTH_SHORT).show()
                        var stringBuilder=StringBuilder()
                        var num:Int=0
                        for(i in list){
                            stringBuilder.append( weatherResponse.list[num].date.toString()+" "+weatherResponse.list[num].weather[0].description+" "+ weatherResponse.list[num].main!!.temp_min+" "+unit+"\n")
                            num++
                        }
                        reportTextView!!.text=stringBuilder

                    }

                }else{
                    Toast.makeText(activity,"Null Response Try Again",Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<MonthlyResponse>?, t: Throwable) {
                //TODO("Not yet implemented")
                reportTextView!!.text=t.message
            }

        })
    }
}