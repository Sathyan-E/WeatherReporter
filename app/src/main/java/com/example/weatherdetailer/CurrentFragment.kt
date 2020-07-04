package com.example.weatherdetailer

import android.content.Context
import android.content.SharedPreferences
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
    lateinit var detailsTextView:TextView
    lateinit var sharedPreferences:SharedPreferences
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

        detailsTextView = view.findViewById(R.id.weather)

        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!
        val user: String? = sharedPreferences?.getString("name",null)
        val city: String? = sharedPreferences?.getString("city",null)

        userTextView.text=user
        cityTextView.text=city


    }
    private fun findWeather(){
        val lat:String? = getData(sharedPreferences,"lat")
        val lon:String? =getData(sharedPreferences,"lon")
        val unit:String? =getData(sharedPreferences,"unit")

        val retrofit=Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
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
                                "Temperature(Min): "+weatherResponse.main!!.temp_min+unit+"\n"+
                                "Temperature(Max): "+weatherResponse.main!!.temp_max+unit+"\n"+
                                weatherResponse.weather!![0].description+"\n"
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
                detailsTextView!!.text=t.message
            }
        })
    }

    override fun onResume() {
        super.onResume()
        findWeather()
    }
    private  fun getData(shared:SharedPreferences,string: String): String? {
        return shared?.getString(string,null)

    }

}