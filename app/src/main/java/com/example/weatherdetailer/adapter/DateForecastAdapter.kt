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
import kotlinx.android.synthetic.main.datefragment_forecast_item.view.*

class DateForecastAdapter (private  val list:List<WeatherResponse>,private var unit:String):RecyclerView.Adapter<DateForecastAdapter.ForecastDataViewHolder>(){

    class ForecastDataViewHolder(containerView:View):RecyclerView.ViewHolder(containerView){

        val forecastMainDescription: TextView =containerView.forecast_report_main_description
        val forecastClimateDescription: TextView =containerView.forecast_report_climate_description
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

        val response: WeatherResponse =list[position]
        holder.forecastMainDescription.text=response.weather[0].main
        holder.forecastClimateDescription.text=response.weather[0].description
        val icon:String= response.weather[0].icon.toString()
        Picasso.get().load("http://openweathermap.org/img/wn/$icon@2x.png").into(holder.forecastClimateIcon)
        val temp:String= response.main!!.temp.toString()+unit
        holder.forecastTemperature.text= temp
        val feelslike=response.main!!.feels_like.toString()+ unit
        holder.forecastFeellikeTemp.text=feelslike
        val wind:String=response.wind!!.speed.toString()+"m/h"
        holder.forecastWindValue.text=wind
        val humidity:String=response.main!!.humudity.toString()+"%"
        holder.forecastHumidityValue.text=humidity
        val pressure:String=response.main!!.pressure.toString()+"hPa"
        holder.forecastPressureValue.text=pressure
        holder.forecastReportDate.text=response.date


    }
    public fun setUnit(u:String){
        unit=u
    }
}