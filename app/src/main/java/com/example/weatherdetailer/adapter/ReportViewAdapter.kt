package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.R
import com.example.weatherdetailer.network.WeatherResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.reportlayoutitem.view.*

class ReportViewAdapter(private val list:List<WeatherResponse>):RecyclerView.Adapter<ReportViewAdapter.ReportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.reportlayoutitem,parent,false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val response:WeatherResponse=list[position]
        holder.mainDescription.text=response.weather[0].main
        holder.climateDescription.text=response.weather[0].description
        val icon:String= response.weather[0].icon.toString()
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png").into(holder.climateIcon)
        holder.temperature.text= response.main!!.temp.toString()
        holder.feelflikeTemp.text=response.main!!.feels_like.toString()
        val wind:String=response.wind!!.speed.toString()+"m/h"
        holder.windValue.text=wind
        val humidity:String=response.main!!.humudity.toString()+"%"
        holder.humidityValue.text=humidity
        val pressure:String=response.main!!.pressure.toString()+"hPa"
        holder.pressureValue.text=pressure
        holder.reportDate.text=response.date
    }

    class ReportViewHolder(containerView:View):RecyclerView.ViewHolder(containerView){
        val mainDescription:TextView=containerView.report_main_description
        val climateDescription: TextView =containerView.report_climate_description
        val climateIcon:ImageView=containerView.report_climate_image
        val temperature:TextView=containerView.report_city_temp
        val feelflikeTemp:TextView=containerView.report_city_feelslike_temp
        val windValue:TextView=containerView.report_city_wind
        val humidityValue:TextView=containerView.report_city_humidity
        val pressureValue:TextView=containerView.report_city_pressure
        val reportDate:TextView=containerView.report_date

    }

}