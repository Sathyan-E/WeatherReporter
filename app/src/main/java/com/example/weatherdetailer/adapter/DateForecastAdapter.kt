package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.R
import com.example.weatherdetailer.network.Current
import com.example.weatherdetailer.network.WeatherResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.datefragment_forecast_item.view.*
import kotlinx.android.synthetic.main.datefragment_pastdata_item.view.*
import kotlinx.android.synthetic.main.reportlayoutitem.view.*
import java.text.SimpleDateFormat
import java.util.*

class DateForecastAdapter (private  val list:List<WeatherResponse>):RecyclerView.Adapter<DateForecastAdapter.ForecastDataViewHolder>(){

    class ForecastDataViewHolder(private val containerView:View):RecyclerView.ViewHolder(containerView){
       /**
        val dateTextView=view.date
        val descriptionTextView=view.description
        val tempTextView=view.temp
        **/
       val forecastMainDescription: TextView =containerView.forecast_report_main_description
       ///past_report_main_description
        val forecastClimateDescription=containerView.forecast_report_climate_description
        val forecastClimateIcon: ImageView =containerView.forecast_report_climate_image
        val forecastTemperature: TextView =containerView.forecast_report_city_temp
        val forecastFeellikeTemp: TextView =containerView.forecast_report_city_feelslike_temp
        val forecastWindValue: TextView =containerView.forecast_report_city_wind
        val forecastHumidityValue: TextView =containerView.forecast_report_city_humidity
        val forecastPressureValue: TextView =containerView.forecast_report_city_pressure
        val forecastReportDate: TextView =containerView.forecast_report_date


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastDataViewHolder {
        val forecastView=LayoutInflater.from(parent.context).inflate(R.layout.datefragment_forecast_item,parent,false)
        return ForecastDataViewHolder(forecastView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder:ForecastDataViewHolder, position: Int) {
        /**
        holder.dateTextView.text=list[position].date
        holder.descriptionTextView.text=list[position].weather[0].description
        holder.tempTextView?.text= list[position].main?.temp.toString()
        **/
        val response: WeatherResponse =list[position]
        holder.forecastMainDescription.text=response.weather[0].main
        holder.forecastClimateDescription.text=response.weather[0].description
        var  icon:String= response.weather[0].icon.toString()
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png").into(holder.forecastClimateIcon)
        // var temp:String= response.main.temp.toString()
        holder.forecastTemperature.text= response.main!!.temp.toString()
        holder.forecastFeellikeTemp.text=response.main!!.feels_like.toString()
        var wind:String=response.wind!!.speed.toString()+"m/h"
        holder.forecastWindValue.text=wind
        var humidity:String=response.main!!.humudity.toString()+"%"
        holder.forecastHumidityValue.text=humidity
        var pressure:String=response.main!!.pressure.toString()+"hPa"
        holder.forecastPressureValue.text=pressure
        /**
        var utc:Long?=response.dt
        var date: Date = Date(utc!!*1000L)
        var sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
        var cal: Calendar = Calendar.getInstance()
        var tz: TimeZone =cal.timeZone
        var currrentday:String= sdf.format(date)
        **/
        holder.forecastReportDate.text=response.date


    }
}