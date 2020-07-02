package com.example.weatherdetailer

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weatherdetailer.network.WeatherResponse
import com.example.weatherdetailer.network.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrentFragment : Fragment() {
    var unitType=""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{
        return inflater.inflate(R.layout.currentfragmentlayout,container,false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
        val userTextView=view.findViewById<TextView>(R.id.userName)
        val cityTextView =view.findViewById<TextView>(R.id.cityname)
        val tempTextView = view.findViewById<TextView>(R.id.temp)
        val detailsTextView = view.findViewById<TextView>(R.id.weather)

        val  sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)
        val user: String? = sharedPreferences?.getString("name",null)
        val city: String? = sharedPreferences?.getString("city",null)
        val lat:String? = sharedPreferences?.getString("lat",null)
        val lon:String? = sharedPreferences?.getString("lon",null)
        val unit:String? = sharedPreferences?.getString("unit",null)
        Toast.makeText(activity,""+city,Toast.LENGTH_SHORT).show()
        userTextView.text=user
        cityTextView.text=city

        val retrofit=Retrofit.Builder().baseUrl("http://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()

        val service = retrofit.create(WeatherService::class.java)
       if (unit=="celsius"){
           unitType="metric"
       }else if(unit=="farenheit"){
           unitType="imperial"
       }

        val call = service.getCurrentWeatherData(lat.toString(),lon.toString(),"0458de72757b2f04185abd9a4b012488",unitType)

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>?) {
                if (response!=null){
                    if (response.code() == 200){
                        val weatherResponse=response.body()
                        val stringBuilder="Country :"+weatherResponse.sys!!.country+"\n"+
                                "Temperature: "+weatherResponse.main!!.temp+"\n"+
                                "Temperature(Min): "+weatherResponse.main!!.temp_min+"\n"+
                                "Temperature(Max): "+weatherResponse.main!!.temp_max+"\n"+
                                "Humidity: "+weatherResponse.main!!.humudity+"\n"+
                                "Pressure: "+weatherResponse.main!!.pressure
                        detailsTextView!!.text=stringBuilder


                    }
                }
                else{
                    Toast.makeText(activity,"response is null",Toast.LENGTH_SHORT).show()
                }


            }

            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable) {
                TODO("Not yet implemented")
                detailsTextView!!.text=t.message
            }
        })
        //Toast.makeText(activity,""+city,Toast.LENGTH_SHORT).show()

    }
}