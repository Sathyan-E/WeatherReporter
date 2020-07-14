package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.R
import com.example.weatherdetailer.network.Current
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.datefragment_pastdata_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class DatePastDataAdapter(private val list: List<Current>,private var unit:String):RecyclerView.Adapter<DatePastDataAdapter.PastDataViewHolder>() {



class PastDataViewHolder(containerView:View):RecyclerView.ViewHolder(containerView){


    val pastMainDescription: TextView =containerView.past_report_main_description
    val pastClimateDescription: TextView =containerView.past_report_climate_description
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

        val response:Current=list[position]
        holder.pastMainDescription.text=response.weather[0].main
        holder.pastClimateDescription.text=response.weather[0].description
        val icon:String= response.weather[0].icon.toString()
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png").into(holder.pastClimateIcon)
        val temp:String= response.temp.toString()+unit
        holder.pastTemperature.text=temp
        val fTemp=response.feels_like.toString()+unit
        holder.pastFeellikeTemp.text=fTemp
        val wind:String=response.w_speed.toString()+"m/h"
        holder.pastWindValue.text=wind
        val humidity:String=response.humiidity.toString()+"%"
        holder.pastHumidityValue.text=humidity
        val pressure:String=response.pressure.toString()+"hPa"
        holder.pastPressureValue.text=pressure
        val utc:Long?=response.dt
        val date = Date(utc!!*1000L)
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss z")
        val currrentday:String= sdf.format(date)
        holder.pastReportDate.text=currrentday
        
    }

    public fun setUnit(s:String){
        unit=s
    }
}