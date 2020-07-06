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
import kotlinx.android.synthetic.main.datefragment_pastdata_item.view.*
import kotlinx.android.synthetic.main.reportlayoutitem.view.*
import java.text.SimpleDateFormat
import java.util.*

class DatePastDataAdapter(private val list: List<Current>):RecyclerView.Adapter<DatePastDataAdapter.PastDataViewHolder>() {



class PastDataViewHolder(val containerView:View):RecyclerView.ViewHolder(containerView){
    /**
    val mainClimate=view.main
    val description=view.description
    val temp=view.temp
    **/
    val pastMainDescription: TextView =containerView.past_report_main_description
    val pastClimateDescription=containerView.past_report_climate_description
    val pastClimateIcon: ImageView =containerView.past_report_climate_image
    val pastTemperature: TextView =containerView.past_report_city_temp
    val pastFeellikeTemp: TextView =containerView.past_report_city_feelslike_temp
    val pastWindValue: TextView =containerView.past_report_city_wind
    val pastHumidityValue: TextView =containerView.past_report_city_humidity
    val pastPressureValue: TextView =containerView.past_report_city_pressure
    val pastReportDate: TextView =containerView.past_report_date

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastDataViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.datefragment_pastdata_item
                ,parent,false)
        return PastDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PastDataViewHolder, position: Int) {


       /**
        holder.mainClimate.text=list[position].weather[0].main
        holder.description.text=list[position].weather[0].description
        holder.temp.text=list[position].temp.toString()
        **/
       val response:Current=list[position]
        holder.pastMainDescription.text=response.weather[0].main
        holder.pastClimateDescription.text=response.weather[0].description
        var  icon:String= response.weather[0].icon.toString()
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png").into(holder.pastClimateIcon)
        // var temp:String= response.main.temp.toString()
        holder.pastTemperature.text= response.temp.toString()
        holder.pastFeellikeTemp.text=response.feels_like.toString()
        var wind:String=response.w_speed.toString()+"m/h"
        holder.pastWindValue.text=wind
        var humidity:String=response.humiidity.toString()+"%"
        holder.pastHumidityValue.text=humidity
        var pressure:String=response.pressure.toString()+"hPa"
        holder.pastPressureValue.text=pressure
        var utc:Long?=response.dt
        var date: Date = Date(utc!!*1000L)
        var sdf: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
        var cal: Calendar = Calendar.getInstance()
        var tz: TimeZone =cal.timeZone
        var currrentday:String= sdf.format(date)
        holder.pastReportDate.text=currrentday
        
    }
}