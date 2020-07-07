package com.example.weatherdetailer

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
    var lastUsedUnit:String=""
    lateinit var sharedPreferences: SharedPreferences
    lateinit var reportTextView:TextView
    private var responseList:MutableList<WeatherResponse> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var recyclerAdapter: ReportViewAdapter? =null
    private lateinit var progrssBar: ProgressBar
  //  private lateinit var cardView:CardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        val view= inflater.inflate(R.layout.reportfragmentlayout,container,false)
        //val nameTextView=view.findViewById<TextView>(R.id.usrnmeReport)
        val cityTextView=view.findViewById<TextView>(R.id.city)
        recyclerView=view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerAdapter= ReportViewAdapter(responseList)
        recyclerView.adapter=recyclerAdapter
        recyclerView.visibility=View.INVISIBLE

        reportTextView = view.findViewById<TextView>(R.id.report)


        sharedPreferences= activity?.getSharedPreferences("weather", Context.MODE_PRIVATE)!!

        val user: String? =getData(sharedPreferences,"name")
        val city: String? = getData(sharedPreferences,"city")

        //nameTextView.text=user
        cityTextView.text=city

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progrssBar=view.findViewById(R.id.report_progressbar)

    }
    private fun loadData(){

        val lat=getData(sharedPreferences,"lat")
        val lon=getData(sharedPreferences,"lon")

        val reportRetofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build()
        val service = reportRetofit.create(WeatherService::class.java)

        val reportCall = service.getForecast(lat.toString(),lon.toString(),"0458de72757b2f04185abd9a4b012488",unitType)

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
                reportTextView!!.text=t.message
            }

        })

    }

    override fun onResume() {
        super.onResume()
        val unit =getData(sharedPreferences,"unit")


        val isConnected=isInternetConnected()
        if (isConnected){
            if(lastUsedUnit!=unit){
                progrssBar.visibility=View.VISIBLE
                if(unit=="celsius"){
                    unitType="metric"
                    lastUsedUnit="celsius"
                }
                else if(unit=="farenheit"){
                    unitType="imperial"
                    lastUsedUnit="farenheit"
                }

                loadData()

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


}